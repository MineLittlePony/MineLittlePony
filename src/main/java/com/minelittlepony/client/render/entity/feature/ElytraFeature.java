package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.*;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import org.jetbrains.annotations.Nullable;

public class ElytraFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends FeatureRenderer<S, M> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/elytra.png");

    private final PonyElytra<S> model = ModelType.ELYTRA.createModel();

    private final FeatureRendererContext<S, M> context;
    private final EquipmentRenderer equipmentRenderer;

    public ElytraFeature(FeatureRendererContext<S, M> context, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.context = context;
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, S state, float limbAngle, float limbDistance) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(state, EquipmentSlot.CHEST, EquipmentModel.LayerType.WINGS, ArmourRendererPlugin.ArmourType.ELYTRA)) {
            EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);

            if (equippable != null && !equippable.model().isEmpty()) {
                Identifier equipmentModel = equippable.model().get();

                float alpha = plugin.getElytraAlpha(stack, model, state);
                if (alpha <= 0) {
                    return;
                }

                matrices.push();
                model.setAngles(state);
                preRenderCallback(state, matrices);
                equipmentRenderer.render(EquipmentModel.LayerType.WINGS, equipmentModel, model, stack, matrices, provider, light, getElytraTexture(state));
                matrices.pop();
            }
        }

        plugin.onArmourRendered(state, matrices, provider, EquipmentSlot.BODY, EquipmentModel.LayerType.WINGS, ArmourRendererPlugin.ArmourType.ELYTRA);
    }

    @SuppressWarnings("unchecked")
    protected void preRenderCallback(S state, MatrixStack stack) {
        if (state instanceof PonyRenderState ponyState && context instanceof PonyRenderContext context) {
            stack.translate(0, 0.45F, 0);
            ((ClientPonyModel<PonyRenderState>)context.getEquineManager().getModels().body()).transform(ponyState, BodyPart.BODY, stack);
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(80));
        }
    }

    @Nullable
    protected Identifier getElytraTexture(S state) {
        if (state instanceof PlayerEntityRenderState playerState) {
            SkinTextures textures = playerState.skinTextures;

            if (textures.elytraTexture() != null) {
                return textures.elytraTexture();
            }

            if (textures.capeTexture() != null && playerState.capeVisible) {
                return textures.capeTexture();
            }
        }

        return null;
    }
}
