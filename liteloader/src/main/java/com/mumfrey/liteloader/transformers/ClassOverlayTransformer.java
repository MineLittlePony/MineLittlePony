package com.mumfrey.liteloader.transformers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.Launch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * This transformer applies one class to another as an "overlay". This works by
 * merging down and replacing all methods and fields from the "overlay" class
 * into the "target" class being transformed. Fields and methods marked with the
 * {@link Obfuscated} annotation will search through the list of provided names
 * to find a matching member in the target class, this allows methods and fields
 * in the target class to be referenced even if they have different names after
 * obfuscation.
 * 
 * <p>The "target" class is identified by a special field which must be named
 * <tt>__TARGET</tt> in the overlay class which must be a private static field
 * of the appropriate target type.</p>
 * 
 * <h3>Notes:</h3>
 * 
 * <ul>
 *     <li>Constructors WILL NOT BE overlaid, see below for instruction merging.
 *     Constructors in the overlay class should throw an InstantiationError.
 *     </li>
 * 
 *     <li>Static method invocations will not be processed by "transformMethod",
 *     this means that any static methods invoked must be accessible from the
 *     context of the transformed class (eg. public or package-private in the
 *     same package).</li>
 *   
 *     <li>The overlay class MUST be a sibling of the target class to ensure
 *     that calls to super.xxx are properly transformed. In other words the
 *     overlay and the transformed class should have the same parent class
 *     although they need not be in the same package unless any package-private
 *     members are accessed.</li>
 *   
 *     <li>It is also possible to merge instructions from a "source" method into
 *     a specific method in the transformed class by annotating the method with
 *     a {@link AppendInsns} annotation, specifying the name of the target
 *     method as the annotation value. The target method signature must match
 *     the source method's signature and both methods must return VOID. The
 *     instructions from the source method will be inserted immediately before
 *     the RETURN opcode in the target method.</li>
 *   
 *     <li>To create a method stub for private methods you wish to invoke in the
 *     target class, decorate the stub method with an {@link Stub} annotation,
 *     this will cause the overlay transformer to NOT merge the method into the
 *     target, but merely verify that it exists in the target class.</li>
 *   
 *     <li>Merge instructions into the constructor by specifying "<init>" as the
 *     target method name.</li>
 * </ul>
 * 
 * @author Adam Mummery-Smith
 * @deprecated Use mixins instead!
 */
@Deprecated
public abstract class ClassOverlayTransformer extends ClassTransformer
{
    /**
     * Global list of overlaid classes, used to transform references in other
     * classes.
     */
    private static final Map<String, String> overlayMap = new HashMap<String, String>();

    /**
     * Remapper for dynamically renaming references to overlays in other classes
     */
    private static SimpleRemapper referenceRemapper;

    /**
     * The first ClassOverlayTransformer to be instantiated accepts
     * responsibility for performing remapping operations and becomes the
     * "remapping agent" transformer. This flag is set to true to indicate that
     * this instance is the remapping agent.
     */
    private boolean remappingAgent = false;

    /**
     * Name of the overlay class
     */
    private final String overlayClassName, overlayClassRef;

    /**
     * Target class to be transformed
     */
    private final String targetClassName;

    /**
     * Fields which get a different name from an {@link Obfuscated} annotation
     */
    private final Map<String, String> renamedFields = new HashMap<String, String>();

    /**
     * Methods which get a different name from an {@link Obfuscated} annotation
     */
    private final Map<String, String> renamedMethods = new HashMap<String, String>();

    /**
     * True to set the sourceFile property when applying the overlay 
     */
    protected boolean setSourceFile = true;

