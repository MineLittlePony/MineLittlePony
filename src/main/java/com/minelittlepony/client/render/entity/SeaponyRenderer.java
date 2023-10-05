package com.minelittlepony.client.render.entity;

import org.jetbrains.annotations.NotNull;

import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.client.mixin.IResizeable;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.GuardianPonyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

public class SeaponyRenderer extends GuardianEntityRenderer {
    public static final Identifier SEAPONY = new Identifier("minelittlepony", "textures/entity/guardian/blueball.png");
    private static final Identifier SEAPONY_TEXTURES = new Identifier("minelittlepony", "textures/entity/guardian");
    public static final Identifier ELDER_SEAPONY = new Identifier("minelittlepony", "textures/entity/elder_guardian/blueball.png");
    private static final Identifier ELDER_SEAPONY_TEXTURES = new Identifier("minelittlepony", "textures/entity/elder_guardian");

    private final AbstractPonyRenderer<GuardianEntity, GuardianPonyModel> ponyRenderer;

    private final float scale;

    public SeaponyRenderer(EntityRendererFactory.Context context, TextureSupplier<GuardianEntity> texture, float scale) {
        super(context);
        ponyRenderer = AbstractPonyRenderer.proxy(context, ModelType.GUARDIAN, texture, scale, features, m -> model = m);
        this.scale = scale;
    }

    public static SeaponyRenderer guardian(EntityRendererFactory.Context context) {
        return new SeaponyRenderer(context, TextureSupplier.ofVariations(SEAPONY_TEXTURES, TextureSupplier.of(SEAPONY)), 1);
    }

    public static SeaponyRenderer elder(EntityRendererFactory.Context context) {
        return new SeaponyRenderer(context, TextureSupplier.ofVariations(ELDER_SEAPONY_TEXTURES, TextureSupplier.of(ELDER_SEAPONY)), ElderGuardianEntity.SCALE);
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
        ponyRenderer.manager.preRender(entity, ModelAttributes.Mode.THIRD_PERSON);

        float height = entity.getStandingEyeHeight();

        // aligns the beam to their horns
        ((IResizeable)entity).setStandingEyeHeight(2 * scale * ponyRenderer.manager.getScaleFactor());
        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
        ((IResizeable)entity).setStandingEyeHeight(height);
    }

    @Override
    protected void setupTransforms(GuardianEntity entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        ponyRenderer.manager.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);
    }
}
