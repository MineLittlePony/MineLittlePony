package com.minelittlepony.render.layer;

import com.google.common.collect.Maps;
import com.minelittlepony.ForgeProxy;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.armour.IEquestrianArmor;
import com.minelittlepony.model.armour.IEquestrianArmor.ArmorLayer;
import com.minelittlepony.model.armour.ModelPonyArmor;
import com.minelittlepony.model.capabilities.IModelArmor;
import com.minelittlepony.util.coordinates.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
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

import java.io.IOException;
import java.util.Map;

import javax.annotation.Nullable;

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
                renderArmor(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, i, ArmorLayer.INNER);
                renderArmor(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, i, ArmorLayer.OUTER);
            }
        }
    }

    private <V extends ModelBiped & IModelArmor> void renderArmor(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EntityEquipmentSlot armorSlot, ArmorLayer layer) {
        ItemStack itemstack = entity.getItemStackFromSlot(armorSlot);

        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemArmor) {

            ItemArmor itemarmor = (ItemArmor) itemstack.getItem();

            ModelPonyArmor armour = getArmorModel(entity, itemstack, armorSlot, layer, pony.getArmor().getArmorForLayer(layer));

            if (armour.prepareToRender(armorSlot, layer)) {

                armour.setModelAttributes(pony.getBody());
                armour.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);
                armour.synchroniseLegs(pony.getBody());

                Tuple<ResourceLocation, Boolean> armourTexture = getArmorTexture(entity, itemstack, armorSlot, layer, null);

                getRenderer().bindTexture(armourTexture.getFirst());

                if (itemarmor.getArmorMaterial() == ArmorMaterial.LEATHER) {
                    Color.glColor(itemarmor.getColor(itemstack), 1);
                    armour.render(entity, move, swing, ticks, headYaw, headPitch, scale);
                    armourTexture = getArmorTexture(entity, itemstack, armorSlot, layer, "overlay");
                    getRenderer().bindTexture(armourTexture.getFirst());
                }

                GlStateManager.color(1, 1, 1, 1);
                armour.render(entity, move, swing, ticks, headYaw, headPitch, scale);

                if (itemstack.isItemEnchanted()) {
                    LayerArmorBase.renderEnchantedGlint(getRenderer(), entity, armour, move, swing, partialTicks, ticks, headYaw, headPitch, scale);
                }
            }
        }
    }

    private Tuple<ResourceLocation, Boolean> getArmorTexture(T entity, ItemStack itemstack, EntityEquipmentSlot slot, ArmorLayer layer,  @Nullable String type) {
        ItemArmor item = (ItemArmor) itemstack.getItem();
        String texture = item.getArmorMaterial().getName();

        String domain = "minecraft";

        int idx = texture.indexOf(':');
        if (idx > -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }

        type = type == null ? "" : String.format("_%s", type);

        String ponyRes = String.format("%s:textures/models/armor/%s_layer_%s%s.png", domain, texture, layer.name().toLowerCase(), type);
        String oldPonyRes = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, layer == ArmorLayer.INNER ? 2 : 1, type);

        ResourceLocation human = getArmorTexture(entity, itemstack, ponyRes, slot, type);
        ResourceLocation oldPony = ponifyResource(getArmorTexture(entity, itemstack, oldPonyRes, slot, type));
        ResourceLocation pony = ponifyResource(human);

        // check resource packs for either texture.
        for (ResourcePackRepository.Entry entry : Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries()) {
            if (entry.getResourcePack().resourceExists(pony)) {
                // ponies are more important
                return new Tuple<>(pony, true);
            } else if (entry.getResourcePack().resourceExists(oldPony)) {
                return new Tuple<>(oldPony, true);
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
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(oldPony);
                return new Tuple<>(oldPony, true);
            } catch (IOException r) {
                return new Tuple<>(human, false);
            }
        }
    }

    private static ResourceLocation ponifyResource(ResourceLocation human) {
        return PONY_ARMOUR.computeIfAbsent(human, key -> {
            String domain = human.getNamespace();
            if ("minecraft".equals(domain)) {
                domain = "minelittlepony"; // it's a vanilla armor. I provide these.
            }
            String path = human.getPath().replace(".png", "_pony.png");
            return new ResourceLocation(domain, path);
        });
    }

    private static ResourceLocation getArmorTexture(EntityLivingBase entity, ItemStack item, String def, EntityEquipmentSlot slot, @Nullable String type) {
        return HUMAN_ARMOUR.computeIfAbsent(ForgeProxy.getArmorTexture(entity, item, def, slot, type), ResourceLocation::new);
    }

    private static ModelPonyArmor getArmorModel(EntityLivingBase entity, ItemStack itemstack, EntityEquipmentSlot slot, ArmorLayer layer, ModelPonyArmor def) {
        ModelBase model = ForgeProxy.getArmorModel(entity, itemstack, slot, def);

        if (model instanceof ModelPonyArmor) {
            return (ModelPonyArmor)model;
        }

        if (model instanceof IEquestrianArmor) {
            return ((IEquestrianArmor) model).getArmorForLayer(layer);
        }

        return def;
    }

}
