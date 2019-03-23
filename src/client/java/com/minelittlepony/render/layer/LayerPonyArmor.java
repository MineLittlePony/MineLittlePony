package com.minelittlepony.render.layer;

import com.minelittlepony.ForgeProxy;
import com.minelittlepony.model.ModelWrapper;
import com.minelittlepony.model.armour.IEquestrianArmour;
import com.minelittlepony.model.armour.IArmourTextureResolver;
import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.DefaultPonyArmorTextureResolver;
import com.minelittlepony.model.capabilities.IModelArmour;
import com.minelittlepony.util.render.Color;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerPonyArmor<T extends EntityLivingBase> extends AbstractPonyLayer<T> {

    private static final IArmourTextureResolver<EntityLivingBase> textures = new DefaultPonyArmorTextureResolver<>();

    private ModelWrapper pony;

    public LayerPonyArmor(RenderLivingBase<T> renderer) {
        super(renderer);
    }

    @Override
    public void doPonyRender(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {
        pony = getPonyRenderer().getModelWrapper();

        for (EntityEquipmentSlot i : EntityEquipmentSlot.values()) {
            if (i.getSlotType() == Type.ARMOR) {
                renderArmor(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, i, ArmourLayer.INNER);
                renderArmor(entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, i, ArmourLayer.OUTER);
            }
        }
    }

    private <V extends ModelBiped & IModelArmour> void renderArmor(T entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EntityEquipmentSlot armorSlot, ArmourLayer layer) {
        ItemStack itemstack = entity.getItemStackFromSlot(armorSlot);

        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemArmor) {

            @SuppressWarnings("unchecked")
            V armour = getArmorModel(entity, itemstack, armorSlot, layer, (V)pony.getArmor().getArmorForLayer(layer));

            if (armour.prepareToRender(armorSlot, layer)) {

                armour.setModelAttributes(pony.getBody());
                armour.setRotationAngles(move, swing, ticks, headYaw, headPitch, scale, entity);
                armour.synchroniseLegs(pony.getBody());

                @SuppressWarnings("unchecked")
                IArmourTextureResolver<T> resolver = armour instanceof IArmourTextureResolver ? (IArmourTextureResolver<T>)armour : (IArmourTextureResolver<T>)textures;

                ResourceLocation armourTexture = resolver.getArmourTexture(entity, itemstack, armorSlot, layer, null);

                getRenderer().bindTexture(armourTexture);

                ItemArmor itemarmor = (ItemArmor) itemstack.getItem();

                if (itemarmor.getArmorMaterial() == ArmorMaterial.LEATHER) {
                    Color.glColor(itemarmor.getColor(itemstack), 1);
                    armour.render(entity, move, swing, ticks, headYaw, headPitch, scale);
                    armourTexture = resolver.getArmourTexture(entity, itemstack, armorSlot, layer, "overlay");
                    getRenderer().bindTexture(armourTexture);
                }

                GlStateManager.color(1, 1, 1, 1);
                armour.render(entity, move, swing, ticks, headYaw, headPitch, scale);

                if (itemstack.isItemEnchanted()) {
                    LayerArmorBase.renderEnchantedGlint(getRenderer(), entity, armour, move, swing, partialTicks, ticks, headYaw, headPitch, scale);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <V extends ModelBiped & IModelArmour> V getArmorModel(EntityLivingBase entity, ItemStack itemstack, EntityEquipmentSlot slot, ArmourLayer layer, V def) {
        ModelBase model = ForgeProxy.getArmorModel(entity, itemstack, slot, def);

        if (model instanceof IModelArmour) {
            return (V)model;
        }

        if (model instanceof IEquestrianArmour) {
            return ((IEquestrianArmour<V>) model).getArmorForLayer(layer);
        }

        return def;
    }
}
