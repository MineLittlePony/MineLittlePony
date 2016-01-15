package com.mumfrey.liteloader.client.transformers;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ClassTransformer;

public class CrashReportTransformer extends ClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (basicClass != null && (Obf.CrashReport$6.name.equals(name) || Obf.CrashReport$6.obf.equals(name)))
        {
            try
            {
                return this.transformCallableJVMFlags(basicClass);
            }
            catch (Exception ex) {}
        }

        return basicClass;
    }

    /**
     * Inject the additional callback for populating the crash report into the
     * CallableJVMFlags class.
     * 
     * @param basicClass basic class
     * @return transformed class
     */
    private byte[] transformCallableJVMFlags(byte[] basicClass)
    {
        ClassNode classNode = this.readClass(basicClass, true);

        for (MethodNode method : classNode.methods)
        {
            if ("<init>".equals(method.name))
            {
                this.transformCallableJVMFlagsConstructor(method);
            }
        }

        return this.writeClass(classNode);
    }

    /**
     * @param ctor
     */
    public void transformCallableJVMFlagsConstructor(MethodNode ctor)
    {
        InsnList code = new InsnList();
        code.add(new VarInsnNode(Opcodes.ALOAD, 1));
        code.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mumfrey/liteloader/core/LiteLoader", "populateCrashReport",
                "(Ljava/lang/Object;)V", false));

        ListIterator<AbstractInsnNode> insns = ctor.instructions.iterator();
        while (insns.hasNext())
        {
            AbstractInsnNode insnNode = insns.next();
            if (insnNode.getOpcode() == Opcodes.RETURN)
            {
                ctor.instructions.insertBefore(insnNode, code);
            }
        }
    }
}
