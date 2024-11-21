package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.item.equipment.EquipmentModel.LayerType;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.*;

public class PonifiedEquipmentRenderer extends EquipmentRenderer {

    private final EquipmentModelLoader modelLoader;

    public PonifiedEquipmentRenderer(EquipmentModelLoader modelLoader) {
        super(modelLoader, MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE));
        this.modelLoader = modelLoader;
    }

    public <S extends PonyRenderState, V extends PonyArmourModel<S>> void render(
            EquipmentSlot equipmentSlot,
            EquipmentModel.LayerType layerType,
            Identifier modelId,
            Models<? extends PonyModel<S>> models,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light
        ) {
        this.render(equipmentSlot, layerType, modelId, models, stack, matrices, vertexConsumers, light, null);
    }

    public <S extends PonyRenderState, V extends PonyArmourModel<S>> void render(
            EquipmentSlot equipmentSlot,
            EquipmentModel.LayerType layerType,
            Identifier modelId,
            Models<? extends PonyModel<S>> models,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            @Nullable Identifier texture
        ) {

        List<EquipmentModel.Layer> layers = modelLoader.get(modelId).getLayers(layerType);
        if (!layers.isEmpty()) {
            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();
            int i = stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, 0) : 0;
            float armorAlpha = plugin.getArmourAlpha(equipmentSlot, layerType);
            boolean hasGlint = plugin.getGlintAlpha(equipmentSlot, stack) > 0 && stack.hasGlint();

            Set<V> drawnModels = new HashSet<>();

            if (armorAlpha > 0) {
                for (EquipmentModel.Layer layer : layers) {
                    int j = getDyeColor(layer, i);
                    if (j != 0) {
                        ArmourTexture armorTexture = plugin.getTextureLookup().getTexture(stack, layerType == LayerType.HUMANOID_LEGGINGS ? ArmourLayer.INNER : ArmourLayer.OUTER, layer);
                        Identifier layerTexture = layer.usePlayerTexture() && texture != null
                            ? texture
                            : armorTexture.texture();

                        VertexConsumer armorConsumer = plugin.getArmourConsumer(equipmentSlot, vertexConsumers, layerTexture, layerType);
                        if (armorConsumer != null) {
                            ArmourVariant variant = layer.usePlayerTexture() ? ArmourVariant.NORMAL : armorTexture.variant();
                            models.getArmourModel(stack, null, variant).ifPresent(model -> {
                                VertexConsumer glintConsumer = hasGlint ? plugin.getGlintConsumer(equipmentSlot, vertexConsumers, layerType) : null;
                                model.render(matrices, glintConsumer != null ? VertexConsumers.union(plugin.getGlintConsumer(equipmentSlot, vertexConsumers, layerType), armorConsumer) : armorConsumer, light, OverlayTexture.DEFAULT_UV, j);
                            });
                        }
                    }
                }
            }

            ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
            if (armorTrim != null && plugin.getTrimAlpha(equipmentSlot, armorTrim, layerType) > 0) {
                VertexConsumer trimConsumer = plugin.getTrimConsumer(equipmentSlot, vertexConsumers, armorTrim, layerType, modelId);
                if (trimConsumer != null) {
                    drawnModels.forEach(model -> {
                        model.render(matrices, trimConsumer, light, OverlayTexture.DEFAULT_UV);
                    });
                }
            }
        }
    }

    private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
        Optional<EquipmentModel.Dyeable> optional = layer.dyeable();
        if (optional.isPresent()) {
            int i = (Integer)((EquipmentModel.Dyeable)optional.get()).colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
            return dyeColor != 0 ? dyeColor : i;
        } else {
            return -1;
        }
    }

}
