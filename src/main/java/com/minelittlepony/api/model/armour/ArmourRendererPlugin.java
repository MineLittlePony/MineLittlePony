package com.minelittlepony.api.model.armour;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.model.armour.ArmourTextureResolver;
import com.mojang.blaze3d.vertex.PoseStack;

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

    default void onArmourRendered(LivingEntityRenderState state, PoseStack matrices, SubmitNodeCollector frame, EquipmentSlot armorSlot, EquipmentClientInfo.LayerType layerType, ArmourType type) {

    }

    default ItemStack[] getArmorStacks(LivingEntity entity, EquipmentSlot armorSlot, EquipmentClientInfo.LayerType layerType, ArmourType type) {
        return new ItemStack[] {
            entity.getItemBySlot(armorSlot)
        };
    }

    default ItemStack[] getArmorStacks(HumanoidRenderState state, EquipmentSlot armorSlot, EquipmentClientInfo.LayerType layerType, ArmourType type) {
        return new ItemStack[] { switch (armorSlot) {
            case HEAD -> state.headEquipment;
            case CHEST -> state.chestEquipment;
            case LEGS -> state.legsEquipment;
            case FEET -> state.feetEquipment;
            case BODY -> state.chestEquipment;
            case MAINHAND -> state.getMainHandItemStack();
            case OFFHAND -> state.mainArm == HumanoidArm.LEFT ? state.leftHandItemStack : state.rightHandItemStack;
            default -> ItemStack.EMPTY;
        }};
    }

    default float getGlintAlpha(EquipmentSlot slot, ItemStack stack) {
        return stack.hasFoil() ? 1 : 0;
    }

    default float getArmourAlpha(EquipmentSlot slot, EquipmentClientInfo.LayerType layer) {
        return 1F;
    }

    default float getTrimAlpha(EquipmentSlot slot, ArmorTrim trim, EquipmentClientInfo.LayerType layer) {
        return 1F;
    }

    default float getElytraAlpha(ItemStack stack, Model<?> model, LivingEntityRenderState entity) {
        return stack.is(Items.ELYTRA) ? 1F : 0F;
    }

    @Nullable
    default RenderType getTrimLayer(EquipmentSlot slot, ArmorTrim trim, EquipmentClientInfo.LayerType layerType, ResourceKey<EquipmentAsset> assetId) {
        return Sheets.armorTrimsSheet(trim.pattern().value().decal());
    }

    @Nullable
    default RenderType getArmourLayer(EquipmentSlot slot, Identifier texture, EquipmentClientInfo.LayerType layer) {
        return RenderTypes.armorCutoutNoCull(texture);
    }

    @Nullable
    default RenderType getGlintLayer(EquipmentSlot slot, EquipmentClientInfo.LayerType layer) {
        return RenderTypes.armorEntityGlint();
    }

    @Nullable
    default RenderType getCapeLayer(HumanoidRenderState entity, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    public enum ArmourType {
        ARMOUR,
        CAPE,
        ELYTRA,
        SKULL
    }
}
