package com.minelittlepony.client.render.entity.state;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.*;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.config.PonyDisplayTags;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.PonyRenderStatePrepareCallback;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.model.armour.ArmourRendererPlugin;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.api.state.PonifiedRenderState;
import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.transform.PonyPosture;
import com.minelittlepony.client.transform.PonyTransformation;

import java.util.*;

public class PonyRenderState extends AvatarRenderState implements PonyModel.AttributedHolder, PonifiedRenderState {
    public final ModelAttributes attributes = new ModelAttributes();

    public float vehicleOffset;
    public float riderOffset;
    public float nameplateYOffset;
    public float legOutset;
    public float wobbleAmount;
    public boolean smallArms;
    public boolean sleepingInBed;
    public boolean submergedInWater;
    public boolean onGround;
    public boolean isTechnoblade;
    public boolean headVisible = true;
    public boolean hornGlowVisible = true;
    public boolean hasHeadBlock;

    public Pony pony = Pony.getManager().getPony(DefaultPonySkinHelper.STEVE);
    public Race race = Race.HUMAN;
    public int glowColor;

    public TransformedModel.BodyType transformation = PonyTransformation.NORMAL;

    public final HeldItemRenderState leftHeldItem = new HeldItemRenderState();
    public final HeldItemRenderState rightHeldItem = new HeldItemRenderState();

    public final List<EquippedHeadRenderState> equippedHeads = new ArrayList<>();

    public PonyPosture posture = PonyPosture.STANDING;

    public void updateState(ItemModelResolver resolver,
            Map<EquipmentSlot, ItemStack> equipment,
            Map<HumanoidArm, ItemStack> armStacks,
            Pony pony, ModelAttributes.Mode mode) {
        headEquipment = equipment.getOrDefault(EquipmentSlot.HEAD, ItemStack.EMPTY);
        this.pony = pony;
        isBaby = attributes.size == SizePreset.FOAL;
        race = pony.race();
        glowColor = attributes.metadata.glowColor();
        transformation = PonyTransformation.forSize(attributes.size);
        vehicleOffset = 0;
        riderOffset = getRiderYOffset();
        nameplateYOffset = getNamePlateYOffset();
        legOutset = getLegOutset();
        isCrouching = attributes.isCrouching && !attributes.isLyingDown;
        sleepingInBed = false;
        submergedInWater = false;
        wobbleAmount = attackTime <= 0 ? 0 : Mth.sin(Mth.sqrt(attackTime) * Mth.TWO_PI) * 0.04F;
        if (hasMagicGlow()) {
            wobbleAmount *= 0.5;
        }
        if (attributes.isSitting) {
            pose = Pose.SITTING;
        }

        headVisible = true;
        // Hide the horn glow if we're being rendered during an iris shadow pass
        hornGlowVisible = !IrisApiCompat.isOnShadowPass();
        isTechnoblade = false;

        // Adjust cape angles
        capeFlap *= 0.3F;
        capeLean *= 3F;

        equippedHeads.clear();
        hasHeadBlock = false;
        ItemStack stack = equipment.getOrDefault(EquipmentSlot.HEAD, ItemStack.EMPTY);
        EquippedHeadRenderState state = EquippedHeadRenderState.of(resolver, stack, null);
        if (!state.isEmpty()) {
            equippedHeads.add(state);
            hasHeadBlock |= state.opaque;
        }

        rightHeldItem.updateItemRenderState(this, resolver, armStacks.getOrDefault(HumanoidArm.RIGHT, ItemStack.EMPTY), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null);
        leftHeldItem.updateItemRenderState(this, resolver, armStacks.getOrDefault(HumanoidArm.LEFT, ItemStack.EMPTY), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null);
    }

