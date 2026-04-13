package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.ModelWithHorn;
import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SwingAnimationComponent;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.*;

import org.jetbrains.annotations.Nullable;

public abstract class PonyRenderer<
        T extends MobEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyRenderer<T, S, M> {
    protected static final float BASE_MODEL_SCALE = 15/16F;

    public PonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture) {
        this(context, key, texture, 1);
    }

    public PonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, key, texture, scale);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void addFeatures(EntityRendererFactory.Context context) {
        super.addFeatures(context);
        addFeature(new StuckArrowsFeatureRenderer(this, context));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateRenderState(T entity, S state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.leftArmPose = getArmPose(entity, Arm.LEFT);
        state.rightArmPose = getArmPose(entity, Arm.RIGHT);
        state.hornGlowVisible = !IrisApiCompat.isOnShadowPass() && this.lookupModel(state).body() instanceof ModelWithHorn h && h.isCasting(state);
    }

    public ArmPose getArmPose(T entity, Arm arm) {
        Hand hand = arm == entity.getMainArm() ? Hand.MAIN_HAND : Hand.OFF_HAND;
        ItemStack mainHandItem = entity.getStackInHand(hand);

        if (mainHandItem.isEmpty()) {
            return ArmPose.EMPTY;
        }

        if (!entity.handSwinging && mainHandItem.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(mainHandItem)) {
            return ArmPose.CROSSBOW_HOLD;
        }

        if (entity.getActiveHand() == hand && entity.getItemUseTimeLeft() > 0) {
            UseAction anim = mainHandItem.getUseAction();
            if (anim == UseAction.BLOCK) {
                return ArmPose.BLOCK;
            }

            if (anim == UseAction.BOW) {
                return ArmPose.BOW_AND_ARROW;
            }

            if (anim == UseAction.TRIDENT) {
                return ArmPose.THROW_TRIDENT;
            }

            if (anim == UseAction.CROSSBOW) {
                return ArmPose.CROSSBOW_CHARGE;
            }

            if (anim == UseAction.SPEAR) {
                return ArmPose.SPEAR;
            }
        }

        @Nullable
        SwingAnimationComponent attack = mainHandItem.get(DataComponentTypes.SWING_ANIMATION);
        if (attack != null && attack.type() == SwingAnimationType.STAB && entity.handSwinging) {
            return ArmPose.SPEAR;
        }

        return mainHandItem.isIn(ItemTags.SPEARS) ? ArmPose.SPEAR : ArmPose.ITEM;
    }
}
