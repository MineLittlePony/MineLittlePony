package com.mumfrey.liteloader.transformers.event;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;
import com.google.common.collect.Maps;
import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ByteCodeUtilities;
import com.mumfrey.liteloader.transformers.ClassTransformer;
import com.mumfrey.liteloader.transformers.ObfProvider;
import com.mumfrey.liteloader.transformers.access.AccessorTransformer;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

/**
 * EventTransformer is the spiritual successor to the
 * <tt>CallbackInjectionTransformer</tt> and is a more advanced and flexible
 * version of the same premise. Like the CallbackInjectionTransformer, it can be
 * used to inject callbacks intelligently into a target method, however it has
 * the following additional capabilities which make it more flexible and
 * scalable:
 * 
 * <ul>
 *     <li>Injections are not restricted to RETURN opcodes or profiler
 *     invocations, each injection is determined by supplying an InjectionPoint
 *     instance to the {@code addEvent} method which is used to find the
 *     injection point(s) in the method.</li>
 *      
 *     <li>Injected events can optionally be specified as *cancellable* which
 *     allows method execution to be pre-emptively halted based on the
 *     cancellation status of the event. For methods with a return value, the
 *     return value may be specified by the event handler.</li>
 *      
 *     <li>Injected events call back against a dynamically-generated proxy
 *     class, this means that it is no longer necessary to provide your own
 *     implementation of a static callback proxy, events can call back directly
 *     against handler methods in your own codebase.</li>
 *      
 *     <li>Event injections are more intelligent about injecting at arbitrary
 *     points in the bytecode without corrupting the local stack, and increase
 *     MAXS as required.</li>
 *      
 *     <li>Event injections do not "collide" like callback injections do - this
 *     means that if multiple events are injected by multiple sources at the
 *     same point in the bytecode, then all event handlers will receive and
 *     handle the event in one go. To provide for this, each event handler is
 *     defined with an intrinsic "priority" which determines its call order when
 *     this situation occurs</li>
 * </ul>
 * 
 * @author Adam Mummery-Smith
 */
public final class EventTransformer extends ClassTransformer
{
    public static final boolean DUMP = Booleans.parseBoolean(System.getProperty("liteloader.debug.dump"), false);

    public static final boolean VALIDATE = Booleans.parseBoolean(System.getProperty("liteloader.debug.validate"), false);

    /**
     * Multidimensional map of class names -> target method signatures -> events
     * to inject. 
     */
    private static Map<String, Map<String, Map<Event, InjectionPoint>>> eventMappings = Maps.newHashMap();

    private static AccessorTransformer accessorTransformer;

    private int globalEventID = 0;

    static class Injection
    {
        private final AbstractInsnNode node;

        private final boolean captureLocals;

        private final Set<Event> events = new TreeSet<Event>();

        private boolean hasLocals = false;

        private LocalVariableNode[] locals;

        public Injection(AbstractInsnNode node, boolean captureLocals)
        {
            this.node = node;
            this.captureLocals = captureLocals;
        }

        public AbstractInsnNode getNode()
        {
            return this.node;
        }

        public Set<Event> getEvents()
        {
            return this.events;
        }

        public LocalVariableNode[] getLocals()
        {
            return this.locals;
        }

        public Type[] getLocalTypes()
        {
            if (this.locals == null) return null;

            Type[] localTypes = new Type[this.locals.length];
            for (int l = 0; l < this.locals.length; l++)
            {
                if (this.locals[l] != null)
                {
                    localTypes[l] = Type.getType(this.locals[l].desc);
                }
            }
            return localTypes;
        }

        public boolean hasLocals()
        {
            return this.hasLocals;
        }

        public void setLocals(LocalVariableNode[] locals)
        {
            this.hasLocals = true;
            if (locals == null) return;
            this.locals = locals;
        }

        public boolean captureLocals()
        {
            return this.captureLocals;
        }

