package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class CapeFeature extends AbstractPonyFeature<PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> {
    private final PlayerCapeModel model;
    private final EquipmentAssetManager equipmentAssets;

    public CapeFeature(PonyRenderContext<?, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> context, EntityModelSet entityModels, EquipmentAssetManager equipmentAssets) {
        super(context);
        this.model = new PonyCapeModel(entityModels.bakeLayer(ModelLayers.PLAYER_CAPE));
        this.equipmentAssets = equipmentAssets;
    }

    static class PonyCapeModel extends PlayerCapeModel {
        private final ModelPart cape = body.getChild("cape");

        public PonyCapeModel(ModelPart root) {
            super(root);
        }

        @Override
        public void setupAnim(final AvatarRenderState state) {
            super.setupAnim(state);
            body.resetPose();
            cape.x = 0;
            cape.y = 18;
            cape.z = state.isPassenger ? -6 : 0;
            cape.xRot -= 90 * Mth.DEG_TO_RAD;
        }
    }

    private boolean hasLayer(ItemStack stack, EquipmentClientInfo.LayerType layerType) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null
                && !equippable.assetId().isEmpty()
                && !equipmentAssets.get(equippable.assetId().get()).getLayers(layerType).isEmpty();
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, PlayerPonyRenderState state, float xRot, float yRot) {
        if (!state.isInvisible && state.showCape) {
            PlayerSkin skinTextures = state.skin;
            if (skinTextures.cape() != null && !hasLayer(state.chestEquipment, EquipmentClientInfo.LayerType.WINGS)) {
                ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

                RenderType capeLayer = plugin.getCapeLayer(state, skinTextures.cape().texturePath());
                if (capeLayer != null) {
                    matrices.pushPose();


                    if (((PlayerPonyRenderState)state).attributes.isSleeping) {
                        matrices.translate(0, 0, 0.4F);
                    } else {
                        matrices.translate(0, 0.44F, 0);
                    }
                    var model = lookupModel(state);

                    model.body().transformAccessory((PlayerPonyRenderState)state, BodyPart.BACK, matrices);
                    matrices.translate(0, -19/16F, 0);

                    if (hasLayer(state.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
                        matrices.translate(0.0F, -0.07F, 0.06875F);
                    }
                    if (state.isBaby) {
                        matrices.scale(1.1F, 1.1F, 1.1F);
                    }

                    queue.submitModel(this.model, state, matrices, capeLayer, light, OverlayTexture.NO_OVERLAY, state.outlineColor);

                    plugin.onArmourRendered(state, matrices, queue, EquipmentSlot.BODY, EquipmentClientInfo.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.CAPE);
                    matrices.popPose();
                }
            }
        }
    }
}
