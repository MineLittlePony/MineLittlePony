package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;

public class Muffin extends AbstractGear implements IStackable {

    private static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/muffin.png");

    public Muffin(ModelPart tree) {
        addPart(tree.getChild("crown"));
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
    public <T extends Entity> Identifier getTexture(T entity, Context<T, ?> context) {
        return TEXTURE;
    }

    @Override
    public float getStackingHeight() {
        return 0.5F;
    }
}
