package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.minelittlepony.common.util.render.RenderLayerUtil;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class CapeFeature extends CapeFeatureRenderer {

    private final PonyRenderContext<?, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> context;

    public CapeFeature(PonyRenderContext<?, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> context, LoadedEntityModels modelLoader, EquipmentModelLoader equipmentModelLoader) {
        super(context.upcast(), modelLoader, equipmentModelLoader);
        this.context = context;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, PlayerEntityRenderState player, float limbAngle, float limbDistance) {
        ClientPonyModel<PlayerPonyRenderState> model = context.getEquineManager().getModels().body();

        if (!player.invisible && player.capeVisible) {
            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

            Identifier capeTexture = player.skinTextures.capeTexture();

            if (capeTexture == null) {
                return;
            }
            VertexConsumer buffer = plugin.getCapeConsumer(player, vertices, capeTexture);
            if (buffer == null) {
                return;
            }

            boolean[] rendered = {false};
            matrices.push();
            super.render(matrices, layer -> {
                if (RenderLayerUtil.getTexture(layer).orElse(null) == capeTexture) {
                    rendered[0] = true;

                    matrices.translate(0, 0.34F, 0);
                    if (((PlayerPonyRenderState)player).getAttributes().isLyingDown) {
                        matrices.translate(0, -0.05F, 0);
                    }
                    model.transform((PlayerPonyRenderState)player, BodyPart.BODY, matrices);
                    model.getBodyPart(BodyPart.BODY).rotate(matrices);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(85));
                    if (player.baby) {
                        matrices.scale(1.1F, 1.1F, 1.1F);
                    }

                    return buffer;
                }
                return vertices.getBuffer(layer);
            }, light, player, limbAngle, limbDistance);
            matrices.pop();

            if (rendered[0]) {
                plugin.onArmourRendered(player, matrices, vertices, EquipmentSlot.BODY, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.CAPE);
            }
        }
    }
}
