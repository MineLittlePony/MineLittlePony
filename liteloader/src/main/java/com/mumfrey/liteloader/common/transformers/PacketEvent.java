package com.mumfrey.liteloader.common.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.core.runtime.Packets;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInfo;

/**
 * Special event used to hook all packets
 * 
 * @author Adam Mummery-Smith
 */
public class PacketEvent extends Event
{
    /**
     * Soft index for this packet, used as a lookup for speed when determining
     * handlers.
     */
    private int packetIndex;

    PacketEvent(Packets packet)
    {
        super("on" + packet.getShortName(), true, 1000);
        this.packetIndex = packet.getIndex();
        this.verbose = false;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.transformers.event.Event
     *      #getEventInfoClassName()
     */
    @Override
    public String getEventInfoClassName()
    {
        return "com/mumfrey/liteloader/common/transformers/PacketEventInfo";
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.transformers.event.Event
     *      #invokeEventInfoConstructor(org.objectweb.asm.tree.InsnList,
     *      boolean)
     */
    @Override
    protected int invokeEventInfoConstructor(InsnList insns, boolean cancellable, boolean pushReturnValue, int marshallVar)
    {
        int ctorMAXS = 0;

        insns.add(new LdcInsnNode(this.name)); ctorMAXS++;
        insns.add(this.methodIsStatic ? new InsnNode(Opcodes.ACONST_NULL) : new VarInsnNode(Opcodes.ALOAD, 0)); ctorMAXS++;
        insns.add(new InsnNode(cancellable ? Opcodes.ICONST_1 : Opcodes.ICONST_0)); ctorMAXS++;
        insns.add(new IntInsnNode(Opcodes.BIPUSH, this.packetIndex));
        insns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, this.eventInfoClass, Obf.constructor.name,
                EventInfo.getConstructorDescriptor().replace(")", "I)"), false));

        return ctorMAXS;
    }
}
