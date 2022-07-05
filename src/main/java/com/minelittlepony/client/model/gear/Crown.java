package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;

public class Crown extends AbstractGear implements IStackable {
    public static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/models/crown.png");

    public Crown(ModelPart tree) {
        addPart(tree.getChild("crown"));
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return model.isWearing(Wearable.CROWN)
            || ((
                       entity instanceof AbstractPiglinEntity
                    || entity instanceof PlayerEntity
                    || entity instanceof ZombifiedPiglinEntity
                ) && entity.hasCustomName() && entity.getCustomName().getString().equalsIgnoreCase("technoblade")
                );
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
        return 0.1F;
    }
}
