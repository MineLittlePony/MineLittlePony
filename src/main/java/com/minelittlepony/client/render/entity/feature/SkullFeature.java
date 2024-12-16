package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.blockentity.skull.PonySkullRenderer;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;

public class SkullFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    protected final ItemModelManager itemModelResolver;
    private final HeadFeatureRenderer.HeadTransformation headTransformation;

    public SkullFeature(PonyRenderContext<?, S, M> context, ItemModelManager itemModelResolver, HeadFeatureRenderer.HeadTransformation headTransformation, boolean scaleForChild) {
        super(context);
        this.itemModelResolver = itemModelResolver;
        this.headTransformation = headTransformation;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider provider, int light, S state, float limbAngle, float limbDistance) {
        ArmourRendererPlugin plugin = ArmourRendererPlugin.INSTANCE.get();

        for (ItemStack stack : plugin.getArmorStacks(state, EquipmentSlot.HEAD, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL)) {

            if (stack.isEmpty()) {
                continue;
            }

            boolean isSkull = stack.getItem() instanceof BlockItem b && b.getBlock() instanceof AbstractSkullBlock;

            if (!isSkull) {
                if (!ArmorFeatureRenderer.hasModel(stack, EquipmentSlot.HEAD)) {
                    itemModelResolver.update(state.headItemRenderState, stack, ModelTransformationMode.HEAD, false, null, null, 0);
                } else {
                    state.headItemRenderState.clear();
                }

                if (state.headItemRenderState.isEmpty()) {
                    continue;
                }
            }

            matrices.push();

            M model = getModelWrapper().body();

            model.transform(state, BodyPart.HEAD, matrices);
            model.getHead().rotate(matrices);

            float f = 1.1F;
            matrices.scale(f, f, f);

            if (isSkull) {
                float n = 1.1875F;
                matrices.scale(n, -n, -n);
                matrices.translate(0, -0.1F, 0.1F);
                matrices.translate(-0.5, 0, -0.5);
                PonySkullRenderer.INSTANCE.renderSkull(matrices, provider, stack, state, state.age, light, true);
            } else if (!ArmorFeatureRenderer.hasModel(stack, EquipmentSlot.HEAD)) {
                matrices.translate(0, 0.1F, -0.1F);
                HeadFeatureRenderer.translate(matrices, headTransformation);
                state.headItemRenderState.render(matrices, provider, light, OverlayTexture.DEFAULT_UV);
            }

            matrices.pop();
        }

        plugin.onArmourRendered(state, matrices, provider, EquipmentSlot.BODY, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL);
    }
}