    public void updateState(ItemModelResolver resolver, @Nullable LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
        headEquipment = entity.getItemBySlot(EquipmentSlot.HEAD);
        this.pony = pony;
        if (entity != null) {
            attributes.updateLivingState(entity, pony, mode);
            attributes.checkRainboom(entity, models.body(), ageInTicks);
        }
        var displayTags = PonyDisplayTags.of(entity);

        attributes.size = displayTags.size().orElseGet(() -> computeSize(entity, attributes.size));
        isBaby = attributes.size == SizePreset.FOAL;
        race = displayTags.race().orElseGet(() -> computeRace(entity, pony));
        glowColor = displayTags.magicColor().orElse(attributes.metadata.glowColor());
        transformation = PonyTransformation.forSize(attributes.size);
        if (isPassenger) {
            vehicleOffset = (float)(entity.getBoundingBox().minY - entity.getVehicle().getBoundingBox().minY - entity.getVehicle().getAttachments().get(EntityAttachment.PASSENGER, 0, 0).y);
        } else {
            vehicleOffset = 0;
        }
        riderOffset = getRiderYOffset();
        nameplateYOffset = getNamePlateYOffset();
        legOutset = getLegOutset();
        isCrouching = attributes.isCrouching && !attributes.isLyingDown;
        sleepingInBed = entity != null && entity.getSleepingPos().isPresent() && entity.level().getBlockState(entity.getSleepingPos().get()).getBlock() instanceof BedBlock;
        submergedInWater = entity != null && entity.isUnderWater();
        wobbleAmount = attackTime <= 0 ? 0 : Mth.sin(Mth.sqrt(attackTime) * Mth.TWO_PI) * 0.04F;
        if (hasMagicGlow()) {
            wobbleAmount *= 0.5;
        }
        if (attributes.isSitting) {
            pose = Pose.SITTING;
        }

        // Prevent head from rendering for ourselves if we are sleeping in first person mode
        headVisible = entity != Minecraft.getInstance().getCameraEntity()
                || !Minecraft.getInstance().options.getCameraType().isFirstPerson()
                || Minecraft.getInstance().gameRenderer.getMainCamera().position().distanceToSqr(entity.getEyePosition(ageInTicks - (int)ageInTicks)) > 1
                || RenderPass.getCurrent() != RenderPass.WORLD
                || IrisApiCompat.isOnShadowPass();

        if (entity != null) {
            updateHeldItems(resolver, entity);
        }
        // Hide the horn glow if we're being rendered during an iris shadow pass
        hornGlowVisible = !IrisApiCompat.isOnShadowPass() && computeIsCasting(entity);
        isTechnoblade = ((
                    entity instanceof AbstractPiglin
                 || entity instanceof Player
                 || entity instanceof ZombifiedPiglin
             ) && nameTag != null && nameTag.getString().equalsIgnoreCase("technoblade")
         );

        // Adjust cape angles
        capeFlap *= 0.3F;
        capeLean *= 3F;

        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        equippedHeads.clear();
        hasHeadBlock = false;
        for (ItemStack stack : plugin.getArmorStacks(entity, EquipmentSlot.HEAD, EquipmentClientInfo.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL)) {
            EquippedHeadRenderState state = EquippedHeadRenderState.of(resolver, stack, entity);
            if (!state.isEmpty()) {
                equippedHeads.add(state);
                hasHeadBlock |= state.opaque;
            }
        }

        this.posture = PonyPosture.of(attributes);

        if (entity != null) {
            posture.updateState(entity, this);
        }

        xRot = attributes.isSleeping ? 0.1f : xRot;
        if (entity instanceof Avatar) {
            yRot = attributes.isSleeping ? (Math.signum(Mth.wrapDegrees(yRot)) * 1.3F) : yRot;
        }

        PonyRenderStatePrepareCallback.EVENT.invoker().onPonyRenderStatePrepared(this, models.body(), mode);
    }