        public void checkCaptureLocals(InjectionPoint injectionPoint)
        {
            if (injectionPoint.captureLocals != this.captureLocals)
            {
                throw new RuntimeException("Overlapping injection points defined with incompatible settings. Attempting to handle "
                        + injectionPoint + " with capture locals [" + injectionPoint.captureLocals + "] but already defined injection point with ["
                        + this.captureLocals + "]");
            }
        }

        public void add(Event event)
        {
            this.events.add(event);
        }

        public int size()
        {
            return this.events.size();
        }

        public Event getHead()
        {
            return this.events.iterator().next();
        }

        public void addEventsToHandler(MethodNode handler)
        {
            for (Event event : this.events)
            {
                event.addToHandler(handler);
            }
        }

        public boolean isCancellable()
        {
            boolean cancellable = false;
            for (Event event : this.events)
                cancellable |= event.isCancellable();
            return cancellable;
        }
    }

    static void addEvent(Event event, String className, String signature, InjectionPoint injectionPoint)
    {
        Map<String, Map<Event, InjectionPoint>> mappings = EventTransformer.eventMappings.get(className);
        if (mappings == null)
        {
            mappings = new HashMap<String, Map<Event, InjectionPoint>>();
            EventTransformer.eventMappings.put(className, mappings);
        }

        Map<Event, InjectionPoint> events = mappings.get(signature);
        if (events == null)
        {
            events = new LinkedHashMap<Event, InjectionPoint>();
            mappings.put(signature, events);
        }

        events.put(event, injectionPoint);
    }

    static void addAccessor(String interfaceName)
    {
        EventTransformer.addAccessor(interfaceName, null);
    }

    static void addAccessor(String interfaceName, ObfProvider obfProvider)
    {
        if (EventTransformer.accessorTransformer == null)
        {
            EventTransformer.accessorTransformer = new AccessorTransformer()
            {
                @Override
                protected void addAccessors() {}
            };
        }

        EventTransformer.accessorTransformer.addAccessor(interfaceName, obfProvider);
    }

