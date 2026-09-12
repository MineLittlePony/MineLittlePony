package com.minelittlepony.client.model.part;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;

public class ReformedChangelingHorn<T extends PonyRenderState> extends UnicornHorn {
    private final ModelPart antlers;

    public ReformedChangelingHorn(ModelPart tree) {
        super(tree);
        antlers = tree.getChild("changeling_antlers");
    }

    @Override
    public void accept(PoseStack matrices, VertexConsumer vertices, int overlay, int light, int color) {
        super.accept(matrices, vertices, overlay, light, color);
        antlers.render(matrices, vertices, overlay, light, color);
    }

    @Override
    public void render(PonyModel model, PonyRenderState state, PoseStack matrices, SubmitNodeCollector frame) {
        super.render(model, state, matrices, frame);
        if (tint != 0) {
            matrices.pushPose();
            model.transformAccessory(state, BodyPart.HEAD, matrices);
            //TODO: make a custom glow for changeling antlers that doesn't look like GARBAGE!!! maybe inherit rendering style from items?
            matrices.popPose();
        }
    }

        @Override
    public void setAngles(PonyModel model, PonyRenderState state) {
        super.setAngles(model, state);
        antlers.resetPose();
        state.transformation.transform(state.attributes, BodyPart.HORN, antlers);
        if (state.attributes.metadata.changelingAntlers() != 0) { // if the antlers trigger pixel is filled
            antlers.visible = horn.visible;
        }
    }

    @Override
    public void setHidden() {
        super.setHidden();
        antlers.visible = false;
    }
}
