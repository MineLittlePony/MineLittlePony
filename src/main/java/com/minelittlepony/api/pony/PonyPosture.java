package com.minelittlepony.api.pony;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.minelittlepony.api.pony.meta.Race;

import java.util.Optional;

public final class PonyPosture {
    public static Optional<Pony> getMountPony(LivingEntity entity) {
        return entity.getVehicle() instanceof LivingEntity mount
                ? Pony.getManager().getPony(mount)
                : Optional.empty();
    }

    public static boolean isCrouching(Pony pony, LivingEntity entity) {
        boolean isSneak = entity.isCrouching();
        boolean isFlying = isFlying(entity);
        boolean isSwimming = isSwimming(entity);

        return !isPerformingRainboom(pony, entity) && !isSwimming && isSneak && !isFlying;
    }

    private static boolean isPerformingRainboom(Pony pony, LivingEntity entity) {
        Vec3 motion = entity.getDeltaMovement();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        return (isFlying(entity) && pony.race().hasWings()) || entity.isFallFlying() & zMotion > 0.4F;
    }

    public static boolean isFlying(LivingEntity entity) {
        return !(isOnGround(entity)
                || entity.isPassenger()
                || (entity.onClimbable() && !(entity instanceof Player player && player.getAbilities().mayfly))
                || entity.isUnderWater()
                || entity.isSleeping());
    }

    /**
     * Checks if the entity is on the ground, or close enough to be "effectively" grounded.
     * this is to keep Pegasus wings from flapping in odd situations (Hypixel).
     */
    private static boolean isOnGround(LivingEntity entity) {
        if (entity.onGround()) {
            return true;
        }

        BlockState below = entity.level().getBlockState(entity.blockPosition().below(1));

        // Check for stairs so we can keep Pegasi from flailing their wings as they descend
        double offsetAmount = below.getBlock() instanceof StairBlock ? 1 : 0.05;

        Vec3 pos = entity.position();
        BlockPos blockpos = BlockPos.containing(
                pos.x,
                pos.y - offsetAmount,
                pos.z
        );

        return !entity.level().isEmptyBlock(blockpos);
    }

    public static boolean isSwimming(LivingEntity entity) {
        return entity.isSwimming() || entity.isVisuallySwimming();
    }

    public static boolean isPartiallySubmerged(LivingEntity entity) {
        return entity.isUnderWater()
                || entity.level().getBlockState(entity.blockPosition()).getFluidState().is(FluidTags.WATER);
    }

    public static boolean isSitting(LivingEntity entity) {
        return entity.isPassenger();
    }

    public static boolean isRidingAPony(LivingEntity entity) {
        return isSitting(entity) && getMountPony(entity).map(Pony::race).orElse(Race.HUMAN) != Race.HUMAN;
    }

    public static boolean hasSeaponyForm(LivingEntity entity) {
        return hasForm(entity, Race.SEAPONY, DefaultPonySkinHelper.SEAPONY_SKIN_TYPE_ID, PonyForm.SEAPONY);
    }

    public static boolean isSeaponyFormActive(LivingEntity entity) {
        return hasSeaponyForm(entity) && isPartiallySubmerged(entity);
    }

    public static boolean hasNirikForm(LivingEntity entity) {
        return hasForm(entity, Race.KIRIN, DefaultPonySkinHelper.NIRIK_SKIN_TYPE_ID, PonyForm.NIRIK);
    }

    public static boolean isNirikFormActive(LivingEntity entity) {
        return false;
    }

    public static boolean hasForm(LivingEntity entity, Race race, Identifier skinId, Identifier ponyform) {
        return Pony.getManager().getPony(entity).filter(pony -> {
            return (pony.race() == race
                    && (entity instanceof Player player && SkinsProxy.getInstance().getSkin(skinId, player).isPresent())
            );
        }).isPresent();
    }
}
