package com.mumfrey.liteloader.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public abstract class EntityUtilities
{
    public static MovingObjectPosition rayTraceFromEntity(Entity entity, double traceDistance, float partialTicks)
    {
        Vec3 var4 = EntityUtilities.getPositionEyes(entity, partialTicks);
        Vec3 var5 = entity.getLook(partialTicks);
        Vec3 var6 = var4.addVector(var5.xCoord * traceDistance, var5.yCoord * traceDistance, var5.zCoord * traceDistance);
        return entity.worldObj.rayTraceBlocks(var4, var6, false, false, true);
    }

    public static Vec3 getPositionEyes(Entity entity, float partialTicks)
    {
        if (partialTicks == 1.0F)
        {
            return new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        }

        double interpX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
        double interpY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks + entity.getEyeHeight();
        double interpZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
        return new Vec3(interpX, interpY, interpZ);
    }
}
