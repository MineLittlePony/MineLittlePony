package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.*;

public class PonifiedEquipmentRenderer extends EquipmentRenderer {
    private static final int TRANSPARENT = 0;

    private final EquipmentModelLoader modelLoader;

    private @Nullable Set<EntityModel<?>> drawnModels;

    public PonifiedEquipmentRenderer(EquipmentModelLoader modelLoader) {
        super(modelLoader, MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE));
        this.modelLoader = modelLoader;
    }

    public <S extends PonyRenderState, V extends ClientPonyModel<S>> void render(
            EquipmentSlot equipmentSlot,
            EquipmentModel.LayerType layerType,
            Identifier modelId,
            S entity,
            Models<V> models,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light
        ) {
        render(equipmentSlot, layerType, modelId, entity, models, stack, matrices, vertexConsumers, light, null);
    }

    public <S extends PonyRenderState, V extends ClientPonyModel<S>> void render(
            EquipmentSlot equipmentSlot,
            EquipmentModel.LayerType layerType,
            Identifier modelId,
            S entity,
            Models<V> models,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light,
            @Nullable Identifier texture
        ) {
        List<EquipmentModel.Layer> layers = modelLoader.get(modelId).getLayers(layerType);
        if (!layers.isEmpty()) {
            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();
            int defaultColor = stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, 0) : 0;
            float armorAlpha = plugin.getArmourAlpha(equipmentSlot, layerType);
            boolean hasGlint = plugin.getGlintAlpha(equipmentSlot, stack) > 0;

            if (armorAlpha > 0) {
                for (EquipmentModel.Layer layer : layers) {
                    int dyeColor = getDyeColor(layer, defaultColor);
                    if (dyeColor != TRANSPARENT) {
                        ArmourTexture armorTexture = plugin.getTextureLookup().getTexture(stack, layerType, layer);
                        Identifier layerTexture = layer.usePlayerTexture() && texture != null ? texture : armorTexture.texture();

                        @Nullable
                        VertexConsumer armorConsumer = getArmorVertexConsumer(plugin, equipmentSlot, vertices, layerTexture, layerType, hasGlint);
                        if (armorConsumer != null) {
                            ArmourVariant variant = layer.usePlayerTexture() ? ArmourVariant.LEGACY : armorTexture.variant();
                            AbstractPonyModel<?> model = models.getArmourModel(stack, layerType, variant);
                            if (model != null) {
                                model.setAngles(entity);
                                models.body().copyTransforms(model);
                                if (setVisibilities(model, equipmentSlot, layerType)) {
                                    model.render(matrices, armorConsumer, light, OverlayTexture.DEFAULT_UV, dyeColor);
                                    if (drawnModels == null) {
                                        drawnModels = new HashSet<>();
                                    }
                                    drawnModels.add(model);
                                }
                            }
                        }
                    }
                }
            }

            if (drawnModels != null) {
                @Nullable
                ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
                @Nullable
                VertexConsumer trimConsumer = armorTrim != null && plugin.getTrimAlpha(equipmentSlot, armorTrim, layerType) > 0 ? plugin.getTrimConsumer(equipmentSlot, vertices, armorTrim, layerType, modelId) : null;
                if (trimConsumer != null) {
                    for (EntityModel<?> model : drawnModels) {
                        model.render(matrices, trimConsumer, light, OverlayTexture.DEFAULT_UV);
                    }
                }
            }

            drawnModels = null;
        }
    }

    @Nullable
    private static VertexConsumer getArmorVertexConsumer(ArmourRendererPlugin plugin, EquipmentSlot slot, VertexConsumerProvider provider, Identifier texture, EquipmentModel.LayerType layerType, boolean glint) {
        VertexConsumer armorConsumer = plugin.getArmourConsumer(slot, provider, texture, layerType);
        if (armorConsumer != null) {
            VertexConsumer glintConsumer = glint ? plugin.getGlintConsumer(slot, provider, layerType) : null;
            if (glintConsumer != null) {
                return VertexConsumers.union(glintConsumer, armorConsumer);
            }
        }
        return armorConsumer;
    }

    private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
        Optional<EquipmentModel.Dyeable> optional = layer.dyeable();
        if (optional.isPresent()) {
            int i = optional.get().colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
            return dyeColor != TRANSPARENT ? dyeColor : i;
        }
        return Colors.WHITE;
    }

    public static boolean setVisibilities(AbstractPonyModel<?> model, EquipmentSlot slot, EquipmentModel.LayerType layer) {
        model.setVisible(false);
        model.body.visible = slot == EquipmentSlot.CHEST;
        model.head.visible = layer == EquipmentModel.LayerType.HUMANOID && slot == EquipmentSlot.HEAD;

        if (slot == (layer == EquipmentModel.LayerType.HUMANOID ? EquipmentSlot.FEET : EquipmentSlot.LEGS)) {
            model.rightArm.visible = true;
            model.leftArm.visible = true;
            model.rightLeg.visible = true;
            model.leftLeg.visible = true;
            return true;
        }

        return model.head.visible || model.body.visible;
    }
}
