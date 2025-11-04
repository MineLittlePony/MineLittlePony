package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.*;
import net.minecraft.client.render.entity.equipment.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
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

    private final SpriteAtlasTexture armorTrimsAtlas;
    private final Function<LayerTextureKey, Identifier> layerTextures;
    private final BiFunction<EquipmentModel.LayerType, Identifier, Identifier> ponifier = Util.memoize((type, texture) -> {
        return ResourceUtil.verifyTexture(texture.withPath(p -> p.replace(type.asString(), "ponified_" + type.asString()))).orElse(texture);
    });

    public PonifiedEquipmentRenderer(EquipmentModelLoader modelLoader, SpriteAtlasTexture armorTrimsAtlas) {
        super(modelLoader, armorTrimsAtlas);
        this.modelLoader = modelLoader;
        this.armorTrimsAtlas = armorTrimsAtlas;
        layerTextures = Util.memoize(key -> key.layer.getFullTextureId(key.layerType));
    }

    record LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {}

    record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> equipmentAssetId) {
        public Identifier getTexture() {
            return trim.getTextureId(layerType.getTrimsDirectory(), equipmentAssetId);
        }
    }

    @Override
    public <S> void render(
            EquipmentModel.LayerType layerType,
            RegistryKey<EquipmentAsset> assetKey,
            Model<? super S> model,
            S state,
            ItemStack stack,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            @Nullable Identifier texture,
            int outlineColor,
            int initialOrder
        ) {
        EquipmentSlot slot = layerType == EquipmentModel.LayerType.WINGS ? EquipmentSlot.CHEST : EquipmentSlot.BODY;

        List<EquipmentModel.Layer> layers = modelLoader.get(assetKey).getLayers(layerType);
        if (!layers.isEmpty()) {
            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

            int i = DyedColorComponent.getColor(stack, 0);
            float alpha = plugin.getArmourAlpha(slot, layerType);
            boolean hasGlint = plugin.getGlintAlpha(slot, stack) > 0;
            int order = initialOrder;

            if (alpha > 0) {
                for (EquipmentModel.Layer layer : layers) {
                    int color = getDyeColor(layer, i);

                    if (color != TRANSPARENT) {
                        color = ColorHelper.withAlpha(alpha, color);
                        Identifier partTexture = ponifier.apply(layerType, layer.usePlayerTexture() && texture != null ? texture : layerTextures.apply(new LayerTextureKey(layerType, layer)));
                        @Nullable
                        RenderLayer armorRenderLayer = plugin.getArmourLayer(slot, partTexture, layerType);
                        if (armorRenderLayer != null) {
                            queue.getBatchingQueue(order++).submitModel(model, state, matrices, armorRenderLayer, light, OverlayTexture.DEFAULT_UV, color, null, outlineColor, null);
                            if (hasGlint) {
                                RenderLayer glintRenderLayer = plugin.getGlintLayer(slot, layerType);
                                if (glintRenderLayer != null) {
                                    queue.getBatchingQueue(order++).submitModel(model, state, matrices, glintRenderLayer, light, OverlayTexture.DEFAULT_UV, color, null, outlineColor, null);
                                }
                            }
                            hasGlint = false;
                        }
                    }
                }

                ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
                if (armorTrim != null) {
                    float trimAlpha = plugin.getTrimAlpha(slot, armorTrim, layerType);
                    if (trimAlpha > 0) {
                        @Nullable
                        RenderLayer trimLayer = plugin.getTrimLayer(slot, armorTrim, layerType, assetKey);
                        if (trimLayer != null) {
                            Sprite sprite = armorTrimsAtlas.getSprite(armorTrim.getTextureId(layerType.getTrimsDirectory(), assetKey));
                            queue.getBatchingQueue(order++).submitModel(model, state, matrices, trimLayer, light, OverlayTexture.DEFAULT_UV, ColorHelper.getWhite(trimAlpha), sprite, outlineColor, null);
                        }
                    }
                }
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
            OrderedRenderCommandQueue queue,
            int light,
            int outlineColor,
            int initialOrder
        ) {
        render(equipmentSlot, layerType, assetId, entity, models, stack, matrices, queue, light, null, outlineColor, initialOrder);
    }

    @SuppressWarnings("unchecked")
    public <S extends PonyRenderState, V extends ClientPonyModel<S>> void render(
            EquipmentSlot equipmentSlot,
            EquipmentModel.LayerType layerType,
            RegistryKey<EquipmentAsset> assetId,
            S state,
            Models<?> models,
            ItemStack stack,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            @Nullable Identifier texture,
            int outlineColor,
            int initialOrder
        ) {
        if (!shouldRender(equipmentSlot, layerType)) {
            return;
        }
        List<EquipmentModel.Layer> layers = modelLoader.get(assetId).getLayers(layerType);
        if (!layers.isEmpty()) {
            var armorState = new State<S>(state, equipmentSlot, layerType);
            Set<PieceModel<S>> drawnModels = new HashSet<>();
            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();
            int defaultColor = stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, 0) : 0;
            float armorAlpha = plugin.getArmourAlpha(equipmentSlot, layerType);
            boolean hasGlint = plugin.getGlintAlpha(equipmentSlot, stack) > 0;
            int order = initialOrder;

            if (armorAlpha > 0) {
                for (EquipmentModel.Layer layer : layers) {
                    int dyeColor = getDyeColor(layer, defaultColor);
                    if (dyeColor != TRANSPARENT) {
                        dyeColor = ColorHelper.withAlpha(armorAlpha, dyeColor);
                        ArmourTexture armorTexture = plugin.getTextureLookup().getTexture(stack, layerType, layer);
                        Identifier partTexture = ponifier.apply(layerType, layer.usePlayerTexture() && texture != null ? texture : layerTextures.apply(new LayerTextureKey(layerType, layer)));
                        @Nullable
                        RenderLayer armorRenderLayer = plugin.getArmourLayer(equipmentSlot, partTexture, layerType);
                        if (armorRenderLayer != null) {
                            ArmourVariant variant = layer.usePlayerTexture() ? ArmourVariant.LEGACY : armorTexture.variant();
                            PieceModel<S> model = new PieceModel<S>((AbstractPonyModel<S>)models.getArmourModel(stack, layerType, variant));

                            queue.getBatchingQueue(order++).submitModel(model, armorState, matrices, armorRenderLayer, light, OverlayTexture.DEFAULT_UV, dyeColor, null, outlineColor, null);
                            if (hasGlint) {
                                RenderLayer glintRenderLayer = plugin.getGlintLayer(equipmentSlot, layerType);
                                if (glintRenderLayer != null) {
                                    queue.getBatchingQueue(order++).submitModel(model, armorState, matrices, glintRenderLayer, light, OverlayTexture.DEFAULT_UV, dyeColor, null, outlineColor, null);
                                }
                            }
                            hasGlint = false;
                            drawnModels.add(model);
                        }
                    }
                }
            }

            if (!drawnModels.isEmpty()) {
                @Nullable
                ArmorTrim armorTrim = stack.get(DataComponentTypes.TRIM);
                if (armorTrim != null) {
                    float trimAlpha = plugin.getTrimAlpha(equipmentSlot, armorTrim, layerType);
                    if (trimAlpha > 0) {
                        @Nullable
                        RenderLayer trimLayer = plugin.getTrimLayer(equipmentSlot, armorTrim, layerType, assetId);
                        if (trimLayer != null) {
                            Sprite sprite = armorTrimsAtlas.getSprite(armorTrim.getTextureId(layerType.getTrimsDirectory(), assetId));
                            for (PieceModel<S> model : drawnModels) {
                                queue.getBatchingQueue(order++).submitModel(model, armorState, matrices, trimLayer, light, OverlayTexture.DEFAULT_UV, ColorHelper.getWhite(trimAlpha), sprite, outlineColor, null);
                            }
                        }
                    }
                }
            }
        }
    }

    private static int getDyeColor(EquipmentModel.Layer layer, int dyeColor) {
        Optional<EquipmentModel.Dyeable> optional = layer.dyeable();
        if (optional.isPresent()) {
            int i = optional.get().colorWhenUndyed().map(ColorHelper::fullAlpha).orElse(0);
            return dyeColor != TRANSPARENT ? dyeColor : i;
        }
        return Colors.WHITE;
    }


    private static class PieceModel<T extends PonyRenderState> extends Model<State<T>> {
        private final AbstractPonyModel<? super T> model;

        public PieceModel(AbstractPonyModel<? super T> model) {
            super(model.getRootPart(), model::getLayer);
            this.model = model;
        }

        @Override
        public void setAngles(State<T> state) {
            model.setAngles(state.ponyState);

            model.setVisible(false);
            model.body.visible = state.slot() == EquipmentSlot.CHEST;
            model.head.visible = state.layer() == EquipmentModel.LayerType.HUMANOID && state.slot() == EquipmentSlot.HEAD;

            if (state.slot() == (state.layer() == EquipmentModel.LayerType.HUMANOID ? EquipmentSlot.FEET : EquipmentSlot.LEGS)) {
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
            model.render(matrices, vertices, light, overlay, color);
        }
    }

    private static record State<T extends PonyRenderState> (
            T ponyState,
            EquipmentSlot slot,
            EquipmentModel.LayerType layer
    ) { }

    public static boolean shouldRender(EquipmentSlot slot, EquipmentModel.LayerType layer) {
        return slot == EquipmentSlot.CHEST
            || layer == EquipmentModel.LayerType.HUMANOID && slot == EquipmentSlot.HEAD
            || (slot == (layer == EquipmentModel.LayerType.HUMANOID ? EquipmentSlot.FEET : EquipmentSlot.LEGS));
    }
}
