package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.PiglinPonyModel;

import java.util.HashMap;
import java.util.Map;

public class PonyPiglinRenderer extends PonyRenderer.Caster<HostileEntity, PiglinPonyModel> {

    private static final Map<EntityType<?>, Identifier> TEXTURES = Util.make(new HashMap<>(), map -> {
        map.put(EntityType.PIGLIN, new Identifier("minelittlepony", "textures/entity/piglin/piglin_pony.png"));
        map.put(EntityType.PIGLIN_BRUTE, new Identifier("minelittlepony", "textures/entity/piglin/piglin_brute_pony.png"));
        map.put(EntityType.ZOMBIFIED_PIGLIN, new Identifier("minelittlepony", "textures/entity/piglin/zombified_piglin_pony.png"));
    });

    public PonyPiglinRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.PIGLIN);
    }

    @Override
    public void scale(HostileEntity entity, MatrixStack stack, float ticks) {
        super.scale(entity, stack, ticks);
        if (entity.getType() == EntityType.PIGLIN_BRUTE) {
            stack.scale(1.15F, 1.15F, 1.15F);
        }
    }

    @Override
    public Identifier findTexture(HostileEntity entity) {
        return TEXTURES.get(entity.getType());
    }

    @Override
    protected boolean isShaking(HostileEntity entity) {
       return entity instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)entity).shouldZombify();
    }
}
