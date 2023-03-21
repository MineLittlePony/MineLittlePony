package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

public class TraderRenderer extends PonyRenderer<WanderingTraderEntity, AlicornModel<WanderingTraderEntity>> {

    public static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/entity/wandering_trader_pony.png");

    public TraderRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ALICORN.getKey(false), TextureSupplier.of(TEXTURE), BASE_MODEL_SCALE);
    }
}
