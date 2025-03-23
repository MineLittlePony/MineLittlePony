package com.minelittlepony.client.model.armour;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.trim.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.*;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public interface ArmourRendererPlugin {
    AtomicReference<ArmourRendererPlugin> INSTANCE = new AtomicReference<>(new ArmourRendererPlugin() {});

    static void register(Function<ArmourRendererPlugin, ArmourRendererPlugin> constructor) {
        INSTANCE.set(constructor.apply(INSTANCE.get()));
    }

    default ArmourTextureLookup getTextureLookup() {
        return ArmourTextureResolver.INSTANCE;
    }

    default void onArmourRendered(LivingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider provider, EquipmentSlot armorSlot, EquipmentModel.LayerType layerType, ArmourType type) {

    }

    default ItemStack[] getArmorStacks(LivingEntity entity, EquipmentSlot armorSlot, EquipmentModel.LayerType layerType, ArmourType type) {
        return new ItemStack[] {
            entity.getEquippedStack(armorSlot)
        };
    }

    default ItemStack[] getArmorStacks(BipedEntityRenderState state, EquipmentSlot armorSlot, EquipmentModel.LayerType layerType, ArmourType type) {
        return new ItemStack[] { switch (armorSlot) {
            case HEAD -> state.equippedHeadStack;
            case CHEST -> state.equippedChestStack;
            case LEGS -> state.equippedLegsStack;
            case FEET -> state.equippedFeetStack;
            case BODY -> state.equippedChestStack;
            default -> ItemStack.EMPTY;
            // TODO: Mojaaaaaaang!!
            //case MAINHAND -> state.getMainHandStack();
            //case OFFHAND -> state.mainArm == Arm.LEFT ? state.leftHandStack : state.rightHandStack;
        }};
    }

    default float getGlintAlpha(EquipmentSlot slot, ItemStack stack) {
        return stack.hasGlint() ? 1 : 0;
    }

    default int getDyeColor(EquipmentSlot slot, ItemStack stack) {
        return stack.isIn(ItemTags.DYEABLE) ? DyedColorComponent.getColor(stack, -6265536) : Colors.WHITE;
    }

    default float getArmourAlpha(EquipmentSlot slot, EquipmentModel.LayerType layer) {
        return 1F;
    }

    default float getTrimAlpha(EquipmentSlot slot, ArmorTrim trim, EquipmentModel.LayerType layer) {
        return 1F;
    }

    default float getElytraAlpha(ItemStack stack, Model model, LivingEntityRenderState entity) {
        return stack.isOf(Items.ELYTRA) ? 1F : 0F;
    }

    @Nullable
    default VertexConsumer getTrimConsumer(EquipmentSlot slot, VertexConsumerProvider provider, ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetId) {
        @Nullable VertexConsumer buffer = getOptionalBuffer(provider, getTrimLayer(slot, trim, layerType, assetId));
        if (buffer == null) {
            return null;
        }
        SpriteAtlasTexture armorTrimsAtlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
        Sprite sprite = armorTrimsAtlas.getSprite(trim.getTextureId(layerType.getTrimsDirectory(), assetId));
        return sprite.getTextureSpecificVertexConsumer(buffer);
    }

    @Nullable
    default RenderLayer getTrimLayer(EquipmentSlot slot, ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetId) {
        return TexturedRenderLayers.getArmorTrims(trim.pattern().value().decal());
    }

    @Nullable
    default VertexConsumer getArmourConsumer(EquipmentSlot slot, VertexConsumerProvider provider, Identifier texture, EquipmentModel.LayerType layer) {
        return getOptionalBuffer(provider, getArmourLayer(slot, texture, layer));
    }

    @Nullable
    default RenderLayer getArmourLayer(EquipmentSlot slot, Identifier texture, EquipmentModel.LayerType layer) {
        return RenderLayer.getArmorCutoutNoCull(texture);
    }

    @Nullable
    default VertexConsumer getGlintConsumer(EquipmentSlot slot, VertexConsumerProvider provider, EquipmentModel.LayerType layer) {
        return getOptionalBuffer(provider, getGlintLayer(slot, layer));
    }

    @Nullable
    default RenderLayer getGlintLayer(EquipmentSlot slot, EquipmentModel.LayerType layer) {
        return RenderLayer.getArmorEntityGlint();
    }

    @Nullable
    default VertexConsumer getCapeConsumer(BipedEntityRenderState entity, VertexConsumerProvider provider, Identifier texture) {
        if (entity.equippedChestStack.isOf(Items.ELYTRA)) {
            return null;
        }
        return getOptionalBuffer(provider, getCapeLayer(entity, texture));
    }

    @Nullable
    default RenderLayer getCapeLayer(BipedEntityRenderState entity, Identifier texture) {
        return RenderLayer.getEntitySolid(texture);
    }

    /**
     * @deprecated Method is no longer used
     */
    @Deprecated
    @Nullable
    default VertexConsumer getElytraConsumer(ItemStack stack, Model model, BipedEntityRenderState state, VertexConsumerProvider provider, Identifier texture) {
        return ItemRenderer.getArmorGlintConsumer(provider, RenderLayer.getArmorCutoutNoCull(texture), getGlintAlpha(EquipmentSlot.CHEST, stack) > 0F);
    }

    @Nullable
    static VertexConsumer getOptionalBuffer(VertexConsumerProvider provider, @Nullable RenderLayer layer) {
        return layer == null ? null : provider.getBuffer(layer);
    }

    public enum ArmourType {
        ARMOUR,
        CAPE,
        ELYTRA,
        SKULL
    }
}
