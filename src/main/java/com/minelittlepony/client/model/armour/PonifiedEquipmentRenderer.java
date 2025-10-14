package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.equipment.*;
import net.minecraft.client.render.entity.model.EntityModel;
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

    private final Set<EntityModel<?>> drawnModels = new HashSet<>();
    private final Function<LayerTextureKey, Identifier> layerTextures;
    private final BiFunction<EquipmentModel.LayerType, Identifier, Identifier> ponifier = Util.memoize((type, texture) -> {
        return ResourceUtil.verifyTexture(texture.withPath(p -> p.replace(type.asString(), "ponified_" + type.asString()))).orElse(texture);
    });

    public PonifiedEquipmentRenderer(EquipmentModelLoader modelLoader) {
        super(modelLoader, MinecraftClient.getInstance().getAtlasManager().getAtlasTexture(Atlases.ARMOR_TRIMS));
        this.modelLoader = modelLoader;
        layerTextures = Util.memoize(key -> key.layer.getFullTextureId(key.layerType));
    }

    record LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {}

    record TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> equipmentAssetId) {
        public Identifier getTexture() {
            return this.trim.getTextureId(this.layerType.getTrimsDirectory(), this.equipmentAssetId);
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

        // textures/entity/equipment/strider_saddle/saddle.png
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

                    if (color != 0) {
                        Identifier partTexture = ponifier.apply(layerType, layer.usePlayerTexture() && texture != null ? texture : layerTextures.apply(new LayerTextureKey(layerType, layer)));
                        @Nullable
                        RenderLayer armorRenderLayer = plugin.getArmourLayer(slot, partTexture, layerType);
                        if (armorRenderLayer != null) {
                            submitArmorPiece(queue.getBatchingQueue(order++), model, state, matrices, armorRenderLayer, light, OverlayTexture.DEFAULT_UV, ColorHelper.withAlpha(alpha, color), layerType, slot, assetKey, partTexture, null);
                            if (hasGlint) {
                                RenderLayer glintRenderLayer = plugin.getGlintLayer(slot, layerType);
                                if (glintRenderLayer != null) {
                                    submitArmorPiece(queue.getBatchingQueue(order++), model, state, matrices, glintRenderLayer, light, OverlayTexture.DEFAULT_UV, ColorHelper.withAlpha(alpha, color), layerType, slot, assetKey, null, null);
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
                            submitArmorPiece(queue.getBatchingQueue(order++), model, state, matrices, trimLayer, light, OverlayTexture.DEFAULT_UV, ColorHelper.getWhite(trimAlpha), layerType, slot, assetKey, null, armorTrim);
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
            Models<V> models,
            ItemStack stack,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            @Nullable Identifier texture,
            int outlineColor,
            int initialOrder
        ) {
        List<EquipmentModel.Layer> layers = modelLoader.get(assetId).getLayers(layerType);
        if (!layers.isEmpty()) {
            ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();
            int defaultColor = stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, 0) : 0;
            float armorAlpha = plugin.getArmourAlpha(equipmentSlot, layerType);
            boolean hasGlint = plugin.getGlintAlpha(equipmentSlot, stack) > 0;
            int order = initialOrder;

            if (armorAlpha > 0) {
                for (EquipmentModel.Layer layer : layers) {
                    int dyeColor = getDyeColor(layer, defaultColor);
                    if (dyeColor != TRANSPARENT) {
                        ArmourTexture armorTexture = plugin.getTextureLookup().getTexture(stack, layerType, layer);
                        Identifier partTexture = ponifier.apply(layerType, layer.usePlayerTexture() && texture != null ? texture : layerTextures.apply(new LayerTextureKey(layerType, layer)));
                        @Nullable
                        RenderLayer armorRenderLayer = plugin.getArmourLayer(equipmentSlot, partTexture, layerType);
                        if (armorRenderLayer != null) {
                            ArmourVariant variant = layer.usePlayerTexture() ? ArmourVariant.LEGACY : armorTexture.variant();
                            AbstractPonyModel<S> model = (AbstractPonyModel<S>)models.getArmourModel(stack, layerType, variant);

                            if (setVisibilities(model, equipmentSlot, layerType)) {
                                submitPonyArmorPiece(queue.getBatchingQueue(order++), model, state, matrices, armorRenderLayer, light, OverlayTexture.DEFAULT_UV, ColorHelper.withAlpha(armorAlpha, dyeColor), layerType, equipmentSlot, assetId, partTexture, null);
                                if (hasGlint) {
                                    RenderLayer glintRenderLayer = plugin.getGlintLayer(equipmentSlot, layerType);
                                    if (glintRenderLayer != null) {
                                        submitPonyArmorPiece(queue.getBatchingQueue(order++), model, state, matrices, glintRenderLayer, light, OverlayTexture.DEFAULT_UV, ColorHelper.withAlpha(armorAlpha, dyeColor), layerType, equipmentSlot, assetId, null, null);
                                    }
                                }
                                hasGlint = false;
                                drawnModels.add(model);
                            }
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
                            for (EntityModel<?> model : drawnModels) {
                                submitArmorPiece(queue.getBatchingQueue(order++), (AbstractPonyModel<S>)model, state, matrices, trimLayer, light, OverlayTexture.DEFAULT_UV, ColorHelper.getWhite(trimAlpha), layerType, equipmentSlot, assetId, null, armorTrim);
                            }
                        }
                    }
                }
            }

            drawnModels.clear();
        }
    }

    private static <S extends PonyRenderState> void submitPonyArmorPiece(
            RenderCommandQueue queue,
            AbstractPonyModel<? super S> model,
            S state,
            MatrixStack matrices,
            RenderLayer renderLayer,
            int light,
            int overlay,
            int tintedColor,
            EquipmentModel.LayerType layerType,
            EquipmentSlot slot,
            RegistryKey<EquipmentAsset> assetKey,
            @Nullable Identifier partTexture,
            @Nullable ArmorTrim trim
        ) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();
        MatrixStack copyMatrices = new MatrixStack();
        queue.submitCustom(matrices, renderLayer, (entry, buffer) -> {
            var provider = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            buffer = trim != null
                    ? plugin.getTrimConsumer(slot, provider, trim, layerType, assetKey) : partTexture != null
                    ? plugin.getArmourConsumer(slot, provider, partTexture, layerType) : plugin.getGlintConsumer(slot, provider, layerType);
            if (buffer != null) {
                copyMatrices.peek().getPositionMatrix().set(entry.getPositionMatrix());
                copyMatrices.peek().getNormalMatrix().set(entry.getNormalMatrix());
                model.setAngles(state);
                if (setVisibilities(model, slot, layerType)) {
                    model.render(matrices, buffer, light, overlay, tintedColor);
                }
            }
        });
    }

    private static <S> void submitArmorPiece(
            RenderCommandQueue queue,
            Model<? super S> model,
            S state,
            MatrixStack matrices,
            RenderLayer renderLayer,
            int light,
            int overlay,
            int tintedColor,
            EquipmentModel.LayerType layerType,
            EquipmentSlot slot,
            RegistryKey<EquipmentAsset> assetKey,
            @Nullable Identifier partTexture,
            @Nullable ArmorTrim trim
        ) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();
        MatrixStack copyMatrices = new MatrixStack();
        queue.submitCustom(matrices, renderLayer, (entry, buffer) -> {
            var provider = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            buffer = trim != null
                    ? plugin.getTrimConsumer(slot, provider, trim, layerType, assetKey) : partTexture != null
                    ? plugin.getArmourConsumer(slot, provider, partTexture, layerType) : plugin.getGlintConsumer(slot, provider, layerType);
            if (buffer != null) {
                copyMatrices.peek().getPositionMatrix().set(entry.getPositionMatrix());
                copyMatrices.peek().getNormalMatrix().set(entry.getNormalMatrix());
                model.setAngles(state);
                model.render(matrices, buffer, light, overlay, tintedColor);
            }
        });
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
