package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.mson.util.RenderList;

import java.util.ArrayList;
import java.util.List;

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

    protected RenderList withStage(BodyPart part) {
        return new RenderList() {
            private final RenderList action = RenderList.of();
            private final List<ModelPart> parts = new ArrayList<>();

            @Nullable
            private T currentState;

            @Override
            public RenderList add(RenderList part) {
                action.add(part);
                return this;
            }

            @Override
            public RenderList add(ModelPart...parts) {
                action.add(parts);
                this.parts.addAll(List.of(parts));
                return this;
            }

            @Override
            public void clear() {
                action.clear();
                parts.clear();
            }

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
                action.pose(state);
                parts.forEach(o -> transform((T)state, part, o));
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

    public void setHeadRotation(float animationProgress, float yaw, float pitch) {
        head.yaw = yaw * MathHelper.RADIANS_PER_DEGREE;
        head.pitch = pitch * MathHelper.RADIANS_PER_DEGREE;
    }

    public void renderHead(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        getHead().render(matrices, vertices, light, overlay, color);
    }

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        switch (part) {
            default:
            case HORN:
            case HEAD: return head;
            case TAIL:
            case LEGS:
            case BACK:
            case WINGS:
            case BODY: return body;
        }
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
