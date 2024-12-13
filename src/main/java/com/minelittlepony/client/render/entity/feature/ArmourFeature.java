package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentModel;

import org.jetbrains.annotations.Nullable;

public class ArmourFeature<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

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
            EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);

            if (hasModel(equippableComponent, armorSlot)) {
                equipmentRenderer.render(armorSlot, layerType, equippableComponent.model().orElseThrow(), entity, models, stack, matrices, vertices, light);
            }
        }

        plugin.onArmourRendered(entity, matrices, vertices, armorSlot, layerType, ArmourRendererPlugin.ArmourType.ARMOUR);
    }

    private static boolean hasModel(@Nullable EquippableComponent component, EquipmentSlot slot) {
        return component != null && component.model().isPresent() && component.slot() == slot;
    }
}
