package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.IModel;
import com.minelittlepony.model.gear.IRenderContext;
import com.minelittlepony.model.gear.IStackable;

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
    public <T extends Entity> Identifier getTexture(T entity, IRenderContext<T, ?> context) {
        return WITCH_TEXTURES;
    }

    @Override
    public float getStackingOffset() {
        return 0.7F;
    }
}
