package com.minelittlepony.client.render;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.HornedPonyModel;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.render.RenderLayerUtil;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class LevitatingItemRenderer {
    private VertexConsumerProvider getProvider(Pony pony, VertexConsumerProvider provider) {
        final int color = pony.metadata().glowColor();
        return layer -> {
            if (layer.getVertexFormat() != VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL) {
                return provider.getBuffer(layer);
            }
            return provider.getBuffer(MagicGlow.getColoured(RenderLayerUtil.getTexture(layer).orElse(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE), color));
        };
    }

    /**
     * Renders an item with a magical overlay.
     */
    public void renderItem(ItemRenderer itemRenderer, @Nullable LivingEntity entity, ItemStack stack, ModelTransformationMode mode, boolean left, MatrixStack matrix, VertexConsumerProvider renderContext, @Nullable World world, int lightUv, int posLong) {

        if (entity != null && (mode.isFirstPerson()
                || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND
                || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)
            ) {
            var context = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(entity);
            if (context != null) {
                var state = context.getAndUpdateRenderState(entity, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false));

                matrix.push();

                boolean doMagic = (mode.isFirstPerson() ? PonyConfig.getInstance().fpsmagic : PonyConfig.getInstance().tpsmagic).get() && state.hasMagicGlow();

                if (doMagic && mode.isFirstPerson()) {
                    setupPerspective(itemRenderer, entity, stack, left, matrix);
                }

                itemRenderer.renderItem(entity, stack, mode, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);

                if (doMagic) {
                    VertexConsumerProvider interceptedContext = getProvider(state.pony, renderContext);

                    if (stack.hasGlint()) {
                        stack = stack.copy();
                        stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
                    }

                    float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false) + entity.age;


                    float driftStrength = 0.002F;
                    float xDrift = MathHelper.sin(tickDelta / 20F) * driftStrength;
                    float zDrift = MathHelper.cos((tickDelta + 20) / 20F) * driftStrength;

                    float scale = 1.1F + (MathHelper.sin(tickDelta / 20F) + 1) * driftStrength;
                    matrix.scale(scale, scale, scale);
                    matrix.translate(0.015F + xDrift, 0.01F, 0.01F + zDrift);

                    itemRenderer.renderItem(entity, stack, mode, left, matrix, interceptedContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);
                    matrix.scale(scale, scale, scale);
                    matrix.translate(-0.03F - xDrift, -0.02F, -0.02F - zDrift);
                    itemRenderer.renderItem(entity, stack, mode, left, matrix, interceptedContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);
                }

                matrix.pop();
                return;
            }
        }

        itemRenderer.renderItem(entity, stack, mode, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    private void setupPerspective(ItemRenderer renderer, LivingEntity entity, ItemStack item, boolean left, MatrixStack stack) {
        UseAction action = item.getUseAction();

        boolean doNormal = entity.getItemUseTime() <= 0 || action == UseAction.NONE || (action == UseAction.CROSSBOW && CrossbowItem.isCharged(item));

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            int sign = left ? 1 : -1;
            float ticks = entity.age * sign;

            float floatAmount = -(float)Math.sin(ticks / 9F) / 40F;
            float driftAmount = -(float)Math.cos(ticks / 6F) / 40F;

            boolean handHeldTool =
                       action == UseAction.BOW
                    || action == UseAction.CROSSBOW
                    || action == UseAction.BLOCK
                    || item.getItem() instanceof ToolItem
                    || item.getItem() instanceof RangedWeaponItem
                    || PonyConfig.getInstance().forwardHoldingItems.get().contains(Registries.ITEM.getId(item.getItem()));

            float distanceChange = handHeldTool ? -0.3F : -0.6F;

            stack.translate(
                    driftAmount - floatAmount / 4F + distanceChange / 1.5F * sign,
                    floatAmount,
                    distanceChange);

            if (!handHeldTool) { // bows have to point forwards
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(sign * -60 + floatAmount));
                stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sign * 30 + driftAmount));
            }
        }
    }
}
