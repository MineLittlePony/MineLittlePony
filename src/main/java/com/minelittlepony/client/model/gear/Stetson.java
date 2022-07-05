package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;

public class Stetson extends AbstractGear implements IStackable {
    private static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/stetson.png");

    public Stetson(ModelPart tree) {
        addPart(tree.getChild("rim"));
    }

    @Override
    public BodyPart getGearLocation() {
        return BodyPart.HEAD;
    }

    @Override
    public <T extends Entity> Identifier getTexture(T entity, Context<T, ?> context) {
        return TEXTURE;
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(Wearable.STETSON);
    }

    @Override
    public float getStackingHeight() {
        return 0.15F;
    }
}
