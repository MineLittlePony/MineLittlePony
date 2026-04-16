package com.minelittlepony.client.render.entity.state;

import net.minecraft.block.*;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EmptyBlockView;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.config.PonyDisplayTags;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.PonyRenderStatePrepareCallback;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.transform.PonyPosture;

import java.util.*;

public class PonyRenderState extends PlayerEntityRenderState implements PonyModel.AttributedHolder, PonifiedRenderState {
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

    public final HeldItemRenderState leftHeldItem = new HeldItemRenderState();
    public final HeldItemRenderState rightHeldItem = new HeldItemRenderState();

    public final List<EquippedHeadRenderState> equippedHeads = new ArrayList<>();

    public PonyPosture posture = PonyPosture.STANDING;

    public void updateState(ItemModelManager resolver,
            Map<EquipmentSlot, ItemStack> equipment,
            Map<Arm, ItemStack> armStacks,
            Pony pony, ModelAttributes.Mode mode) {
        this.equippedHeadStack = equipment.getOrDefault(EquipmentSlot.HEAD, ItemStack.EMPTY);
        this.pony = pony;
        baby = attributes.size == SizePreset.FOAL;
        race = pony.race();
        glowColor = attributes.metadata.glowColor();
        vehicleOffset = 0;
        riderOffset = getRiderYOffset();
        nameplateYOffset = getNamePlateYOffset();
        legOutset = getLegOutset();
        isInSneakingPose = attributes.isCrouching && !attributes.isLyingDown;
        sleepingInBed = false;
        submergedInWater = false;
        wobbleAmount = handSwingProgress <= 0 ? 0 : MathHelper.sin(MathHelper.sqrt(handSwingProgress) * MathHelper.PI * 2) * 0.04F;
        if (hasMagicGlow()) {
            wobbleAmount *= 0.5;
        }
        if (attributes.isSitting) {
            pose = EntityPose.SITTING;
        }

        headVisible = true;
        // Hide the horn glow if we're being rendered during an iris shadow pass
        hornGlowVisible = !IrisApiCompat.isOnShadowPass();
        isTechnoblade = false;

        // Adjust cape angles
        // capePitch
        field_53537 *= 0.3F;
        // capeRoll
        field_53538 *= 3F;

        equippedHeads.clear();
        hasHeadBlock = false;
        ItemStack stack = equipment.getOrDefault(EquipmentSlot.HEAD, ItemStack.EMPTY);
        EquippedHeadRenderState state = EquippedHeadRenderState.of(resolver, stack, null);
        if (!state.isEmpty()) {
            equippedHeads.add(state);
            hasHeadBlock |= state.opaque;
        }

        rightHeldItem.updateItemRenderState(this, resolver, armStacks.getOrDefault(Arm.RIGHT, ItemStack.EMPTY), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null);
        leftHeldItem.updateItemRenderState(this, resolver, armStacks.getOrDefault(Arm.LEFT, ItemStack.EMPTY), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null);
    }

