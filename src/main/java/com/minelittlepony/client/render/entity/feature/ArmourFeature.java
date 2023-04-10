package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.armour.*;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.armour.DefaultArmourTextureResolver;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.common.util.Color;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;

public class ArmourFeature<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyFeature<T, M> {

    public ArmourFeature(IPonyRenderContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        ModelWrapper<T, M> pony = getModelWrapper();

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.ARMOR) {
                renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.INNER);
                renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.OUTER);
            }
        }
    }

    public static <T extends LivingEntity, V extends BipedEntityModel<T> & IArmourModel<T>> void renderArmor(
            ModelWrapper<T, ? extends IPonyModel<T>> pony, MatrixStack matrices,
                    VertexConsumerProvider renderContext, int light, T entity,
                    float limbDistance, float limbAngle,
                    float age, float headYaw, float headPitch,
                    EquipmentSlot armorSlot, ArmourLayer layer) {

        ItemStack stack = entity.getEquippedStack(armorSlot);

        if (stack.isEmpty()) {
            return;
        }

        IArmourTextureResolver resolver = DefaultArmourTextureResolver.INSTANCE;
        Identifier texture = resolver.getTexture(entity, stack, armorSlot, layer, null);
        ArmourVariant variant = resolver.getVariant(layer, texture);

        boolean glint = stack.hasGlint();
        Item item = stack.getItem();

        pony.getArmourModel(stack, layer, variant)
                .filter(m -> m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body()))
                .ifPresent(model -> {
            float red = 1;
            float green = 1;
            float blue = 1;

            if (item instanceof DyeableArmorItem dyeable) {
                int color = dyeable.getColor(stack);
                red = Color.r(color);
                green = Color.g(color);
                blue = Color.b(color);
            }

            model.render(matrices, getArmorConsumer(renderContext, texture, glint), light, OverlayTexture.DEFAULT_UV, red, green, blue, 1);

            if (item instanceof DyeableArmorItem) {
                Identifier tex = resolver.getTexture(entity, stack, armorSlot, layer, "overlay");
                pony.getArmourModel(stack, layer, resolver.getVariant(layer, tex))
                        .filter(m -> m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body()))
                        .ifPresent(m -> {
                    m.render(matrices, getArmorConsumer(renderContext, tex, false), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                });
            }
        });
    }

    private static VertexConsumer getArmorConsumer(VertexConsumerProvider provider, Identifier texture, boolean glint) {
        return ItemRenderer.getArmorGlintConsumer(provider, RenderLayer.getArmorCutoutNoCull(texture), false, glint);
    }

}
