package com.mumfrey.liteloader.transformers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.Callback.CallbackType;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Transformer which injects callbacks by searching for profiler invocations
 * and RETURN opcodes.
 * 
 * @author Adam Mummery-Smith
 * @deprecated Use Event Injection instead
 */
@Deprecated
public abstract class CallbackInjectionTransformer extends ClassTransformer
{
    /**
     * Mappings for profiler method invocations
     */
    private Map<String, Map<String, Callback>> profilerCallbackMappings = new HashMap<String, Map<String, Callback>>();

    /**
     * Mappings for pre-return and method start callbacks
     */
    private Map<String, Map<String, Callback>> callbackMappings = new HashMap<String, Map<String, Callback>>();

    public CallbackInjectionTransformer()
    {
        this.addCallbacks();
    }

    /**
     * Subclasses must override this method and add their mappings
     */
    protected abstract void addCallbacks();

    /**
     * @param className
     * @param methodName
     * @param methodSignature
     * @param callback
     */
    protected final void addCallback(String className, String methodName, String methodSignature, Callback callback)
    {
        if (callback.isProfilerCallback())
        {
            if (!this.profilerCallbackMappings.containsKey(className))
            {
                this.profilerCallbackMappings.put(className, new HashMap<String, Callback>());
            }

            String signature = CallbackInjectionTransformer.generateSignature(className, methodName, methodSignature, callback.getProfilerMethod(),
                    callback.getProfilerMethodSignature(), callback.getSectionName());
            this.addCallbackMapping(this.profilerCallbackMappings.get(className), signature, callback);
        }
        else
        {
            if (!this.callbackMappings.containsKey(className))
            {
                this.callbackMappings.put(className, new HashMap<String, Callback>());
            }

            String signature = CallbackInjectionTransformer.generateSignature(className, methodName, methodSignature, callback.getType());
            this.addCallbackMapping(this.callbackMappings.get(className), signature, callback);
        }
    }

    /**
     * @param callbacks
     * @param signature
     * @param callback
     */
    private void addCallbackMapping(Map<String, Callback> callbacks, String signature, Callback callback)
    {
        if (callbacks.containsKey(signature))
        {
            Callback existingCallback = callbacks.get(signature);
            if (existingCallback.equals(callback)) return;

            if (callback.injectReturn() || existingCallback.injectReturn())
            {
                String errorMessage = String.format("Callback for %s is already defined for %s, cannot add %s",
                        signature, existingCallback, callback);
                LiteLoaderLogger.severe(errorMessage);
                throw new InjectedCallbackCollisionError(errorMessage);
            }

            existingCallback.addChainedCallback(callback);
        }
        else
        {
            callbacks.put(signature, callback);
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.launchwrapper.IClassTransformer
     * #transform(java.lang.String, java.lang.String, byte[])
     */
    @Override
    public final byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass != null && this.profilerCallbackMappings.containsKey(transformedName) || this.callbackMappings.containsKey(transformedName))
        {
            return this.injectCallbacks(basicClass, this.profilerCallbackMappings.get(transformedName), this.callbackMappings.get(transformedName));
        }

        return basicClass;
    }

