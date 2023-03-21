package com.minelittlepony.client.render.entity;

import org.jetbrains.annotations.NotNull;

import com.minelittlepony.client.mixin.IResizeable;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.GuardianPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

public class SeaponyRenderer extends GuardianEntityRenderer {
    public static final Identifier TEXTURE = new Identifier("minelittlepony", "textures/entity/seapony.png");

    private final AbstractPonyRenderer<GuardianEntity, GuardianPonyModel> ponyRenderer;

    public SeaponyRenderer(EntityRendererFactory.Context context, float scale) {
        super(context);
        ponyRenderer = AbstractPonyRenderer.proxy(context, ModelType.GUARDIAN, TextureSupplier.of(TEXTURE), scale, features, m -> model = m);
    }

    public static SeaponyRenderer guardian(EntityRendererFactory.Context context) {
        return new SeaponyRenderer(context, 1);
    }

    public static SeaponyRenderer elder(EntityRendererFactory.Context context) {
        return new SeaponyRenderer(context, 1);
    }

    @Override
    @NotNull
    public final Identifier getTexture(GuardianEntity entity) {
        return ponyRenderer.getTexture(entity);
    }

    @Override
    protected void scale(GuardianEntity entity, MatrixStack stack, float ticks) {
        ponyRenderer.scale(entity, stack, ticks);
    }

    @Override
    public void render(GuardianEntity entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        IResizeable resize = (IResizeable)entity;
        EntityDimensions origin = resize.getCurrentSize();

        // aligns the beam to their horns
        resize.setCurrentSize(EntityDimensions.changing(origin.width, entity instanceof ElderGuardianEntity ? 6 : 3));

        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);

        resize.setCurrentSize(origin);
    }
}
