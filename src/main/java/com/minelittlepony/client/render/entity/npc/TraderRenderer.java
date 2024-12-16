package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

public class TraderRenderer extends PonyRenderer<WanderingTraderEntity, PonyRenderState, AlicornModel<PonyRenderState>> {
    public static final Identifier TEXTURE = MineLittlePony.id("textures/entity/wandering_trader_pony.png");

    public TraderRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ALICORN.steveKey(), TextureSupplier.of(TEXTURE), BASE_MODEL_SCALE);
    }

    @Override
    public PonyRenderState createRenderState() {
        return new PonyRenderState();
    }
}
