package com.mumfrey.liteloader.transformers.access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.launchwrapper.Launch;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ByteCodeUtilities;
import com.mumfrey.liteloader.transformers.ClassTransformer;
import com.mumfrey.liteloader.transformers.ObfProvider;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Transformer which can inject accessor methods defined by an annotated
 * interface into a target class. 
 * 
 * @author Adam Mummery-Smith
 */
public abstract class AccessorTransformer extends ClassTransformer
{
    static final Pattern ordinalRefPattern = Pattern.compile("^#([0-9]{1,5})$");

    /**
     * An injection record
     * 
     * @author Adam Mummery-Smith
     */
    class AccessorInjection
    {
        /**
         * Full name of the interface to inject
         */
        private final String iface;

        /**
         * Obfuscation table class specified by the interface
         */
        private final Class<? extends Obf> table;

        /**
         * Obfuscation provider for this context
         */
        private final ObfProvider obfProvider;

        /**
         * Target class to inject into 
         */
        private final Obf target;

        /**
         * Create a new new accessor using the specified template interface
         * 
         * @param iface Template interface
         * @throws IOException Thrown if an problem occurs when loading the
         *      interface bytecode
         */
        protected AccessorInjection(String iface) throws IOException
        {
            this(iface, null);
        }

        /**
         * Create a new new accessor using the specified template interface
         * 
         * @param iface Template interface
         * @param obfProvider Obfuscation provider for this context
         * @throws IOException Thrown if an problem occurs when loading the
         *      interface bytecode
         */
        protected AccessorInjection(String iface, ObfProvider obfProvider) throws IOException
        {
            ClassNode ifaceNode = ByteCodeUtilities.loadClass(iface, false);

            if (ifaceNode.interfaces.size() > 0)
            {
                String interfaceList = ifaceNode.interfaces.toString().replace('/', '.');
                throw new RuntimeException("Accessor interface must not extend other interfaces. Found " + interfaceList + " in " + iface);
            }

            this.iface = iface;
            this.obfProvider = obfProvider;
            this.table = this.setupTable(ifaceNode);
            this.target = this.setupTarget(ifaceNode);
        }

        /**
         * Get an obfuscation table mapping by name, first uses any supplied
         * context provider, then any obfuscation table class specified by an
         * {@link ObfTableClass} annotation on the interface itself, and fails
         * over onto the LiteLoader obfuscation table. If the entry is not
         * matched in any of the above locations then an exception is thrown. 
         * 
         * @param name Obfuscation table entry to fetch
         */
        private Obf getObf(List<String> names)
        {
            String name = names.get(0);

            Matcher ordinalPattern = AccessorTransformer.ordinalRefPattern.matcher(name);
            if (ordinalPattern.matches())
            {
                int ordinal = Integer.parseInt(ordinalPattern.group(1));
                return new Obf.Ord(ordinal);
            }

            if (this.obfProvider != null)
            {
                Obf obf = this.obfProvider.getByName(name);
                if (obf != null)
                {
                    return obf;
                }
            }

            Obf obf = Obf.getByName(this.table, name);
            if (obf != null)
            {
                return obf;
            }

            if (names.size() > 0 && names.size() < 4)
            {
                String name2 = names.size() > 1 ? names.get(1) : name;
                String name3 = names.size() > 2 ? names.get(2) : name;
                return new AccessorTransformer.Mapping(name, name2, name3);
            }

            throw new RuntimeException("Invalid obfuscation table entry specified: '" + names + "'");
        }

        /**
         * Get the target class of this injection
         */
        protected Obf getTarget()
        {
            return this.target;
        }

        /**
         * Inspects the target class for an {@link ObfTableClass} annotation and
         * attempts to get a handle for the class specified. On failure, the
         * LiteLoader {@link Obf} is returned.
         */
        @SuppressWarnings("unchecked")
        private Class<? extends Obf> setupTable(ClassNode ifaceNode)
        {
            AnnotationNode annotation = ByteCodeUtilities.getInvisibleAnnotation(ifaceNode, ObfTableClass.class);
            if (annotation != null)
            {
                try
                {
                    Type obfTableType = ByteCodeUtilities.getAnnotationValue(annotation);
                    return (Class<? extends Obf>)Class.forName(obfTableType.getClassName(), true, Launch.classLoader);
                }
                catch (ClassNotFoundException ex)
                {
                    ex.printStackTrace();
                }
            }

            return Obf.class;
        }