    protected void updateHeldItems(ItemModelResolver resolver, LivingEntity entity) {
        rightHeldItem.updateItemRenderState(this, resolver, entity.getItemHeldByArm(HumanoidArm.RIGHT), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
        leftHeldItem.updateItemRenderState(this, resolver, entity.getItemHeldByArm(HumanoidArm.LEFT), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
    }

    @Override
    public ArmPose getArmPoseForArm(final HumanoidArm arm) {
        return arm == HumanoidArm.RIGHT ? rightArmPose : leftArmPose;
    }

    protected Race computeRace(@Nullable LivingEntity entity, Pony pony) {
        return pony.race();
    }

    protected Size computeSize(@Nullable LivingEntity entity, Size size) {
        return size;
    }

    public boolean computeIsCasting(@Nullable LivingEntity entity) {
        return leftArmPose != ArmPose.EMPTY || rightArmPose != ArmPose.EMPTY;
    }

    @Override
    public final Race getRace() {
        return race;
    }

    @Override
    public final AvatarRenderState getRenderState() {
        return this;
    }

    @Override
    public final TransformedModel.BodyType getBodyType() {
        return transformation;
    }

    public boolean hasMagicGlow() {
        return race.hasHorn() && glowColor != 0;
    }

    /**
     * Gets the current leg swing amount.
     */
    @Override
    public float getSwingAmount() {
        return attackTime;
    }

    protected float getLegOutset() {
        float outset = attributes.isGoingFast ? 5.5F : attributes.isLyingDown ? 3.6F : attributes.isCrouching ? 1 : 5;
        return smallArms ? Math.max(1, outset - 1) : outset;
    }

    /**
     * Gets the y-offset applied to entities riding this one.
     */
    protected float getRiderYOffset() {
        return switch ((SizePreset)attributes.size) {
            case NORMAL -> 0.4F;
            default -> 0.25F;
        };
    }

    @Override
    public boolean isWearing(Wearable wearable) {
        return attributes.isWearing(wearable) || isTechnoblade && wearable == Wearable.CROWN;
    }

    private float getNamePlateYOffset() {
        // We start by negating the height calculation done by mahjong.
        float y = -(boundingBoxHeight + 0.5F);

        // Then we add our own offsets.
        y += attributes.visualHeight * attributes.size.scaleFactor() + 0.75F;

        if (isCrouching) {
            y -= 0.25F;
        }

        if (hasPose(Pose.SLEEPING)) {
            y += 0.5F;
        }

        return y;
    }

    @Override
    public ModelAttributes getAttributes() {
        return attributes;
    }

    @Override
    public final Optional<PonyModel.AttributedHolder> getPonyState() {
        return Optional.of(this);
    }

    @Override
    public final boolean isOf(EntityType<?> entityType) {
        return this.entityType == entityType;
    }

    public HeldItemRenderState getHeldItem(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? leftHeldItem : rightHeldItem;
    }

    public static class HeldItemRenderState {
        public ItemUseAnimation action = ItemUseAnimation.NONE;
        public boolean forwardFacing;
        public boolean chargedCrossbow;
        public boolean repositionFirstPerson;
        public boolean handHeldTool;

        public float levitatingItemXDrift;
        public float levitatingItemZDrift;
        public float levitatingItemScale;

        public final ItemStackRenderState glintlessHandItemState = new ItemStackRenderState();

        public void updateItemRenderState(PonyRenderState state, ItemModelResolver resolver, ItemStack stack, ItemDisplayContext context, @Nullable LivingEntity entity) {
            action = stack.getUseAnimation();
            forwardFacing = PonyConfig.getInstance().forwardHoldingItems.get().contains(BuiltInRegistries.ITEM.getKey(stack.getItem()));
            chargedCrossbow = action == ItemUseAnimation.CROSSBOW && CrossbowItem.isCharged(stack);
            repositionFirstPerson = state.ticksUsingItem <= 0 || action == ItemUseAnimation.NONE || chargedCrossbow;
            handHeldTool = action == ItemUseAnimation.BOW
                    || action == ItemUseAnimation.CROSSBOW
                    || action == ItemUseAnimation.BLOCK
                    || stack.has(DataComponents.TOOL)
                    || forwardFacing;

            if (state.hasMagicGlow()) {
                float driftStrength = 0.002F;
                levitatingItemXDrift = Mth.sin(state.ageInTicks / 20F) * driftStrength;
                levitatingItemZDrift = Mth.cos((state.ageInTicks + 20) / 20F) * driftStrength;

                levitatingItemScale = 0.2F + (Mth.sin(state.ageInTicks / 10F) + 1) * driftStrength * 6;

                Boolean glintOverride = stack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
                stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, false);

                if (entity == null) {
                    resolver.updateForTopItem(glintlessHandItemState, stack, context, null, null, 0);
                } else {
                    resolver.updateForLiving(glintlessHandItemState, stack, context, entity);
                }

                stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, glintOverride);
            } else {
                glintlessHandItemState.clear();
            }
        }
    }

    public record EquippedHeadRenderState(
            ItemStackRenderState item,
            boolean opaque,
            @Nullable SkullBlock.Type skullType,
            @Nullable ResolvableProfile wearingSkullProfile) {
        static final EquippedHeadRenderState EMPTY = new EquippedHeadRenderState(new ItemStackRenderState(), false, null, null);

        public static EquippedHeadRenderState of(ItemModelResolver resolver, ItemStack stack, @Nullable LivingEntity entity) {
            if (stack.isEmpty()) {
                return EMPTY;
            }

            if (stack.getItem() instanceof BlockItem b && b.getBlock() instanceof AbstractSkullBlock skullBlock) {
                return new EquippedHeadRenderState(EMPTY.item(), skullBlock.defaultBlockState().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), skullBlock.getType(), stack.get(DataComponents.PROFILE));
            }

            if (!HumanoidArmorLayer.shouldRender(stack, EquipmentSlot.HEAD)) {
                ItemStackRenderState item = new ItemStackRenderState();
                if (entity != null) {
                    resolver.updateForLiving(item, stack, ItemDisplayContext.HEAD, entity);
                } else {
                    resolver.updateForTopItem(item, stack, ItemDisplayContext.HEAD, null, null, 0);
                }
                if (!item.isEmpty()) {
                    return new EquippedHeadRenderState(item, false, null, null);
                }
            }

            return EMPTY;
        }

        public boolean isEmpty() {
            return item.isEmpty() && skullType == null;
        }
    }
}
