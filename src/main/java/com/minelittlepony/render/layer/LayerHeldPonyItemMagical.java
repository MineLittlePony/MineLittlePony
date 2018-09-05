package com.minelittlepony.render.layer;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.model.capabilities.IModelUnicorn;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

public class LayerHeldPonyItemMagical<T extends EntityLivingBase> extends LayerHeldPonyItem<T> {

    public LayerHeldPonyItemMagical(RenderLivingBase<T> livingPony) {
        super(livingPony);
    }

    protected boolean isUnicorn() {
        return getMainModel() instanceof IModelUnicorn && this.<IModelUnicorn>getPonyModel().canCast();
    }

    @Override
    protected void preItemRender(T entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
        if (isUnicorn()) {
            GlStateManager.translate(hand == EnumHandSide.LEFT ? -0.6F : 0, 0.5F, -0.3F);
        } else {
            super.preItemRender(entity, drop, transform, hand);
        }
    }

    @Override
    protected void postItemRender(T entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
        if (isUnicorn()) {
            MineLittlePony.getRenderManager().getMagicRenderer().renderItemGlow(entity, drop, transform, hand, this.<IModelUnicorn>getPonyModel().getMagicColor());
        }
    }

    @Override
    protected void renderArm(EnumHandSide side) {
        if (isUnicorn()) {
            this.<IModelUnicorn>getPonyModel().getUnicornArmForSide(side).postRender(0.0625F);
        } else {
            super.renderArm(side);
        }
    }
}
