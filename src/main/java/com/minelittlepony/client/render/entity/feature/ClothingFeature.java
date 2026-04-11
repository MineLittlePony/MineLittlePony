package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelKey;
import com.mojang.blaze3d.vertex.PoseStack;

// separate class in case I need it later
public class ClothingFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends RenderLayer<S, M> {

    protected final RenderLayerParent<S, M> context;
    private final M model;
    private final Identifier texture;

    public ClothingFeature(RenderLayerParent<S, M> context, ModelKey<? super M> model, Identifier texture) {
        super(context);
        this.context = context;
        this.model = model.createModel();
        this.texture = texture;
    }

    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector frame, int light, S state, float yRot, float xRot) {
        model.setupAnim(state);
        frame.order(1).submitModel(model, state, matrices, model.renderType(texture), light, OverlayTexture.NO_OVERLAY, CommonColors.WHITE, null, state.outlineColor, null);
    }
}