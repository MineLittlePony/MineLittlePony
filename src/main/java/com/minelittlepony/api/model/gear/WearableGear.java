package com.minelittlepony.api.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.pony.meta.Wearable;

public class WearableGear extends AbstractGearModel {

    protected final Wearable wearable;
    protected final BodyPart location;

    public WearableGear(Wearable wearable, BodyPart location, float stackingHeight) {
        super(stackingHeight);
        this.wearable = wearable;
        this.location = location;
    }

    @Override
    public BodyPart getGearLocation() {
        return location;
    }

    @Override
    public boolean canRender(PonyModel<?> model, Entity entity) {
        return model.isWearing(wearable);
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, Context<T, ?> context) {
        return context.getDefaultTexture(entity, wearable);
    }
}
