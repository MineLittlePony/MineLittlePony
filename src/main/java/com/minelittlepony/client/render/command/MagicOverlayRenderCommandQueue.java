package com.minelittlepony.client.render.command;

import net.fabricmc.fabric.api.renderer.v1.mesh.MeshView;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessRenderCommandQueue;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand;
import net.minecraft.client.render.command.OrderedRenderCommandQueue.Custom;
import net.minecraft.client.render.command.OrderedRenderCommandQueue.LayeredCustom;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
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
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MagicOverlayRenderCommandQueue implements RenderCommandQueue, AccessRenderCommandQueue {
    private final OrderedRenderCommandQueue owner;
    private final RenderCommandQueue parent;
    protected final Function<RenderLayer, @Nullable RenderLayer> layer;
    protected final int color;

    public MagicOverlayRenderCommandQueue(OrderedRenderCommandQueue owner, RenderCommandQueue parent, Function<RenderLayer, @Nullable RenderLayer> layer, int color) {
        this.owner = owner;
        this.parent = parent;
        this.layer = layer;
        this.color = color;
    }

    public OrderedRenderCommandQueue unwrap() {
        return owner;
    }

    @Override
    public void submitBlock(MatrixStack matrices, BlockState state, int light, int overlay, int outlineColor) {
        parent.submitBlock(matrices, state, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, 0);
    }

    @Override
    public void submitBlockStateModel(MatrixStack matrices, RenderLayer renderLayer, BlockStateModel model, float r, float g, float b, int light, int overlay, int outlineColor) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            parent.submitBlockStateModel(matrices, layer.apply(renderLayer), model, ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, 0);
        }
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, int tintedColor, Sprite sprite, int outline, CrumblingOverlayCommand crumblingOverlay) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            parent.submitModel(model, state, matrices, renderLayer, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, color, sprite, 0, null);
        }
    }

    @Override
    public void submitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, Glint glintType) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            List<BakedQuad> adjustedQuad = new ArrayList<>();
            for (var quad : quads) {
                adjustedQuad.add(new BakedQuad(quad.vertexData(), 0, quad.face(), quad.sprite(), false, 1));
            }
            parent.submitItem(matrices, displayContext, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, 0, new int[] {color}, adjustedQuad, renderLayer, Glint.NONE);
        }
    }

    @Override
    public void fabric_submitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, Glint glintType, MeshView mesh) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            List<BakedQuad> adjustedQuad = new ArrayList<>();
            for (var quad : quads) {
                adjustedQuad.add(new BakedQuad(quad.vertexData(), 0, quad.face(), quad.sprite(), false, 1));
            }
            ((AccessRenderCommandQueue)parent).fabric_submitItem(matrices, displayContext, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, 0, new int[] {color}, adjustedQuad, renderLayer, Glint.NONE, mesh);
        }
    }

    @Override
    public void submitModelPart(ModelPart part, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, @Nullable Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, CrumblingOverlayCommand crumblingOverlay, int i) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            parent.submitModelPart(part, matrices, renderLayer, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, null, false, false, color, null, i);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void submitCustom(MatrixStack matrices, RenderLayer renderLayer, Custom customRenderer) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            if (customRenderer instanceof CustomModelRenderCommand custom) {
                final RenderLayer l = renderLayer;
                customRenderer = new CustomModelRenderCommand<>(custom.matrices(), custom.command(), renderLayer, c -> custom.bufferFunc().apply(c) == null ? null : l, custom.anglesFunc());
            }
            parent.submitCustom(matrices, renderLayer, customRenderer);
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
