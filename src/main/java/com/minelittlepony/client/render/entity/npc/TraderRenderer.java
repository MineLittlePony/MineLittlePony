package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.ModelAlicorn;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.PonyRenderer.Caster;

public class TraderRenderer extends PonyRenderer.Caster<WanderingTraderEntity, ModelAlicorn<WanderingTraderEntity>> {

    public static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/entity/wandering_trader_pony.png");

    public TraderRenderer(EntityRenderDispatcher manager) {
        super(manager, ModelType.ALICORN.getKey(false));
    }

    @Override
    public Identifier findTexture(WanderingTraderEntity entity) {
        return TEXTURE;
    }

    @Override
    public void scale(WanderingTraderEntity entity, MatrixStack stack, float ticks) {
        super.scale(entity, stack, ticks);
        stack.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }
}
