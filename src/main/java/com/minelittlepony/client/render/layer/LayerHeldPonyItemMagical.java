package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.PonyRenderManager;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.IPonyModel;
import com.minelittlepony.model.IUnicorn;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AbsoluteHand;

public class LayerHeldPonyItemMagical<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends LayerHeldPonyItem<T, M> {

    public LayerHeldPonyItemMagical(IPonyRender<T, M> context) {
        super(context);
    }

    protected boolean isUnicorn() {
        return getModel() instanceof IUnicorn<?> && ((IUnicorn<?>)getModel()).canCast();
    }

    @Override
    protected void preItemRender(T entity, ItemStack drop, ModelTransformation.Type transform, AbsoluteHand hand) {
        if (isUnicorn()) {
            GlStateManager.translatef(hand == AbsoluteHand.LEFT ? -0.6F : 0, 0.5F, -0.3F);
        } else {
            super.preItemRender(entity, drop, transform, hand);
        }
    }

    @Override
    protected void postItemRender(T entity, ItemStack drop, ModelTransformation.Type transform, AbsoluteHand hand) {
        if (isUnicorn()) {
            PonyRenderManager.getInstance().getMagicRenderer().renderItemGlow(entity, drop, transform, hand, ((IUnicorn<?>)getModel()).getMagicColor());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void renderArm(AbsoluteHand side) {
        if (isUnicorn()) {
            ((IUnicorn<PonyRenderer>)getModel()).getUnicornArmForSide(side).applyTransform(0.0625F);
        } else {
            super.renderArm(side);
        }
    }
}
