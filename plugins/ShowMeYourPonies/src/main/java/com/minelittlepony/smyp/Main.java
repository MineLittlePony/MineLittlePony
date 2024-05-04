package com.minelittlepony.smyp;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import nl.enjarai.showmeyourskin.client.ModRenderLayers;
import nl.enjarai.showmeyourskin.config.HideableEquipment;
import nl.enjarai.showmeyourskin.util.ArmorContext;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.model.armour.ArmourLayer;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;

public class Main implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArmourRendererPlugin.register(PluginImpl::new);
    }

    static class PluginImpl implements ArmourRendererPlugin {
        @Nullable
        private ArmorContext context;

        private final ArmourRendererPlugin parent;

        PluginImpl(ArmourRendererPlugin parent) {
            this.parent = parent;
        }

        @Override
        public ItemStack[] getArmorStacks(LivingEntity entity, EquipmentSlot armorSlot, ArmourLayer layer) {
            context = new ArmorContext(HideableEquipment.fromSlot(armorSlot), entity);
            return parent.getArmorStacks(entity, armorSlot, layer);
        }

        @Override
        public boolean shouldRenderGlint(EquipmentSlot slot, ItemStack stack) {
            return parent.shouldRenderGlint(slot, stack) && (context == null || !context.shouldModify() || context.getApplicableGlintTransparency() > 0);
        }

        @Override
        @Nullable
        public VertexConsumer getArmourConsumer(EquipmentSlot slot, VertexConsumerProvider provider, Identifier texture, ArmourLayer layer) {
            if (context != null && context.shouldModify()) {
                float transparency = context.getApplicablePieceTransparency();
                if (transparency <= 0) {
                    return null;
                }
                if (transparency < 1) {
                    return provider.getBuffer(ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(texture));
                }
            }
            return parent.getArmourConsumer(slot, provider, texture, layer);
        }

        @Override
        @Nullable
        public VertexConsumer getTrimConsumer(EquipmentSlot slot, VertexConsumerProvider provider, RegistryEntry<ArmorMaterial> material, ArmorTrim trim, ArmourLayer layer) {
            if (context != null && context.shouldModify()) {
                float transparency = context.getApplicableTrimTransparency();
                if (transparency <= 0) {
                    return null;
                }

                if (transparency < 1) {
                    Sprite sprite = MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE).getSprite(layer == ArmourLayer.INNER ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
                    return sprite.getTextureSpecificVertexConsumer(
                            provider.getBuffer(ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE))
                    );
                }
            }
            return parent.getTrimConsumer(slot, provider, material, trim, layer);
        }
    }
}
