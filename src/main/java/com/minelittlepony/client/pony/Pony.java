package com.minelittlepony.client.pony;

import com.google.common.base.MoreObjects;
import com.minelittlepony.MineLittlePony;
import com.minelittlepony.client.PonyRenderManager;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.transform.PonyTransformation;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.IPonyData;
import com.minelittlepony.pony.meta.Race;
import com.minelittlepony.pony.meta.Size;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mojang.blaze3d.platform.GlStateManager.getTexLevelParameter;
import static org.lwjgl.opengl.GL11.*;

@Immutable
public class Pony implements IPony {

    private static final AtomicInteger ponyCount = new AtomicInteger();

    private final int ponyId = ponyCount.getAndIncrement();

    private final Identifier texture;
    private final IPonyData metadata;

    private boolean initialized = false;

    public Pony(Identifier resource) {
        texture = resource;
        metadata = checkSkin(texture);
    }

    private IPonyData checkSkin(Identifier resource) {
        IPonyData data = checkPonyMeta(resource);
        if (data != null) {
            return data;
        }

        NativeImage ponyTexture = getBufferedImage(resource);
        return checkSkin(ponyTexture);
    }

    @Override
    public void updateForEntity(Entity entity) {
        if (!initialized) {
            initialized = true;
            entity.calculateDimensions();
        }
    }

    @Nullable
    private IPonyData checkPonyMeta(Identifier resource) {
        try {
            Resource res = MinecraftClient.getInstance().getResourceManager().getResource(resource);

            PonyData data = res.getMetadata(PonyData.SERIALISER);

            if (data != null) {
                return data;
            }
        } catch (FileNotFoundException e) {
            // Ignore uploaded texture
        } catch (IOException e) {
            MineLittlePony.logger.warn("Unable to read {} metadata", resource, e);
        }

        return null;
    }

    public static NativeImage getBufferedImage(@Nullable Identifier resource) {

        if (resource == null) {
            return MissingSprite.getMissingSpriteTexture().getImage();
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        TextureManager textures = mc.getTextureManager();

        // recreate NativeImage from the GL matrix
        textures.bindTexture(resource);

        int format = getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
        int width = getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH);
        int height = getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_HEIGHT);

        NativeImage.Format channels = NativeImage.Format.RGBA;
        if (format == GL_RGB) {
            channels = NativeImage.Format.RGB;
        }

        NativeImage image = new NativeImage(channels, width, height, false);
        image.loadFromTextureImage(0, false);
        return image;

    }

    private IPonyData checkSkin(NativeImage bufferedimage) {
        MineLittlePony.logger.debug("\tStart skin check for pony #{} with image {}.", ponyId, bufferedimage);
        return PonyData.parse(bufferedimage);
    }

    @Override
    public boolean isPerformingRainboom(LivingEntity entity) {
        Vec3d motion = entity.getVelocity();
        double zMotion = Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        return (isFlying(entity) && canFly()) || entity.isFallFlying() & zMotion > 0.4F;
    }

    @Override
    public boolean isCrouching(LivingEntity entity) {

        boolean isSneak = entity.isSneaking();
        boolean isFlying = isFlying(entity);
        boolean isSwimming = isSwimming(entity);

        return !isPerformingRainboom(entity) && !isSwimming && isSneak && !isFlying;
    }

    @Override
    public boolean isFlying(LivingEntity entity) {
        return !(entity.onGround
                || entity.hasVehicle()
                || (entity.isClimbing() && !(entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.allowFlying))
                || entity.isInWater()
                || entity.isSleeping());
    }

    @Override
    public boolean isSwimming(LivingEntity entity) {
        return entity.isSwimming() || entity.isInSwimmingPose();
    }

    @Override
    public boolean isPartiallySubmerged(LivingEntity entity) {
        return entity.isInWater()
                || entity.getEntityWorld().getBlockState(entity.getBlockPos()).getMaterial() == Material.WATER;
    }

    @Override
    public boolean isFullySubmerged(LivingEntity entity) {
        return entity.isInWater()
                && entity.getEntityWorld().getBlockState(new BlockPos(getVisualEyePosition(entity))).getMaterial() == Material.WATER;
    }

    protected Vec3d getVisualEyePosition(LivingEntity entity) {
        Size size = entity.isBaby() ? Size.FOAL : metadata.getSize();

        return new Vec3d(entity.x, entity.y + (double) entity.getEyeHeight(entity.getPose()) * size.getScaleFactor(), entity.z);
    }

    @Override
    public boolean isWearingHeadgear(LivingEntity entity) {
        ItemStack stack = entity.getEquippedStack(EquipmentSlot.HEAD);

        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();

        return !(item instanceof ArmorItem) || ((ArmorItem) item).getSlotType() != EquipmentSlot.HEAD;
    }

    @Override
    public Race getRace(boolean ignorePony) {
        return metadata.getRace().getEffectiveRace(ignorePony);
    }

    @Override
    public Identifier getTexture() {
        return texture;
    }

    @Override
    public IPonyData getMetadata() {
        return metadata;
    }

    @Override
    public boolean isRidingInteractive(LivingEntity entity) {
        return PonyRenderManager.getInstance().getPonyRenderer(entity.getVehicle()) != null;
    }

    @Override
    public IPony getMountedPony(LivingEntity entity) {
        Entity mount = entity.getVehicle();

        IPonyRender<LivingEntity, ?> render = PonyRenderManager.getInstance().getPonyRenderer(mount);

        return render == null ? null : render.getEntityPony((LivingEntity)mount);
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

        return new Vec3d(
                MathHelper.lerp(delta, entity.prevRenderX, entity.x),
                MathHelper.lerp(delta, entity.prevRenderY, entity.y),
                MathHelper.lerp(delta, entity.prevRenderZ, entity.z));
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
}
