package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.WitchPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.util.Identifier;

public class WitchRenderer extends PonyRenderer<WitchEntity, WitchPonyModel> {

    private static final Identifier WITCH_TEXTURES = new Identifier("minelittlepony", "textures/entity/witch_pony.png");

    public WitchRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.WITCH, TextureSupplier.of(WITCH_TEXTURES), BASE_MODEL_SCALE);
    }
}
