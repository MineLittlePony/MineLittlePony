package com.mumfrey.liteloader.transformers.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ByteCodeUtilities;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * An injectable "event". An event is like a regular callback except that it is
 * more intelligent about where it can be injected in the bytecode and also
 * supports conditional "cancellation", which is the ability to conditionally
 * return from the containing method with a custom return value.
 * 
 * @author Adam Mummery-Smith
 */
public class Event implements Comparable<Event>
{
    /**
     * Natural ordering of events, for use with sorting events which have the
     * same priority.
     */
    private static int eventOrder = 0;

    /**
     * All the events which exist and their registered listeners
     */
    private static final Set<Event> events = new HashSet<Event>();

    private static final List<Map<MethodNode, List<Event>>> proxyHandlerMethods = new ArrayList<Map<MethodNode, List<Event>>>();

    private static int proxyInnerClassIndex = 1;
    
    static
    {
        Event.resizeProxyList();
    }

    /**
     * The name of this event
     */
    protected final String name;

    /**
     * Whether this event is cancellable - if it is cancellable then the
     * isCancelled() -> RETURN code will be injected.
     */
    protected final boolean cancellable;

    /**
     * Natural order of this event, for sorting 
     */
    private final int order;

    /**
     * Priority of this event, for sorting 
     */
    private final int priority;

    private Set<MethodInfo> listeners = new HashSet<MethodInfo>();

    /**
     * Method this event is currently "attached" to, we "attach" at the
     * beginning of a method injection in order to save recalculating things
     * like the return type and descriptor for each invocation, this means we
     * need to calculate these things at most once for each method this event is
     * injecting into.
     */
    protected MethodNode method;

    /**
     * Descriptor for this event in the context of the attached method 
     */
    protected String eventDescriptor;

    /**
     * Method's original MAXS, used as a base to work out whether we need to
     * increase the MAXS value.
     */
    protected int methodMAXS = 0;

    /**
     * True if the attached method is static, used so that we know whether to
     * push "this" onto the stack when constructing the EventInfo, or "null"
     */
    protected boolean methodIsStatic;

    /**
     * Return type for the attached method, used to determine which EventInfo
     * class to use and which method to invoke.
     */
    protected Type methodReturnType;

    protected String eventInfoClass;

    protected Set<MethodInfo> pendingInjections;

    private int injectionCount = 0;

    protected boolean verbose;

    protected Event(String name, boolean cancellable, int priority)
    {
        this.name = name.toLowerCase();
        this.priority = priority;
        this.order = Event.eventOrder++;
        this.cancellable = cancellable;
        this.verbose = true;

        if (Event.events.contains(this))
        {
            throw new IllegalArgumentException("Event " + name + " is already defined");
        }

        Event.events.add(this);
    }

    /**
     * Creates a new event with the specified name, if an event with the
     * specified name already exists then the existing event is returned
     * instead.
     * 
     * @param name Event name (case insensitive)
     * @return new Event instance or existing Event instance
     */
    public static Event getOrCreate(String name)
    {
        return Event.getOrCreate(name, false, 1000, false);
    }

    /**
     * Creates a new event with the specified name, if an event with the
     * specified name already exists then the existing event is returned
     * instead.
     * 
     * @param name Event name (case insensitive)
     * @param cancellable True if the event should be created as cancellable
     * @return new Event instance or existing Event instance
     */
    public static Event getOrCreate(String name, boolean cancellable)
    {
        return Event.getOrCreate(name, cancellable, 1000, true);
    }

    /**
     * Creates a new event with the specified name, if an event with the
     * specified name already exists then the existing event is returned
     * instead.
     * 
     * @param name Event name (case insensitive)
     * @param cancellable True if the event should be created as cancellable
     * @param priority Priority for the event, only used when multiple events
     *      are being injected at the same instruction
     * @return new Event instance or existing Event instance
     */
    public static Event getOrCreate(String name, boolean cancellable, int priority)
    {
        return getOrCreate(name, cancellable, priority, true);
    }

    protected static Event getOrCreate(String name, boolean cancellable, int priority, boolean defining)
    {
        Event event = Event.getEvent(name);
        if (event != null)
        {
            if (!event.cancellable && cancellable && defining)
            {
                throw new IllegalArgumentException("Attempted to define the event " + event.name + " with cancellable '"
                        + cancellable + "' but the event is already defined with cancellable is '" + event.cancellable + "'");
            }

            return event;
        }

        return new Event(name, cancellable, priority);
    }

