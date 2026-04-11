package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

public class GlowingEyesFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends RenderLayer<S, M> {

    private final TextureSupplier<S> textureSupplier;

    public GlowingEyesFeature(PonyRenderContext<?, S, M> context, TextureSupplier<S> textureSupplier) {
        super(context.upcast());
        this.textureSupplier = textureSupplier;
    }

    public GlowingEyesFeature(PonyRenderContext<?, S, M> context, Identifier texture) {
        this(context, TextureSupplier.of(texture));
    }


    @Override
    public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, S state, float xRot, float yRot) {
        queue.order(1).submitModel(getParentModel(), state, matrices, RenderTypes.eyes(textureSupplier.apply(state)), light, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
    }
}
