package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.WitchPonyModel;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class WitchRenderer extends PonyRenderer<WitchEntity, WitchPonyModel> {

    private static final Identifier WITCH_TEXTURES = new Identifier("minelittlepony", "textures/entity/witch_pony.png");

    public WitchRenderer(EntityRendererFactory.Context context) {
        super(context, ModelType.WITCH, TextureSupplier.of(WITCH_TEXTURES), BASE_MODEL_SCALE);
    }

    @Override
    protected HeldItemFeature<WitchEntity, WitchPonyModel> createHeldItemFeature(EntityRendererFactory.Context context) {
        return new HeldItemFeature<WitchEntity, WitchPonyModel>(this) {
            @Override
            protected void preItemRender(WitchEntity entity, ItemStack drop, ModelTransformationMode transform, Arm hand, MatrixStack stack) {
                super.preItemRender(entity, drop, transform, hand, stack);
                stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10));
            }
        };
    }
}
