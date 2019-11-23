package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.client.ForgeProxy;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.armour.DefaultArmourTextureResolver;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.util.render.Color;
import com.minelittlepony.model.armour.ArmourLayer;
import com.minelittlepony.model.armour.IArmour;
import com.minelittlepony.model.armour.IArmourTextureResolver;
import com.minelittlepony.model.armour.IEquestrianArmour;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
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
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        pony = getContext().getModelWrapper();

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.ARMOR) {
                renderArmor(stack, renderContext, lightUv, entity, limbDistance, limbAngle, tickDelta, age, headYaw, headPitch, i, ArmourLayer.INNER);
                renderArmor(stack, renderContext, lightUv, entity, limbDistance, limbAngle, tickDelta, age, headYaw, headPitch, i, ArmourLayer.OUTER);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <V extends BipedEntityModel<T> & IArmour> void renderArmor(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch, EquipmentSlot armorSlot, ArmourLayer layer) {
        ItemStack itemstack = entity.getEquippedStack(armorSlot);

        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ArmorItem) {

            V armour = LayerPonyArmor.getArmorModel(entity, itemstack, armorSlot, layer, pony.<V>getArmor().getArmorForLayer(layer));

            if (armour.prepareToRender(armorSlot, layer)) {
                ((BipedEntityModel<T>)pony.getBody()).setAttributes(armour);
                armour.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch);
                armour.synchroniseLegs(pony.getBody());

                IArmourTextureResolver<T> resolver = armour instanceof IArmourTextureResolver ? (IArmourTextureResolver<T>)armour : (IArmourTextureResolver<T>)textures;

                Identifier armourTexture = resolver.getArmourTexture(entity, itemstack, armorSlot, layer, null);
                armour.setVariant(resolver.getArmourVariant(layer, armourTexture));

                boolean glint = itemstack.hasEnchantmentGlint();

                ArmorItem itemarmor = (ArmorItem) itemstack.getItem();

                if (itemarmor.getMaterial() == ArmorMaterials.LEATHER) {

                    float red = 1;
                    float green = 1;
                    float blue = 1;

                    if (itemarmor instanceof DyeableArmorItem) {
                        int color = ((DyeableArmorItem)itemarmor).getColor(itemstack);
                        red = Color.r(color);
                        green = Color.g(color);
                        blue = Color.b(color);
                    }

                    VertexConsumer vertices = ItemRenderer.getArmorVertexConsumer(renderContext, RenderLayer.getEntityCutoutNoCull(armourTexture), false, glint);

                    armour.render(stack, vertices, lightUv, OverlayTexture.DEFAULT_UV, red, green, blue, 1);
                    armourTexture = resolver.getArmourTexture(entity, itemstack, armorSlot, layer, "overlay");
                    armour.setVariant(resolver.getArmourVariant(layer, armourTexture));
                }

                VertexConsumer vertices = ItemRenderer.getArmorVertexConsumer(renderContext, RenderLayer.getEntityCutoutNoCull(armourTexture), false, glint);
                armour.render(stack, vertices, lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
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
