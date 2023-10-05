package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.client.render.ArmorRenderLayers;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.common.util.Color;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;

public class ArmourFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {

    public ArmourFeature(PonyRenderContext<T, M> context, BakedModelManager bakery) {
        super(context);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        Models<T, M> pony = getModelWrapper();

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.ARMOR) {
                renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.INNER);
                renderArmor(pony, stack, renderContext, lightUv, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.OUTER);
            }
        }
    }

    public static <T extends LivingEntity, V extends PonyArmourModel<T>> void renderArmor(
            Models<T, ? extends PonyModel<T>> pony, MatrixStack matrices,
                    VertexConsumerProvider renderContext, int light, T entity,
                    float limbDistance, float limbAngle,
                    float age, float headYaw, float headPitch,
                    EquipmentSlot armorSlot, ArmourLayer layer) {

        ItemStack stack = entity.getEquippedStack(armorSlot);

        if (stack.isEmpty()) {
            return;
        }

        Identifier texture = ArmourTextureResolver.INSTANCE.getTexture(entity, stack, armorSlot, layer, null);
        ArmourVariant variant = ArmourTextureResolver.INSTANCE.getVariant(layer, texture);

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
                Identifier tex = ArmourTextureResolver.INSTANCE.getTexture(entity, stack, armorSlot, layer, "overlay");
                pony.getArmourModel(stack, layer, ArmourTextureResolver.INSTANCE.getVariant(layer, tex))
                        .filter(m -> m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body()))
                        .ifPresent(m -> {
                    m.render(matrices, getArmorConsumer(renderContext, tex, false), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                });
            }

            if (stack.getItem() instanceof ArmorItem armor) {
                ArmorTrim.getTrim(entity.getWorld().getRegistryManager(), stack, true).ifPresent(trim -> {
                    pony.getArmourModel(stack, layer, ArmourVariant.TRIM)
                            .filter(m -> m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body()))
                            .ifPresent(m -> {
                        m.render(matrices, getTrimConsumer(renderContext, armor.getMaterial(), trim, layer, glint), light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                    });
                });
            }
        });
    }

    private static VertexConsumer getArmorConsumer(VertexConsumerProvider provider, Identifier texture, boolean glint) {
        return ItemRenderer.getArmorGlintConsumer(provider, ArmorRenderLayers.getArmorTranslucentNoCull(texture, false), false, glint);
    }

    private static VertexConsumer getTrimConsumer(VertexConsumerProvider provider, ArmorMaterial material, ArmorTrim trim, ArmourLayer layer, boolean glint) {
        SpriteAtlasTexture armorTrimsAtlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
        Sprite sprite = armorTrimsAtlas.getSprite(
            layer == ArmourLayer.INNER ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material)
        );
        RenderLayer renderLayer = ArmorRenderLayers.getArmorTranslucentNoCull(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE, trim.getPattern().value().decal());

        return sprite.getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(provider, renderLayer, true, glint));
    }
}
