package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.SwingAnimation;

import org.jetbrains.annotations.Nullable;

public abstract class PonyRenderer<
        T extends Mob,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyRenderer<T, S, M> {
    protected static final float BASE_MODEL_SCALE = 15/16F;

    public PonyRenderer(EntityRendererProvider.Context context, ModelKey<? super M> key, TextureSupplier<T> texture) {
        this(context, key, texture, 1);
    }

    public PonyRenderer(EntityRendererProvider.Context context, ModelKey<? super M> key, TextureSupplier<T> texture, float scale) {
        super(context, key, texture, scale);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void addFeatures(EntityRendererProvider.Context context) {
        super.addFeatures(context);
        addLayer(new ArrowLayer(this, context));
    }

    @Override
    public void extractRenderState(T entity, S state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.leftArmPose = getArmPose(entity, HumanoidArm.LEFT);
        state.rightArmPose = getArmPose(entity, HumanoidArm.RIGHT);
        state.hornGlowVisible = !IrisApiCompat.isOnShadowPass() && state.computeIsCasting(entity);
    }

    protected ArmPose getArmPose(T entity, HumanoidArm arm) {
        InteractionHand hand = arm == entity.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack mainHandItem = entity.getItemInHand(hand);

        if (mainHandItem.isEmpty()) {
            return ArmPose.EMPTY;
        }

        if (!entity.swinging && mainHandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(mainHandItem)) {
            return ArmPose.CROSSBOW_HOLD;
        }

        if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
            ItemUseAnimation anim = mainHandItem.getUseAnimation();
            if (anim == ItemUseAnimation.BLOCK) {
                return ArmPose.BLOCK;
            }

            if (anim == ItemUseAnimation.BOW) {
                return ArmPose.BOW_AND_ARROW;
            }

            if (anim == ItemUseAnimation.TRIDENT) {
                return ArmPose.THROW_TRIDENT;
            }

            if (anim == ItemUseAnimation.CROSSBOW) {
                return ArmPose.CROSSBOW_CHARGE;
            }

            if (anim == ItemUseAnimation.SPEAR) {
                return ArmPose.SPEAR;
            }
        }

        @Nullable
        SwingAnimation attack = mainHandItem.get(DataComponents.SWING_ANIMATION);
        if (attack != null && attack.type() == SwingAnimationType.STAB && entity.swinging) {
            return ArmPose.SPEAR;
        }

        return mainHandItem.is(ItemTags.SPEARS) ? ArmPose.SPEAR : ArmPose.ITEM;
    }
}