        /**
         * Locates the {@link Accessor} annotation on the interface in order to
         * determine the target class.
         */
        private Obf setupTarget(ClassNode ifaceNode)
        {
            AnnotationNode annotation = ByteCodeUtilities.getInvisibleAnnotation(ifaceNode, Accessor.class);
            if (annotation == null)
            {
                throw new RuntimeException("Accessor interfaces must be annotated with an @Accessor annotation specifying the target class");
            }

            List<String> targetClass = ByteCodeUtilities.<List<String>>getAnnotationValue(annotation);
            if (targetClass == null || targetClass.isEmpty())
            {
                throw new RuntimeException("Invalid @Accessor annotation, the annotation must specify a target class");
            }

            return this.getObf(targetClass);
        }

        /**
         * Apply this injection to the specified target ClassNode
         * 
         * @param classNode Class tree to apply to
         */
        protected void apply(ClassNode classNode)
        {
            String ifaceRef = this.iface.replace('.', '/');

            if (classNode.interfaces.contains(ifaceRef))
            {
                LiteLoaderLogger.debug("[AccessorTransformer] Skipping %s because %s was already applied", classNode.name, this.iface);
                return;
            }

            classNode.interfaces.add(ifaceRef);

            try
            {
                LiteLoaderLogger.debug("[AccessorTransformer] Loading %s", this.iface);
                ClassNode ifaceNode = ByteCodeUtilities.loadClass(this.iface, AccessorTransformer.this);

                for (MethodNode method : ifaceNode.methods)
                {
                    this.addMethod(classNode, method);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        /**
         * Add a method from the interface to the target class
         * 
         * @param classNode Target class
         * @param method Method to add
         */
        private void addMethod(ClassNode classNode, MethodNode method)
        {
            if (!this.addMethodToClass(classNode, method))
            {
                LiteLoaderLogger.debug("[AccessorTransformer] Method %s already exists in %s", method.name, classNode.name);
                return;
            }

            LiteLoaderLogger.debug("[AccessorTransformer] Attempting to add %s to %s", method.name, classNode.name);

            List<String> targetId = null;
            AnnotationNode accessor = ByteCodeUtilities.getInvisibleAnnotation(method, Accessor.class);
            AnnotationNode invoker = ByteCodeUtilities.getInvisibleAnnotation(method, Invoker.class);
            if (accessor != null)
            {
                targetId = ByteCodeUtilities.<List<String>>getAnnotationValue(accessor);
                Obf target = this.getObf(targetId);
                if (this.injectAccessor(classNode, method, target)) return;
            }
            else if (invoker != null)
            {
                targetId = ByteCodeUtilities.<List<String>>getAnnotationValue(invoker);
                Obf target = this.getObf(targetId);
                if (this.injectInvoker(classNode, method, target)) return;
            }
            else
            {
                LiteLoaderLogger.severe("[AccessorTransformer] Method %s for %s has no @Accessor or @Invoker annotation, the method will "
                        + "be ABSTRACT!", method.name, this.iface);
                this.injectException(classNode, method, "No @Accessor or @Invoker annotation on method");
                return;
            }

            LiteLoaderLogger.severe("[AccessorTransformer] Method %s for %s could not locate target member, the method will be ABSTRACT!",
                    method.name, this.iface);
            this.injectException(classNode, method, "Could not locate target class member '" + targetId + "'");
        }

        /**
         * Inject an accessor method into the target class
         * 
         * @param classNode
         * @param method
         * @param targetName
         */
        private boolean injectAccessor(ClassNode classNode, MethodNode method, Obf target)
        {
            FieldNode targetField = ByteCodeUtilities.findField(classNode, target);
            if (targetField != null)
            {
                LiteLoaderLogger.debug("[AccessorTransformer] Found field %s for %s", targetField.name, method.name);
                if (Type.getReturnType(method.desc) != Type.VOID_TYPE)
                {
                    this.populateGetter(classNode, method, targetField);
                }
                else
                {
                    this.populateSetter(classNode, method, targetField);
                }

                return true;
            }

            return false;
        }

        /**
         * Inject an invoke (proxy) method into the target class
         * 
         * @param classNode
         * @param method
         * @param targetName
         */
        private boolean injectInvoker(ClassNode classNode, MethodNode method, Obf target)
        {
            MethodNode targetMethod = ByteCodeUtilities.findMethod(classNode, target, method.desc);
            if (targetMethod != null)
            {
                LiteLoaderLogger.debug("[AccessorTransformer] Found method %s for %s", targetMethod.name, method.name);
                this.populateInvoker(classNode, method, targetMethod);
                return true;
            }

            return false;
        }

        /**
         * Populate the bytecode instructions for a getter accessor
         * 
         * @param classNode
         * @param method
         * @param field
         */
        private void populateGetter(ClassNode classNode, MethodNode method, FieldNode field)
        {
            Type returnType = Type.getReturnType(method.desc);
            Type fieldType = Type.getType(field.desc);
            if (!returnType.equals(fieldType))
            {
                throw new RuntimeException("Incompatible types! Field type: " + fieldType + " Method type: " + returnType);
            }
            boolean isStatic = (field.access & Opcodes.ACC_STATIC) != 0;

            method.instructions.clear();
            method.maxLocals = ByteCodeUtilities.getFirstNonArgLocalIndex(method);
            method.maxStack = fieldType.getSize();

            if (isStatic)
            {
                method.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field.name, field.desc));
            }
            else
            {
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classNode.name, field.name, field.desc));
            }

