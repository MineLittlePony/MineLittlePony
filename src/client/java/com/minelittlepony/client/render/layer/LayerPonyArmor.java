package com.minelittlepony.client.render.layer;

import com.minelittlepony.client.ForgeProxy;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.armour.DefaultArmourTextureResolver;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.util.render.Color;
import com.minelittlepony.model.IPonyModel;
import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.IArmour;
import com.minelittlepony.model.armour.IArmourTextureResolver;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class LayerPonyArmor<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyLayer<T, M> {

    private static final IArmourTextureResolver<LivingEntity> textures = new DefaultArmourTextureResolver<>();

    private ModelWrapper<T, M> pony;

    public LayerPonyArmor(IPonyRender<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        pony = getContext().getModelWrapper();

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.ARMOR) {
                renderArmor(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, i, ArmourLayer.INNER);
                renderArmor(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, i, ArmourLayer.OUTER);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <V extends BipedEntityModel<T> & IArmour> void renderArmor(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EquipmentSlot armorSlot, ArmourLayer layer) {
        ItemStack itemstack = entity.getEquippedStack(armorSlot);

        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ArmorItem) {

            V armour = LayerPonyArmor.getArmorModel(entity, itemstack, armorSlot, layer, pony.<V>getArmor().getArmorForLayer(layer));

            if (armour.prepareToRender(armorSlot, layer)) {
                ((BipedEntityModel<T>)pony.getBody()).setAttributes(armour);
                armour.setAngles(entity, move, swing, ticks, headYaw, headPitch, scale);
                armour.synchroniseLegs(pony.getBody());

                IArmourTextureResolver<T> resolver = armour instanceof IArmourTextureResolver ? (IArmourTextureResolver<T>)armour : (IArmourTextureResolver<T>)textures;

                Identifier armourTexture = resolver.getArmourTexture(entity, itemstack, armorSlot, layer, null);

                getContext().bindTexture(armourTexture);

                ArmorItem itemarmor = (ArmorItem) itemstack.getItem();

                if (itemarmor.getMaterial() == ArmorMaterials.LEATHER) {
                    if (itemarmor instanceof DyeableArmorItem) {
                        Color.glColor(((DyeableArmorItem)itemarmor).getColor(itemstack), 1);
                    }

                    armour.render(entity, move, swing, ticks, headYaw, headPitch, scale);
                    armourTexture = resolver.getArmourTexture(entity, itemstack, armorSlot, layer, "overlay");
                    getContext().bindTexture(armourTexture);
                }

                GlStateManager.color4f(1, 1, 1, 1);
                armour.render(entity, move, swing, ticks, headYaw, headPitch, scale);

                if (itemstack.hasEnchantmentGlint()) {
                    ArmorFeatureRenderer.renderEnchantedGlint(this::bindTexture, entity, armour, move, swing, partialTicks, ticks, headYaw, headPitch, scale);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity, V extends BipedEntityModel<T> & IArmour> V getArmorModel(T entity, ItemStack itemstack, EquipmentSlot slot, ArmourLayer layer, V def) {
        BipedEntityModel<T> model = ForgeProxy.getArmorModel(entity, itemstack, slot, def);

        if (model instanceof IArmour) {
            return (V)model;
        }

        if (model instanceof IEquestrianArmour) {
            return ((IEquestrianArmour<V>) model).getArmorForLayer(layer);
        }

        return def;
    }
}
