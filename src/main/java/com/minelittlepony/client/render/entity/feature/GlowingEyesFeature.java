package com.minelittlepony.client.render.entity.feature;

import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class GlowingEyesFeature<
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends FeatureRenderer<S, M> {

    private final TextureSupplier<S> textureSupplier;

    public GlowingEyesFeature(PonyRenderContext<?, S, M> context, TextureSupplier<S> textureSupplier) {
        super(context.upcast());
        this.textureSupplier = textureSupplier;
    }

    public GlowingEyesFeature(PonyRenderContext<?, S, M> context, Identifier texture) {
        this(context, TextureSupplier.of(texture));
    }


    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        queue.getBatchingQueue(1)
            .submitModel(this.getContextModel(), state, matrices, RenderLayers.eyes(textureSupplier.apply(state)), light, OverlayTexture.DEFAULT_UV, -1, null, state.outlineColor, null);
    }
}
