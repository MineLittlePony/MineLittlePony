package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import org.jetbrains.annotations.Nullable;

public class ElytraFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends FeatureRenderer<S, M> {
    private final PonyElytra<S> model = ModelType.ELYTRA.createModel();

    private final FeatureRendererContext<S, M> context;
    private final EquipmentRenderer equipmentRenderer;

    public ElytraFeature(FeatureRendererContext<S, M> context, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.context = context;
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(state, EquipmentSlot.CHEST, EquipmentModel.LayerType.WINGS, ArmourRendererPlugin.ArmourType.ELYTRA)) {
            EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);

            if (equippable != null && !equippable.assetId().isEmpty()) {
                RegistryKey<EquipmentAsset> equipmentModel = equippable.assetId().get();

                float alpha = plugin.getElytraAlpha(stack, model, state);
                if (alpha <= 0) {
                    return;
                }

                matrices.push();
                model.setAngles(state);
                preRenderCallback(state, matrices);
                equipmentRenderer.render(EquipmentModel.LayerType.WINGS, equipmentModel, model, state, stack, matrices, queue, light, getElytraTexture(state), state.outlineColor, 0);
                matrices.pop();
            }
        }

        plugin.onArmourRendered(state, matrices, queue, EquipmentSlot.CHEST, EquipmentModel.LayerType.WINGS, ArmourRendererPlugin.ArmourType.ELYTRA);
    }

    @SuppressWarnings("unchecked")
    protected void preRenderCallback(S state, MatrixStack stack) {
        if (state instanceof PonyRenderState ponyState && context instanceof PonyRenderContext context) {
            stack.translate(0, 0.45F, 0);
            ((ClientPonyModel<PonyRenderState>)context.getEquineManager().lookupModel(state).body()).transform(ponyState, BodyPart.BODY, stack);
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(80));
        }
    }

    @Nullable
    protected Identifier getElytraTexture(S state) {
        if (state instanceof PlayerEntityRenderState playerState) {
            SkinTextures textures = playerState.skinTextures;

            if (textures.elytra() != null) {
                return textures.elytra().texturePath();
            }

            if (textures.cape() != null && playerState.capeVisible) {
                return textures.cape().texturePath();
            }
        }

        return null;
    }
}
