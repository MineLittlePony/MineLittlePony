package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.Models;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

public class ArmourFeature<T extends LivingEntity, S extends PonyRenderState, M extends EntityModel<? super S> & PonyModel<S>> extends AbstractPonyFeature<S, M> {

    private final PonifiedEquipmentRenderer equipmentRenderer;

    public ArmourFeature(PonyRenderContext<T, S, M> context, EquipmentModelLoader modelLoader) {
        super(context);
        this.equipmentRenderer = new PonifiedEquipmentRenderer(modelLoader);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, S entity, float limbDistance, float limbAngle) {
        renderArmor(getModelWrapper(), matrices, provider, light, entity, limbDistance, limbAngle, equipmentRenderer);
    }

    public static <S extends PonyRenderState, V extends PonyArmourModel<S>> void renderArmor(
            Models<? extends PonyModel<S>> pony, MatrixStack matrices,
                    VertexConsumerProvider provider, int light, S entity,
                    float limbDistance, float limbAngle, PonifiedEquipmentRenderer equipmentRenderer) {

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                renderArmor(pony, matrices, provider, light, entity, limbDistance, limbAngle, i, ArmourLayer.INNER, equipmentRenderer);
                renderArmor(pony, matrices, provider, light, entity, limbDistance, limbAngle, i, ArmourLayer.OUTER, equipmentRenderer);
            }
        }
    }

    private static <S extends PonyRenderState, V extends PonyArmourModel<S>> void renderArmor(
            Models<? extends PonyModel<S>> models, MatrixStack matrices,
                    VertexConsumerProvider vertices, int light, S entity,
                    float limbDistance, float limbAngle,
                    EquipmentSlot armorSlot, ArmourLayer layer, PonifiedEquipmentRenderer equipmentRenderer) {

        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(entity, armorSlot, layer, ArmourRendererPlugin.ArmourType.ARMOUR)) {
            EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);

            if (equippableComponent != null && hasModel(equippableComponent, armorSlot)) {
                EquipmentModel.LayerType layerType = layer == ArmourLayer.INNER
                        ? EquipmentModel.LayerType.HUMANOID_LEGGINGS
                        : EquipmentModel.LayerType.HUMANOID;
                Identifier modelId = equippableComponent.model().orElseThrow();
                equipmentRenderer.render(armorSlot, layerType, modelId, models, stack, matrices, vertices, light);
            }
        }

        plugin.onArmourRendered(entity, matrices, vertices, armorSlot, layer, ArmourRendererPlugin.ArmourType.ARMOUR);
    }

    private static boolean hasModel(EquippableComponent component, EquipmentSlot slot) {
        return component.model().isPresent() && component.slot() == slot;
    }
}
