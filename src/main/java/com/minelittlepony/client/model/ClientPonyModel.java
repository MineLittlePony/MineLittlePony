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

    @Deprecated
    @Nullable
    protected T currentState;

    public ClientPonyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    public void onSetModelAngles(PosingCallback<T> callback) {
        onSetModelAngles = callback;
    }

    /**
     * Sets the model's various rotation angles.
     */
    @SuppressWarnings("unchecked")
    @Override
    public final void setAngles(PlayerEntityRenderState entity) {
        currentState = (T)entity;
        super.setAngles((PlayerEntityRenderState)entity);

        setModelVisibilities((T)entity);
        setModelAngles((T)entity);

        if (onSetModelAngles != null) {
            onSetModelAngles.poseModel(this, (T)entity);
        }
    }

    protected void setModelVisibilities(T state) {
    }

    protected void setModelAngles(T entity) {
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
    public ArmPose getArmPose(PlayerEntityRenderState state, Arm side) {
        return super.getArmPose(state, side);
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
