package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

/**
 * The raw pony model without any implementations.
 * Will act effectively the same as a normal player model without any hints
 * of being cute and adorable.
 *
 * Modders can extend this class to make their own pony models if they wish.
 */
public abstract class ClientPonyModel<T extends PonyRenderState> extends PlayerEntityModel implements PonyModel<T> {
    @Nullable
    protected PosingCallback<T> onSetModelAngles;

    public ClientPonyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    public void onSetModelAngles(PosingCallback<T> callback) {
        onSetModelAngles = callback;
    }

    @Override
    public ModelPart getForeLeg(Arm side) {
        return getArm(side);
    }

    @Override
    public ModelPart getHindLeg(Arm side) {
        return side == Arm.LEFT ? leftLeg : rightLeg;
    }

    @Override
    public <S extends PlayerEntityRenderState> ArmPose getArmPoseForSide(S state, Arm side) {
        return getArmPose(state, side);
    }

    @Override
    public void setHatVisible(boolean visible) {

    }

    static void resetPivot(ModelPart part) {
        part.setPivot(part.getDefaultTransform().pivotX(), part.getDefaultTransform().pivotY(), part.getDefaultTransform().pivotZ());
    }

    static void resetPivot(ModelPart...parts) {
        for (ModelPart part : parts) {
            resetPivot(part);
        }
    }

    public interface PosingCallback<S extends PonyRenderState> {
        void poseModel(ClientPonyModel<S> model, S state);
    }
}
