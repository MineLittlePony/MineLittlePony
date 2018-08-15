package com.minelittlepony.render.layer;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.capabilities.IModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerHeldPonyItem<T extends EntityLivingBase> extends AbstractPonyLayer<T> {

    public LayerHeldPonyItem(RenderLivingBase<T> livingPony) {
        super(livingPony);
    }

    protected ItemStack getLeftItem(T entity) {
        boolean main = entity.getPrimaryHand() == EnumHandSide.LEFT;

        return main ? entity.getHeldItemMainhand() : entity.getHeldItemOffhand();
    }

    protected ItemStack getRightItem(T entity) {
        boolean main = entity.getPrimaryHand() == EnumHandSide.RIGHT;

        return main ? entity.getHeldItemMainhand() : entity.getHeldItemOffhand();
    }

    @Override
    public void doPonyRender(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {

        ItemStack left = getLeftItem(entity);
        ItemStack right = getRightItem(entity);

        if (!left.isEmpty() || !right.isEmpty()) {
            ModelBase model = getMainModel();

            pushMatrix();
            if (model instanceof IModel) {
                ((IModel) model).transform(BodyPart.LEGS);
            }

            if (model.isChild) {
                translate(0, 0.625, 0);
                rotate(-20, -1, 0, 0);
                scale(.5, .5, .5);
            }

            renderHeldItem(entity, right, TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            renderHeldItem(entity, left, TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);

            popMatrix();
        }
    }

    private void renderHeldItem(T entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
        if (!drop.isEmpty()) {
            GlStateManager.pushMatrix();
            renderArm(hand);

            if (entity.isSneaking()) {
                GlStateManager.translate(0, 0.2F, 0);
            }

            GlStateManager.rotate(-90, 1, 0, 0);
            GlStateManager.rotate(180, 0, 1, 0);

            preItemRender(entity, drop, transform, hand);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, drop, transform, hand == EnumHandSide.LEFT);
            postItemRender(entity, drop, transform, hand);

            GlStateManager.popMatrix();
        }
    }

    protected void preItemRender(T entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
        GlStateManager.translate(0.0425F, 0.125F, -1);
    }

    protected void postItemRender(T entity, ItemStack drop, TransformType transform, EnumHandSide hand) {
    }

    /**
     * Renders the main arm
     */
    protected void renderArm(EnumHandSide side) {
        this.<ModelBiped>getMainModel().postRenderArm(0.0625F, side);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
