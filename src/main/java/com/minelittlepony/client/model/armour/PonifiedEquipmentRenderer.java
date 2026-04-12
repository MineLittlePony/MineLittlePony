package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.armour.*;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.util.RenderList;
import com.minelittlepony.util.ResourceUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PonifiedEquipmentRenderer extends EquipmentLayerRenderer {
    private static final int TRANSPARENT = 0;

    private final EquipmentAssetManager modelLoader;

    private final TextureAtlas armorTrimsAtlas;
    private final Function<LayerTextureKey, Identifier> layerTextures;
    private final BiFunction<EquipmentClientInfo.LayerType, Identifier, Identifier> ponifier = Util.memoize((type, texture) -> {
        return ResourceUtil.verifyTexture(texture.withPath(p -> p.replace(type.getSerializedName(), "ponified_" + type.getSerializedName()))).orElse(texture);
    });

    public PonifiedEquipmentRenderer(EquipmentAssetManager modelLoader, TextureAtlas armorTrimsAtlas) {
        super(modelLoader, armorTrimsAtlas);
        this.modelLoader = modelLoader;
        this.armorTrimsAtlas = armorTrimsAtlas;
        layerTextures = Util.memoize(key -> key.layer.getTextureLocation(key.layerType));
    }

    record LayerTextureKey(EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer) {}

    record TrimSpriteKey(ArmorTrim trim, EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> equipmentAssetId) {
        public Identifier getTexture() {
            return trim.layerAssetId(layerType.trimAssetPrefix(), equipmentAssetId);
        }
    }

    @Override
    public <S> void renderLayers(
            EquipmentClientInfo.LayerType layerType,
            ResourceKey<EquipmentAsset> assetKey,
            Model<? super S> model,
            S state,
            ItemStack stack,
            PoseStack matrices,
            SubmitNodeCollector queue,
            int light,
            @Nullable Identifier texture,
            int outlineColor,
            int initialOrder
        ) {
        EquipmentSlot slot = layerType == EquipmentClientInfo.LayerType.WINGS ? EquipmentSlot.CHEST : EquipmentSlot.BODY;

        List<EquipmentClientInfo.Layer> layers = modelLoader.get(assetKey).getLayers(layerType);
        if (!layers.isEmpty()) {
            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

            int i = DyedItemColor.getOrDefault(stack, 0);
            float alpha = plugin.getArmourAlpha(slot, layerType);
            boolean hasGlint = plugin.getGlintAlpha(slot, stack) > 0;
            int order = initialOrder;

            if (alpha > 0) {
                for (EquipmentClientInfo.Layer layer : layers) {
                    int color = getDyeColor(layer, i);

                    if (color != TRANSPARENT) {
                        color = ARGB.color(alpha, color);
                        Identifier partTexture = ponifier.apply(layerType, layer.usePlayerTexture() && texture != null ? texture : layerTextures.apply(new LayerTextureKey(layerType, layer)));
                        @Nullable
                        RenderType armorRenderLayer = plugin.getArmourLayer(slot, partTexture, layerType);
                        if (armorRenderLayer != null) {
                            queue.order(order++).submitModel(model, state, matrices, armorRenderLayer, light, OverlayTexture.NO_OVERLAY, color, null, outlineColor, null);
                            if (hasGlint) {
                                RenderType glintRenderLayer = plugin.getGlintLayer(slot, layerType);
                                if (glintRenderLayer != null) {
                                    queue.order(order++).submitModel(model, state, matrices, glintRenderLayer, light, OverlayTexture.NO_OVERLAY, color, null, outlineColor, null);
                                }
                            }
                            hasGlint = false;
                        }
                    }
                }

                ArmorTrim armorTrim = stack.get(DataComponents.TRIM);
                if (armorTrim != null) {
                    float trimAlpha = plugin.getTrimAlpha(slot, armorTrim, layerType);
                    if (trimAlpha > 0) {
                        @Nullable
                        RenderType trimLayer = plugin.getTrimLayer(slot, armorTrim, layerType, assetKey);
                        if (trimLayer != null) {
                            TextureAtlasSprite sprite = armorTrimsAtlas.getSprite(armorTrim.layerAssetId(layerType.trimAssetPrefix(), assetKey));
                            queue.order(order++).submitModel(model, state, matrices, trimLayer, light, OverlayTexture.NO_OVERLAY, ARGB.white(trimAlpha), sprite, outlineColor, null);
                        }
                    }
                }
            }
        }
    }

    public <S extends PonyRenderState, V extends ClientPonyModel<S>> void renderLayers(
            EquipmentSlot equipmentSlot,
            EquipmentClientInfo.LayerType layerType,
            ResourceKey<EquipmentAsset> assetId,
            S entity,
            Models<V> models,
            ItemStack stack,
            PoseStack matrices,
            SubmitNodeCollector queue,
            int light,
            int outlineColor,
            int initialOrder
        ) {
        renderLayers(equipmentSlot, layerType, assetId, entity, models, stack, matrices, queue, light, null, outlineColor, initialOrder);
    }

    @SuppressWarnings("unchecked")
    public <S extends PonyRenderState, V extends ClientPonyModel<S>> void renderLayers(
            EquipmentSlot equipmentSlot,
            EquipmentClientInfo.LayerType layerType,
            ResourceKey<EquipmentAsset> assetId,
            S state,
            Models<?> models,
            ItemStack stack,
            PoseStack matrices,
            SubmitNodeCollector queue,
            int light,
            @Nullable Identifier texture,
            int outlineColor,
            int initialOrder
        ) {
        if (!shouldRender(equipmentSlot, layerType)) {
            return;
        }
        List<EquipmentClientInfo.Layer> layers = modelLoader.get(assetId).getLayers(layerType);
        if (!layers.isEmpty()) {
            var armorState = new State<S>(state, equipmentSlot, layerType);
            Set<PieceModel<S>> drawnModels = new HashSet<>();
            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();
            int defaultColor = DyedItemColor.getOrDefault(stack, 0);
            float armorAlpha = plugin.getArmourAlpha(equipmentSlot, layerType);
            boolean hasGlint = plugin.getGlintAlpha(equipmentSlot, stack) > 0;
            int order = initialOrder;

            if (armorAlpha > 0) {
                for (EquipmentClientInfo.Layer layer : layers) {
                    int dyeColor = getDyeColor(layer, defaultColor);
                    if (dyeColor != TRANSPARENT) {
                        dyeColor = ARGB.color(armorAlpha, dyeColor);
                        ArmourTexture armorTexture = plugin.getTextureLookup().getTexture(stack, layerType, layer);
                        Identifier partTexture = ponifier.apply(layerType, layer.usePlayerTexture() && texture != null ? texture : layerTextures.apply(new LayerTextureKey(layerType, layer)));
                        @Nullable
                        RenderType armorRenderLayer = plugin.getArmourLayer(equipmentSlot, partTexture, layerType);
                        if (armorRenderLayer != null) {
                            ArmourVariant variant = layer.usePlayerTexture() ? ArmourVariant.LEGACY : armorTexture.variant();
                            PieceModel<S> model = new PieceModel<S>((AbstractPonyModel<S>)models.getArmourModel(stack, layerType, variant));

                            queue.order(order++).submitModel(model, armorState, matrices, armorRenderLayer, light, OverlayTexture.NO_OVERLAY, dyeColor, null, outlineColor, null);
                            if (hasGlint) {
                                RenderType glintRenderLayer = plugin.getGlintLayer(equipmentSlot, layerType);
                                if (glintRenderLayer != null) {
                                    queue.order(order++).submitModel(model, armorState, matrices, glintRenderLayer, light, OverlayTexture.NO_OVERLAY, dyeColor, null, outlineColor, null);
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
                ArmorTrim armorTrim = stack.get(DataComponents.TRIM);
                if (armorTrim != null) {
                    float trimAlpha = plugin.getTrimAlpha(equipmentSlot, armorTrim, layerType);
                    if (trimAlpha > 0) {
                        @Nullable
                        RenderType trimLayer = plugin.getTrimLayer(equipmentSlot, armorTrim, layerType, assetId);
                        if (trimLayer != null) {
                            TextureAtlasSprite sprite = armorTrimsAtlas.getSprite(armorTrim.layerAssetId(layerType.trimAssetPrefix(), assetId));
                            for (PieceModel<S> model : drawnModels) {
                                queue.order(order++).submitModel(model, armorState, matrices, trimLayer, light, OverlayTexture.NO_OVERLAY, ARGB.white(trimAlpha), sprite, outlineColor, null);
                            }
                        }
                    }
                }
            }
        }
    }

    private static int getDyeColor(EquipmentClientInfo.Layer layer, int dyeColor) {
        Optional<EquipmentClientInfo.Dyeable> optional = layer.dyeable();
        if (optional.isPresent()) {
            int i = optional.get().colorWhenUndyed().map(ARGB::opaque).orElse(0);
            return dyeColor != TRANSPARENT ? dyeColor : i;
        }
        return CommonColors.WHITE;
    }


    private static class PieceModel<T extends PonyRenderState> extends Model<State<T>> {
        private final AbstractPonyModel<? super T> model;

        private boolean showChest;
        private boolean showHead;
        private boolean showLegs;

        private final RenderList mainRenderList;

        public PieceModel(AbstractPonyModel<? super T> model) {
            super(model.root(), model::renderType);
            this.model = model;
            this.mainRenderList = RenderList.of()
                    .add(model.bodyRenderList.checked(() -> showChest))
                    .add(model.headRenderList.checked(() -> showHead))
                    .add(model.legsRenderList.checked(() -> showLegs));
        }

        @Override
        public void setupAnim(State<T> state) {
            model.setupAnim(state.ponyState);
            showChest = state.slot() == EquipmentSlot.CHEST;
            showHead = state.layer() == EquipmentClientInfo.LayerType.HUMANOID && state.slot() == EquipmentSlot.HEAD;
            showLegs = state.slot() == (state.layer() == EquipmentClientInfo.LayerType.HUMANOID ? EquipmentSlot.FEET : EquipmentSlot.LEGS);
        }

        @Override
        public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
            mainRenderList.accept(matrices, vertices, light, overlay, color);
        }
    }

    private static record State<T extends PonyRenderState> (
            T ponyState,
            EquipmentSlot slot,
            EquipmentClientInfo.LayerType layer
    ) { }

    public static boolean shouldRender(EquipmentSlot slot, EquipmentClientInfo.LayerType layer) {
        return slot == EquipmentSlot.CHEST
            || layer == EquipmentClientInfo.LayerType.HUMANOID && slot == EquipmentSlot.HEAD
            || (slot == (layer == EquipmentClientInfo.LayerType.HUMANOID ? EquipmentSlot.FEET : EquipmentSlot.LEGS));
    }
}