    /**
     * @param basicClass
     * @param profilerMappings
     */
    private byte[] injectCallbacks(byte[] basicClass, Map<String, Callback> profilerMappings, Map<String, Callback> mappings)
    {
        ClassNode classNode = this.readClass(basicClass, true);
        String className = classNode.name.replace('/', '.');
        String classType = Type.getObjectType(classNode.name).toString();

        for (MethodNode method : classNode.methods)
        {
            int returnNumber = 0;
            String section = null;
            int methodReturnOpcode = Type.getReturnType(method.desc).getOpcode(Opcodes.IRETURN);

            if (mappings != null)
            {
                String headSignature = CallbackInjectionTransformer.generateSignature(classNode.name, method.name, method.desc,
                        CallbackType.REDIRECT);
                if (mappings.containsKey(headSignature))
                {
                    Callback callback = mappings.get(headSignature);
                    InsnList callbackInsns = this.genCallbackInsns(classType, method, callback);
                    if (callbackInsns != null)
                    {
                        LiteLoaderLogger.info("Injecting %s callback for %s in class %s", callback.getType().name().toLowerCase(),
                                callback, className);
                        method.instructions.insert(callbackInsns);
                        if (callback.injectReturn()) continue;
                    }
                }
            }

            Map<MethodInsnNode, Callback> profilerCallbackInjectionNodes = new HashMap<MethodInsnNode, Callback>();

            Iterator<AbstractInsnNode> iter = method.instructions.iterator();
            AbstractInsnNode lastInsn = null;
            while (iter.hasNext())
            {
                AbstractInsnNode insn = iter.next();
                if (profilerMappings != null && insn.getOpcode() == Opcodes.INVOKEVIRTUAL)
                {
                    MethodInsnNode invokeNode = (MethodInsnNode)insn;
                    if (Obf.Profiler.ref.equals(invokeNode.owner) || Obf.Profiler.obf.equals(invokeNode.owner))
                    {
                        section = "";
                        if (lastInsn instanceof LdcInsnNode)
                        {
                            section = ((LdcInsnNode)lastInsn).cst.toString();
                        }

                        String signature = CallbackInjectionTransformer.generateSignature(classNode.name, method.name, method.desc, invokeNode.name,
                                invokeNode.desc, section);

                        if (profilerMappings.containsKey(signature))
                        {
                            profilerCallbackInjectionNodes.put(invokeNode, profilerMappings.get(signature).getNextCallback());
                        }
                    }
                }
                else if (mappings != null && insn.getOpcode() == methodReturnOpcode)
                {
                    String returnSignature = CallbackInjectionTransformer.generateSignature(classNode.name, method.name, method.desc,
                            CallbackType.RETURN);
                    if (mappings.containsKey(returnSignature))
                    {
                        Callback callback = mappings.get(returnSignature);
                        InsnList callbackInsns = this.genCallbackInsns(classType, method, callback, returnNumber++);
                        if (callbackInsns != null)
                        {
                            LiteLoaderLogger.info("Injecting method return callback for %s in class %s", callback, className);
                            method.instructions.insertBefore(insn, callbackInsns);
                        }
                        else
                        {
                            LiteLoaderLogger.severe("Skipping callback mapping %s because the return behaviour does not match the method signature",
                                    returnSignature);
                        }
                    }
                }

                lastInsn = insn;
            }

            for (Entry<MethodInsnNode, Callback> profilerCallbackNode : profilerCallbackInjectionNodes.entrySet())
            {
                Callback callback = profilerCallbackNode.getValue();

                LiteLoaderLogger.info("Injecting profiler invocation callback for %s in class %s", callback, className);
                InsnList injected = this.genProfilerCallbackInsns(new InsnList(), callback, callback.refNumber++);
                method.instructions.insert(profilerCallbackNode.getKey(), injected);
            }
        }

        return this.writeClass(classNode);
    }

