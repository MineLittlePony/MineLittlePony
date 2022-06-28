package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ParaspriteModel;

public class VexRenderer extends MobEntityRenderer<VexEntity, ParaspriteModel<VexEntity>> {
    public static final Identifier PARASPRITE_PONIES = new Identifier("minelittlepony", "textures/entity/illager/vex_pony");

    public VexRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.VEX.createModel(), 0.3F);
    }

    @Override
    protected void scale(VexEntity entity, MatrixStack stack, float ticks) {
        stack.scale(0.4F, 0.4F, 0.4F);
    }

    @Override
    public Identifier getTexture(VexEntity entity) {
        return MineLittlePony.getInstance().getVariatedTextures().get(PARASPRITE_PONIES, entity);
    }

}
