package com.minelittlepony.client.render;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.common.util.Color;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class LevitatingItemRenderer {

    private static int tint;
    private static boolean enabled;

    public static boolean isEnabled() {
        return enabled;
    }

    public static RenderLayer getRenderLayer(Identifier texture) {
        if (!isEnabled()) {
            return RenderLayer.getEntityTranslucent(texture);
        }
        return MagicGlow.getTintedTexturedLayer(texture, Color.r(tint), Color.g(tint), Color.b(tint), 0.8F);
    }

    public static RenderLayer getRenderLayer() {
        return getRenderLayer(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
    }

    /**
     * Renders a magical overlay over an item in third person.
     */
    public void renderItemGlow(LivingEntity entity, ItemStack drop, ModelTransformation.Mode transform, Arm hand, int glowColor, MatrixStack stack, VertexConsumerProvider renderContext) {
        setColor(glowColor);
        stack.push();

        ItemRenderer renderItem = MinecraftClient.getInstance().getItemRenderer();

        stack.scale(1.1F, 1.1F, 1.1F);

        stack.translate(0.01F, 0.01F, 0.01F);

        renderItem.renderItem(entity, drop, transform, hand == Arm.LEFT, stack, renderContext, entity.world, 0x0F00F0, OverlayTexture.DEFAULT_UV, 0);
        stack.translate(-0.02F, -0.02F, -0.02F);
        renderItem.renderItem(entity, drop, transform, hand == Arm.LEFT, stack, renderContext, entity.world, 0x0F00F0, OverlayTexture.DEFAULT_UV, 0);

        stack.pop();
        unsetColor();
    }

    private void setColor(int glowColor) {
        enabled = true;
        tint = glowColor;
    }

    private void unsetColor() {
        enabled = false;
    }

    /**
     * Renders an item in first person optionally with a magical overlay.
     */
    public void renderItemInFirstPerson(ItemRenderer itemRenderer, @Nullable LivingEntity entity, ItemStack stack, ModelTransformation.Mode mode, boolean left, MatrixStack matrix, VertexConsumerProvider renderContext, @Nullable World world, int lightUv, int posLong) {

        if (entity instanceof PlayerEntity && (mode.isFirstPerson() || mode == ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND)) {

            IPony pony = MineLittlePony.getInstance().getManager().getPony((PlayerEntity)entity);

            matrix.push();

            boolean doMagic = MineLittlePony.getInstance().getConfig().fpsmagic.get() && pony.getMetadata().hasMagic();

            if (doMagic) {
                setupPerspective(itemRenderer, entity, stack, left, matrix);
            }

            itemRenderer.renderItem(entity, stack, mode, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);

            if (doMagic) {
                setColor(pony.getMetadata().getGlowColor());

                matrix.scale(1.1F, 1.1F, 1.1F);

                matrix.translate(0.015F, 0.01F, 0.01F);

                itemRenderer.renderItem(entity, stack, mode, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);
                matrix.translate(-0.03F, -0.02F, -0.02F);
                itemRenderer.renderItem(entity, stack, mode, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);

                unsetColor();
            }

            matrix.pop();
        } else {
            itemRenderer.renderItem(entity, stack, mode, left, matrix, renderContext, world, lightUv, OverlayTexture.DEFAULT_UV, posLong);
        }
    }

    /**
     * Moves held items to look like they're floating in the player's field.
     */
    private void setupPerspective(ItemRenderer renderer, LivingEntity entity, ItemStack item, boolean left, MatrixStack stack) {
        UseAction action = item.getUseAction();

        boolean doNormal = entity.getItemUseTime() <= 0 || action == UseAction.NONE || action == UseAction.CROSSBOW;

        if (doNormal) { // eating, blocking, and drinking are not transformed. Only held items.
            int sign = left ? 1 : -1;
            float ticks = entity.age * sign;

            float floatAmount = -(float)Math.sin(ticks / 9F) / 40F;
            float driftAmount = -(float)Math.cos(ticks / 6F) / 40F;

            boolean handHeldTool =
                       action == UseAction.BOW
                    || action == UseAction.CROSSBOW
                    || action == UseAction.BLOCK;

            float distanceChange = handHeldTool ? -0.3F : -0.6F;

            stack.translate(
                    driftAmount - floatAmount / 4F + distanceChange / 1.5F * sign,
                    floatAmount,
                    distanceChange);

            if (!handHeldTool) { // bows have to point forwards
                stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(sign * -60 + floatAmount));
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(sign * 30 + driftAmount));
            }
        }
    }
}
