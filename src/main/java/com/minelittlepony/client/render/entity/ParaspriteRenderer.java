package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ParaspriteModel;

public class ParaspriteRenderer extends MobEntityRenderer<StriderEntity, ParaspriteModel> {

    private static final Identifier NORMAL = new Identifier("minelittlepony", "textures/entity/strider/strider_pony.png");
    private static final Identifier CONFUSED = new Identifier("minelittlepony", "textures/entity/strider/strider_confused_pony.png");

    private static final Identifier SADDLE = new Identifier("minelittlepony", "textures/entity/strider/strider_saddle_pony.png");

    public ParaspriteRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, ModelType.PARASPRITE.createModel(), 0.5F);
        addFeature(new SaddleFeatureRenderer<>(this, ModelType.PARASPRITE.createModel(), SADDLE));
    }

    @Override
    public Identifier getTexture(StriderEntity entity) {
        return entity.isCold() ? CONFUSED : NORMAL;
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
