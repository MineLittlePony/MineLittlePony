package com.minelittlepony.api.pony;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.model.PreviewModel;
import com.minelittlepony.client.SkinsProxy;
import com.minelittlepony.client.render.entity.AquaticPlayerPonyRenderer;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class PonyPosture {
    public static Optional<Pony> getMountPony(LivingEntity entity) {
        return entity.getVehicle() instanceof LivingEntity mount
                ? Pony.getManager().getPony(mount)
                : Optional.empty();
    }

    public static boolean isCrouching(Pony pony, LivingEntity entity) {
        boolean isSneak = entity.isInSneakingPose();
        boolean isFlying = isFlying(entity);
        boolean isSwimming = isSwimming(entity);

        return !isPerformingRainboom(pony, entity) && !isSwimming && isSneak && !isFlying;
    }

    private static boolean isPerformingRainboom(Pony pony, LivingEntity entity) {
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
        BlockPos blockpos = BlockPos.ofFloored(
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
                || entity.getEntityWorld().getBlockState(entity.getBlockPos()).getFluidState().isIn(FluidTags.WATER);
    }

    public static boolean isSitting(LivingEntity entity) {
        return entity.hasVehicle();
    }

    public static boolean isRidingAPony(LivingEntity entity) {
        return isSitting(entity) && getMountPony(entity).map(Pony::race).orElse(Race.HUMAN) != Race.HUMAN;
    }

    public static boolean isSeaponyModifier(LivingEntity entity) {
        if (entity instanceof PreviewModel preview) {
            return preview.forceSeapony();
        }
        return hasSeaponyForm(entity) && isPartiallySubmerged(entity);
    }

    public static boolean hasSeaponyForm(LivingEntity entity) {
        if (entity instanceof PreviewModel preview) {
            return preview.forceSeapony();
        }
        return Pony.getManager().getPony(entity).filter(pony -> {
            return (pony.race() == Race.SEAPONY
                    || (entity instanceof AbstractClientPlayerEntity player && SkinsProxy.instance.getSkin(AquaticPlayerPonyRenderer.SKIN_TYPE_ID, player).isPresent())
            );
        }).isPresent();
    }
}
