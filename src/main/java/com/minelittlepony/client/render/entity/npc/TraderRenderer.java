package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.PonyRenderer;

public class TraderRenderer extends PonyRenderer.Caster<WanderingTraderEntity, AlicornModel<WanderingTraderEntity>> {

    public static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/entity/wandering_trader_pony.png");

    public TraderRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ALICORN.getKey(false));
    }

    @Override
    public Identifier getTexture(WanderingTraderEntity entity) {
        return TEXTURE;
    }

    @Override
    public void scale(WanderingTraderEntity entity, MatrixStack stack, float ticks) {
        super.scale(entity, stack, ticks);
        stack.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }
}
