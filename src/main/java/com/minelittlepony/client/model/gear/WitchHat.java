package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;

public class WitchHat extends AbstractGear implements IStackable {

    private static final Identifier WITCH_TEXTURES = new Identifier("textures/entity/witch.png");

    public WitchHat(ModelPart tree) {
        addPart(tree.getChild("hat"));
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
    public <T extends Entity> Identifier getTexture(T entity, Context<T, ?> context) {
        return WITCH_TEXTURES;
    }

    @Override
    public float getStackingHeight() {
        return 0.7F;
    }
}
