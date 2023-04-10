package com.minelittlepony.client.render.entity.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.PillagerPonyModel;
import com.minelittlepony.client.render.entity.feature.IllagerHeldItemFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;
import com.minelittlepony.client.render.entity.PonyRenderer;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;

public class PillagerRenderer extends PonyRenderer<PillagerEntity, PillagerPonyModel<PillagerEntity>> {

    private static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/entity/illager/pillager_pony.png");

    public PillagerRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.PILLAGER, TextureSupplier.of(TEXTURE));
    }

    @Override
    protected HeldItemFeature<PillagerEntity, PillagerPonyModel<PillagerEntity>> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new IllagerHeldItemFeature<>(this, context.getHeldItemRenderer());
    }
}
