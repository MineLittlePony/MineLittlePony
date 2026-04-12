package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;

import org.jetbrains.annotations.Nullable;

public class ElytraFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends RenderLayer<S, M> {
    private final PonyElytra model = ModelType.ELYTRA.createModel();

    private final RenderLayerParent<S, M> context;
    private final EquipmentLayerRenderer equipmentRenderer;

    public ElytraFeature(RenderLayerParent<S, M> context, EquipmentLayerRenderer equipmentRenderer) {
        super(context);
        this.context = context;
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, S state, float limbAngle, float limbDistance) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(state, EquipmentSlot.CHEST, EquipmentClientInfo.LayerType.WINGS, ArmourRendererPlugin.ArmourType.ELYTRA)) {
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);

            if (equippable != null && !equippable.assetId().isEmpty()) {
                ResourceKey<EquipmentAsset> equipmentModel = equippable.assetId().get();

                float alpha = plugin.getElytraAlpha(stack, model, state);
                if (alpha <= 0) {
                    return;
                }

                matrices.pushPose();
                model.setupAnim(state);
                preRenderCallback(state, matrices);
                equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.WINGS, equipmentModel, model, state, stack, matrices, queue, light, getElytraTexture(state), state.outlineColor, 0);
                matrices.popPose();
            }
        }

        plugin.onArmourRendered(state, matrices, queue, EquipmentSlot.CHEST, EquipmentClientInfo.LayerType.WINGS, ArmourRendererPlugin.ArmourType.ELYTRA);
    }

    @SuppressWarnings("unchecked")
    protected void preRenderCallback(S state, PoseStack stack) {
        if (state instanceof PonyRenderState ponyState && context instanceof PonyRenderContext context) {
            stack.translate(0, 0.45F, 0);
            context.getEquineManager().lookupModel(state).body().transform(ponyState, BodyPart.BODY, stack);
            stack.mulPose(Axis.XP.rotationDegrees(80));
        }
    }

    @Nullable
    protected Identifier getElytraTexture(S state) {
        if (state instanceof AvatarRenderState playerState) {
            PlayerSkin textures = playerState.skin;

            if (textures.elytra() != null) {
                return textures.elytra().texturePath();
            }

            if (textures.cape() != null && playerState.showCape) {
                return textures.cape().texturePath();
            }
        }

        return null;
    }
}
