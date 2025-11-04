package com.minelittlepony.client.render.command;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.command.*;
import net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand;
import net.minecraft.client.render.command.OrderedRenderCommandQueue.Custom;
import net.minecraft.client.render.command.OrderedRenderCommandQueue.LayeredCustom;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState.LeashData;
import net.minecraft.client.render.entity.state.EntityRenderState.ShadowPiece;
import net.minecraft.client.render.item.ItemRenderState.Glint;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;

import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.util.List;
import java.util.function.Function;

public class MagicOverlayRenderCommandQueue implements RenderCommandQueue {
    static final float TRANSLATION_SCALE = 1/8F;

    private final OrderedRenderCommandQueue owner;
    private final RenderCommandQueue parent;
    protected final Function<RenderLayer, @Nullable RenderLayer> layer;
    protected final int color;
    protected final List<Pass> passes;

    private final float red;
    private final float green;
    private final float blue;

    public record Pass(Vec3d translation, float scale) { }

    public MagicOverlayRenderCommandQueue(OrderedRenderCommandQueue owner, RenderCommandQueue parent, Function<RenderLayer, @Nullable RenderLayer> layer, int color, List<Pass> passes) {
        this.owner = owner;
        this.parent = parent;
        this.layer = layer;
        this.color = ColorHelper.withAlpha(0.5F, color);
        this.passes = passes;
        red = ColorHelper.getRedFloat(color);
        green = ColorHelper.getGreenFloat(color);
        blue = ColorHelper.getBlueFloat(color);
    }

    public OrderedRenderCommandQueue unwrap() {
        return owner;
    }

    private void submitCustomPasses(MatrixStack matrices, RenderLayer layer, PassedCustom custom, float scaleMultiple, @Nullable Sprite sprite) {
        MatrixStack commandMatrix = new MatrixStack();
        parent.submitCustom(matrices, layer, (entry, buffer) -> {
            buffer = sprite == null ? buffer : sprite.getTextureSpecificVertexConsumer(buffer);
            commandMatrix.peek().copy(entry);
            for (var pass : passes) {
                commandMatrix.push();
                commandMatrix.translate(pass.translation().multiply(TRANSLATION_SCALE));
                custom.render(commandMatrix, new ScaledVertexConsumer(buffer, pass.scale() * scaleMultiple, color, commandMatrix), pass);
                commandMatrix.pop();
            }
        });
    }

    interface PassedCustom {
        void render(MatrixStack matrices, VertexConsumer buffer, Pass pass);
    }

    @Override
    public void submitBlock(MatrixStack matrices, BlockState state, int light, int overlay, int outlineColor) {
        RenderLayer layer = this.layer.apply(RenderLayers.getEntityBlockLayer(state));
        if (layer != null) {
            submitCustomPasses(matrices, layer, (transform, buffer, pass) -> {
                if (state.getRenderType() != BlockRenderType.INVISIBLE) {
                    BlockModelRenderer.render(transform.peek(), buffer, MinecraftClient.getInstance().getBlockRenderManager().getModel(state), red, green, blue, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
                }
            }, 1, null);
            ((LoadedBlockEntityModels)MinecraftClient.getInstance().getBakedModelManager().getBlockEntityModelsSupplier().get()).render(state.getBlock(), ItemDisplayContext.NONE, matrices, owner, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
        }
    }

    @Override
    public void submitBlockStateModel(MatrixStack matrices, RenderLayer renderLayer, BlockStateModel model, float r, float g, float b, int light, int overlay, int outlineColor) {
        var l = layer.apply(renderLayer);
        if (l != null) {
            submitCustomPasses(matrices, l, (transform, buffer, pass) -> {
                BlockModelRenderer.render(transform.peek(), buffer, model, red, green, blue, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
            }, 1, null);
        }
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, int tintedColor, Sprite sprite, int outline, CrumblingOverlayCommand crumblingOverlay) {
        var l = layer.apply(renderLayer);
        if (l != null) {
            for (var pass : passes) {
                matrices.push();
                matrices.translate(pass.translation().multiply(TRANSLATION_SCALE));
                CustomModelRenderCommand.<S>submit(parent, model, state, matrices, l, light, overlay, color, sprite, 0, null, (command, provider) -> {
                    return new ScaledVertexConsumer(provider.getBuffer(l), pass.scale(), color, command.matrices());
                }, null);
                matrices.pop();
            }
        }
    }

    @Override
    public void submitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, Glint glintType) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            submitCustomPasses(matrices, renderLayer, (transform, buffer, pass) -> {
                MatrixStack.Entry entry = transform.peek();
                for (BakedQuad quad : quads) {
                    buffer.quad(entry, quad, red, green, blue, 1, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
                }
            }, 2.3F, null);
        }
    }

    @Override
    public void submitModelPart(ModelPart part, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, @Nullable Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, CrumblingOverlayCommand crumblingOverlay, int i) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            submitCustomPasses(matrices, renderLayer, (transform, buffer, pass) -> {
                part.render(transform, buffer, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, color);
            }, 1, sprite);
        }
    }

    @Override
    public void submitCustom(MatrixStack matrices, RenderLayer renderLayer, Custom customRenderer) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            submitCustomPasses(matrices, renderLayer, (transform, buffer, pass) -> {
                customRenderer.render(transform.peek(), new ScaledVertexConsumer(buffer, pass.scale(), color, transform));
            }, 1, null);
        }
    }

    @Override
    public void submitCustom(LayeredCustom customRenderer) { }

    @Override
    public void submitDebugHitbox(MatrixStack matrices, EntityRenderState renderState, EntityHitboxAndView debugHitbox) { }

    @Override
    public void submitShadowPieces(MatrixStack matrices, float shadowRadius, List<ShadowPiece> shadowPieces) { }

    @Override
    public void submitLabel(MatrixStack matrices, Vec3d nameLabelPos, int y, Text label, boolean notSneaking, int light, double squaredDistanceToCamera, CameraRenderState cameraState) { }

    @Override
    public void submitText(MatrixStack matrices, float x, float y, OrderedText text, boolean dropShadow, TextLayerType layerType, int light, int color, int backgroundColor, int outlineColor) { }

    @Override
    public void submitFire(MatrixStack matrices, EntityRenderState renderState, Quaternionf rotation) { }

    @Override
    public void submitLeash(MatrixStack matrices, LeashData leashData) { }

    @Override
    public void submitMovingBlock(MatrixStack matrices, MovingBlockRenderState state) { }
}
