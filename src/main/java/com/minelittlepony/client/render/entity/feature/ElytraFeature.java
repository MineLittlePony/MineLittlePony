package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.PonyElytra;
import com.minelittlepony.client.model.armour.ArmourLayer;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

public class ElytraFeature<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends EntityModel<? super S> & PonyModel<S>
    > extends AbstractPonyFeature<S, M> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/elytra.png");

    private final PonyElytra<S> model = ModelType.ELYTRA.createModel();

    private final EquipmentRenderer equipmentRenderer;

    public ElytraFeature(PonyRenderContext<T, S, M> context, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, S entity, float limbAngle, float limbDistance) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(entity, EquipmentSlot.CHEST, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.ELYTRA)) {
            EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);

            if (equippable != null && !equippable.model().isEmpty()) {
                Identifier equipmentModel = equippable.model().get();

                float alpha = plugin.getElytraAlpha(stack, model, entity);
                if (alpha <= 0) {
                    return;
                }

                matrices.push();
                model.setAngles(entity);
                preRenderCallback(entity, matrices);
                equipmentRenderer.render(EquipmentModel.LayerType.WINGS, equipmentModel, model, stack, matrices, provider, light, getElytraTexture(entity));
                matrices.pop();
            }
        }

        plugin.onArmourRendered(entity, matrices, provider, EquipmentSlot.BODY, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.ELYTRA);
    }

    protected void preRenderCallback(S state, MatrixStack stack) {
        M body = getModelWrapper().body();
        stack.translate(0, state.riderOffset, 0.125);
        body.transform(state, BodyPart.BODY, stack);
    }

    protected Identifier getElytraTexture(S state) {
        if (state instanceof PlayerEntityRenderState playerState) {
            SkinTextures textures = playerState.skinTextures;

            if (textures.elytraTexture() != null) {
                return textures.elytraTexture();
            }

            if (textures.capeTexture() != null && state.capeVisible) {
                return textures.capeTexture();
            }
        }

        return TEXTURE;
    }
}
