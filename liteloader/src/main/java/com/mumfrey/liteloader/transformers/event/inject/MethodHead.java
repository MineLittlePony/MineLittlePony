package com.mumfrey.liteloader.transformers.event.inject;

import java.util.Collection;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;

/**
 * An injection point which locates the first instruction in a method body
 *  
 * @author Adam Mummery-Smith
 */
public class MethodHead extends InjectionPoint
{
    public MethodHead()
    {
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes, Event event)
    {
        nodes.add(insns.getFirst());
        return true;
    }
}