    @SuppressWarnings("unchecked")
    public void updateState(ItemModelManager resolver, @Nullable LivingEntity entity, Models<?> models, Pony pony, ModelAttributes.Mode mode) {
        this.equippedHeadStack = entity.getEquippedStack(EquipmentSlot.HEAD);
        this.pony = pony;
        if (entity != null) {
            attributes.updateLivingState(entity, pony, mode);
            attributes.checkRainboom(entity, models.body(), age);
        }
        attributes.size = PonyDisplayTags.of(entity).size().orElseGet(() -> computeSize(entity, attributes.size));
        baby = attributes.size == SizePreset.FOAL;
        race = PonyDisplayTags.of(entity).race().orElseGet(() -> computeRace(entity, pony));
        glowColor = PonyDisplayTags.of(entity).magicColor().orElse(attributes.metadata.glowColor());

        if (hasVehicle) {
            vehicleOffset = (float)(entity.getBoundingBox().minY - entity.getVehicle().getBoundingBox().minY - entity.getVehicle().getAttachments().getPoint(EntityAttachmentType.PASSENGER, 0, 0).y);
        } else {
            vehicleOffset = 0;
        }
        riderOffset = getRiderYOffset();
        nameplateYOffset = getNamePlateYOffset();
        legOutset = getLegOutset();
        isInSneakingPose = attributes.isCrouching && !attributes.isLyingDown;
        sleepingInBed = entity != null && entity.getSleepingPosition().isPresent() && entity.getEntityWorld().getBlockState(entity.getSleepingPosition().get()).getBlock() instanceof BedBlock;
        submergedInWater = entity != null && entity.isSubmergedInWater();
        wobbleAmount = handSwingProgress <= 0 ? 0 : MathHelper.sin(MathHelper.sqrt(handSwingProgress) * MathHelper.PI * 2) * 0.04F;
        if (hasMagicGlow()) {
            wobbleAmount *= 0.5;
        }
        if (attributes.isSitting) {
            pose = EntityPose.SITTING;
        }

        // Prevent head from rendering for ourselves if we are sleeping in first person mode
        headVisible = entity != MinecraftClient.getInstance().getCameraEntity()
                || !MinecraftClient.getInstance().options.getPerspective().isFirstPerson()
                || MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos().squaredDistanceTo(entity.getCameraPosVec(age - (int)age)) > 1
                || RenderPass.getCurrent() != RenderPass.WORLD
                || IrisApiCompat.isOnShadowPass();

        if (entity != null) {
            updateHeldItems(resolver, entity);
        }
        // Hide the horn glow if we're being rendered during an iris shadow pass
        hornGlowVisible = !IrisApiCompat.isOnShadowPass() && models.body() instanceof ModelWithHorn h && h.isCasting(this);
        isTechnoblade = ((
                    entity instanceof AbstractPiglinEntity
                 || entity instanceof PlayerEntity
                 || entity instanceof ZombifiedPiglinEntity
             ) && displayName != null && displayName.getString().equalsIgnoreCase("technoblade")
         );

        // Adjust cape angles
        // capePitch
        field_53537 *= 0.3F;
        // capeRoll
        field_53538 *= 3F;

        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        equippedHeads.clear();
        hasHeadBlock = false;
        for (ItemStack stack : plugin.getArmorStacks(entity, EquipmentSlot.HEAD, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL)) {
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

        this.pitch = attributes.isSleeping ? 0.1f : this.pitch;
        if (entity instanceof PlayerLikeEntity) {
            relativeHeadYaw = attributes.isSleeping ? (Math.signum(MathHelper.wrapDegrees(relativeHeadYaw)) * 1.3F) : relativeHeadYaw;
        }

        PonyRenderStatePrepareCallback.EVENT.invoker().onPonyRenderStatePrepared(this, models.body(), mode);
    }

    protected void updateHeldItems(ItemModelManager resolver, LivingEntity entity) {
        rightHeldItem.updateItemRenderState(this, resolver, entity.getStackInArm(Arm.RIGHT), ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
        leftHeldItem.updateItemRenderState(this, resolver, entity.getStackInArm(Arm.LEFT), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, entity);
    }

    public ArmPose getArmPoseForArm(final Arm arm) {
        return arm == Arm.RIGHT ? rightArmPose : leftArmPose;
    }

    protected Race computeRace(@Nullable LivingEntity entity, Pony pony) {
        return pony.race();
    }

    protected Size computeSize(@Nullable LivingEntity entity, Size size) {
        return size;
    }

    @Override
    public final Race getRace() {
        return race;
    }

    public boolean hasMagicGlow() {
        return race.hasHorn() && glowColor != 0;
    }

    /**
     * Gets the current leg swing amount.
     */
    @Override
    public float getSwingAmount() {
        return this.handSwingProgress;
    }

    protected float getLegOutset() {
        float outset = attributes.isLyingDown ? 3.6F : attributes.isCrouching ? 1 : 5;
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
        float y = -(height + 0.5F);

        // Then we add our own offsets.
        y += attributes.visualHeight * attributes.size.scaleFactor() + 0.75F;

        if (isInSneakingPose) {
            y -= 0.25F;
        }

        if (isInPose(EntityPose.SLEEPING)) {
            y += 0.5F;
        }

        return y;
    }

    @Override
    public ModelAttributes getAttributes() {
        return attributes;
    }

    public HeldItemRenderState getHeldItem(Arm arm) {
        return arm == Arm.LEFT ? leftHeldItem : rightHeldItem;
    }

    public static class HeldItemRenderState {
        public UseAction action = UseAction.NONE;
        public boolean forwardFacing;
        public boolean chargedCrossbow;
        public boolean repositionFirstPerson;
        public boolean handHeldTool;

        public float levitatingItemXDrift;
        public float levitatingItemZDrift;
        public float levitatingItemScale;

        public final ItemRenderState glintlessHandItemState = new ItemRenderState();

        public void updateItemRenderState(PonyRenderState state, ItemModelManager resolver, ItemStack stack, ItemDisplayContext context, @Nullable LivingEntity entity) {
            action = stack.getUseAction();
            forwardFacing = PonyConfig.getInstance().forwardHoldingItems.get().contains(Registries.ITEM.getId(stack.getItem()));
            chargedCrossbow = action == UseAction.CROSSBOW && CrossbowItem.isCharged(stack);
            repositionFirstPerson = state.itemUseTime <= 0 || action == UseAction.NONE || chargedCrossbow;
            handHeldTool = action == UseAction.BOW
                    || action == UseAction.CROSSBOW
                    || action == UseAction.BLOCK
                    || stack.contains(DataComponentTypes.TOOL)
                    || forwardFacing;

            if (PonyConfig.getInstance().tpsmagic.get() && state.hasMagicGlow()) {
                float driftStrength = 0.002F;
                levitatingItemXDrift = MathHelper.sin(state.age / 20F) * driftStrength;
                levitatingItemZDrift = MathHelper.cos((state.age + 20) / 20F) * driftStrength;

                levitatingItemScale = 0.2F + (MathHelper.sin(state.age / 10F) + 1) * driftStrength * 6;

                Boolean glintOverride = stack.get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
                stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);

                if (entity == null) {
                    resolver.clearAndUpdate(glintlessHandItemState, stack, context, null, null, 0);
                } else {
                    resolver.updateForLivingEntity(glintlessHandItemState, stack, context, entity);
                }

                stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glintOverride);
            } else {
                glintlessHandItemState.clear();
            }
        }
    }

