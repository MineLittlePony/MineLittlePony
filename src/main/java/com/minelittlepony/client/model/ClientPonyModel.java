package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.util.RenderList;

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

    protected RenderList withStage(BodyPart part, RenderList action) {
        return new RenderList() {
            @Nullable
            private T currentState;

            @Override
            public void accept(MatrixStack stack, VertexConsumer vertices, int overlay, int light, int color) {
                stack.push();
                if (currentState != null) {
                    transform(currentState, part, stack);
                }
                action.accept(stack, vertices, overlay, light, color);
                stack.pop();
            }

            @SuppressWarnings("unchecked")
            public <S> void pose(S state) {
                currentState = (T)state;
            }
        };
    }

    /**
     * Sets the model's various rotation angles.
     */
    @SuppressWarnings("unchecked")
    @Override
    public final void setAngles(PlayerEntityRenderState state) {
        super.setAngles((T)state);

        setModelVisibilities((T)state);
        setModelAngles((T)state);

        if (onSetModelAngles != null) {
            onSetModelAngles.poseModel(this, (T)state);
        }
    }

    protected void setModelVisibilities(T state) {
    }

    protected void setModelAngles(T state) {
    }

    @Override
    public ModelPart getForeLeg(Arm side) {
        return getArm(side);
    }

    @Override
    public ModelPart getHindLeg(Arm side) {
        return side == Arm.LEFT ? leftLeg : rightLeg;
    }

    public interface PosingCallback<S extends PonyRenderState> {
        void poseModel(ClientPonyModel<S> model, S state);
    }
}
