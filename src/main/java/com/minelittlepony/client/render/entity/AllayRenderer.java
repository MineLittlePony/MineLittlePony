package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.BreezieModel;

/**
 * AKA a breezie :D
 */
public class AllayRenderer extends MobEntityRenderer<AllayEntity, BreezieModel<AllayEntity>> {
    public static final Identifier BREEZIE_PONIES = new Identifier("minelittlepony", "textures/entity/allay/pony");

    public AllayRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.ALLAY.createModel(), 0.4f);
        addFeature(new HeldItemFeatureRenderer<AllayEntity, BreezieModel<AllayEntity>>(this, context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(AllayEntity allayEntity) {
        return MineLittlePony.getInstance().getVariatedTextures().get(BREEZIE_PONIES, allayEntity);
    }

    @Override
    protected void scale(AllayEntity entity, MatrixStack stack, float ticks) {
        stack.scale(0.4F, 0.4F, 0.4F);
    }

    @Override
    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}
