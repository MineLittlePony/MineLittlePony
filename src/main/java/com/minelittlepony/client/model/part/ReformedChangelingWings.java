package com.minelittlepony.client.model.part;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.api.ModelView;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ReformedChangelingWings <S extends PonyRenderState> extends PonyWings {
    private ModelPart openElytra;
    // closed bug wing elytra are considered "closed wing" models

    public ReformedChangelingWings(ModelPart tree) {
        super(tree);
    }

    @Override
    public void init(ModelView context) {
        super.init(context);
        openElytra = pegasus.getBodyPart(BodyPart.BODY).getChild("bug_open_elytra");
    }

    @Override
    public void setAngles(PonyModel model, PonyRenderState state) {
        super.setAngles(model, state);
        openElytra.resetPose();
        openElytra.visible = pegasus.wingsAreOpen(state);
        if (state.isCrouching) {
            openElytra.xRot -= Mth.PI / 5;
            openElytra.z += 6F;
            openElytra.y += 1.25F;
        }
    }
}
