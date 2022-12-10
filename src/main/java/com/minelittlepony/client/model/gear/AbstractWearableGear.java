package com.minelittlepony.client.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.pony.meta.Wearable;

public abstract class AbstractWearableGear extends AbstractGear {

    protected final Wearable wearable;
    protected final BodyPart location;

    protected AbstractWearableGear(Wearable wearable, BodyPart location) {
        this.wearable = wearable;
        this.location = location;
    }

    @Override
    public BodyPart getGearLocation() {
        return location;
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(wearable);
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, Context<T, ?> context) {
        return context.getDefaultTexture(entity, wearable);
    }
}
