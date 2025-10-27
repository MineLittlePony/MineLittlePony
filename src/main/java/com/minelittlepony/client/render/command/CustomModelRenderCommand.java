package com.minelittlepony.client.render.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.*;
import net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;

import org.jetbrains.annotations.Nullable;

import java.util.function.*;

public record CustomModelRenderCommand<S>(
        MatrixStack matrices,
        OrderedRenderCommandQueueImpl.ModelCommand<S> command,
        RenderLayer layer,
        @Nullable BiFunction<CustomModelRenderCommand<S>, VertexConsumerProvider, VertexConsumer> bufferFunc,
        @Nullable Predicate<CustomModelRenderCommand<S>> anglesFunc) implements OrderedRenderCommandQueue.Custom {

    public static <S> void submit(
            RenderCommandQueue queue,
            Model<? super S> model, S state,
            MatrixStack matrices, RenderLayer renderLayer,
            int light, int overlay, int tint,
            @Nullable Sprite sprite, int outline,
            @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay,
            @Nullable BiFunction<CustomModelRenderCommand<S>, VertexConsumerProvider, VertexConsumer> layerFunc,
            @Nullable Predicate<CustomModelRenderCommand<S>> anglesFunc) {
        queue.submitCustom(matrices, renderLayer, new CustomModelRenderCommand<>(
                new MatrixStack(),
                new OrderedRenderCommandQueueImpl.ModelCommand<>(matrices.peek(), model, state, light, overlay, tint, sprite, outline, crumblingOverlay),
                renderLayer,
                layerFunc,
                anglesFunc
        ));
    }

    public static <S> void submit(
            RenderCommandQueue queue,
            Model<? super S> model, S state,
            MatrixStack matrices, RenderLayer renderLayer,
            int light, int overlay, int outlineColor,
            @Nullable CrumblingOverlayCommand crumblingOverlay,
            @Nullable BiFunction<CustomModelRenderCommand<S>, VertexConsumerProvider, VertexConsumer> layerFunc,
            @Nullable Predicate<CustomModelRenderCommand<S>> anglesFunc) {
        submit(queue, model, state, matrices, renderLayer, light, overlay, -1, null, outlineColor, crumblingOverlay, layerFunc, anglesFunc);
    }

    @Override
    public void render(Entry matricesEntry, VertexConsumer buffer) {
        var provider = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        buffer = bufferFunc == null ? buffer : bufferFunc.apply(this, provider);
        if (buffer != null) {
            command.model().setAngles(command.state());
            if (anglesFunc == null || anglesFunc.test(this)) {
                matrices.push();
                matrices.peek().copy(matricesEntry);

                renderModel(buffer);

                if (command.outlineColor() != 0 && (layer.getAffectedOutline().isPresent() || layer.isOutline())) {
                    OutlineVertexConsumerProvider outlines = MinecraftClient.getInstance().getBufferBuilders().getOutlineVertexConsumers();
                    outlines.setColor(command.outlineColor());
                    renderModel(outlines.getBuffer(layer));
                }

                if (command.crumblingOverlay() != null && layer.hasCrumbling()) {
                    renderModel(new OverlayVertexConsumer(
                        MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers().getBuffer(ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(command.crumblingOverlay().progress())),
                        command.crumblingOverlay().cameraMatricesEntry(),
                        1
                    ));
                }

                matrices.pop();
            }
        }
    }

    private void renderModel(VertexConsumer buffer) {
        command.model().render(
            matrices,
            command.sprite() == null ? buffer : command.sprite().getTextureSpecificVertexConsumer(buffer),
            command.lightCoords(),
            command.overlayCoords(),
            command.tintedColor()
        );
    }
}
