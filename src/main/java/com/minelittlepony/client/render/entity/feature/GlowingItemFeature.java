package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.IUnicorn;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.PonyRenderDispatcher;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;

public class GlowingItemFeature<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends HeldItemFeature<T, M> {

    public GlowingItemFeature(IPonyRenderContext<T, M> context) {
        super(context);
    }

    protected boolean isUnicorn() {
        return MineLittlePony.getInstance().getConfig().tpsmagic.get()
            && getContextModel() instanceof IUnicorn
            && ((IUnicorn)getContextModel()).canCast();
    }

    @Override
    protected void preItemRender(T entity, ItemStack drop, ModelTransformation.Mode transform, Arm arm, MatrixStack stack) {
        super.preItemRender(entity, drop, transform, arm, stack);

        if (!isUnicorn()) {
            return;
        }

        float left = arm == Arm.LEFT ? 1 : -1;

        stack.translate(-0.3F - (0.3F * left), 0.375F, 0.6F);

        UseAction action = drop.getUseAction();

        if ((action == UseAction.SPYGLASS || action == UseAction.BOW) && entity.getItemUseTimeLeft() > 0) {
            Arm main = entity.getMainArm();
            if (entity.getActiveHand() == Hand.OFF_HAND) {
                main = main.getOpposite();
            }
            if (main == arm) {
                if (action == UseAction.SPYGLASS) {
                    Size size = getContextModel().getSize();
                    float x = 0.4F;
                    float z = -0.8F;

                    if (size == Sizes.TALL || size == Sizes.YEARLING) {
                        z += 0.05F;
                    } else if (size == Sizes.FOAL) {
                        x -= 0.1F;
                        z -= 0.1F;
                    }

                    stack.translate(x * left, -0.2, z);
                } else {
                    stack.translate(0, 0.2, -0.6);
                }
            }
        }
    }

    @Override
    protected void postItemRender(T entity, ItemStack drop, ModelTransformation.Mode transform, Arm hand, MatrixStack stack, VertexConsumerProvider renderContext) {
        if (isUnicorn()) {
            PonyRenderDispatcher.getInstance().getMagicRenderer().renderItemGlow(entity, drop, transform, hand, ((IUnicorn)getContextModel()).getMagicColor(), stack, renderContext);
        }
    }
}
