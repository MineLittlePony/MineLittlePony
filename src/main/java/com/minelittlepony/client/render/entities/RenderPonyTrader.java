package com.minelittlepony.client.render.entities;

import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.races.ModelAlicorn;
import com.mojang.blaze3d.platform.GlStateManager;

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
    public void scale(WanderingTraderEntity entity, float ticks) {
        super.scale(entity, ticks);
        GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
    }
}
