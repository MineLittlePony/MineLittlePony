package com.minelittlepony.client.render.entity;

import com.minelittlepony.api.model.IUnicorn;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.feature.GearFeature;
import com.minelittlepony.client.render.entity.feature.HeldItemFeature;
import com.minelittlepony.client.render.entity.feature.GlowingItemFeature;
import com.minelittlepony.client.render.entity.feature.ArmourFeature;
import com.minelittlepony.client.render.entity.feature.SkullFeature;
import com.minelittlepony.client.render.entity.feature.ElytraFeature;
import com.minelittlepony.mson.api.ModelKey;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import javax.annotation.Nonnull;

public abstract class PonyRenderer<T extends MobEntity, M extends EntityModel<T> & IPonyModel<T>> extends MobEntityRenderer<T, M> implements IPonyRenderContext<T, M> {

    protected EquineRenderManager<T, M> manager = new EquineRenderManager<>(this);

    public PonyRenderer(EntityRendererFactory.Context context, ModelKey<? super M> key) {
        super(context, null, 0.5F);

        this.model = manager.setModel(key).getBody();

        addLayers(context);
    }

    protected void addLayers(EntityRendererFactory.Context context) {
        addFeature(new ArmourFeature<>(this));
        addFeature(createItemHoldingLayer());
        //addFeature(new StuckArrowsFeatureRenderer<>(this));
        addFeature(new SkullFeature<>(this, context.getModelLoader()));
        addFeature(new ElytraFeature<>(this));
        addFeature(new GearFeature<>(this));
    }

    protected abstract HeldItemFeature<T, M> createItemHoldingLayer();

    @Override
    public void render(T entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);
        DebugBoundingBoxRenderer.render(manager.getPony(entity), this, entity, stack, renderContext, tickDelta);
    }

    @Override
    protected void setupTransforms(T entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        manager.preRenderCallback(entity, stack, partialTicks);
        if (getModel() instanceof PlayerEntityModel) {
            ((PlayerEntityModel<?>)getModel()).setVisible(true);
        }

        if (getModel().getAttributes().isSitting) {
            stack.translate(0, 0.125D, 0);
        }

        rotationYaw = manager.getRenderYaw(entity, rotationYaw, partialTicks);
        super.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);

        manager.applyPostureTransform(entity, stack, rotationYaw, partialTicks);
    }

    @Override
    public boolean shouldRender(T entity, Frustum visibleRegion, double camX, double camY, double camZ) {
        return super.shouldRender(entity, manager.getFrustrum(entity, visibleRegion), camX, camY, camZ);
    }

    @Override
    public void scale(T entity, MatrixStack stack, float tickDelta) {
        shadowRadius = manager.getShadowScale();

        if (entity.isBaby()) {
            shadowRadius *= 3; // undo vanilla shadow scaling
        }

        if (!entity.hasVehicle()) {
            stack.translate(0, 0, -entity.getWidth() / 2); // move us to the center of the shadow
        } else {
            stack.translate(0, entity.getHeightOffset(), 0);
        }
    }

    @Override
    public ModelWrapper<T, M> getModelWrapper() {
        return manager.playerModel;
    }

    @Override
    protected void renderLabelIfPresent(T entity, Text name, MatrixStack stack, VertexConsumerProvider renderContext, int maxDistance) {
        stack.push();
        stack.translate(0, manager.getNamePlateYOffset(entity), 0);
        super.renderLabelIfPresent(entity, name, stack, renderContext, maxDistance);
        stack.pop();
    }

    @Deprecated
    @Override
    @Nonnull
    public final Identifier getTexture(T entity) {
        return findTexture(entity);
    }

    @Override
    public EquineRenderManager<T, M> getInternalRenderer() {
        return manager;
    }

    @Override
    public IPony getEntityPony(T entity) {
        return MineLittlePony.getInstance().getManager().getPony(findTexture(entity));
    }

    public abstract static class Caster<T extends MobEntity, M extends ClientPonyModel<T> & IUnicorn<ModelPart>> extends PonyRenderer<T, M> {

        public Caster(EntityRendererFactory.Context context, ModelKey<? super M> key) {
            super(context, key);
        }

        @Override
        protected HeldItemFeature<T, M> createItemHoldingLayer() {
            return new GlowingItemFeature<>(this);
        }
    }

    public abstract static class Proxy<T extends MobEntity, M extends EntityModel<T> & IPonyModel<T>> extends PonyRenderer<T, M> {

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Proxy(List exportedLayers, EntityRendererFactory.Context context, ModelKey<M> key) {
            super(context, key);

            exportedLayers.addAll(features);
        }

        @Override
        protected void addLayers(EntityRendererFactory.Context context) {
            features.clear();
            super.addLayers(context);
        }

        public final Identifier getTextureFor(T entity) {
            return super.getTexture(entity);
        }
    }
}
