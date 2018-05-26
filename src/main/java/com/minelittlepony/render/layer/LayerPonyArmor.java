package com.minelittlepony.render.layer;

import com.google.common.collect.Maps;
import com.minelittlepony.ForgeProxy;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.util.coordinates.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.Map;

public class LayerPonyArmor<T extends EntityLivingBase> extends AbstractPonyLayer<T> {

    private static final Map<String, ResourceLocation> HUMAN_ARMOUR = Maps.newHashMap();
    private static final Map<ResourceLocation, ResourceLocation> PONY_ARMOUR = Maps.newHashMap();

    private ModelWrapper pony;

    public LayerPonyArmor(RenderLivingBase<T> renderer) {
        super(renderer);
    }

    @Override
    public void doPonyRender(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        pony = getPonyRenderer().getModelWrapper();

        for (EntityEquipmentSlot i : EntityEquipmentSlot.values()) {
            if (i.getSlotType() == Type.ARMOR) {
                renderArmor(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, i);
            }
        }
    }

    private void renderArmor(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EntityEquipmentSlot armorSlot) {
        ItemStack itemstack = entity.getItemStackFromSlot(armorSlot);

        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemArmor) {

            ItemArmor itemarmor = (ItemArmor) itemstack.getItem();

            ModelPonyArmor armour = getArmorModel(entity, itemstack, armorSlot, pony.getArmor().getArmorForSlot(armorSlot));
            armour.setModelAttributes(pony.getBody());
            armour.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);
            armour.synchroniseLegs(pony.getBody());

            Tuple<ResourceLocation, Boolean> armors = getArmorTexture(entity, itemstack, armorSlot, null);
            prepareToRender(armour, armorSlot, armors.getSecond());

            getRenderer().bindTexture(armors.getFirst());

            if (itemarmor.getArmorMaterial() == ArmorMaterial.LEATHER) {
                Color.glColor(itemarmor.getColor(itemstack), 1);
                armour.render(entity, move, swing, ticks, headYaw, headPitch, scale);
                armors = getArmorTexture(entity, itemstack, armorSlot, "overlay");
                getRenderer().bindTexture(armors.getFirst());
            }

            GlStateManager.color(1, 1, 1, 1);
            armour.render(entity, move, swing, ticks, headYaw, headPitch, scale);

            if (itemstack.isItemEnchanted()) {
                LayerArmorBase.renderEnchantedGlint(getRenderer(), entity, armour, move, swing, partialTicks, ticks, headYaw, headPitch, scale);
            }
        }
    }

    private Tuple<ResourceLocation, Boolean> getArmorTexture(T entity, ItemStack itemstack, EntityEquipmentSlot slot, @Nullable String type) {
        ItemArmor item = (ItemArmor) itemstack.getItem();
        String texture = item.getArmorMaterial().getName();

        String domain = "minecraft";

        int idx = texture.indexOf(':');
        if (idx > -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }

        type = type == null ? "" : String.format("_%s", type);

        String ponyRes = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, slot == EntityEquipmentSlot.LEGS ? 2 : 1, type);

        ponyRes = getArmorTexture(entity, itemstack, ponyRes, slot, type);

        ResourceLocation human = getHumanResource(ponyRes);
        ResourceLocation pony = getPonyResource(human);

        // check resource packs for either texture.
        for (ResourcePackRepository.Entry entry : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries()) {
            if (entry.getResourcePack().resourceExists(pony)) {
                // ponies are more important
                return new Tuple<>(pony, true);
            } else if (entry.getResourcePack().resourceExists(human)) {
                // but I guess I'll take a human
                return new Tuple<>(human, false);
            }
        }
        // the default pack
        try {
            Minecraft.getMinecraft().getResourceManager().getResource(pony);
            return new Tuple<>(pony, true);
        } catch (IOException e) {
            return new Tuple<>(human, false);
        }
    }

    private void prepareToRender(ModelPonyArmor model, EntityEquipmentSlot slot, boolean isPony) {
        model.setVisible(false);

        switch (slot) {
            case HEAD:
                model.showHead(isPony);
                break;
            case FEET:
                model.showFeet(true);
                break;
            case LEGS:
                model.showFeet(true);
                model.showLegs(isPony);
            case CHEST:
                model.showSaddle(isPony);
            default:
        }
    }

    private static ResourceLocation getHumanResource(String resource) {
        return HUMAN_ARMOUR.computeIfAbsent(resource, ResourceLocation::new);
    }

    private static ResourceLocation getPonyResource(ResourceLocation human) {
        return PONY_ARMOUR.computeIfAbsent(human, key -> {
            String domain = human.getResourceDomain();
            if ("minecraft".equals(domain)) {
                domain = "minelittlepony"; // it's a vanilla armor. I provide these.
            }
            String path = human.getResourcePath().replace(".png", "_pony.png");
            return new ResourceLocation(domain, path);
        });
    }

    private static String getArmorTexture(EntityLivingBase entity, ItemStack item, String def, EntityEquipmentSlot slot, @Nullable String type) {
        return ForgeProxy.getArmorTexture(entity, item, def, slot, type);
    }

    private static ModelPonyArmor getArmorModel(EntityLivingBase entity, ItemStack itemstack, EntityEquipmentSlot slot, ModelPonyArmor def) {
        ModelBase model = ForgeProxy.getArmorModel(entity, itemstack, slot, def);
        if (model instanceof ModelPonyArmor) {
            return (ModelPonyArmor) model;
        }

        return def;
    }

}
