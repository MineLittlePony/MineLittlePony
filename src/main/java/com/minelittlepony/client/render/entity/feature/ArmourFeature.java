package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.util.render.MatrixStackUtil;

import java.util.*;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Unit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ArmourFeature<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {
    private static final Logger LOGGER = LogManager.getLogger("PonifiedEquipmentRenderer");

    private static boolean FABRIC_API_FAILURE;

    private final PonifiedEquipmentRenderer equipmentRenderer;

    public ArmourFeature(PonyRenderContext<T, S, M> context, EquipmentModelLoader modelLoader) {
        super(context);
        this.equipmentRenderer = new PonifiedEquipmentRenderer(modelLoader);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, S entity, float limbDistance, float limbAngle) {
        renderArmor(getModelWrapper(), matrices, provider, light, entity, limbDistance, limbAngle, equipmentRenderer);
    }

    public static <S extends PonyRenderState, V extends ClientPonyModel<S>> void renderArmor(
            Models<V> pony, MatrixStack matrices,
            VertexConsumerProvider provider, int light, S entity,
            float limbDistance, float limbAngle, PonifiedEquipmentRenderer equipmentRenderer) {

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                renderArmor(pony, matrices, provider, light, entity, limbDistance, limbAngle, i, EquipmentModel.LayerType.HUMANOID_LEGGINGS, equipmentRenderer);
                renderArmor(pony, matrices, provider, light, entity, limbDistance, limbAngle, i, EquipmentModel.LayerType.HUMANOID, equipmentRenderer);
            }
        }
    }

    private static <S extends PonyRenderState, V extends ClientPonyModel<S>> void renderArmor(
            Models<V> models, MatrixStack matrices,
            VertexConsumerProvider vertices, int light, S entity,
            float limbDistance, float limbAngle,
            EquipmentSlot armorSlot, EquipmentModel.LayerType layerType, PonifiedEquipmentRenderer equipmentRenderer) {

        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(entity, armorSlot, layerType, ArmourRendererPlugin.ArmourType.ARMOUR)) {
            render(armorSlot, layerType, entity, models, stack, matrices, vertices, light, equipmentRenderer);
        }

        plugin.onArmourRendered(entity, matrices, vertices, armorSlot, layerType, ArmourRendererPlugin.ArmourType.ARMOUR);
    }

    private static <S extends PonyRenderState, V extends ClientPonyModel<S>> void render(
            EquipmentSlot slot,
            EquipmentModel.LayerType layerType,
            S entity,
            Models<V> models,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light, PonifiedEquipmentRenderer equipmentRenderer
        ) {
        if (!FABRIC_API_FAILURE && PonyConfig.getInstance().enableFabricModelsApiSupport.get()) {
            try {
                if (FabricArmorRendererInvoker.renderArmor(stack, models, matrices, vertices, light, entity, slot, layerType)) {
                    return;
                }
            } catch (Throwable t) {
                LOGGER.error("Failure calling fabric armor rendering api", t);
                FABRIC_API_FAILURE = true;
            }
        }
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        if (hasModel(equippableComponent, slot)) {
            equipmentRenderer.render(slot, layerType, equippableComponent.assetId().orElseThrow(), entity, models, stack, matrices, vertices, light, null);
        }
    }

    private static boolean hasModel(@Nullable EquippableComponent component, EquipmentSlot slot) {
        return component != null && component.assetId().isPresent() && component.slot() == slot;
    }

    private static final class FabricArmorRendererInvoker {
        private static final Map<ArmorRenderer, Unit> FAILING_RENDERERS = new WeakHashMap<>();

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static <S extends PonyRenderState, V extends ClientPonyModel<S>> boolean renderArmor(
                ItemStack stack,
                Models<V> models, MatrixStack matrices,
                VertexConsumerProvider vertices, int light, S entity,
                EquipmentSlot armorSlot, EquipmentModel.LayerType layerType) {
            ArmorRenderer renderer = ArmorRendererRegistryImpl.get(stack.getItem());

            if (renderer != null && !FAILING_RENDERERS.containsKey(renderer)) {
                MatrixStack isolation = MatrixStackUtil.pushIsolation(matrices);
                try {
                    isolation.push();
                    models.body().transform(entity, getBodyPart(armorSlot), isolation);
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
