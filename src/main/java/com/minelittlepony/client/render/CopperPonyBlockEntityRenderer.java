package com.minelittlepony.client.render;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.CopperGolemStatueBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;

import org.joml.Vector3fc;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.SpikeModel;
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

        var oxidation = state.oxidationState;
        Identifier texture = MineLittlePony.id("textures/entity/copper_golem/" + (oxidation == WeatherState.UNAFFECTED ? "" : oxidation.getSerializedName() + "_") + "copper_golem_dragon.png");

        RenderType renderLayer = RenderTypes.entityCutout(texture);
        frame.submitModel(model, state, matrices, renderLayer, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        matrices.popPose();
    }

    public static class SpecialModelRenderer implements NoDataSpecialModelRenderer {
        private final SpikeModel.BlockModel model = new SpikeModel.BlockModel(ModelType.SPIKE.createTree().get());
        private final CopperGolemStatueRenderState state = new CopperGolemStatueRenderState();
        private final Identifier texture;

        public SpecialModelRenderer(Identifier texture) {
            this.texture = MineLittlePony.id(texture.getPath().replace(".png", "_dragon.png"));
            state.direction = Direction.SOUTH;
            state.pose = CopperGolemStatueBlock.Pose.STANDING;
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
        public void submit(PoseStack matrices, SubmitNodeCollector frame, int light, int overlay, boolean hasFoil, int outline) {
            frame.submitModel(model, state, matrices, RenderTypes.entityCutout(texture), light, overlay, CommonColors.WHITE, null, outline, null);
        }
    };
}
