package com.minelittlepony.client.render;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.CopperGolemStatueBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CopperGolemStatueBlock;

import org.joml.Vector3fc;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SpikeModel;
import com.minelittlepony.client.render.entity.CopperPonyRenderer;
import com.minelittlepony.util.ResourceUtil;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.function.Consumer;

public class CopperPonyBlockEntityRenderer extends CopperGolemStatueBlockRenderer {
    private final SpikeModel.BlockModel model = new SpikeModel.BlockModel(ModelType.SPIKE.createTree().get());

    public CopperPonyBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(CopperGolemStatueRenderState state, PoseStack matrices, SubmitNodeCollector frame, CameraRenderState camera) {
        matrices.pushPose();
        matrices.translate(0.5F, 0.0F, 0.5F);
        RenderType renderLayer = RenderTypes.entityCutout(CopperPonyRenderer.STAGE_TEXTURES.apply(state.oxidationState));
        frame.submitModel(model, state, matrices, renderLayer, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        matrices.popPose();
    }

    public static class CopperPonyModelRenderer<T> implements SpecialModelRenderer<Boolean> {
        private final SpikeModel.BlockModel model = new SpikeModel.BlockModel(ModelType.SPIKE.createTree().get());
        private final CopperGolemStatueRenderState state = new CopperGolemStatueRenderState();
        private final Identifier texture;

        private final NoDataSpecialModelRenderer renderer;

        public CopperPonyModelRenderer(NoDataSpecialModelRenderer renderer, Identifier texture, CopperGolemStatueBlock.Pose pose) {
            this.renderer = renderer;
            this.texture = MineLittlePony.id(texture.getPath().replace(".png", "_dragon.png"));
            state.pose = pose;
            model.setupAnim(state);
        }

        public boolean shouldApply() {
            return MobRenderers.COPPER_GOLEMS.option().get() && ResourceUtil.textureExists(texture);
        }

        @Override
        public void getExtents(Consumer<Vector3fc> output) {
            model.root().getExtentsForGui(new PoseStack(), output);
        }

        @Override
        public Boolean extractArgument(ItemStack stack) {
            return shouldApply();
        }

        @Override
        public void submit(Boolean data, PoseStack matrices, SubmitNodeCollector frame, int light, int overlay, boolean hasFoil, int outline) {
            if (data != null && data) {
                frame.submitModel(model, state, matrices, RenderTypes.entityCutout(texture), light, overlay, CommonColors.WHITE, null, outline, null);
            } else {
                renderer.submit(null, matrices, frame, light, overlay, hasFoil, outline);
            }
        }
    };
}
