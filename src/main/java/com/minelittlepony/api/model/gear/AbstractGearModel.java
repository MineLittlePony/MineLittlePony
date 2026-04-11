package com.minelittlepony.api.model.gear;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.minelittlepony.api.model.PonyModel;
import com.mojang.blaze3d.vertex.PoseStack;

public abstract class AbstractGearModel<T extends HumanoidRenderState & PonyModel.AttributedHolder> extends Model<Gear.GearRenderState<T>> implements Gear<T> {

    private final float stackingHeight;

    public AbstractGearModel(ModelPart root, float stackingHeight) {
        super(root, RenderTypes::entitySolid);
        this.stackingHeight = stackingHeight;
    }

    @Override
    public void render(PoseStack stack, GearRenderState<T> state, SubmitNodeCollector frame, RenderType renderType, int overlay, int light, int color) {
        frame.submitModel(this, state, stack, renderType, light, overlay, color, null, state.entityState.outlineColor, null);
    }

    @Override
    public float getStackingHeight() {
        return stackingHeight;
    }
}
