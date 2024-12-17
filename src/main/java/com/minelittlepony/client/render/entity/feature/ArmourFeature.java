package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.common.util.Color;

import java.util.*;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.util.render.MatrixStackUtil;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Colors;
import net.minecraft.util.Unit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArmourFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {
    private static final Logger LOGGER = LogManager.getLogger("PonifiedEquipmentRenderer");

    private static boolean FABRIC_API_FAILURE;

    public ArmourFeature(PonyRenderContext<T, M> context, BakedModelManager bakery) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        renderArmor(getModelWrapper(), matrices, provider, light, entity, limbDistance, limbAngle, age, headYaw, headPitch);
    }

    public static <T extends LivingEntity, V extends PonyArmourModel<T>> void renderArmor(
            Models<T, ? extends PonyModel<T>> pony, MatrixStack matrices,
                    VertexConsumerProvider provider, int light, T entity,
                    float limbDistance, float limbAngle,
                    float age, float headYaw, float headPitch) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                renderArmor(pony, matrices, provider, light, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.INNER, plugin);
                renderArmor(pony, matrices, provider, light, entity, limbDistance, limbAngle, age, headYaw, headPitch, i, ArmourLayer.OUTER, plugin);
            }
        }
    }

    private static <T extends LivingEntity, V extends PonyArmourModel<T>> void renderArmor(
            Models<T, ? extends PonyModel<T>> pony, MatrixStack matrices,
                    VertexConsumerProvider provider, int light, T entity,
                    float limbDistance, float limbAngle,
                    float age, float headYaw, float headPitch,
                    EquipmentSlot armorSlot, ArmourLayer layer, ArmourRendererPlugin plugin) {

        for (ItemStack stack : plugin.getArmorStacks(entity, armorSlot, layer, ArmourRendererPlugin.ArmourType.ARMOUR)) {
            if (stack.isEmpty()) {
                continue;
            }

            if (!FABRIC_API_FAILURE && PonyConfig.getInstance().enableFabricModelsApiSupport.get()) {
                try {
                    if (FabricArmorRendererInvoker.renderArmor(stack, pony, matrices, provider, light, entity, armorSlot)) {
                        continue;
                    }
                } catch (Throwable t) {
                    LOGGER.error("Failure calling fabric armor rendering api", t);
                    FABRIC_API_FAILURE = true;
                }
            }

            float glintAlpha = plugin.getGlintAlpha(armorSlot, stack);
            boolean glint = glintAlpha > 0;
            int color = plugin.getDyeColor(armorSlot, stack);

            Set<PonyArmourModel<?>> models = glint ? new HashSet<>() : null;

            ArmourTextureLookup textureLookup = plugin.getTextureLookup();

            float alpha = plugin.getArmourAlpha(armorSlot, layer);

            if (alpha > 0) {
                for (ArmorMaterial.Layer armorLayer : textureLookup.getArmorLayers(stack, color)) {
                    ArmourTexture layerTexture = textureLookup.getTexture(stack, layer, armorLayer);

                    if (layerTexture == ArmourTexture.UNKNOWN) {
                        continue;
                    }

                    var m = pony.getArmourModel(stack, layer, layerTexture.variant()).orElse(null);
                    if (m != null && m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body())) {
                        VertexConsumer armorConsumer = plugin.getArmourConsumer(armorSlot, provider, layerTexture.texture(), layer);
                        if (armorConsumer != null) {
                            int armorTint = Colors.WHITE;
                            if (armorLayer.isDyeable() && color != Colors.WHITE) {
                                armorTint = color;
                            }
                            m.render(matrices, armorConsumer, light, OverlayTexture.DEFAULT_UV, (armorTint & 0xFFFFFF) | ((int)(alpha * 255) << 24));
                        }
                        if (glint) {
                            models.add(m);
                        }
                    }
                }
            }

            ArmorTrim trim = stack.get(DataComponentTypes.TRIM);

            if (trim != null && stack.getItem() instanceof ArmorItem armor) {
                float trimAlpha = plugin.getTrimAlpha(armorSlot, armor.getMaterial(), trim, layer);
                if (trimAlpha > 0) {
                    var m = pony.getArmourModel(stack, layer, ArmourVariant.TRIM).orElse(null);
                    if (m != null && m.poseModel(entity, limbAngle, limbDistance, age, headYaw, headPitch, armorSlot, layer, pony.body())) {
                        VertexConsumer trimConsumer = plugin.getTrimConsumer(armorSlot, provider, armor.getMaterial(), trim, layer);
                        if (trimConsumer != null) {
                            m.render(matrices, trimConsumer, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
                        }
                    }
                }
            }

            if (glint) {
                VertexConsumer glintConsumer = plugin.getGlintConsumer(armorSlot, provider, layer);
                if (glintConsumer != null) {
                    for (var m : models) {
                        m.render(matrices, glintConsumer, light, OverlayTexture.DEFAULT_UV, Color.argbToHex(glintAlpha, 1, 1, 1));
                    }
                }
            }
        }

        plugin.onArmourRendered(entity, matrices, provider, armorSlot, layer, ArmourRendererPlugin.ArmourType.ARMOUR);
    }


    private static final class FabricArmorRendererInvoker {
        private static final Map<ArmorRenderer, Unit> FAILING_RENDERERS = new WeakHashMap<>();

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static <T extends LivingEntity, V extends ClientPonyModel<T>> boolean renderArmor(
                ItemStack stack,
                Models<T, ? extends PonyModel<T>> models, MatrixStack matrices,
                VertexConsumerProvider vertices, int light, T entity,
                EquipmentSlot armorSlot) {
            ArmorRenderer renderer = ArmorRendererRegistryImpl.get(stack.getItem());

            if (renderer != null && !FAILING_RENDERERS.containsKey(renderer)) {
                MatrixStack isolation = MatrixStackUtil.pushIsolation(matrices);
                try {
                    isolation.push();
                    models.body().transform(getBodyPart(armorSlot), isolation);
                    renderer.render(isolation, vertices, stack, entity, armorSlot, light, (BipedEntityModel)models.body());
                    isolation.pop();
                } catch (Throwable t) {
                    LOGGER.error("Exception occured whilst rendering custom armor via fabric api. Renderer {} has been disabled", renderer, t);
                    FAILING_RENDERERS.put(renderer, Unit.INSTANCE);
                } finally {
                    MatrixStackUtil.popIsolation();
                }
                return true;
            }
            return false;
        }

        private static BodyPart getBodyPart(EquipmentSlot slot) {
            return switch (slot) {
                case HEAD -> BodyPart.HEAD;
                case CHEST, BODY -> BodyPart.BODY;
                case LEGS, FEET, MAINHAND, OFFHAND -> BodyPart.LEGS;
            };
        }
    }
}
