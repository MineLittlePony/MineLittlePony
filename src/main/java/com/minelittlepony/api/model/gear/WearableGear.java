package com.minelittlepony.api.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class WearableGear extends AbstractGearModel {

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
    public boolean canRender(PonyModel<?> model, EntityRenderState entity) {
        return entity instanceof PonyRenderState state && state.isWearing(wearable);
    }

    @Override
    public <S extends EntityRenderState> Identifier getTexture(S entity, Context<S, ?> context) {
        return context.getDefaultTexture(entity, wearable);
    }
}
