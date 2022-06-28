package com.minelittlepony.client.pony;

import com.google.common.base.MoreObjects;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.api.pony.network.MsgPonyData;
import com.minelittlepony.api.pony.network.fabric.Channel;
import com.minelittlepony.api.pony.network.fabric.PonyDataCallback;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.PonyRenderDispatcher;
import com.minelittlepony.client.transform.PonyTransformation;
import com.minelittlepony.settings.PonyLevel;

import java.util.Objects;

import net.fabricmc.api.EnvType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.StairsBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class Pony implements IPony {

    private final Identifier texture;
    private final Memoize<IPonyData> metadata;

    private boolean defaulted = false;

    Pony(Identifier resource, Memoize<IPonyData> data) {
        texture = resource;
        metadata = data;
    }

    Pony(Identifier resource) {
        this(resource, PonyData.parse(resource));
    }

    public IPony defaulted() {
        defaulted = true;
        return this;
    }

    @Override
    public boolean isDefault() {
        return defaulted;
    }

    @Override
    public void updateForEntity(Entity entity) {
        if (!metadata.isPresent()) {
            return;
        }

        if (entity instanceof RegistrationHandler && ((RegistrationHandler)entity).shouldUpdateRegistration(this)) {
            entity.calculateDimensions();

            PlayerEntity clientPlayer = MinecraftClient.getInstance().player;
            if (clientPlayer != null) {
                if (Objects.equals(entity, clientPlayer) || Objects.equals(((PlayerEntity)entity).getGameProfile(), clientPlayer.getGameProfile())) {
                    Channel.broadcastPonyData(new MsgPonyData(getMetadata(), defaulted));
                }
            }
            PonyDataCallback.EVENT.invoker().onPonyDataAvailable((PlayerEntity)entity, getMetadata(), defaulted, EnvType.CLIENT);
        }
    }

    @Override
    public boolean isPerformingRainboom(LivingEntity entity) {
        Vec3d motion = entity.getVelocity();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        return (isFlying(entity) && canFly()) || entity.isFallFlying() & zMotion > 0.4F;
    }

    @Override
    public boolean isCrouching(LivingEntity entity) {

        boolean isSneak = entity.isInSneakingPose();
        boolean isFlying = isFlying(entity);
        boolean isSwimming = isSwimming(entity);

        return !isPerformingRainboom(entity) && !isSwimming && isSneak && !isFlying;
    }

    @Override
    public boolean isFlying(LivingEntity entity) {
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
    private boolean isOnGround(LivingEntity entity) {
        if (entity.isOnGround()) {
            return true;
        }

        BlockState below = entity.getEntityWorld()
                .getBlockState(entity.getBlockPos().down(1));

        // Check for stairs so we can keep Pegasi from flailing their wings as they descend
        double offsetAmount = below.getBlock() instanceof StairsBlock ? 1 : 0.05;

        Vec3d pos = entity.getPos();
        BlockPos blockpos = new BlockPos(
                Math.floor(pos.x),
                Math.floor(pos.y - offsetAmount),
                Math.floor(pos.z));

        return !entity.getEntityWorld().isAir(blockpos);
    }

    @Override
    public boolean isSwimming(LivingEntity entity) {
        return entity.isSwimming() || entity.isInSwimmingPose();
    }

    @Override
    public boolean isPartiallySubmerged(LivingEntity entity) {
        return entity.isSubmergedInWater()
                || entity.getEntityWorld().getBlockState(entity.getBlockPos()).getMaterial() == Material.WATER;
    }

    @Override
    public boolean isFullySubmerged(LivingEntity entity) {
        return entity.isSubmergedInWater()
                && entity.getEntityWorld().getBlockState(new BlockPos(getVisualEyePosition(entity))).getMaterial() == Material.WATER;
    }

    protected Vec3d getVisualEyePosition(LivingEntity entity) {
        Size size = entity.isBaby() ? Sizes.FOAL : getMetadata().getSize();

        return new Vec3d(
                entity.getX(),
                entity.getY() + (double) entity.getEyeHeight(entity.getPose()) * size.getScaleFactor(),
                entity.getZ()
        );
    }

    @Override
    public Race getRace(boolean ignorePony) {
        return getEffectiveRace(getMetadata().getRace(), ignorePony);
    }

    @Override
    public Identifier getTexture() {
        return texture;
    }

    @Override
    public IPonyData getMetadata() {
        return metadata.get(PonyData.NULL);
    }

    @Override
    public boolean isSitting(LivingEntity entity) {
        return entity.hasVehicle();
    }

    @Override
    public boolean isRidingInteractive(LivingEntity entity) {

        if (isSitting(entity) && entity.getVehicle() instanceof LivingEntity) {
            return PonyRenderDispatcher.getInstance().getPonyRenderer((LivingEntity) entity.getVehicle()) != null;
        }

        return false;
    }

    @Override
    public IPony getMountedPony(LivingEntity entity) {
        if (entity.hasVehicle() && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity mount = (LivingEntity) entity.getVehicle();

            IPonyRenderContext<LivingEntity, ?> render = PonyRenderDispatcher.getInstance().getPonyRenderer(mount);

            return render == null ? null : render.getEntityPony(mount);
        }
        return null;
    }

    @Override
    public Vec3d getAbsoluteRidingOffset(LivingEntity entity) {
        IPony ridingPony = getMountedPony(entity);

        if (ridingPony != null) {
            LivingEntity ridee = (LivingEntity)entity.getVehicle();

            Vec3d offset = PonyTransformation.forSize(ridingPony.getMetadata().getSize()).getRiderOffset();
            float scale = ridingPony.getMetadata().getSize().getScaleFactor();

            return ridingPony.getAbsoluteRidingOffset(ridee)
                    .add(0, offset.y - ridee.getHeight() * 1/scale, 0);
        }

        float delta = MinecraftClient.getInstance().getTickDelta();

        Entity vehicle = entity.getVehicle();
        double vehicleOffset = vehicle == null ? 0 : vehicle.getHeight() - vehicle.getMountedHeightOffset();

        return new Vec3d(
                MathHelper.lerp(delta, entity.prevX, entity.getX()),
                MathHelper.lerp(delta, entity.prevY, entity.getY()) + vehicleOffset,
                MathHelper.lerp(delta, entity.prevZ, entity.getZ())
        );
    }

    @Override
    public Box getComputedBoundingBox(LivingEntity entity) {
        float scale = getMetadata().getSize().getScaleFactor() + 0.1F;

        Vec3d pos = getAbsoluteRidingOffset(entity);

        float width = entity.getWidth() * scale;

        return new Box(
                - width, (entity.getHeight() * scale), -width,
                  width, 0,                        width).offset(pos);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("texture", texture)
                .add("metadata", metadata)
                .toString();
    }

    /**
     * Gets the actual race determined by the given pony level.
     * PonyLevel.HUMANS would force all races to be humans.
     * PonyLevel.BOTH is no change.
     * PonyLevel.PONIES (should) return a pony if this is a human. Don't be fooled, though. It doesn't.
     */
    public static Race getEffectiveRace(Race race, boolean ignorePony) {
        if (MineLittlePony.getInstance().getConfig().getEffectivePonyLevel(ignorePony) == PonyLevel.HUMANS) {
            return Race.HUMAN;
        }

        return race;
    }

    public interface RegistrationHandler {
        boolean shouldUpdateRegistration(Pony pony);
    }
}
