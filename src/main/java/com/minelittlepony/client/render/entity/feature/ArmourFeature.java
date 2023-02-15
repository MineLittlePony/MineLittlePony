package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.armour.*;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.common.util.Color;

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
import net.minecraft.item.*;
import net.minecraft.util.Identifier;

public class ArmourFeature<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyFeature<T, M> {

    public ArmourFeature(IPonyRenderContext<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        ModelWrapper<T, M> pony = getContext().getModelWrapper();

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.ARMOR) {
                renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.INNER);
                renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.OUTER);
            }
        }
    }

    public static <T extends LivingEntity, V extends BipedEntityModel<T> & IArmourModel> void renderArmor(
            ModelWrapper<T, ? extends IPonyModel<T>> pony, MatrixStack stack,
                    VertexConsumerProvider renderContext, int lightUv, T entity,
                    float limbDistance, float limbAngle,
                    float age, float headYaw, float headPitch,
                    EquipmentSlot armorSlot, ArmourLayer layer) {

        ItemStack itemstack = entity.getEquippedStack(armorSlot);

        if (itemstack.isEmpty()) {
            return;
        }

        PonyArmourModel<T> model = pony.getArmourModel(itemstack, layer).orElse(null);
        if (model == null) {
            return;
        }

        model.setMetadata(pony.body().getMetadata());

        if (model.prepareToRender(armorSlot, layer)) {
            pony.body().copyAttributes(model);
            model.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch);
            model.synchroniseAngles(pony.body());

            Item item = itemstack.getItem();

            float red = 1;
            float green = 1;
            float blue = 1;

            if (item instanceof DyeableArmorItem) {
                int color = ((DyeableArmorItem)item).getColor(itemstack);
                red = Color.r(color);
                green = Color.g(color);
                blue = Color.b(color);
            }

            IArmourTextureResolver resolver = model.getArmourTextureResolver();

            boolean glint = itemstack.hasGlint();

            renderArmourPart(stack, renderContext, lightUv, glint, model, red, green, blue, resolver, layer, resolver.getTexture(entity, itemstack, armorSlot, layer, null));

            if (item instanceof DyeableArmorItem) {
                renderArmourPart(stack, renderContext, lightUv, false, model, 1, 1, 1, resolver, layer, resolver.getTexture(entity, itemstack, armorSlot, layer, "overlay"));
            }
        }
    }

    private static <T extends LivingEntity, V extends BipedEntityModel<T> & IArmourModel> void renderArmourPart(
            MatrixStack matrices, VertexConsumerProvider provider,
            int light, boolean glint, V model, float r, float g, float b, IArmourTextureResolver resolver, ArmourLayer layer, Identifier texture) {

        VertexConsumer vertices = ItemRenderer.getArmorGlintConsumer(provider, RenderLayer.getArmorCutoutNoCull(texture), false, glint);

        model.setVariant(resolver.getVariant(layer, texture));
        model.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, r, g, b, 1);
    }
}