    /**
     * @param injected
     * @param callback
     * @param refNumber
     */
    private InsnList genProfilerCallbackInsns(InsnList injected, Callback callback, int refNumber)
    {
        injected.add(new LdcInsnNode(refNumber));
        injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, callback.getCallbackClass(), callback.getCallbackMethod(), "(I)V", false));

        if (callback.getChainedCallbacks().size() > 0)
        {
            for (Callback chainedCallback : callback.getChainedCallbacks())
                this.genProfilerCallbackInsns(injected, chainedCallback, refNumber);
        }

        return injected;
    }

    /**
     * Generate bytecode for injecting the specified callback into the specified
     * methodNode.
     * 
     * @param classType
     * @param methodNode
     * @param callback
     */
    private InsnList genCallbackInsns(String classType, MethodNode methodNode, Callback callback)
    {
        return this.genCallbackInsns(classType, methodNode, callback, -1);
    }

    /**
     * Generate bytecode for injecting the specified callback into the specified
     * methodNode.
     * 
     * @param classType
     * @param methodNode
     * @param callback
     * @param returnNumber
     */
    private InsnList genCallbackInsns(String classType, MethodNode methodNode, Callback callback, int returnNumber)
    {
        return this.genCallbackInsns(new InsnList(), classType, methodNode, callback, returnNumber);
    }

    /**
     * @param injected
     * @param classType
     * @param methodNode
     * @param callback
     * @param returnNumber
     */
    private InsnList genCallbackInsns(InsnList injected, String classType, MethodNode methodNode, Callback callback, int returnNumber)
    {
        // First work out some flags which alter the behaviour of this injection
        boolean methodReturnsVoid = Type.getReturnType(methodNode.desc).equals(Type.VOID_TYPE);
        boolean methodIsStatic = (methodNode.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
        boolean hasReturnRef = returnNumber > -1;

        // Generate the parts of the callback signature that we need
        Type callbackReturnType = Type.getReturnType(methodNode.desc);
        String callbackReturnValueArg = methodReturnsVoid ? "" : callbackReturnType.toString();
        String classInstanceArg = methodIsStatic ? "" : classType;

        // If this is a pre-return injection, push the invocation reference onto the call stack
        if (hasReturnRef) injected.insert(new IntInsnNode(Opcodes.BIPUSH, returnNumber));

        // If the method is non-static, then we pass in the class instance as an argument
        if (!methodIsStatic) injected.add(new VarInsnNode(Opcodes.ALOAD, 0));

        // Push the method arguments onto the stack
        int argNumber = methodIsStatic ? 0 : 1;
        for (Type type : Type.getArgumentTypes(methodNode.desc))
        {
            injected.add(new VarInsnNode(type.getOpcode(Opcodes.ILOAD), argNumber));
            argNumber += type.getSize();
        }

        // Generate the callback method descriptor
        String callbackMethodDesc = String.format("(%s%s%s%s)%s", hasReturnRef ? callbackReturnValueArg : "", hasReturnRef ? "I" : "",
                classInstanceArg, CallbackInjectionTransformer.getMethodArgs(methodNode), callbackReturnType);

        // Add the callback method insn to the injected instructions list
        injected.add(new MethodInsnNode(Opcodes.INVOKESTATIC, callback.getCallbackClass(), callback.getCallbackMethod(), callbackMethodDesc, false));

        // If the callback RETURNs a value then push the appropriate RETURN opcode into the insns list
        if (callback.injectReturn())
        {
            injected.add(new InsnNode(callbackReturnType.getOpcode(Opcodes.IRETURN)));
        }
        else if (callback.getChainedCallbacks().size() > 0)
        {
            for (Callback chainedCallback : callback.getChainedCallbacks())
            {
                this.genCallbackInsns(injected, classType, methodNode, chainedCallback, returnNumber);
            }
        }

        // return the generated code
        return injected;
    }

    /**
     * @param method
     */
    private static String getMethodArgs(MethodNode method)
    {
        return method.desc.substring(1, method.desc.lastIndexOf(')'));
    }

    /**
     * @param className
     * @param methodName
     * @param methodSignature
     * @param invokeName
     * @param invokeSig
     * @param section
     */
    private static String generateSignature(String className, String methodName, String methodSignature,
            String invokeName, String invokeSig, String section)
    {
        return String.format("%s::%s%s@%s%s/%s", className.replace('.', '/'), methodName, methodSignature, invokeName, invokeSig, section);
    }

    /**
     * @param className
     * @param methodName
     * @param methodSignature
     * @param callbackType
     */
    private static String generateSignature(String className, String methodName, String methodSignature, Callback.CallbackType callbackType)
    {
        return String.format("%s::%s%s@%s", className.replace('.', '/'), methodName, methodSignature, callbackType.getSignature());
    }
}
