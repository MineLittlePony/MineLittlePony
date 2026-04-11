package com.minelittlepony.client.render.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.mojang.blaze3d.vertex.*;

import java.util.function.*;

public record CustomModelRenderCommand<S>(
        PoseStack matrices,
        SubmitNodeStorage.ModelSubmit<S> command,
        RenderType layer,
        @Nullable BiFunction<CustomModelRenderCommand<S>, BufferSource, VertexConsumer> bufferFunc,
        @Nullable Predicate<CustomModelRenderCommand<S>> anglesFunc) implements SubmitNodeCollector.CustomGeometryRenderer {

    public static <S> void submit(
            OrderedSubmitNodeCollector queue,
            Model<? super S> model, S state,
            PoseStack matrices, RenderType renderLayer,
            int light, int overlay, int tint,
            @Nullable TextureAtlasSprite sprite, int outline,
            @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
            @Nullable BiFunction<CustomModelRenderCommand<S>, BufferSource, VertexConsumer> layerFunc,
            @Nullable Predicate<CustomModelRenderCommand<S>> anglesFunc) {
        queue.submitCustomGeometry(matrices, renderLayer, new CustomModelRenderCommand<>(
                new PoseStack(),
                IrisApiCompat.iris$capture(new SubmitNodeStorage.ModelSubmit<>(matrices.last().copy(), model, state, light, overlay, tint, sprite, outline, crumblingOverlay)),
                renderLayer,
                layerFunc,
                anglesFunc
        ));
    }

    public static <S> void submit(
            OrderedSubmitNodeCollector queue,
            Model<? super S> model, S state,
            PoseStack matrices, RenderType renderLayer,
            int light, int overlay, int outline,
            @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
            @Nullable BiFunction<CustomModelRenderCommand<S>, BufferSource, VertexConsumer> layerFunc,
            @Nullable Predicate<CustomModelRenderCommand<S>> anglesFunc) {
        submit(queue, model, state, matrices, renderLayer, light, overlay, -1, null, outline, crumblingOverlay, layerFunc, anglesFunc);
    }

    @Override
    public void render(PoseStack.Pose pose, VertexConsumer buffer) {
        var provider = Minecraft.getInstance().renderBuffers().bufferSource();
        buffer = bufferFunc == null ? buffer : bufferFunc.apply(this, provider);
        if (buffer != null) {
            command.model().setupAnim(command.state());
            if (anglesFunc == null || anglesFunc.test(this)) {
                matrices.pushPose();
                matrices.last().set(pose);

                renderModel(buffer);

                if (command.outlineColor() != 0 && (layer.outline().isPresent() || layer.isOutline())) {
                    OutlineBufferSource outlines = Minecraft.getInstance().renderBuffers().outlineBufferSource();
                    outlines.setColor(command.outlineColor());
                    renderModel(outlines.getBuffer(layer));
                }

                if (command.crumblingOverlay() != null && layer.affectsCrumbling()) {
                    renderModel(new SheetedDecalTextureGenerator(
                        Minecraft.getInstance().renderBuffers().crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(command.crumblingOverlay().progress())),
                        command.crumblingOverlay().cameraPose(),
                        1
                    ));
                }

                matrices.popPose();

                if (bufferFunc != null) {
                    provider.endBatch();
                }
            }
        }
    }

    private void renderModel(VertexConsumer buffer) {
        matrices.pushPose();
        command.model().renderToBuffer(
            matrices,
            command.sprite() == null ? buffer : command.sprite().wrap(buffer),
            command.lightCoords(),
            command.overlayCoords(),
            command.tintedColor()
        );
        matrices.popPose();
    }
}