    @Override
    public final byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass != null && EventTransformer.eventMappings.containsKey(transformedName))
        {
            return this.injectEvents(name, transformedName, basicClass, EventTransformer.eventMappings.get(transformedName));
        }

        if (EventTransformer.accessorTransformer != null)
        {
            return EventTransformer.accessorTransformer.transform(name, transformedName, basicClass);
        }

        return basicClass;
    }

    private byte[] injectEvents(String name, String transformedName, byte[] basicClass, Map<String, Map<Event, InjectionPoint>> mappings)
    {
        if (mappings == null) return basicClass;

        ClassNode classNode = this.readClass(basicClass, true);

        for (MethodNode method : classNode.methods)
        {
            String signature = MethodInfo.generateSignature(method.name, method.desc);
            Map<Event, InjectionPoint> methodInjections = mappings.get(signature);
            if (methodInjections != null)
            {
                this.injectIntoMethod(classNode, signature, method, methodInjections);
            }
        }

        if (EventTransformer.accessorTransformer != null)
        {
            EventTransformer.accessorTransformer.apply(name, transformedName, basicClass, classNode);
        }

        if (EventTransformer.VALIDATE)
        {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(new CheckClassAdapter(writer));
        }

        byte[] bytes = this.writeClass(classNode);

        if (EventTransformer.DUMP)
        {
            try
            {
                FileUtils.writeByteArrayToFile(new File(".classes/" + Obf.lookupMCPName(transformedName).replace('.', '/') + ".class"), bytes);
            }
            catch (IOException ex) {}
        }

        return bytes;
    }

    /**
     * @param classNode
     * @param signature
     * @param method
     * @param methodInjections
     */
    void injectIntoMethod(ClassNode classNode, String signature, MethodNode method, Map<Event, InjectionPoint> methodInjections)
    {
        Map<AbstractInsnNode, Injection> injectionPoints = this.findInjectionPoints(classNode, method, methodInjections);

        for (Entry<AbstractInsnNode, Injection> injectionPoint : injectionPoints.entrySet())
        {
            this.injectEventsAt(classNode, method, injectionPoint.getKey(), injectionPoint.getValue());
        }

        for (Event event : methodInjections.keySet())
        {
            event.notifyInjected(method.name, method.desc, classNode.name);
            event.detach();
        }
    }

    /**
     * @param classNode
     * @param method
     * @param methodInjections
     */
    private Map<AbstractInsnNode, Injection> findInjectionPoints(ClassNode classNode, MethodNode method, Map<Event, InjectionPoint> methodInjections)
    {
        ReadOnlyInsnList insns = new ReadOnlyInsnList(method.instructions);
        Collection<AbstractInsnNode> nodes = new ArrayList<AbstractInsnNode>(32);
        Map<AbstractInsnNode, Injection> injectionPoints = new LinkedHashMap<AbstractInsnNode, Injection>();
        for (Entry<Event, InjectionPoint> eventEntry : methodInjections.entrySet())
        {
            Event event = eventEntry.getKey();
            event.attach(method);
            InjectionPoint injectionPoint = eventEntry.getValue();
            nodes.clear();
            if (injectionPoint.find(method.desc, insns, nodes, event))
            {
                for (AbstractInsnNode node : nodes)
                {
                    Injection injection = injectionPoints.get(node);
                    if (injection == null)
                    {
                        injection = new Injection(node, injectionPoint.captureLocals());
                        injectionPoints.put(node, injection);
                    }
                    else
                    {
                        injection.checkCaptureLocals(injectionPoint);
                    }

                    if (injectionPoint.captureLocals() && !injection.hasLocals())
                    {
                        LocalVariableNode[] locals = ByteCodeUtilities.getLocalsAt(classNode, method, node);
                        injection.setLocals(locals);
                        if (injectionPoint.logLocals())
                        {
                            int startPos = ByteCodeUtilities.getFirstNonArgLocalIndex(method);

                            LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
                            LiteLoaderLogger.debug("Logging local variables for " + injectionPoint);
                            for (int i = startPos; i < locals.length; i++)
                            {
                                LocalVariableNode local = locals[i];
                                if (local != null)
                                {
                                    LiteLoaderLogger.debug("    Local[%d] %s %s", i, ByteCodeUtilities.getTypeName(Type.getType(local.desc)),
                                            local.name);
                                }
                            }
                            LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
                        }
                    }

                    injection.add(event);
                }
            }
        }

        return injectionPoints;
    }

    /**
     * @param classNode
     * @param method
     * @param injectionPoint
     * @param injection
     */
    private void injectEventsAt(ClassNode classNode, MethodNode method, AbstractInsnNode injectionPoint, Injection injection)
    {
        Event head = injection.getHead();

        Verbosity verbosity = head.isVerbose() ? Verbosity.NORMAL : Verbosity.VERBOSE;
        LiteLoaderLogger.info(verbosity, "Injecting %s[x%d] in %s in %s", head.getName(), injection.size(), method.name,
                ClassTransformer.getSimpleClassName(classNode));

        MethodNode handler = head.inject(injectionPoint, injection.isCancellable(), this.globalEventID, injection.captureLocals(),
                injection.getLocalTypes());
        injection.addEventsToHandler(handler);

        this.globalEventID++;
    }

    public static void dumpInjectionState()
    {
        int uninjectedCount = 0;
        int eventCount = 0;

        LiteLoaderLogger.debug("EventInjectionTransformer: Injection State");
        LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
        for (Entry<String, Map<String, Map<Event, InjectionPoint>>> mapping : EventTransformer.eventMappings.entrySet())
        {
            LiteLoaderLogger.debug("Class: %s", mapping.getKey());
            for (Entry<String, Map<Event, InjectionPoint>> classMapping : mapping.getValue().entrySet())
            {
                LiteLoaderLogger.debug("    Method: %s", classMapping.getKey());
                for (Event event : classMapping.getValue().keySet())
                {
                    uninjectedCount += event.dumpInjectionState();
                    eventCount++;
                }
            }
        }
        LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
        LiteLoaderLogger.debug("Listed %d injection candidates with %d uninjected", eventCount, uninjectedCount);
        LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
    }
}