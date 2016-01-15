//package com.mumfrey.liteloader.transformers.event;
//
//import org.objectweb.asm.Opcodes;
//import org.objectweb.asm.tree.AbstractInsnNode;
//import org.objectweb.asm.tree.InsnList;
//import org.objectweb.asm.tree.InsnNode;
//import org.objectweb.asm.tree.JumpInsnNode;
//import org.objectweb.asm.tree.MethodInsnNode;
//import org.objectweb.asm.tree.VarInsnNode;
//
//public class Jump extends Event
//{
//    Jump(String name, boolean cancellable, int priority)
//    {
//        super(name, cancellable, priority);
//    }
//
//    @Override
//    protected void validate(AbstractInsnNode injectionPoint, boolean cancellable, int globalEventID)
//    {
//        if (!(injectionPoint instanceof JumpInsnNode))
//        {
//            throw new IllegalArgumentException("Attempted to inject a JUMP event where no JUMP is present");
//        }
//
//        super.validate(injectionPoint, cancellable, globalEventID);
//    }
//
//    @Override
//    protected void injectCancellationCode(InsnList insns, AbstractInsnNode injectionPoint, int eventInfoVar) throws IllegalArgumentException
//    {
//        int opcode = injectionPoint.getOpcode();
//
//        if (opcode == Opcodes.JSR) throw new IllegalArgumentException("Can't jump on finally clause");
//
//        if (opcode == Opcodes.IFEQ || opcode == Opcodes.IFNE || opcode == Opcodes.IFLT || opcode == Opcodes.IFGE
//                || opcode == Opcodes.IFGT || opcode == Opcodes.IFLE || opcode == Opcodes.IFNULL || opcode == Opcodes.IFNONNULL)
//        {
//            insns.add(new InsnNode(Opcodes.POP));
//        }
//
//        if (opcode == Opcodes.IF_ICMPEQ || opcode == Opcodes.IF_ICMPNE || opcode == Opcodes.IF_ICMPLT || opcode == Opcodes.IF_ICMPGE
//                || opcode == Opcodes.IF_ICMPGT || opcode == Opcodes.IF_ICMPLE || opcode == Opcodes.IF_ACMPEQ || opcode == Opcodes.IF_ACMPNE)
//        {
//            insns.add(new InsnNode(Opcodes.POP));
//            insns.add(new InsnNode(Opcodes.POP));
//        }
//
//        insns.add(new VarInsnNode(Opcodes.ALOAD, eventInfoVar));
//        insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, this.eventInfoClass, "isCancelled", "()Z"));
//
//        ((JumpInsnNode)injectionPoint).setOpcode(Opcodes.IFEQ);
//    }
//}
