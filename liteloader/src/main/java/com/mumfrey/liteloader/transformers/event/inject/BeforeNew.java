package com.mumfrey.liteloader.transformers.event.inject;

import java.util.Collection;
import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.TypeInsnNode;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;

public class BeforeNew extends InjectionPoint
{
    private final String[] classNames;

    private final int ordinal;

    public BeforeNew(Obf className)
    {
        this(-1, className.names);
    }

    public BeforeNew(String... classNames)
    {
        this(-1, classNames);
    }

    public BeforeNew(int ordinal, Obf className)
    {
        this(ordinal, className.names);
    }

    public BeforeNew(int ordinal, String... classNames)
    {
        this.ordinal = Math.max(-1, ordinal);
        this.classNames = classNames;

        for (int i = 0; i < this.classNames.length; i++)
        {
            this.classNames[i] = this.classNames[i].replace('.', '/');
        }
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

            if (insn instanceof TypeInsnNode && insn.getOpcode() == Opcodes.NEW && this.matchesOwner((TypeInsnNode)insn))
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

    private boolean matchesOwner(TypeInsnNode insn)
    {
        for (String className : this.classNames)
        {
            if (className.equals(insn.desc)) return true;
        }

        return false;
    }

}
