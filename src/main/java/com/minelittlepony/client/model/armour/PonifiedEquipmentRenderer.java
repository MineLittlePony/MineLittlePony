package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.ColorHelper;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.util.ResourceUtil;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PonifiedEquipmentRenderer extends EquipmentRenderer {
    private static final int TRANSPARENT = 0;

    private final EquipmentModelLoader modelLoader;

    private @Nullable Set<EntityModel<?>> drawnModels;
    private final Function<LayerTextureKey, Identifier> layerTextures;
    private final Function<TrimSpriteKey, Sprite> trimSprites;
    private final BiFunction<EquipmentModel.LayerType, Identifier, Identifier> ponifier = Util.memoize((type, texture) -> {
        return ResourceUtil.verifyTexture(texture.withPath(p -> p.replace(type.asString(), "ponified_" + type.asString()))).orElse(texture);
    });

    public PonifiedEquipmentRenderer(EquipmentModelLoader modelLoader) {
        super(modelLoader, MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE));
        this.modelLoader = modelLoader;
        var armorTrimsAtlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
        layerTextures = Util.memoize(key -> key.layer.getFullTextureId(key.layerType));
        trimSprites = Util.memoize(key -> armorTrimsAtlas.getSprite(key.getTexture()));
    }

    record LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {}

    record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> equipmentAssetId) {
        public Identifier getTexture() {
            return this.trim.getTextureId(this.layerType.getTrimsDirectory(), this.equipmentAssetId);
        }
    }

    @Override
    public void render(
            EquipmentModel.LayerType layerType,
            RegistryKey<EquipmentAsset> assetKey,
            Model model,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            @Nullable Identifier texture
        ) {
        // extures/entity/equipment/strider_saddle/saddle.png
        List<EquipmentModel.Layer> layers = modelLoader.get(assetKey).getLayers(layerType);
        if (!layers.isEmpty()) {
            int i = DyedColorComponent.getColor(stack, 0);
            boolean glint = stack.hasGlint();

            for (EquipmentModel.Layer layer : layers) {
                int color = getDyeColor(layer, i);
                if (color != 0) {
                    model.render(matrices, ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(
                        ponifier.apply(layerType, layer.usePlayerTexture() && texture != null ? texture : layerTextures.apply(new LayerTextureKey(layerType, layer)))
                    ), glint), light, OverlayTexture.DEFAULT_UV, color);
                    glint = false;
                }
            }

            ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
            if (armorTrim != null) {
                Sprite sprite = trimSprites.apply(new TrimSpriteKey(armorTrim, layerType, assetKey));
                model.render(matrices, sprite.getTextureSpecificVertexConsumer(
                        vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims(armorTrim.pattern().value().decal()))
                ), light, OverlayTexture.DEFAULT_UV);
            }
        }
    }

    public <S extends PonyRenderState, V extends ClientPonyModel<S>> void render(
            EquipmentSlot equipmentSlot,
            EquipmentModel.LayerType layerType,
            RegistryKey<EquipmentAsset> assetId,
            S entity,
            Models<V> models,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light
        ) {
        render(equipmentSlot, layerType, assetId, entity, models, stack, matrices, vertexConsumers, light, null);
    }

    public <S extends PonyRenderState, V extends ClientPonyModel<S>> void render(
            EquipmentSlot equipmentSlot,
            EquipmentModel.LayerType layerType,
            RegistryKey<EquipmentAsset> assetId,
            S entity,
            Models<V> models,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light,
            @Nullable Identifier texture
        ) {
        List<EquipmentModel.Layer> layers = modelLoader.get(assetId).getLayers(layerType);
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
                VertexConsumer trimConsumer = armorTrim != null && plugin.getTrimAlpha(equipmentSlot, armorTrim, layerType) > 0 ? plugin.getTrimConsumer(equipmentSlot, vertices, armorTrim, layerType, assetId) : null;
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