    public record EquippedHeadRenderState(
            ItemRenderState item,
            boolean opaque,
            @Nullable SkullType skullType,
            @Nullable ProfileComponent wearingSkullProfile) {
        static final EquippedHeadRenderState EMPTY = new EquippedHeadRenderState(new ItemRenderState(), false, null, null);

        public static EquippedHeadRenderState of(ItemModelManager resolver, ItemStack stack, @Nullable LivingEntity entity) {
            if (stack.isEmpty()) {
                return EMPTY;
            }

            if (stack.getItem() instanceof BlockItem b && b.getBlock() instanceof AbstractSkullBlock skullBlock) {
                return new EquippedHeadRenderState(EMPTY.item(), skullBlock.getDefaultState().isSolidBlock(EmptyBlockView.INSTANCE, BlockPos.ORIGIN), skullBlock.getSkullType(), stack.get(DataComponentTypes.PROFILE));
            }

            if (!ArmorFeatureRenderer.hasModel(stack, EquipmentSlot.HEAD)) {
                ItemRenderState item = new ItemRenderState();
                if (entity != null) {
                    resolver.updateForLivingEntity(item, stack, ItemDisplayContext.HEAD, entity);
                } else {
                    resolver.clearAndUpdate(item, stack, ItemDisplayContext.HEAD, null, null, 0);
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
