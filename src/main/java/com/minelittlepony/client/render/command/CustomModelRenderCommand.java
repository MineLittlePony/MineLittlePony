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

import java.util.function.Function;
import java.util.function.Predicate;

public record CustomModelRenderCommand<S>(
        MatrixStack matrices,
        OrderedRenderCommandQueueImpl.ModelCommand<S> command,
        RenderLayer layer,
        @Nullable Function<OrderedRenderCommandQueueImpl.ModelCommand<S>, VertexConsumer> bufferFunc,
        @Nullable Predicate<OrderedRenderCommandQueueImpl.ModelCommand<S>> anglesFunc) implements OrderedRenderCommandQueue.Custom {

    public static <S> void submit(
            RenderCommandQueue queue,
            Model<? super S> model, S state,
            MatrixStack matrices, RenderLayer renderLayer,
            int light, int overlay, int tint,
            @Nullable Sprite sprite, int outline,
            @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay,
            @Nullable Function<OrderedRenderCommandQueueImpl.ModelCommand<S>, VertexConsumer> layerFunc,
            @Nullable Predicate<OrderedRenderCommandQueueImpl.ModelCommand<S>> anglesFunc) {
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
            @Nullable Function<OrderedRenderCommandQueueImpl.ModelCommand<S>, VertexConsumer> layerFunc,
            @Nullable Predicate<OrderedRenderCommandQueueImpl.ModelCommand<S>> anglesFunc) {
        submit(queue, model, state, matrices, renderLayer, light, overlay, -1, null, outlineColor, crumblingOverlay, layerFunc, anglesFunc);
    }

    @Override
    public void render(Entry matricesEntry, VertexConsumer buffer) {
        buffer = bufferFunc == null ? buffer : bufferFunc.apply(command);
        if (buffer != null) {
            command.model().setAngles(command.state());
            if (anglesFunc == null || anglesFunc.test(command)) {
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
