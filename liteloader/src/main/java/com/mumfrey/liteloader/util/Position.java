package com.mumfrey.liteloader.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

/**
 * A 3D vector position with rotation as well
 * 
 * @author Adam Mummery-Smith
 */
public class Position extends Vec3
{
    public final float yaw;

    public final float pitch;

    public Position(double x, double y, double z)
    {
        this(x, y, z, 0.0F, 0.0F);
    }

    public Position(double x, double y, double z, float yaw, float pitch)
    {
        super(x, y, z);

        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(Entity entity)
    {
        this(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
    }

    public Position(Entity entity, boolean usePrevious)
    {
        this(usePrevious ? entity.prevPosX : entity.posX,
             usePrevious ? entity.prevPosY : entity.posY,
             usePrevious ? entity.prevPosZ : entity.posZ,
             usePrevious ? entity.prevRotationYaw : entity.rotationYaw,
             usePrevious ? entity.prevRotationPitch : entity.rotationPitch);
    }

    public void applyTo(Entity entity)
    {
        entity.posX = this.xCoord;
        entity.posY = this.yCoord;
        entity.posZ = this.zCoord;
        entity.rotationYaw = this.yaw;
        entity.rotationPitch = this.pitch;
    }

    @Override
    public String toString()
    {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ", " + this.yaw + ", " + this.pitch + ")";
    }
}
