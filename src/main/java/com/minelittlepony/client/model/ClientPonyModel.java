package com.minelittlepony.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.HumanoidArm;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.api.model.*;
import com.minelittlepony.client.render.entity.state.PonyRenderState;
import com.minelittlepony.common.util.Untyped;

/**
 * The raw pony model without any implementations.
 * Will act effectively the same as a normal player model without any hints
 * of being cute and adorable.
 *
 * Modders can extend this class to make their own pony models if they wish.
 */
public abstract class ClientPonyModel<T extends PonyRenderState> extends PlayerModel implements PonyModel<T> {
    @Nullable
    protected PosingCallback<T> onSetModelAngles;

    public ClientPonyModel(ModelPart tree, boolean smallArms) {
        super(tree, smallArms);
    }

    public void onSetModelAngles(PosingCallback<T> callback) {
        onSetModelAngles = callback;
    }

    /**
     * Sets the model's various rotation angles.
     */
    @Override
    public final void setupAnim(AvatarRenderState state) {
        T s = Untyped.cast(state);
        super.setupAnim(s);
        resetPose();
        setModelAngles(s);

        if (onSetModelAngles != null) {
            onSetModelAngles.poseModel(this, s);
        }
    }

    protected void setModelAngles(T state) {
    }

    @Override
    public ModelPart getBodyPart(BodyPart part) {
        return switch (part) {
            default -> head;
            case HORN, HEAD -> head;
            case TAIL, LEGS, BACK, WINGS, BODY -> body;
        };
    }

    @Override
    public ModelPart getForeLeg(HumanoidArm side) {
        return getArm(side);
    }

    @Override
    public ModelPart getHindLeg(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? leftLeg : rightLeg;
    }
}
