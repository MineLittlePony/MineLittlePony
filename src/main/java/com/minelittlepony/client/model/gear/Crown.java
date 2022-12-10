package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;

public class Crown extends AbstractWearableGear implements IStackable {

    public Crown(ModelPart tree) {
        super(Wearable.CROWN, BodyPart.HEAD);
        addPart(tree.getChild("crown"));
    }

    @Override
    public boolean canRender(IModel model, Entity entity) {
        return super.canRender(model, entity)
            || ((
                       entity instanceof AbstractPiglinEntity
                    || entity instanceof PlayerEntity
                    || entity instanceof ZombifiedPiglinEntity
                ) && entity.hasCustomName() && entity.getCustomName().getString().equalsIgnoreCase("technoblade")
                );
    }

    @Override
    public float getStackingHeight() {
        return 0.1F;
    }
}
