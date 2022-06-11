package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import com.minelittlepony.client.model.ModelType;

public class AllayRenderer extends MobEntityRenderer<AllayEntity, AllayEntityModel> {
    private static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/entity/allay/allay_pony.png");

    public AllayRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ALLAY.createModel(), 0.4f);
        addFeature(new HeldItemFeatureRenderer<AllayEntity, AllayEntityModel>(this, context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(AllayEntity allayEntity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}
