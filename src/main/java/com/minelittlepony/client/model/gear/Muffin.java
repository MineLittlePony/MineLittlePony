package com.minelittlepony.client.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.gear.IStackable;
import com.minelittlepony.mson.api.ModelContext;

public class Muffin extends AbstractGear implements IStackable {

    private static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/muffin.png");

    @Override
    public void init(ModelContext context) {
        addPart(context.findByName("crown"));
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(Wearable.MUFFIN);
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, IRenderContext<T, ?> context) {
        return TEXTURE;
    }

    @Override
    public float getStackingOffset() {
        return 0.5F;
    }
}
