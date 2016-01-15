package com.mumfrey.liteloader.client.transformers;

import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.launch.LiteLoaderTweaker;
import com.mumfrey.liteloader.transformers.access.AccessorTransformer;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

public class MinecraftTransformer extends AccessorTransformer
{
    private static final String TWEAKCLASS = LiteLoaderTweaker.class.getName().replace('.', '/');

    @Override
    protected void addAccessors()
    {
        this.addAccessor(Obf.IMinecraft.name);
        this.addAccessor(Obf.IGuiTextField.name);
        this.addAccessor(Obf.IEntityRenderer.name);
        this.addAccessor(Obf.ISoundHandler.name);
    }

    @Override
    protected void postTransform(String name, String transformedName, ClassNode classNode)
    {
        if ((Obf.Minecraft.name.equals(transformedName) || Obf.Minecraft.obf.equals(transformedName)))
        {
            for (MethodNode method : classNode.methods)
            {
                if (Obf.startGame.obf.equals(method.name) || Obf.startGame.srg.equals(method.name) || Obf.startGame.name.equals(method.name))
                {
                    this.transformStartGame(method);
                }
            }
        }
    }

    private void transformStartGame(MethodNode method)
    {
        InsnList insns = new InsnList(); 

        boolean found = false;

        Iterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext())
        {
            AbstractInsnNode insn = iter.next();
            insns.add(insn);

            if (insn instanceof TypeInsnNode && insn.getOpcode() == Opcodes.NEW && insns.getLast() != null)
            {
                TypeInsnNode typeNode = (TypeInsnNode)insn;
                if (!found && (Obf.EntityRenderer.obf.equals(typeNode.desc) || Obf.EntityRenderer.ref.equals(typeNode.desc)))
                {
                    LiteLoaderLogger.info("MinecraftTransformer found INIT injection point, this is good.");
                    found = true;

                    insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MinecraftTransformer.TWEAKCLASS, Obf.init.name, "()V", false));
                    insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, MinecraftTransformer.TWEAKCLASS, Obf.postInit.name, "()V", false));
                }
            }

            if (LiteLoaderTweaker.loadingBarEnabled())
            {
                if (insn instanceof LdcInsnNode)
                {
                    LdcInsnNode ldcInsn = (LdcInsnNode)insn;
                    if ("textures/blocks".equals(ldcInsn.cst))
                    {
                        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Obf.LoadingBar.ref, "initTextures", "()V", false));
                    }
                }

                insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Obf.LoadingBar.ref, "incrementProgress", "()V", false));
            }
        }

        method.instructions = insns;

        if (!found) LiteLoaderLogger.severe("MinecraftTransformer failed to find INIT injection point, the game will probably crash pretty soon.");
    }
}
