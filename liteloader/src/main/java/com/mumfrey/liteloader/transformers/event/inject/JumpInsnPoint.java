package com.mumfrey.liteloader.transformers.event.inject;

import java.util.Collection;
import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;

/**
 * An injection point which searches for JUMP opcodes (if, try/catch, continue,
 * break, conditional assignment, etc.) with either a particular opcode or at a
 * particular ordinal in the method body (eg. "the Nth JUMP insn" where N is the
 * ordinal of the instruction). By default it returns all JUMP instructions in a
 * method body.
 * 
 * @author Adam Mummery-Smith
 */
public class JumpInsnPoint extends InjectionPoint
{
    private final int opCode;

    private final int ordinal;

    public JumpInsnPoint()
    {
        this(0, -1);
    }

    public JumpInsnPoint(int ordinal)
    {
        this(0, ordinal);
    }

    public JumpInsnPoint(int opCode, int ordinal)
    {
        this.opCode = opCode;
        this.ordinal = ordinal;
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes, Event event)
    {
        boolean found = false;
        int ordinal = 0;

        ListIterator<AbstractInsnNode> iter = insns.iterator();
        while (iter.hasNext())
        {
            AbstractInsnNode insn = iter.next();

            if (insn instanceof JumpInsnNode && (this.opCode == -1 || insn.getOpcode() == this.opCode))
            {
                if (this.ordinal == -1 || this.ordinal == ordinal)
                {
                    nodes.add(insn);
                    found = true;
                }

                ordinal++;
            }
        }

        return found;
    }
}
