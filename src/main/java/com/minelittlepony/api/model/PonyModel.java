package com.minelittlepony.api.model;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;

import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.mson.api.MsonModel;
import com.minelittlepony.mson.util.RenderList;

public interface PonyModel<T extends EntityRenderState & PonyModel.AttributedHolder> extends MsonModel, ModelWithHooves<T>, HeadedModel, TransformedModel<T> {
    @Override
    ModelPart getBodyPart(BodyPart part);

    RenderList getRenderList(BodyPart part);

    void onSetModelAngles(PosingCallback<T> callback);

    public interface PosingCallback<S extends EntityRenderState & PonyModel.AttributedHolder> {
        void poseModel(PonyModel<S> model, S state);
    }

    public interface AttributedHolder {
        ModelAttributes getAttributes();

        AvatarRenderState getRenderState();

        TransformedModel.BodyType getBodyType();

        ArmPose getArmPoseForArm(HumanoidArm arm);

        Race getRace();

        boolean hasMagicGlow();

        @Deprecated
        float getSwingAmount();

        /**
         * Tests if this model is wearing the given piece of gear.
         */
        boolean isWearing(Wearable wearable);

        /**
         * Checks whether this state represents a certain entity type.
         */
        boolean isOf(EntityType<?> entityType);
    }
}
