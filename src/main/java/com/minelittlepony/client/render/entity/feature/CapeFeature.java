package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PlayerPonyRenderState;

import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class CapeFeature extends CapeFeatureRenderer {

    private final PonyRenderContext<?, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> context;

    private final EquipmentModelLoader equipmentModelLoader;

    public CapeFeature(PonyRenderContext<?, PlayerPonyRenderState, ClientPonyModel<PlayerPonyRenderState>> context, LoadedEntityModels modelLoader, EquipmentModelLoader equipmentModelLoader) {
        super(context.upcast(), modelLoader, equipmentModelLoader);
        this.context = context;
        this.equipmentModelLoader = equipmentModelLoader;
    }

    private boolean hasCustomModelForLayer(ItemStack stack, EquipmentModel.LayerType layerType) {
        EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
        return equippable != null
                && !equippable.assetId().isEmpty()
                && !equipmentModelLoader.get(equippable.assetId().get()).getLayers(layerType).isEmpty();
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
            if (!state.invisible && state.capeVisible) {
                SkinTextures skinTextures = state.skinTextures;
                if (skinTextures.cape() != null && !hasCustomModelForLayer(state.equippedChestStack, EquipmentModel.LayerType.WINGS)) {
                    ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

                    RenderLayer capeLayer = plugin.getCapeLayer(state, skinTextures.cape().texturePath());
                    if (capeLayer != null) {

                        matrixStack.push();
                        if (hasCustomModelForLayer(state.equippedChestStack, EquipmentModel.LayerType.HUMANOID)) {
                            matrixStack.translate(0.0F, -0.053125F, 0.06875F);
                        }

                        ClientPonyModel<PlayerPonyRenderState> model = context.lookupModel(state).body();

                        matrixStack.translate(0, 0.34F, 0);
                        model.transform((PlayerPonyRenderState)state, BodyPart.BODY, matrixStack);
                        model.body.applyTransform(matrixStack);
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(85 - model.body.pitch * MathHelper.DEGREES_PER_RADIAN));
                        if (state.baby) {
                            matrixStack.scale(1.1F, 1.1F, 1.1F);
                        }

                        queue.submitModel(model, state, matrixStack, capeLayer, light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);

                        matrixStack.pop();

                        plugin.onArmourRendered(state, matrixStack, queue, EquipmentSlot.BODY, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.CAPE);
                    }
                }
            }
        }
}
