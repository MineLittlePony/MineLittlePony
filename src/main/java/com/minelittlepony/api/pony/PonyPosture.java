package com.minelittlepony.api.pony;

import com.minelittlepony.api.pony.meta.Race;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class PonyPosture {
    public static Optional<IPony> getMountPony(LivingEntity entity) {
        return entity.getVehicle() instanceof LivingEntity mount
                ? IPony.getManager().getPony(mount)
                : Optional.empty();
    }

    public static boolean isCrouching(IPony pony, LivingEntity entity) {
        boolean isSneak = entity.isInSneakingPose();
        boolean isFlying = isFlying(entity);
        boolean isSwimming = isSwimming(entity);

        return !isPerformingRainboom(pony, entity) && !isSwimming && isSneak && !isFlying;
    }

    private static boolean isPerformingRainboom(IPony pony, LivingEntity entity) {
        Vec3d motion = entity.getVelocity();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        return (isFlying(entity) && pony.race().hasWings()) || entity.isFallFlying() & zMotion > 0.4F;
    }

    public static boolean isFlying(LivingEntity entity) {
        return !(isOnGround(entity)
                || entity.hasVehicle()
                || (entity.isClimbing() && !(entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().allowFlying))
                || entity.isSubmergedInWater()
                || entity.isSleeping());
    }

    /**
     * Checks if the entity is on the ground, or close enough to be "effectively" grounded.
     * this is to keep Pegasus wings from flapping in odd situations (Hypixel).
     */
    private static boolean isOnGround(LivingEntity entity) {
        if (entity.isOnGround()) {
            return true;
        }

        BlockState below = entity.getEntityWorld()
                .getBlockState(entity.getBlockPos().down(1));

        // Check for stairs so we can keep Pegasi from flailing their wings as they descend
        double offsetAmount = below.getBlock() instanceof StairsBlock ? 1 : 0.05;

        Vec3d pos = entity.getPos();
        BlockPos blockpos = new BlockPos(
                pos.x,
                pos.y - offsetAmount,
                pos.z
        );

        return !entity.getEntityWorld().isAir(blockpos);
    }

    public static boolean isSwimming(LivingEntity entity) {
        return entity.isSwimming() || entity.isInSwimmingPose();
    }

    public static boolean isPartiallySubmerged(LivingEntity entity) {
        return entity.isSubmergedInWater()
                || entity.getEntityWorld().getBlockState(entity.getBlockPos()).getMaterial() == Material.WATER;
    }

    public static boolean isSitting(LivingEntity entity) {
        return entity.hasVehicle();
    }

    public static boolean isRidingAPony(LivingEntity entity) {
        return isSitting(entity) && getMountPony(entity).map(IPony::race).orElse(Race.HUMAN) != Race.HUMAN;
    }
}