    /**
     * @param overlayClassName
     */
    protected ClassOverlayTransformer(String overlayClassName)
    {
        this.overlayClassName = overlayClassName;
        this.overlayClassRef = overlayClassName.replace('.', '/');

        String targetClassName = null;
        ClassNode overlayClass = this.loadOverlayClass("<none>", true);
        for (FieldNode field : overlayClass.fields)
        {
            if ("__TARGET".equals(field.name) && ((field.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC))
            {
                targetClassName = Type.getType(field.desc).getClassName();
            }
        }

        if (targetClassName == null)
        {
            throw new RuntimeException(String.format("Overlay class %s is missing a __TARGET field, unable to identify target class",
                    this.overlayClassName));
        }

        this.targetClassName = targetClassName;
        ClassOverlayTransformer.overlayMap.put(this.overlayClassRef, this.targetClassName.replace('.', '/'));

        // If this is the first ClassOverlayTransformer, the referenceMapper will be null
        if (ClassOverlayTransformer.referenceRemapper == null)
        {
            // Therefore create the referenceMapper and accept responsibility for class remapping
            ClassOverlayTransformer.referenceRemapper = new SimpleRemapper(ClassOverlayTransformer.overlayMap);
            this.remappingAgent = true;
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.launchwrapper.IClassTransformer
     *      #transform(java.lang.String, java.lang.String, byte[])
     */
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (this.targetClassName != null && this.targetClassName.equals(transformedName))
        {
            try
            {
                return this.applyOverlay(transformedName, basicClass);
            }
            catch (InvalidOverlayException th)
            {
                LiteLoaderLogger.severe(th, "Class overlay failed: %s %s", th.getClass().getName(), th.getMessage());
                th.printStackTrace();
            }
        }
        else if (this.overlayClassName.equals(transformedName))
        {
            throw new RuntimeException(String.format("%s is an overlay class and cannot be referenced directly", this.overlayClassName));
        }
        else if (this.remappingAgent && basicClass != null)
        {
            return this.remapClass(transformedName, basicClass);
        }

        return basicClass;
    }

    /**
     * Remap references to overlay classes in other classes to the overlay class
     * 
     * @param transformedName
     * @param basicClass
     */
    private byte[] remapClass(String transformedName, byte[] basicClass)
    {
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(classReader, 0);

        RemappingClassAdapter remappingAdapter = new RemappingClassAdapter(classWriter, ClassOverlayTransformer.referenceRemapper);
        classReader.accept(remappingAdapter, ClassReader.EXPAND_FRAMES);

        return classWriter.toByteArray();
    }

    /**
     * Apply the overlay to the class described by basicClass
     * 
     * @param transformedName
     * @param classBytes
     */
    protected byte[] applyOverlay(String transformedName, byte[] classBytes)
    {
        ClassNode overlayClass = this.loadOverlayClass(transformedName, true);
        ClassNode targetClass = this.readClass(classBytes, true);

        LiteLoaderLogger.info("Applying overlay %s to %s", this.overlayClassName, transformedName);

        try
        {
            this.verifyClasses(targetClass, overlayClass);
            this.overlayInterfaces(targetClass, overlayClass);
            this.overlayAttributes(targetClass, overlayClass);
            this.overlayFields(targetClass, overlayClass);
            this.findRenamedMethods(targetClass, overlayClass);
            this.overlayMethods(targetClass, overlayClass);
        }
        catch (Exception ex)
        {
            throw new InvalidOverlayException("Unexpecteded error whilst applying the overlay class", ex);
        }

        this.postOverlayTransform(transformedName, targetClass, overlayClass);

        return this.writeClass(targetClass);
    }

    protected void postOverlayTransform(String transformedName, ClassNode targetClass, ClassNode overlayClass)
    {
        // Stub
    }

    /**
     * Perform pre-flight checks on the overlay and target classes
     * 
     * @param targetClass
     * @param overlayClass
     */
    protected void verifyClasses(ClassNode targetClass, ClassNode overlayClass)
    {
        if (targetClass.superName == null || overlayClass.superName == null || !targetClass.superName.equals(overlayClass.superName))
        {
            throw new InvalidOverlayException("Overlay classes must have the same superclass as their target class");
        }
    }

    /**
     * Overlay interfaces implemented by the overlay class onto the target class
     * 
     * @param targetClass
     * @param overlayClass
     */
    private void overlayInterfaces(ClassNode targetClass, ClassNode overlayClass)
    {
        for (String interfaceName : overlayClass.interfaces)
        {
            if (!targetClass.interfaces.contains(interfaceName))
            {
                targetClass.interfaces.add(interfaceName);
            }
        }
    }

    /**
     * Overlay misc attributes from overlay class onto the target class
     * 
     * @param targetClass
     * @param overlayClass
     */
    private void overlayAttributes(ClassNode targetClass, ClassNode overlayClass)
    {
        if (this.setSourceFile ) targetClass.sourceFile = overlayClass.sourceFile;
    }

    /**
     * Overlay fields from overlay class into the target class. It is vital that
     * this is done before overlayMethods because we need to compute renamed
     * fields so that transformMethod can rename field references in the
     * method body.
     * 
     * @param targetClass
     * @param overlayClass
     */
    private void overlayFields(ClassNode targetClass, ClassNode overlayClass)
    {
        for (FieldNode field : overlayClass.fields)
        {
            if ((field.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC && (field.access & Opcodes.ACC_PRIVATE) != Opcodes.ACC_PRIVATE)
            {
                throw new InvalidOverlayException(String.format("Overlay classes cannot contain non-private static methods or fields, found %s",
                        field.name));
            }

            FieldNode target = ByteCodeUtilities.findTargetField(targetClass, field);
            if (target == null)
            {
                targetClass.fields.add(field);
            }
            else
            {
                if (!target.desc.equals(field.desc))
                {
                    throw new InvalidOverlayException(String.format("The field %s in the target class has a conflicting signature", field.name));
                }

                if (!target.name.equals(field.name))
                {
                    this.renamedFields.put(field.name, target.name);
                }
            }
        }
    }

    /**
     * Called before merging methods to build the map of original method names
     * -> new method names, this is then used by transformMethod to remap.  
     * 
     * @param targetClass
     * @param overlayClass
     */
    private void findRenamedMethods(ClassNode targetClass, ClassNode overlayClass)
    {
        for (MethodNode overlayMethod : overlayClass.methods)
        {
            if (ByteCodeUtilities.getVisibleAnnotation(overlayMethod, Stub.class) != null
                    || (ByteCodeUtilities.getVisibleAnnotation(overlayMethod, AppendInsns.class) == null && !overlayMethod.name.startsWith("<")))
            {
                this.checkRenameMethod(targetClass, overlayMethod);
            }
        }
    }

    /**
     * Overlay methods from the overlay class into the target class
     * 
     * @param targetClass
     * @param overlayClass
     */
    private void overlayMethods(ClassNode targetClass, ClassNode overlayClass)
    {
        for (MethodNode overlayMethod : overlayClass.methods)
        {
            this.transformMethod(overlayMethod, overlayClass.name, targetClass.name);

            AnnotationNode appendAnnotation = ByteCodeUtilities.getVisibleAnnotation(overlayMethod, AppendInsns.class);
            AnnotationNode stubAnnotation = ByteCodeUtilities.getVisibleAnnotation(overlayMethod, Stub.class);

            if (stubAnnotation != null)
            {
                MethodNode target = ByteCodeUtilities.findTargetMethod(targetClass, overlayMethod);
                if (target == null)
                {
                    throw new InvalidOverlayException(String.format("Stub method %s was not located in the target class", overlayMethod.name));
                }
            }
            else if (appendAnnotation != null)
            {
                String targetMethodName = ByteCodeUtilities.<String>getAnnotationValue(appendAnnotation);
                this.appendInsns(targetClass, targetMethodName, overlayMethod);
            }
            else if (!overlayMethod.name.startsWith("<"))
            {
                if ((overlayMethod.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
                        && (overlayMethod.access & Opcodes.ACC_PRIVATE) != Opcodes.ACC_PRIVATE)
                {
                    continue;
                }

                MethodNode target = ByteCodeUtilities.findTargetMethod(targetClass, overlayMethod);
                if (target != null) targetClass.methods.remove(target);
                targetClass.methods.add(overlayMethod);
            }
            else if ("<clinit>".equals(overlayMethod.name))
            {
                this.appendInsns(targetClass, overlayMethod.name, overlayMethod);
            }
        }
    }

    /**
     * Handles "re-parenting" the method supplied, changes all references to the
     * overlay class to refer to the target class (for field accesses and method
     * invocations) and also renames fields accesses to their obfuscated
     * versions.
     * 
     * @param method
     * @param fromClass
     * @param toClass
     */
    private void transformMethod(MethodNode method, String fromClass, String toClass)
    {
        Iterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext())
        {
            AbstractInsnNode insn = iter.next();

            if (insn instanceof MethodInsnNode)
            {
                MethodInsnNode methodInsn = (MethodInsnNode)insn;
                if (methodInsn.owner.equals(fromClass))
                {
                    methodInsn.owner = toClass;

                    String methodDescriptor = methodInsn.name + methodInsn.desc;
                    if (this.renamedMethods.containsKey(methodDescriptor))
                    {
                        methodInsn.name = this.renamedMethods.get(methodDescriptor);
                    }
                }
            }
            if (insn instanceof FieldInsnNode)
            {
                FieldInsnNode fieldInsn = (FieldInsnNode)insn;
                if (fieldInsn.owner.equals(fromClass)) fieldInsn.owner = toClass;

                if (this.renamedFields.containsKey(fieldInsn.name))
                {
                    String newName = this.renamedFields.get(fieldInsn.name);
                    fieldInsn.name = newName;
                }
            }
        }
    }

    /**
     * Handles appending instructions from the source method to the target
     * method.
     * 
     * @param targetClass
     * @param targetMethodName
     * @param sourceMethod
     */
    private void appendInsns(ClassNode targetClass, String targetMethodName, MethodNode sourceMethod)
    {
        if (Type.getReturnType(sourceMethod.desc) != Type.VOID_TYPE)
        {
            throw new IllegalArgumentException("Attempted to merge insns into a method which does not return void");
        }

        if (targetMethodName == null || targetMethodName.length() == 0) targetMethodName = sourceMethod.name;

        Set<String> obfuscatedNames = new HashSet<String>();
        AnnotationNode obfuscatedAnnotation = ByteCodeUtilities.getVisibleAnnotation(sourceMethod, Obfuscated.class);
        if (obfuscatedAnnotation != null)
        {
            obfuscatedNames.addAll(ByteCodeUtilities.<List<String>>getAnnotationValue(obfuscatedAnnotation));
        }

        for (MethodNode method : targetClass.methods)
        {
            if ((targetMethodName.equals(method.name) || obfuscatedNames.contains(method.name)) && sourceMethod.desc.equals(method.desc))
            {
                AbstractInsnNode returnNode = null;
                Iterator<AbstractInsnNode> findReturnIter = method.instructions.iterator();
                while (findReturnIter.hasNext())
                {
                    AbstractInsnNode insn = findReturnIter.next();
                    if (insn.getOpcode() == Opcodes.RETURN)
                    {
                        returnNode = insn;
                        break;
                    }
                }

                Iterator<AbstractInsnNode> injectIter = sourceMethod.instructions.iterator();
                while (injectIter.hasNext())
                {
                    AbstractInsnNode insn = injectIter.next();
                    if (!(insn instanceof LineNumberNode) && insn.getOpcode() != Opcodes.RETURN)
                    {
                        method.instructions.insertBefore(returnNode, insn);
                    }
                }
            }
        }
    }

    /**
     * @param targetClass
     * @param searchFor
     */
    private void checkRenameMethod(ClassNode targetClass, MethodNode searchFor)
    {
        MethodNode target = ByteCodeUtilities.findTargetMethod(targetClass, searchFor);
        if (target != null && !target.name.equals(searchFor.name))
        {
            String methodDescriptor = searchFor.name + searchFor.desc;
            this.renamedMethods.put(methodDescriptor, target.name);
            searchFor.name = target.name;
        }
    }

    /**
     * @param transformedName
     * @throws InvalidOverlayException
     */
    private ClassNode loadOverlayClass(String transformedName, boolean runTransformers)
    {
        byte[] overlayBytes = null;

        try
        {
            if ((overlayBytes = Launch.classLoader.getClassBytes(this.overlayClassName)) == null)
            {
                throw new InvalidOverlayException(String.format("The specified overlay '%s' was not found", this.overlayClassName));
            }

            if (runTransformers)
            {
                overlayBytes = ByteCodeUtilities.applyTransformers(this.overlayClassName, overlayBytes, this);
            }
        }
        catch (IOException ex)
        {
            LiteLoaderLogger.severe("Failed to load overlay %s for %s, no overlay was applied", this.overlayClassName, transformedName);
            throw new InvalidOverlayException("An error was encountered whilst loading the overlay class", ex);
        }

        return this.readClass(overlayBytes, false);
    }
}
