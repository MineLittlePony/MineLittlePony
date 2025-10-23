package com.minelittlepony.client.render.command;

import net.fabricmc.fabric.api.renderer.v1.mesh.MeshView;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessRenderCommandQueue;
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
import net.minecraft.util.math.*;

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
    protected final List<MatrixStack.Entry> passes;

    public MagicOverlayRenderCommandQueue(OrderedRenderCommandQueue owner, RenderCommandQueue parent, Function<RenderLayer, @Nullable RenderLayer> layer, int color, List<MatrixStack.Entry> passes) {
        this.owner = owner;
        this.parent = parent;
        this.layer = layer;
        this.color = ColorHelper.withAlpha(0.5F, color);
        this.passes = passes;
    }

    public OrderedRenderCommandQueue unwrap() {
        return owner;
    }

    @Override
    public void submitBlock(MatrixStack matrices, BlockState state, int light, int overlay, int outlineColor) {
        RenderLayer layer = this.layer.apply(RenderLayers.getEntityBlockLayer(state));
        if (layer != null) {
            MatrixStack commandMatrix = new MatrixStack();
            parent.submitCustom(matrices, layer, (entry, buffer) -> {
                commandMatrix.push();
                commandMatrix.peek().copy(entry);

                for (var pass : passes) {
                    commandMatrix.push();
                    applyPass(pass, commandMatrix);

                    if (state.getRenderType() != BlockRenderType.INVISIBLE) {
                        BlockStateModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
                        BlockModelRenderer.render(commandMatrix.peek(), buffer, model, ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
                    }

                    commandMatrix.pop();
                }

                commandMatrix.pop();
            });
            ((LoadedBlockEntityModels)MinecraftClient.getInstance().getBakedModelManager().getBlockEntityModelsSupplier().get()).render(state.getBlock(), ItemDisplayContext.NONE, matrices, owner, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
        }
    }

    @Override
    public void submitBlockStateModel(MatrixStack matrices, RenderLayer renderLayer, BlockStateModel model, float r, float g, float b, int light, int overlay, int outlineColor) {
        var l = layer.apply(renderLayer);
        if (l != null) {
            for (var pass : passes) {
                matrices.push();
                applyPass(pass, matrices);
                parent.submitBlockStateModel(matrices, l, model, ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color), LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0);
                matrices.pop();
            }
        }
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, int tintedColor, Sprite sprite, int outline, CrumblingOverlayCommand crumblingOverlay) {
        var l = layer.apply(renderLayer);
        if (l != null) {
            for (var pass : passes) {
                matrices.push();
                applyPass(pass, matrices);
                parent.submitModel(model, state, matrices, l, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, color, sprite, 0, null);
                matrices.pop();
            }
        }
    }

    @Override
    public void submitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, Glint glintType) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            quads = getColoredQuads(quads);
            int[] tints = new int[] {color};
            for (var pass : passes) {
                matrices.push();
                applyPass(pass, matrices);
                parent.submitItem(matrices, displayContext, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, 0, tints, quads, renderLayer, Glint.NONE);
                matrices.pop();
            }
        }
    }

    @Override
    public void fabric_submitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, Glint glintType, MeshView mesh) {
        renderLayer = layer.apply(renderLayer);
        if (renderLayer != null) {
            quads = getColoredQuads(quads);
            int[] tints = new int[] {color};
            for (var pass : passes) {
                matrices.push();
                applyPass(pass, matrices);
                ((AccessRenderCommandQueue)parent).fabric_submitItem(matrices, displayContext, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, 0, tints, quads, renderLayer, Glint.NONE, mesh);
                matrices.pop();
            }
        }
    }

    @Override
    public void submitModelPart(ModelPart part, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, @Nullable Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, CrumblingOverlayCommand crumblingOverlay, int i) {
        var l = layer.apply(renderLayer);
        if (l != null) {
            for (var pass : passes) {
                matrices.push();
                applyPass(pass, matrices);
                parent.submitModelPart(part, matrices, l, LightmapTextureManager.MAX_LIGHT_COORDINATE, 0, null, false, false, color, null, i);
                matrices.pop();
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void submitCustom(MatrixStack matrices, RenderLayer renderLayer, Custom customRenderer) {
        var l = layer.apply(renderLayer);
        if (l != null) {

            for (var pass : passes) {
                matrices.push();
                applyPass(pass, matrices);
                Custom c = customRenderer;
                if (c instanceof CustomModelRenderCommand custom) {
                    c = new CustomModelRenderCommand(matrices, custom.command(), l, cc -> custom.bufferFunc().apply(cc) == null ? null : l, custom.anglesFunc());
                }
                parent.submitCustom(matrices, l, c);
                matrices.pop();
            }
        }
    }

    private void applyPass(MatrixStack.Entry pass, MatrixStack matrices) {
        matrices.peek().getPositionMatrix().mul(pass.getPositionMatrix());
        matrices.peek().getNormalMatrix().mul(pass.getNormalMatrix());
    }

    private List<BakedQuad> getColoredQuads(List<BakedQuad> quads) {
        List<BakedQuad> adjustedQuad = new ArrayList<>();
        for (var quad : quads) {
            adjustedQuad.add(new BakedQuad(quad.vertexData(), 0, quad.face(), quad.sprite(), false, 1));
        }
        return adjustedQuad;
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

    @SuppressWarnings("unused")
    private static int[] inflateVertices(int[] packedVertices, Direction face, float inflation) {
        int[] vertices = new int[packedVertices.length];
        System.arraycopy(packedVertices, 0, vertices, 0, vertices.length);

        Vec3i normal = face.getOpposite().getVector();
        Vec3i normalizedNormal = new Vec3i(Math.abs(normal.getX()), Math.abs(normal.getY()), Math.abs(normal.getZ()));

        for (int i = 0; i < vertices.length; i += 8) {
            int vertexIndex = i / 8;
            int inner = vertexIndex > 0 && vertexIndex < 3 ? 1 : -1;
            int lower = vertexIndex < 2 ? 1 : -1;
            int xDir = normal.getX() + (normalizedNormal.getY() * lower) + (-lower * normal.getZ());
            int yDir = normal.getY() + (normalizedNormal.getX() * inner) + (normalizedNormal.getZ() * inner);
            int zDir = normal.getZ() + (inner * normal.getY()) + (lower * normal.getX());
            vertices[i] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i]) - inflation * xDir);
            vertices[i + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i + 1]) - inflation * yDir);
            vertices[i + 2] = Float.floatToRawIntBits(Float.intBitsToFloat(vertices[i + 2]) - inflation * zDir);
        }

        return vertices;
    }
}
