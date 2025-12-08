package com.minelittlepony.api.model.gear;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.model.PonyModel;

public abstract class AbstractGearModel<T extends BipedEntityRenderState & PonyModel.AttributedHolder> extends Model<Gear.GearRenderState<T>> implements Gear<T> {

    private final float stackingHeight;

    public AbstractGearModel(ModelPart root, float stackingHeight) {
        super(root, RenderLayers::entitySolid);
        this.stackingHeight = stackingHeight;
    }

    @Override
    public void render(MatrixStack stack, GearRenderState<T> state, OrderedRenderCommandQueue queue, RenderLayer layer, int overlay, int light, int color) {
        queue.submitModel(this, state, stack, layer, light, overlay, color, null, state.entityState.outlineColor, null);
    }

    @Override
    public float getStackingHeight() {
        return stackingHeight;
    }
}