    /**
     * Get the name of the event (all lowercase)
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get whether this event is cancellable or not
     */
    public boolean isCancellable()
    {
        return this.cancellable;
    }

    /**
     * Get the event priority
     */
    public int getPriority()
    {
        return this.priority;
    }

    /**
     * Set whether to log at INFO or DEBUG
     */
    public Event setVerbose(boolean verbose)
    {
        this.verbose = verbose;
        return this;
    }

    /**
     * Get whether to log at INFO or DEBUG
     */
    public boolean isVerbose()
    {
        return this.verbose;
    }

    /**
     * Get whether this event is currently attached to a method
     */
    public boolean isAttached()
    {
        return this.method != null;
    }

    /**
     * Attaches this event to a particular method, this occurs before injection
     * in order to allow the event to configure its internal state appropriately
     * for the method's signature. Since a single event may be injected into
     * multiple target methods, and may also be injected at multiple points in
     * the same method, this saves us recalculating this information for every
     * injection, and instead just calculate once per method.
     * 
     * @param method Method to attach to
     */
    void attach(final MethodNode method)
    {
        if (this.method != null)
        {
            throw new IllegalStateException("Attempted to attach the event " + this.name + " to " + method.name
                    + " but the event was already attached to " + this.method.name + "!");
        }

        this.method           = method;
        this.methodReturnType = Type.getReturnType(method.desc);
        this.methodMAXS       = method.maxStack;
        this.methodIsStatic   = (method.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
        this.eventInfoClass   = this.getEventInfoClassName();
        this.eventDescriptor  = String.format("(L%s;%s)V", this.eventInfoClass, method.desc.substring(1, method.desc.indexOf(')')));
    }

    /**
     * Detach from the attached method, called once injection is completed for a
     * particular method.
     */
    void detach()
    {
        this.method = null;
    }

    void addPendingInjection(MethodInfo targetMethod)
    {
        if (this.pendingInjections == null)
        {
            this.pendingInjections = new HashSet<MethodInfo>();
        }

        this.pendingInjections.add(targetMethod);
    }

    void notifyInjected(String method, String desc, String className)
    {
        MethodInfo thisInjection = null;

        if (this.pendingInjections != null)
        {
            for (MethodInfo pendingInjection : this.pendingInjections)
            {
                if (pendingInjection.matches(method, desc, className))
                {
                    thisInjection = pendingInjection;
                    break;
                }
            }
        }

        if (thisInjection != null)
        {
            this.pendingInjections.remove(thisInjection);
        }
    }

    int dumpInjectionState()
    {
        int uninjectedCount = 0;
        int pendingInjectionCount = this.pendingInjections != null ? this.pendingInjections.size() : 0;

        LiteLoaderLogger.debug("        Event: %-40s   Injected: %d   Pending: %d %s", this.name, this.injectionCount, pendingInjectionCount,
                this.injectionCount == 0 ? " <<< NOT INJECTED >>>" : "");
        if (pendingInjectionCount > 0)
        {
            for (MethodInfo pending : this.pendingInjections)
            {
                LiteLoaderLogger.debug("           Pending: %s.%s", pending.getOwners(), pending.toString());
                uninjectedCount++;
            }
        }

        return uninjectedCount;
    }

    /**
     * Pre-flight check
     * 
     * @param injectionPoint
     * @param cancellable
     * @param globalEventID
     */
    protected void validate(final AbstractInsnNode injectionPoint, boolean cancellable, final int globalEventID)
    {
        if (this.method == null)
        {
            throw new IllegalStateException("Attempted to inject the event " + this.name + " but the event is not attached!");
        }
    }

    /**
     * Inject bytecode for this event into the currently attached method. When
     * multiple events want to be injected into the same method at the same
     * point only the first event is injected, subsequent events are simply
     * added to the same handler delegate in the EventProxy class. 
     *  
     * @param injectionPoint Point to inject code, new instructions will be
     *      injected directly ahead of the specifed insn
     * @param cancellable Cancellable flag, if true then the cancellation code
     *      (conditional return) will be injected as well
     * @param globalEventID Global event ID, used to map a callback to the
     *      relevant event handler delegate method in EventProxy
     * 
     * @return MethodNode for the event handler delegate
     */
    final MethodNode inject(final AbstractInsnNode injectionPoint, boolean cancellable, final int globalEventID, final boolean captureLocals,
            final Type[] locals)
    {
        // Pre-flight checks
        this.validate(injectionPoint, cancellable, globalEventID);

        Type[] arguments = Type.getArgumentTypes(this.method.desc);
        int initialFrameSize = ByteCodeUtilities.getFirstNonArgLocalIndex(arguments, !this.methodIsStatic);

        boolean doCaptureLocals = captureLocals && locals != null && locals.length > initialFrameSize;
        String eventDescriptor = this.generateEventDescriptor(doCaptureLocals, locals, arguments, initialFrameSize);

        // Create the handler delegate method
        MethodNode handler = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, Event.getHandlerName(globalEventID),
                eventDescriptor, null, null);
        Event.addMethodToActiveProxy(handler);

        LiteLoaderLogger.debug("Event %s is spawning handler %s in class %s", this.name, handler.name, Event.getActiveProxyRef());

        int ctorMAXS = 0, invokeMAXS = arguments.length + (doCaptureLocals ? locals.length - initialFrameSize : 0);
        int marshallVar = this.method.maxLocals++;

        InsnList insns = new InsnList();

        boolean pushReturnValue = false;

        // If this is a ReturnEventInfo AND we are right before a RETURN opcode (so we can expect the *original* return
        // value to be on the stack, then we dup the return value into a local var so we can push it later when we invoke 
        // the ReturnEventInfo ctor
        if (injectionPoint instanceof InsnNode && injectionPoint.getOpcode() >= Opcodes.IRETURN && injectionPoint.getOpcode() < Opcodes.RETURN)
        {
            pushReturnValue = true;
            insns.add(new InsnNode(Opcodes.DUP));
            insns.add(new VarInsnNode(this.methodReturnType.getOpcode(Opcodes.ISTORE), marshallVar));
        }

        // Instance the EventInfo for this event
        insns.add(new TypeInsnNode(Opcodes.NEW, this.eventInfoClass)); ctorMAXS++;
        insns.add(new InsnNode(Opcodes.DUP)); ctorMAXS++; invokeMAXS++;
        ctorMAXS += this.invokeEventInfoConstructor(insns, cancellable, pushReturnValue, marshallVar);
        insns.add(new VarInsnNode(Opcodes.ASTORE, marshallVar));

        // Call the event handler method in the proxy
        insns.add(new VarInsnNode(Opcodes.ALOAD, marshallVar));
        ByteCodeUtilities.loadArgs(arguments, insns, this.methodIsStatic ? 0 : 1);
        if (doCaptureLocals)
        {
            ByteCodeUtilities.loadLocals(locals, insns, initialFrameSize);
        }
        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Event.getActiveProxyRef(), handler.name, handler.desc, false));

        if (cancellable)
        {
            // Inject the if (e.isCancelled()) return e.getReturnValue();
            this.injectCancellationCode(insns, injectionPoint, marshallVar);
        }

        // Inject our generated code into the method
        this.method.instructions.insertBefore(injectionPoint, insns);
        this.method.maxStack = Math.max(this.method.maxStack, Math.max(this.methodMAXS + ctorMAXS, this.methodMAXS + invokeMAXS));

        return handler;
    }

    private String generateEventDescriptor(final boolean captureLocals, final Type[] locals, Type[] argumentTypes, int startIndex)
    {
        if (!captureLocals) return this.eventDescriptor;

        String eventDescriptor = this.eventDescriptor.substring(0, this.eventDescriptor.indexOf(')'));
        for (int l = startIndex; l < locals.length; l++)
        {
            if (locals[l] != null) eventDescriptor += locals[l].getDescriptor();
        }

        return eventDescriptor + ")V";
    }

    protected int invokeEventInfoConstructor(InsnList insns, boolean cancellable, boolean pushReturnValue, int marshallVar)
    {
        int ctorMAXS = 0;

        insns.add(new LdcInsnNode(this.name)); ctorMAXS++;
        insns.add(this.methodIsStatic ? new InsnNode(Opcodes.ACONST_NULL) : new VarInsnNode(Opcodes.ALOAD, 0)); ctorMAXS++;
        insns.add(new InsnNode(cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0)); ctorMAXS++;

        if (pushReturnValue)
        {
            insns.add(new VarInsnNode(this.methodReturnType.getOpcode(Opcodes.ILOAD), marshallVar));
            insns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, this.eventInfoClass, Obf.constructor.name,
                    EventInfo.getConstructorDescriptor(this.methodReturnType), false));
        }
        else
        {
            insns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, this.eventInfoClass, Obf.constructor.name,
                    EventInfo.getConstructorDescriptor(), false));
        }

        return ctorMAXS;
    }

    protected String getEventInfoClassName()
    {
        return EventInfo.getEventInfoClassName(this.methodReturnType).replace('.', '/');
    }

    /**
     * if (e.isCancelled()) return e.getReturnValue();
     * 
     * @param insns
     * @param injectionPoint
     * @param marshallVar
     */
    protected void injectCancellationCode(final InsnList insns, final AbstractInsnNode injectionPoint, int marshallVar)
    {
        insns.add(new VarInsnNode(Opcodes.ALOAD, marshallVar));
        insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, this.eventInfoClass, EventInfo.getIsCancelledMethodName(), 
                EventInfo.getIsCancelledMethodSig(), false));

        LabelNode notCancelled = new LabelNode();
        insns.add(new JumpInsnNode(Opcodes.IFEQ, notCancelled));

        // If this is a void method, just injects a RETURN opcode, otherwise we need to get the return value from the EventInfo
        this.injectReturnCode(insns, injectionPoint, marshallVar);

        insns.add(notCancelled);
    }

    /**
     * Inject the appropriate return code for the method type
     * 
     * @param insns
     * @param injectionPoint
     * @param eventInfoVar
     */
    protected void injectReturnCode(final InsnList insns, final AbstractInsnNode injectionPoint, int eventInfoVar)
    {
        if (this.methodReturnType.equals(Type.VOID_TYPE))
        {
            // Void method, so just return void
            insns.add(new InsnNode(Opcodes.RETURN));
        }
        else
        {
            // Non-void method, so work out which accessor to call to get the return value, and return it
            insns.add(new VarInsnNode(Opcodes.ALOAD, eventInfoVar));
            String accessor = ReturnEventInfo.getReturnAccessor(this.methodReturnType);
            String descriptor = ReturnEventInfo.getReturnDescriptor(this.methodReturnType);
            insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, this.eventInfoClass, accessor, descriptor, false));
            if (this.methodReturnType.getSort() == Type.OBJECT)
            {
                insns.add(new TypeInsnNode(Opcodes.CHECKCAST, this.methodReturnType.getInternalName()));
            }
            insns.add(new InsnNode(this.methodReturnType.getOpcode(Opcodes.IRETURN)));
        }
    }

    /**
     * Add this event to the specified handler 
     * 
     * @param handler
     */
    void addToHandler(MethodNode handler)
    {
        LiteLoaderLogger.debug("Adding event %s to handler %s", this.name, handler.name);

        Event.getEventsForHandlerMethod(handler).add(this);
        this.injectionCount++;
    }

    /**
     * Add a listener for this event, the listener
     * 
     * @param listener
     * @return fluent interface
     */
    public Event addListener(MethodInfo listener)
    {
        if (listener.hasDesc())
        {
            throw new IllegalArgumentException("Descriptor is not allowed for listener methods");
        }

        if (this.pendingInjections != null && this.pendingInjections.size() == 0)
        {
            throw new EventAlreadyInjectedException("The event " + this.name
                    + " was already injected and has 0 pending injections, addListener() is not allowed at this point");
        }

        this.listeners.add(listener);

        return this;
    }

    /**
     * Get currently registered listeners for this event
     */
    public Set<MethodInfo> getListeners()
    {
        return Collections.<MethodInfo>unmodifiableSet(this.listeners);
    }

    /**
     * Get an event by name (case insensitive)
     * 
     * @param eventName
     */
    static Event getEvent(String eventName)
    {
        for (Event event : Event.events)
            if (event.name.equalsIgnoreCase(eventName))
            {
                return event;
            }

        return null;
    }

    /**
     * Populates the event proxy class with delegating methods for all injected
     * events.
     * 
     * @param classNode
     * @param proxyIndex
     */
    static ClassNode populateProxy(final ClassNode classNode, int proxyIndex)
    {
        int handlerCount = 0;
        int invokeCount = 0;
        int lineNumber = proxyIndex < 2 ? 210 : 10; // From EventProxy.java, this really is only to try and make stack traces a bit easier to read

        LiteLoaderLogger.info("Generating new Event Handler Proxy Class %s", classNode.name.replace('/', '.'));

        Map<MethodNode, List<Event>> handlerMethods = Event.proxyHandlerMethods.get(Event.proxyInnerClassIndex); 
        Event.proxyInnerClassIndex++;

        // Loop through all handlers and inject a method for each one
        for (Entry<MethodNode, List<Event>> handler : handlerMethods.entrySet())
        {
            MethodNode handlerMethod = handler.getKey();
            List<Event> handlerEvents = handler.getValue();

            // Args is used to inject appropriate LOAD opcodes to put the method arguments on the stack for each handler invocation
            Type[] args = Type.getArgumentTypes(handlerMethod.desc);

            // Add our generated method to the the class
            classNode.methods.add(handlerMethod);
            handlerCount++;

            InsnList insns = handlerMethod.instructions;
            for (Event event : handlerEvents)
            {
                Set<MethodInfo> listeners = event.listeners;
                if (listeners.size() > 0)
                {
                    LabelNode tryCatchStart = new LabelNode();
                    LabelNode tryCatchEnd = new LabelNode();
                    LabelNode tryCatchHandler1 = new LabelNode();
                    LabelNode tryCatchHandler2 = new LabelNode();
                    LabelNode tryCatchExit = new LabelNode();

                    handlerMethod.tryCatchBlocks.add(new TryCatchBlockNode(tryCatchStart, tryCatchEnd,
                            tryCatchHandler1, "java/lang/NoSuchMethodError"));
                    handlerMethod.tryCatchBlocks.add(new TryCatchBlockNode(tryCatchStart, tryCatchEnd,
                            tryCatchHandler2, "java/lang/NoClassDefFoundError"));

                    insns.add(tryCatchStart); // try {

                    for (MethodInfo listener : listeners)
                    {
                        invokeCount++;

                        LabelNode lineNumberLabel = new LabelNode(new Label());
                        insns.add(lineNumberLabel);
                        insns.add(new LineNumberNode(++lineNumber, lineNumberLabel));

                        ByteCodeUtilities.loadArgs(args, insns, 0);
                        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, listener.ownerRef, listener.getOrInflectName(event.name),
                                handlerMethod.desc, false));
                    }

                    insns.add(tryCatchEnd); // }
                    insns.add(new JumpInsnNode(Opcodes.GOTO, tryCatchExit));

                    insns.add(tryCatchHandler1); // catch (NoSuchMethodError err) {
                    insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Obf.EventProxy.ref, "onMissingHandler",
                            "(Ljava/lang/Error;Lcom/mumfrey/liteloader/transformers/event/EventInfo;)V", false));
                    insns.add(new JumpInsnNode(Opcodes.GOTO, tryCatchExit));

                    insns.add(tryCatchHandler2); // } catch (NoClassDefFoundError err) {
                    insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Obf.EventProxy.ref, "onMissingClass",
                            "(Ljava/lang/Error;Lcom/mumfrey/liteloader/transformers/event/EventInfo;)V", false));
                    insns.add(new JumpInsnNode(Opcodes.GOTO, tryCatchExit));

                    insns.add(tryCatchExit); // }
                }
            }

            insns.add(new InsnNode(Opcodes.RETURN));
        }

        LiteLoaderLogger.info("Successfully generated event handler proxy class with %d handlers(s) and %d total invocations",
                handlerCount, invokeCount);

        return classNode;
    }

    private static List<Event> addMethodToActiveProxy(MethodNode handlerMethod)
    {
        Event.resizeProxyList();

        ArrayList<Event> events = new ArrayList<Event>();
        Event.proxyHandlerMethods.get(Event.proxyInnerClassIndex).put(handlerMethod, events);
        return events;
    }

    private static void resizeProxyList()
    {
        while (Event.proxyHandlerMethods.size() < Event.proxyInnerClassIndex + 1)
        {
            Event.proxyHandlerMethods.add(new LinkedHashMap<MethodNode, List<Event>>());
        }
    }

    private static List<Event> getEventsForHandlerMethod(MethodNode handlerMethod)
    {
        for (Map<MethodNode, List<Event>> handlers : Event.proxyHandlerMethods)
        {
            List<Event> events = handlers.get(handlerMethod);
            if (events != null) return events;
        }

        return Event.addMethodToActiveProxy(handlerMethod);
    }

    private static String getHandlerName(int globalEventID)
    {
        return String.format("$event%05x", globalEventID);
    }

    private static String getActiveProxyRef()
    {
        return Obf.EventProxy.ref + (Event.proxyInnerClassIndex > 1 ? "$" + Event.proxyInnerClassIndex : "");
    }

    @Override
    public int compareTo(Event other)
    {
        if (other == null) return 0;
        if (other.priority == this.priority) return this.order - other.order;
        return (this.priority - other.priority);
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this) return true;
        if (other instanceof Event) return ((Event)other).name.equals(this.name);
        return false;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
