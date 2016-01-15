package com.mumfrey.liteloader.transformers;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * Base class for transformers which work via ClassNode
 * 
 * @author Adam Mummery-Smith
 */
public abstract class ClassTransformer implements IClassTransformer
{
    public static final String HORIZONTAL_RULE =
            "----------------------------------------------------------------------------------------------------";

    private ClassReader classReader;
    private ClassNode classNode;

    /**
     * @param basicClass
     */
    protected final ClassNode readClass(byte[] basicClass, boolean cacheReader)
    {
        ClassReader classReader = new ClassReader(basicClass);
        if (cacheReader) this.classReader = classReader;

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
        return classNode;
    }

    /**
     * @param classNode
     */
    protected final byte[] writeClass(ClassNode classNode)
    {
        // Use optimised writer for speed
        if (this.classReader != null && this.classNode == classNode)
        {
            this.classNode = null;
            IsolatedClassWriter writer = new IsolatedClassWriter(this.classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            this.classReader = null;
            classNode.accept(writer);
            return writer.toByteArray();
        }

        this.classNode = null;

        IsolatedClassWriter writer = new IsolatedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    protected static String getSimpleClassName(ClassNode classNode)
    {
        String className = classNode.name.replace('/', '.');
        int dotPos = className.lastIndexOf('.');
        return dotPos == -1 ? className : className.substring(dotPos + 1);
    }
}