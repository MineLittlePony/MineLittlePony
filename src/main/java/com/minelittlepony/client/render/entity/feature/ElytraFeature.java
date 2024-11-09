package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.PonyPosture;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.PonyElytra;
import com.minelittlepony.client.model.armour.ArmourLayer;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class ElytraFeature<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends EntityModel<? super S> & PonyModel<S>
    > extends AbstractPonyFeature<S, M> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/elytra.png");

    private final PonyElytra<T> model = ModelType.ELYTRA.createModel();

    public ElytraFeature(PonyRenderContext<T, S, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, S entity, float limbAngle, float limbDistance) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(entity, EquipmentSlot.CHEST, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.ELYTRA)) {
            float alpha = plugin.getElytraAlpha(stack, model, entity);
            if (alpha <= 0) {
                return;
            }

            VertexConsumer vertexConsumer = plugin.getElytraConsumer(stack, model, entity, provider, getElytraTexture(entity));
            if (vertexConsumer == null) {
                return;
            }

            matrices.push();
            preRenderCallback(matrices);

            model.setAngles(entity);
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, (Colors.WHITE & 0xFFFFFF) | (int)(alpha * 255) << 24);

            matrices.pop();
        }

        plugin.onArmourRendered(entity, matrices, provider, EquipmentSlot.BODY, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.ELYTRA);
    }

    protected void preRenderCallback(S state, MatrixStack stack) {
        M body = getModelWrapper().body();
        stack.translate(0, state.riderOffset, 0.125);
        body.transform(state, BodyPart.BODY, stack);
    }

    protected Identifier getElytraTexture(T entity) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            SkinTextures textures = player.getSkinTextures();

            if (textures.elytraTexture() != null) {
                return textures.elytraTexture();
            }

            if (textures.capeTexture() != null && player.isPartVisible(PlayerModelPart.CAPE)) {
                return textures.capeTexture();
            }
        }

        return TEXTURE;
    }
}
