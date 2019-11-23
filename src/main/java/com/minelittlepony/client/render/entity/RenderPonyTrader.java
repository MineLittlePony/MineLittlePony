package com.minelittlepony.client.render.entity;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.entity.race.ModelAlicorn;

public class RenderPonyTrader extends RenderPonyMob.Caster<WanderingTraderEntity, ModelAlicorn<WanderingTraderEntity>> {

    public static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/entity/wandering_trader_pony.png");

    public RenderPonyTrader(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
        super(manager, new ModelAlicorn<>(false));
    }

    @Override
    public Identifier findTexture(WanderingTraderEntity entity) {
        return TEXTURE;
    }

    @Override
    public void scale(WanderingTraderEntity entity, MatrixStack stack, float ticks) {
        super.scale(entity, stack, ticks);
        stack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
