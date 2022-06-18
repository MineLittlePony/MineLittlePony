package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ModelType;

public class StriderRenderer extends MobEntityRenderer<StriderEntity, EntityModel<StriderEntity>> {
    public static final Identifier DRAGON_PONIES = new Identifier("minelittlepony", "textures/entity/strider/pony");
    public static final Identifier COLD_DRAGON_PONIES = new Identifier("minelittlepony", "textures/entity/strider/cold_pony");

    private static final Identifier SADDLE = new Identifier("minelittlepony", "textures/entity/strider/strider_saddle_pony.png");

    public StriderRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.STRIDER.createModel(), 0.5F);
        addFeature(new SaddleFeatureRenderer<>(this, ModelType.STRIDER_SADDLE.createModel(), SADDLE));
    }

    @Override
    public Identifier getTexture(StriderEntity entity) {
        return MineLittlePony.getInstance().getVariatedTextures().get(entity.isCold() ? COLD_DRAGON_PONIES : DRAGON_PONIES, entity);
    }

    @Override
    protected void scale(StriderEntity entity, MatrixStack stack, float ticks) {
        float scale = 0.9375F;
        if (entity.isBaby()) {
            scale *= 0.5F;
            shadowRadius = 0.25F;
        } else {
            shadowRadius = 0.5F;
        }

        stack.scale(scale, scale, scale);
    }

    @Override
    protected boolean isShaking(StriderEntity entity) {
        return entity.isCold();
    }
}
