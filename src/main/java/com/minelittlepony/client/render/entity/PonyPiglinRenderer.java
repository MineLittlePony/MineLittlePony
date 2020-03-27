package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.PiglinPonyModel;

public class PonyPiglinRenderer extends PonyRenderer.Caster<HostileEntity, PiglinPonyModel> {
    public static final Identifier NORMAL = new Identifier("minelittlepony", "textures/entity/piglin/piglin_pony.png");
    public static final Identifier ZOMBIFIED = new Identifier("minelittlepony", "textures/entity/piglin/zombified_piglin_pony.png");

    public PonyPiglinRenderer(EntityRenderDispatcher manager) {
        super(manager, ModelType.PIGLIN);
    }

    @Override
    public Identifier findTexture(HostileEntity entity) {
        return entity instanceof PiglinEntity ? NORMAL : new Identifier("minelittlepony", "textures/entity/piglin/zombified_piglin_pony.png");
    }

    @Override
    protected boolean isShaking(HostileEntity entity) {
       return entity instanceof PiglinEntity && ((PiglinEntity)entity).canConvert();
    }
}
