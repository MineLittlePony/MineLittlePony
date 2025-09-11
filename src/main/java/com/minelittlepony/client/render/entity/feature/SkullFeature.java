package com.minelittlepony.client.render.entity.feature;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.client.render.entity.state.PonyRenderState.EquippedHeadRenderState;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;

import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;

public class SkullFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    protected final ItemModelManager itemModelResolver;
    private final HeadFeatureRenderer.HeadTransformation headTransformation;
    private final LoadedEntityModels models;

    public SkullFeature(PonyRenderContext<?, S, M> context, LoadedEntityModels models, ItemModelManager itemModelResolver, HeadFeatureRenderer.HeadTransformation headTransformation, boolean scaleForChild) {
        super(context);
        this.itemModelResolver = itemModelResolver;
        this.headTransformation = headTransformation;
        this.models = models;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertices, int light, S state, float limbAngle, float limbDistance) {
        for (EquippedHeadRenderState headState : state.equippedHeads) {
            matrices.push();

            M model = getModelWrapper().body();

            model.transform(state, BodyPart.HEAD, matrices);
            model.getHead().rotate(matrices);

            float f = 1.1F;
            matrices.scale(f, f, f);

            if (headState.skullType() != null) {
                float n = 1.1875F;
                matrices.scale(n, -n, -n);
                matrices.translate(0, -0.1F, 0.1F);
                matrices.translate(-0.5, 0, -0.5);
                SkullBlockEntityRenderer.renderSkull(null, 180, state.headItemAnimationProgress, matrices, vertices, light,
                        SkullBlockEntityRenderer.getModels(models, headState.skullType()),
                        SkullBlockEntityRenderer.getRenderLayer(headState.skullType(), headState.wearingSkullProfile())
                );
            } else {
                matrices.translate(0, 0.1F, -0.1F);
                HeadFeatureRenderer.translate(matrices, headTransformation);
                headState.item().render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
            }

            matrices.pop();
        }

        ArmourRendererPlugin.INSTANCE.get().onArmourRendered(state, matrices, vertices, EquipmentSlot.BODY, EquipmentModel.LayerType.HUMANOID, ArmourRendererPlugin.ArmourType.SKULL);
    }
}
