package com.minelittlepony.api.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.meta.Wearable;

public class WearableGear<T extends BipedEntityRenderState & PonyModel.AttributedHolder> extends AbstractGearModel<T> {

    protected final Wearable wearable;
    protected final BodyPart location;

    public WearableGear(ModelPart root, Wearable wearable, BodyPart location, float stackingHeight) {
        super(root, stackingHeight);
        this.wearable = wearable;
        this.location = location;
    }

    @Override
    public BodyPart getGearLocation() {
        return location;
    }

    @Override
    public boolean canRender(PonyModel<?> model, T state) {
        return state.getAttributes().isWearing(wearable);
    }

    @Override
    public Identifier getTexture(T entity, Context<T, ?> context) {
        return context.getDefaultTexture(entity, wearable);
    }
}
