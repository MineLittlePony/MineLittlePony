package com.minelittlepony.client.render.entity.state;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.SkullBlock.SkullType;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
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
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.PonyModelPrepareCallback;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.*;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.transform.PonyPosture;

import java.util.ArrayList;
import java.util.List;

public class PonyRenderState extends PlayerEntityRenderState implements PonyModel.AttributedHolder {
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

    public Pony pony = Pony.getManager().getPony(DefaultPonySkinHelper.STEVE);
    public Race race = Race.HUMAN;

    public final HeldItemRenderState leftHeldItem = new HeldItemRenderState();
    public final HeldItemRenderState rightHeldItem = new HeldItemRenderState();

    public final ItemRenderState glintlessRightHandItemState = new ItemRenderState();
    public final ItemRenderState glintlessLeftHandItemState = new ItemRenderState();

    public final List<EquippedHeadRenderState> equippedHeads = new ArrayList<>();

    public void updateState(ItemModelManager resolver, LivingEntity entity, PonyModel<?> model, Pony pony, ModelAttributes.Mode mode) {
        this.equippedHeadStack = entity.getEquippedStack(EquipmentSlot.HEAD);
        this.pony = pony;
        attributes.updateLivingState(entity, pony, mode);
        attributes.checkRainboom(entity, model, age);
        baby = attributes.size == SizePreset.FOAL;
        race = pony.race();
        vehicleOffset = hasVehicle ? entity.getVehicle().getEyeHeight(pose) : 0;
        riderOffset = getRiderYOffset();
        nameplateYOffset = getNamePlateYOffset(entity);
        legOutset = getLegOutset();
        isInSneakingPose = attributes.isCrouching && !attributes.isLyingDown;
        sleepingInBed = entity.getSleepingPosition().isPresent() && entity.getEntityWorld().getBlockState(entity.getSleepingPosition().get()).getBlock() instanceof BedBlock;
        submergedInWater = entity.isSubmergedInWater();
        wobbleAmount = handSwingProgress <= 0 ? 0 : MathHelper.sin(MathHelper.sqrt(handSwingProgress) * MathHelper.PI * 2) * 0.04F;
        if (attributes.isSitting) {
            pose = EntityPose.SITTING;
        }

        isTechnoblade = ((
                    entity instanceof AbstractPiglinEntity
                 || entity instanceof PlayerEntity
                 || entity instanceof ZombifiedPiglinEntity
             ) && entity.hasCustomName() && entity.getCustomName().getString().equalsIgnoreCase("technoblade")
         );

        leftHeldItem.update(entity.getStackInArm(Arm.LEFT));
        rightHeldItem.update(entity.getStackInArm(Arm.RIGHT));

        // Adjust cape angles
        // capePitch
        field_53537 *= 0.3F;
        // capeRoll
        field_53538 *= 3F;

        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        equippedHeads.clear();
        for (ItemStack stack : plugin.getArmorStacks(entity, EquipmentSlot.HEAD, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL)) {
            EquippedHeadRenderState state = EquippedHeadRenderState.of(resolver, stack, entity);
            if (!state.isEmpty()) {
                equippedHeads.add(state);
            }
        }

        PonyPosture.of(attributes).updateState(entity, this);
        PonyModelPrepareCallback.EVENT.invoker().onPonyModelPrepared(attributes, model, ModelAttributes.Mode.OTHER);
    }

    @Override
    public final Race getRace() {
        return race;
    }

    public boolean hasMagicGlow() {
        return race.hasHorn() && attributes.metadata.glowColor() != 0;
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

    /**
     * Tests if this model is wearing the given piece of gear.
     */
    public boolean isWearing(Wearable wearable) {
        return attributes.isEmbedded(wearable) || attributes.featureSkins.contains(wearable.getId()) || isTechnoblade && wearable == Wearable.CROWN;
    }

    private float getNamePlateYOffset(LivingEntity entity) {
        // We start by negating the height calculation done by mahjong.
        float y = -(height + 0.5F);

        // Then we add our own offsets.
        y += attributes.visualHeight * attributes.size.scaleFactor() + 0.25F;
        y += vehicleOffset;

        if (isInSneakingPose) {
            y -= 0.25F;
        }

        if (isInPose(EntityPose.SLEEPING)) {
            y /= 2;
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

        private void update(ItemStack stack) {
            action = stack.getUseAction();
            forwardFacing = PonyConfig.getInstance().forwardHoldingItems.get().contains(Registries.ITEM.getId(stack.getItem()));
        }
    }

    public record EquippedHeadRenderState(
            ItemRenderState item,
            @Nullable SkullType skullType,
            @Nullable ProfileComponent wearingSkullProfile) {
        static final EquippedHeadRenderState EMPTY = new EquippedHeadRenderState(new ItemRenderState(), null, null);

        public static EquippedHeadRenderState of(ItemModelManager resolver, ItemStack stack, LivingEntity entity) {
            if (stack.isEmpty()) {
                return EMPTY;
            }

            if (stack.getItem() instanceof BlockItem b && b.getBlock() instanceof AbstractSkullBlock skullBlock) {
                return new EquippedHeadRenderState(EMPTY.item(), skullBlock.getSkullType(), stack.get(DataComponentTypes.PROFILE));
            }

            if (!ArmorFeatureRenderer.hasModel(stack, EquipmentSlot.HEAD)) {
                ItemRenderState item = new ItemRenderState();
                resolver.updateForLivingEntity(item, stack, ItemDisplayContext.HEAD, entity);
                if (!item.isEmpty()) {
                    return new EquippedHeadRenderState(item, null, null);
                }
            }

            return EMPTY;
        }

        public boolean isEmpty() {
            return item.isEmpty() && skullType == null;
        }
    }
}
