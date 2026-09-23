package com.minelittlepony.client.model.part;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ReformedChangelingWings <S extends PonyRenderState> extends PonyWings {
    private ModelPart openWingCover; // closed wing covers are considered "closed wing" models

    public ReformedChangelingWings(ModelPart tree) { super(tree); }

    @Override
    public void init(ModelView context) {
        super.init(context);
        openWingCover = context.findByName("wing_cover_open");
    }

    @Override
    public void setAngles(PonyModel model, PonyRenderState state) {
        super.setAngles(model, state);
        openWingCover.resetPose();
        if (state.isCrouching) {
            openWingCover.xRot -= Mth.PI / 5;
            openWingCover.z += 6F;
            openWingCover.y += 1.25F;
        }
        state.transformation.transform(state.attributes, BodyPart.WINGS, openWingCover);
        openWingCover.visible = pegasus.wingsAreOpen(state);
    }

    @Override
    public void accept(PoseStack matrices, VertexConsumer vertices, int overlay, int light, int color) {
        super.accept(matrices, vertices, overlay, light, color);
        if (visible) {
            openWingCover.render(matrices, vertices, overlay, light, color);
        }
    }
}