            method.instructions.add(new InsnNode(returnType.getOpcode(Opcodes.IRETURN)));
        }

        /**
         * Populate the bytecode instructions for a setter
         * 
         * @param classNode
         * @param method
         * @param field
         */
        private void populateSetter(ClassNode classNode, MethodNode method, FieldNode field)
        {
            Type[] argTypes = Type.getArgumentTypes(method.desc);
            if (argTypes.length != 1)
            {
                throw new RuntimeException("Invalid setter! " + method.name + " must take exactly one argument");
            }
            Type argType = argTypes[0];
            Type fieldType = Type.getType(field.desc);
            if (!argType.equals(fieldType))
            {
                throw new RuntimeException("Incompatible types! Field type: " + fieldType + " Method type: " + argType);
            }
            boolean isStatic = (field.access & Opcodes.ACC_STATIC) != 0;

            method.instructions.clear();
            method.maxLocals = ByteCodeUtilities.getFirstNonArgLocalIndex(method);
            method.maxStack = fieldType.getSize();

            if (isStatic)
            {
                method.instructions.add(new VarInsnNode(argType.getOpcode(Opcodes.ILOAD), 0));
                method.instructions.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, field.name, field.desc));
            }
            else
            {
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.add(new VarInsnNode(argType.getOpcode(Opcodes.ILOAD), 1));
                method.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, field.name, field.desc));
            }

            method.instructions.add(new InsnNode(Opcodes.RETURN));
        }

        /**
         * Populate the bytecode instructions for an invoker (proxy) method
         * 
         * @param classNode
         * @param method
         * @param targetMethod
         */
        private void populateInvoker(ClassNode classNode, MethodNode method, MethodNode targetMethod)
        {
            Type[] args = Type.getArgumentTypes(targetMethod.desc);
            Type returnType = Type.getReturnType(targetMethod.desc);
            boolean isStatic = (targetMethod.access & Opcodes.ACC_STATIC) != 0;

            method.instructions.clear();
            method.maxStack = (method.maxLocals = ByteCodeUtilities.getFirstNonArgLocalIndex(method)) + 1;

            if (isStatic)
            {
                ByteCodeUtilities.loadArgs(args, method.instructions, 0);
                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, targetMethod.name, targetMethod.desc, false));
            }
            else
            {
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                ByteCodeUtilities.loadArgs(args, method.instructions, 1);
                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, classNode.name, targetMethod.name, targetMethod.desc, false));
            }

            method.instructions.add(new InsnNode(returnType.getOpcode(Opcodes.IRETURN)));
        }

        /**
         * Populate bytecode instructions for a method which throws an exception
         * 
         * @param classNode
         * @param method
         * @param message
         */
        private void injectException(ClassNode classNode, MethodNode method, String message)
        {
            InsnList insns = method.instructions;
            method.maxStack = 2;

            insns.clear();
            insns.add(new TypeInsnNode(Opcodes.NEW, "java/lang/RuntimeException"));
            insns.add(new InsnNode(Opcodes.DUP));
            insns.add(new LdcInsnNode(message));
            insns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false));
            insns.add(new InsnNode(Opcodes.ATHROW));
        }

        /**
         * Add a method from the template interface to the target class 
         * 
         * @param classNode
         * @param method
         */
        private boolean addMethodToClass(ClassNode classNode, MethodNode method)
        {
            MethodNode existingMethod = ByteCodeUtilities.findTargetMethod(classNode, method);
            if (existingMethod != null) return false;
            classNode.methods.add(method);
            method.access = method.access & ~Opcodes.ACC_ABSTRACT;
            return true;
        }
    }

    protected static class Mapping extends Obf
    {
        protected Mapping(String seargeName, String obfName, String mcpName)
        {
            super(seargeName, obfName, mcpName);
        }
    }

    /**
     * List of accessors to inject
     */
    private final List<AccessorInjection> accessors = new ArrayList<AccessorInjection>();

    /**
     * ctor
     */
    public AccessorTransformer()
    {
        this.addAccessors();
    }

    /**
     * @param interfaceName
     */
    public void addAccessor(String interfaceName)
    {
        this.addAccessor(interfaceName, null);
    }

    /**
     * Add an accessor to the accessors list
     * 
     * @param interfaceName
     * @param obfProvider
     */
    public void addAccessor(String interfaceName, ObfProvider obfProvider)
    {
        try
        {
            this.accessors.add(new AccessorInjection(interfaceName, obfProvider));
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.debug(ex);
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.launchwrapper.IClassTransformer
     *      #transform(java.lang.String, java.lang.String, byte[])
     */
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        ClassNode classNode = null;

        classNode = this.apply(name, transformedName, basicClass, classNode);

        if (classNode != null)
        {
            this.postTransform(name, transformedName, classNode);
            return this.writeClass(classNode);
        }

        return basicClass;
    }

    /**
     * Apply this transformer, used when this transformer is acting as a
     * delegate via another transformer (eg. an EventTransformer) and the parent
     * transformer already has a ClassNode for the target class.
     * 
     * @param name
     * @param transformedName
     * @param basicClass
     * @param classNode
     */
    public ClassNode apply(String name, String transformedName, byte[] basicClass, ClassNode classNode)
    {
        for (Iterator<AccessorInjection> iter = this.accessors.iterator(); iter.hasNext(); )
        {
            AccessorInjection accessor = iter.next();
            Obf target = accessor.getTarget();
            if (target.obf.equals(transformedName) || target.name.equals(transformedName))
            {
                LiteLoaderLogger.debug("[AccessorTransformer] Processing access injections in %s", transformedName);
                if (classNode == null) classNode = this.readClass(basicClass, true);
                accessor.apply(classNode);
                iter.remove();
            }
        }

        return classNode;
    }

    /**
     * Subclasses should add their accessors here
     */
    protected void addAccessors()
    {
    }

    /**
     * Called after transformation is applied, allows custom transforms to be
     * performed by subclasses.
     * 
     * @param name
     * @param transformedName
     * @param classNode
     */
    protected void postTransform(String name, String transformedName, ClassNode classNode)
    {
    }
}
