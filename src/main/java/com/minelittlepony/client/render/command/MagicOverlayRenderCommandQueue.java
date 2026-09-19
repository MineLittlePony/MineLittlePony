package com.minelittlepony.client.render.command;

import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState.LeashState;
import net.minecraft.client.renderer.entity.state.EntityRenderState.ShadowPiece;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives.Group;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.*;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;
import org.joml.*;

import com.minelittlepony.client.compat.iris.IrisApiCompat;
import com.minelittlepony.common.util.Untyped;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.*;
import java.util.function.Function;

public class MagicOverlayRenderCommandQueue implements OrderedSubmitNodeCollector {
    static final float TRANSLATION_SCALE = 1/8F;

    private final SubmitNodeCollector owner;
    private final OrderedSubmitNodeCollector parent;
    protected final Function<RenderType, @Nullable RenderType> layer;
    protected final int color;
    protected final List<Pass> passes;

    public record Pass(Vec3 translation, float scale) { }

    public MagicOverlayRenderCommandQueue(SubmitNodeCollector owner, OrderedSubmitNodeCollector parent, Function<RenderType, @Nullable RenderType> layer, int color, List<Pass> passes) {
        this.owner = owner;
        this.parent = parent;
        this.layer = IrisApiCompat.isIrisLoaded() ? layer.andThen(IrisApiCompat::wrapExactlyOnce) : layer;
        this.color = ARGB.color(0.5F, color);
        this.passes = passes;
    }

    public SubmitNodeCollector unwrap() {
        return owner;
    }

    @Nullable
    private RenderType getFinalRenderType(RenderType renderType) {
        if (IrisApiCompat.isOnShadowPass()) {
            return null;
        }
        return layer.apply(renderType);
    }

    private List<BlockStateModelPart> scaleBlockParts(List<BlockStateModelPart> parts) {
        return parts.stream().map(part -> {
            QuadCollection.Builder quadCollection = new QuadCollection.Builder();
            Direction.stream().forEach(direction -> {
                scaleQuads(part.getQuads(direction)).forEach(quad -> quadCollection.addCulledFace(direction, quad));
            });
            scaleQuads(part.getQuads(null)).forEach(quad -> quadCollection.addUnculledFace(quad));
            return (BlockStateModelPart)new SimpleModelWrapper(quadCollection.build(), part.useAmbientOcclusion(), part.particleMaterial());
        }).toList();
    }

    private List<BakedQuad> scaleQuads(List<BakedQuad> quads) {
        List<BakedQuad>[] remappedQuads = Untyped.cast(new List[passes.size()]);

        final float baseScale = 2.3F / 15F;

        quads.forEach(quad -> {
            RenderType renderType = getFinalRenderType(quad.materialInfo().itemRenderType());
            if (renderType == null) {
                return;
            }

            for (int i = 0; i < passes.size(); i++) {
                if (remappedQuads[i] == null) {
                    remappedQuads[i] = new ArrayList<>();
                }
                remappedQuads[i].add(VertexTransforms.inflateQuad(quad, VertexTransforms.materialOf(quad.materialInfo(), renderType, Transparency.TRANSLUCENT), passes.get(i).scale() * baseScale));
            }
        });

        return Arrays.stream(remappedQuads).filter(Objects::nonNull).flatMap(List::stream).toList();
    }

    private void submitCustomPasses(PoseStack matrices, RenderType layer, PassedCustom custom, float scaleMultiple, @Nullable UvMapping uvMapping) {
        var l = layer = getFinalRenderType(layer);
        if (layer == null) {
            return;
        }
        PoseStack commandMatrix = new PoseStack();
        parent.submitCustomGeometry(matrices, layer, (entry, buffer) -> {
            buffer = uvMapping == null ? buffer : uvMapping.wrap(buffer);
            commandMatrix.last().set(entry);
            var scaledBuffer = new ScaledVertexConsumer(buffer, l, color, commandMatrix);
            for (var pass : passes) {
                commandMatrix.pushPose();
                commandMatrix.translate(pass.translation().scale(TRANSLATION_SCALE));
                custom.render(commandMatrix, scaledBuffer.setScale(pass.scale() * scaleMultiple));
                commandMatrix.popPose();
            }
        });
    }

    interface PassedCustom {
        void render(PoseStack matrices, VertexConsumer buffer);
    }

    @Override
    public void submitBlockModel(PoseStack matrices, RenderType renderLayer, List<BlockStateModelPart> parts, int[] tintLayers, int light, int overlay, int outlineColor) {
        renderLayer = getFinalRenderType(renderLayer);
        if (renderLayer == null) {
            return;
        }

        parts = scaleBlockParts(parts);

        if (!parts.isEmpty()) {
            parent.submitBlockModel(matrices, renderLayer, parts, new int[] { color }, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        }
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, PoseStack matrices, RenderType renderType, int light, int overlay, int tintedColor, @Nullable UvMapping uvMapping, int outlineColor) {
        renderType = getFinalRenderType(renderType);
        if (renderType == null) {
            return;
        }

        parent.submitModel(new ScaledVertexModel<>(model, passes, renderType), state, matrices, renderType, light, overlay, color, uvMapping, 0);
    }

    @Override
    public void submitItem(PoseStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outline, int[] tintLayers, ItemQuads quads, FoilType foilType) {
        var quadList = scaleQuads(quads.all());
        if (!quadList.isEmpty()) {
            parent.submitItem(matrices, displayContext, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0, new int[] { color }, ItemQuads.split(quadList), FoilType.NONE);
        }
    }

    @Override
    public void submitModelPart(ModelPart part, PoseStack matrices, RenderType renderLayer, int light, int overlay, @Nullable UvMapping uvMapping, int tintedColor, int outlineColor) {
        renderLayer = getFinalRenderType(renderLayer);
        if (renderLayer != null) {
            submitCustomPasses(matrices, renderLayer, (transform, buffer) -> part.render(transform, buffer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color), 1, uvMapping);
        }
    }

    @Override
    public void submitCustomGeometry(PoseStack matrices, RenderType renderLayer, CustomGeometryRenderer customRenderer) {
        renderLayer = getFinalRenderType(renderLayer);
        if (renderLayer != null) {
            submitCustomPasses(matrices, renderLayer, (transform, buffer) -> customRenderer.render(transform.last(), buffer), 1, null);
        }
    }

    @Override
    public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress, boolean isBlockTranslucent) { }

    @Override
    public void submitQuadParticleGroup(QuadParticleRenderState particles) { }

    @Override
    public void submitShadow(PoseStack poseStack, float radius, List<ShadowPiece> pieces) { }

    @Override
    public void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords, CameraRenderState camera) { }

    @Override
    public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence string, boolean dropShadow, DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor) { }

    @Override
    public void submitFlame(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation) { }

    @Override
    public void submitLeash(PoseStack poseStack, LeashState leashState) { }

    @Override
    public void submitMovingBlock(PoseStack matrices, MovingBlockRenderState state, int outlineColor) { }

    @Override
    public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) { }

    @Override
    public void submitGizmoPrimitives(Group group, CameraRenderState camera, boolean onTop) { }

    @Override
    public void submitTextBackground(PoseStack poseStack, float x0, float y0, float x1, float y1, int color, DisplayMode displayMode, int lightCoords) { }

    @Override
    public <S> void submitCrumblingOverlay(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords, int tintedColor, CrumblingOverlay crumblingOverlay) { }
}
