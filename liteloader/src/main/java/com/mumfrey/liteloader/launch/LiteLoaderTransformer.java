package com.mumfrey.liteloader.launch;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ClassTransformer;

public class LiteLoaderTransformer extends ClassTransformer
{
    private static final String LITELOADER_TWEAKER_CLASS = LiteLoaderTweaker.class.getName().replace('.', '/');

    private static final String METHOD_PRE_BEGIN_GAME = "preBeginGame";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass == null) return basicClass;

        if (Obf.MinecraftMain.name.equals(transformedName))
        {
            return this.transformMain(basicClass);
        }
        else if (Obf.Blocks.obf.equals(transformedName)
                || Obf.Blocks.name.equals(transformedName)
                || Obf.Items.obf.equals(transformedName)
                || Obf.Items.name.equals(transformedName))
        {
            return this.stripFinalModifiers(basicClass);
        }

        return basicClass;
    }

    private byte[] transformMain(byte[] basicClass)
    {
        ClassNode classNode = this.readClass(basicClass, true);

        for (MethodNode method : classNode.methods)
        {
            if ("main".equals(method.name))
            {
                method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, LiteLoaderTransformer.LITELOADER_TWEAKER_CLASS,
                        LiteLoaderTransformer.METHOD_PRE_BEGIN_GAME, "()V", false));
            }
        }

        return this.writeClass(classNode);
    }

    private byte[] stripFinalModifiers(byte[] basicClass)
    {
        ClassNode classNode = this.readClass(basicClass, true);

        for (FieldNode field : classNode.fields)
        {
            field.access = field.access & ~Opcodes.ACC_FINAL;
        }

        return this.writeClass(classNode);
    }
}
