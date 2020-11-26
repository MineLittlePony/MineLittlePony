package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.WitchPonyModel;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class WitchRenderer extends PonyRenderer<WitchEntity, WitchPonyModel> {

    private static final Identifier WITCH_TEXTURES = new Identifier("minelittlepony", "textures/entity/witch_pony.png");

    public WitchRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.WITCH);
    }

    @Override
    protected HeldItemFeature<WitchEntity, WitchPonyModel> createItemHoldingLayer() {
        return new HeldItemFeature<WitchEntity, WitchPonyModel>(this) {
            @Override
            protected void preItemRender(WitchEntity entity, ItemStack drop, ModelTransformation.Mode transform, Arm hand, MatrixStack stack) {
                stack.translate(0, -0.3F, -0.8F);
                stack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(10));
            }
        };
    }

    @Override
    public void scale(WitchEntity entity, MatrixStack stack, float ticks) {
        super.scale(entity, stack, ticks);
        stack.scale(BASE_MODEL_SCALE, BASE_MODEL_SCALE, BASE_MODEL_SCALE);
    }

    @Override
    public Identifier findTexture(WitchEntity entity) {
        return WITCH_TEXTURES;
    }
}
