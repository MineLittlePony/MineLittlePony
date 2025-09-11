package com.minelittlepony.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class LevitatingItemRenderer {
    public static VertexConsumerProvider getProvider(Pony pony, VertexConsumerProvider provider) {
        final int color = pony.metadata().glowColor();
        return MagicGlow.getProvider(color, provider, new MatrixStack());
    }

    /**
     * Renders an item with a magical overlay.
     */
    public boolean renderItem(ItemRenderer itemRenderer, @Nullable LivingEntity entity, ItemStack stack, ItemDisplayContext mode,
            MatrixStack matrices, VertexConsumerProvider vertices, @Nullable World world,
            int light, int overlay, int seed, Operation<Void> original) {

        if (entity == null || !(mode.isFirstPerson()
                || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || mode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
            ) {
            return false;
        }

        var context = MineLittlePony.getInstance().getRenderDispatcher().getPonyRenderer(entity);
        if (context == null) {
            return false;
        }

        var state = context.getAndUpdateRenderState(entity, MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false));

        matrices.push();

        boolean doMagic = (mode.isFirstPerson() ? PonyConfig.getInstance().fpsmagic : PonyConfig.getInstance().tpsmagic).get() && state.hasMagicGlow();

        if (doMagic && mode.isFirstPerson()) {
            setupPerspective(state, stack, mode.isLeftHand(), matrices);
        }

        original.call(itemRenderer, entity, stack, mode, matrices, vertices, world, light, overlay, seed);

        if (doMagic) {
            VertexConsumerProvider interceptedContext = MagicGlow.getProvider(state.pony.metadata().glowColor(), vertices, matrices);

            if (stack.hasGlint()) {
                stack = stack.copy();
                stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
            }

            float driftStrength = 0.002F;
            float xDrift = MathHelper.sin(state.age / 20F) * driftStrength;
            float zDrift = MathHelper.cos((state.age + 20) / 20F) * driftStrength;

            float scale = 1.1F + (MathHelper.sin(state.age / 20F) + 1) * driftStrength;
            matrices.scale(scale, scale, scale);
            matrices.translate(0.015F + xDrift, 0.01F, 0.01F + zDrift);

            original.call(itemRenderer, entity, stack, mode, matrices, interceptedContext, world, light, OverlayTexture.DEFAULT_UV, seed);
            matrices.scale(scale, scale, scale);
            matrices.translate(-0.03F - xDrift, -0.02F, -0.02F - zDrift);
            original.call(itemRenderer, entity, stack, mode, matrices, interceptedContext, world, light, OverlayTexture.DEFAULT_UV, seed);
        }

        matrices.pop();
        return true;
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    public static void setupPerspective(PonyRenderState state, ItemStack item, boolean left, MatrixStack stack) {
        UseAction action = item.getUseAction();

        boolean doNormal = state.itemUseTime <= 0 || action == UseAction.NONE || (action == UseAction.CROSSBOW && CrossbowItem.isCharged(item));

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            int sign = left ? 1 : -1;
            float ticks = state.age * sign;

            float floatAmount = -(float)Math.sin(ticks / 9F) / 40F;
            float driftAmount = -(float)Math.cos(ticks / 6F) / 40F;

            boolean handHeldTool =
                       action == UseAction.BOW
                    || action == UseAction.CROSSBOW
                    || action == UseAction.BLOCK
                    || item.contains(DataComponentTypes.TOOL)
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
