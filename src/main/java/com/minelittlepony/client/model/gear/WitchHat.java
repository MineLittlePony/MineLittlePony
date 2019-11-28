package com.minelittlepony.client.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.gear.IStackable;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.pony.meta.Wearable;

public class WitchHat extends AbstractGear implements IStackable {

    private static final Identifier WITCH_TEXTURES = new Identifier("textures/entity/witch.png");

    @Override
    public void init(ModelContext context) {
        accept(context.findByName("witch_hat"));
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(Wearable.HAT);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, IRenderContext<T, ?> context) {
        return WITCH_TEXTURES;
    }

    @Override
    public float getStackingOffset() {
        return 0.7F;
    }
}
