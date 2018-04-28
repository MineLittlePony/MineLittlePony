package com.minelittlepony.render.layer;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
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

    @Override
    public void doPonyRender(T entity, float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale) {

        boolean mainRight = entity.getPrimaryHand() == EnumHandSide.RIGHT;

        ItemStack itemMain = entity.getHeldItemMainhand();
        ItemStack itemOff = entity.getHeldItemOffhand();

        ItemStack left = mainRight ? itemOff : itemMain;
        ItemStack right = mainRight ? itemMain : itemOff;

        if (!left.isEmpty() || !right.isEmpty()) {
            ModelBase model = getRenderer().getMainModel();

            pushMatrix();
            if (model instanceof AbstractPonyModel) {
                ((AbstractPonyModel) model).transform(BodyPart.LEGS);
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
        ((ModelBiped) getRenderer().getMainModel()).postRenderArm(0.0625F, side);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
