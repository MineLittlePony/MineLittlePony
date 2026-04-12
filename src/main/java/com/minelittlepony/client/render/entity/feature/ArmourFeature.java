package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.*;
import com.minelittlepony.api.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.*;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.util.render.MatrixStackUtil;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.*;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

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

    public ArmourFeature(PonyRenderContext<T, S, M> context, EquipmentAssetManager equipmentAssets, TextureAtlas armorTrimsAtlas) {
        super(context);
        this.equipmentRenderer = new PonifiedEquipmentRenderer(equipmentAssets, armorTrimsAtlas);
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, S entity, float xRot, float yRot) {
        renderArmor(getContext().getEquineManager().lookupModel(entity), matrices, queue, light, entity, equipmentRenderer);
    }

    public static <S extends PonyRenderState, V extends ClientPonyModel<S>> void renderArmor(
            Models<V> pony, PoseStack matrices,
            SubmitNodeCollector queue, int light, S entity,
            PonifiedEquipmentRenderer equipmentRenderer) {

        for (EquipmentSlot i : EquipmentSlot.values()) {
            if (i.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                renderArmor(pony, matrices, queue, light, entity, i, EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS, equipmentRenderer);
                renderArmor(pony, matrices, queue, light, entity, i, EquipmentClientInfo.LayerType.HUMANOID, equipmentRenderer);
            }
        }
    }

    private static <S extends PonyRenderState, V extends ClientPonyModel<S>> void renderArmor(
            Models<V> models, PoseStack matrices,
            SubmitNodeCollector queue, int light, S entity,
            EquipmentSlot armorSlot, EquipmentClientInfo.LayerType layerType, PonifiedEquipmentRenderer equipmentRenderer) {

        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(entity, armorSlot, layerType, ArmourRendererPlugin.ArmourType.ARMOUR)) {
            render(armorSlot, layerType, entity, models, stack, matrices, queue, light, equipmentRenderer);
        }

        plugin.onArmourRendered(entity, matrices, queue, armorSlot, layerType, ArmourRendererPlugin.ArmourType.ARMOUR);
    }

    private static <S extends PonyRenderState, V extends ClientPonyModel<S>> void render(
            EquipmentSlot slot,
            EquipmentClientInfo.LayerType layerType,
            S state,
            Models<V> models,
            ItemStack stack,
            PoseStack matrices,
            SubmitNodeCollector queue,
            int light, PonifiedEquipmentRenderer equipmentRenderer
        ) {
        if (!FABRIC_API_FAILURE && PonyConfig.getInstance().enableFabricModelsApiSupport.get()) {
            try {
                if (FabricArmorRendererInvoker.renderArmor(stack, models, matrices, queue, light, state, slot, layerType)) {
                    return;
                }
            } catch (Throwable t) {
                LOGGER.error("Failure calling fabric armor rendering api", t);
                FABRIC_API_FAILURE = true;
            }
        }
        Equippable equippableComponent = stack.get(DataComponents.EQUIPPABLE);
        if (hasModel(equippableComponent, slot) && (slot != EquipmentSlot.HEAD || state.headVisible)) {
            equipmentRenderer.renderLayers(slot, layerType, equippableComponent.assetId().orElseThrow(), state, models, stack, matrices, queue, light, null, state.outlineColor, 1);
        }
    }

    private static boolean hasModel(@Nullable Equippable component, EquipmentSlot slot) {
        return component != null && component.assetId().isPresent() && component.slot() == slot;
    }

    private static final class FabricArmorRendererInvoker {
        private static final Map<ArmorRenderer, Unit> FAILING_RENDERERS = new WeakHashMap<>();

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static <S extends PonyRenderState, V extends ClientPonyModel<S>> boolean renderArmor(
                ItemStack stack,
                Models<V> models, PoseStack matrices,
                SubmitNodeCollector queue, int light, S entity,
                EquipmentSlot armorSlot, EquipmentClientInfo.LayerType layerType) {
            ArmorRenderer renderer = ArmorRendererRegistryImpl.get(stack.getItem());

            if (renderer != null && !FAILING_RENDERERS.containsKey(renderer)) {
                PoseStack isolation = MatrixStackUtil.pushIsolation(matrices);
                try {
                    isolation.pushPose();
                    models.body().transform(entity, getBodyPart(armorSlot), isolation);
                    renderer.render(isolation, queue, stack, entity, armorSlot, light, (HumanoidModel)models.body());
                    isolation.popPose();
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
                case CHEST, BODY, SADDLE -> BodyPart.BODY;
                case LEGS, FEET, MAINHAND, OFFHAND -> BodyPart.LEGS;
            };
        }
    }
}
