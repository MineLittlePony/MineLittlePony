package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.RenderPony;
import com.minelittlepony.client.render.entity.feature.LayerGear;
import com.minelittlepony.client.render.entity.feature.LayerHeldPonyItem;
import com.minelittlepony.client.render.entity.feature.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.entity.feature.LayerPonyArmor;
import com.minelittlepony.client.render.entity.feature.LayerPonyCustomHead;
import com.minelittlepony.client.render.entity.feature.LayerPonyElytra;
import com.minelittlepony.model.IUnicorn;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.pony.IPony;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import javax.annotation.Nonnull;

public abstract class RenderPonyMob<T extends MobEntity, M extends EntityModel<T> & IPonyModel<T>> extends MobEntityRenderer<T, M> implements IPonyRender<T, M> {

    protected RenderPony<T, M> renderPony = new RenderPony<>(this);

    @SuppressWarnings("unchecked")
    public RenderPonyMob(EntityRenderDispatcher manager, ModelKey<? super M> key) {
        super(manager, (M)key.createModel(), 0.5F);

        this.model = renderPony.setPonyModel((ModelKey<M>)key).getBody();

        addLayers();
    }

    protected void addLayers() {
        addFeature(new LayerPonyArmor<>(this));
        addFeature(createItemHoldingLayer());
        //addFeature(new StuckArrowsFeatureRenderer<>(this));
        addFeature(new LayerPonyCustomHead<>(this));
        addFeature(new LayerPonyElytra<>(this));
        addFeature(new LayerGear<>(this));
    }

    protected abstract LayerHeldPonyItem<T, M> createItemHoldingLayer();

    @Override
    public void render(T entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        if (entity.isInSneakingPose()) {
            stack.translate(0, 0.125D, 0);
        }

        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);

        DebugBoundingBoxRenderer.instance.render(renderPony.getPony(entity), entity, stack, renderContext);
    }

    @Override
    protected void setupTransforms(T entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        rotationYaw = renderPony.getRenderYaw(entity, rotationYaw, partialTicks);
        super.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);

        renderPony.applyPostureTransform(entity, stack, rotationYaw, partialTicks);
    }

    @Override
    public boolean isVisible(T entity, Frustum visibleRegion, double camX, double camY, double camZ) {
        return super.isVisible(entity, renderPony.getFrustrum(entity, visibleRegion), camX, camY, camZ);
    }

    @Override
    public void scale(T entity, MatrixStack stack, float ticks) {
        renderPony.preRenderCallback(entity, stack, ticks);
        if (this.getModel() instanceof PlayerEntityModel) {
            ((PlayerEntityModel<?>)getModel()).setVisible(true);
        }

        // shadowRadius
        field_4673 = renderPony.getShadowScale();

        if (entity.isBaby()) {
            field_4673 *= 3; // undo vanilla shadow scaling
        }

        if (!entity.hasVehicle()) {
            stack.translate(0, 0, -entity.getWidth() / 2); // move us to the center of the shadow
        } else {
            stack.translate(0, entity.getHeightOffset(), 0);
        }
    }

    @Override
    public ModelWrapper<T, M> getModelWrapper() {
        return renderPony.playerModel;
    }

    @Override
    protected void renderLabelIfPresent(T entity, String name, MatrixStack stack, VertexConsumerProvider renderContext, int maxDistance) {
        stack.translate(0, renderPony.getNamePlateYOffset(entity), 0);
        super.renderLabelIfPresent(entity, name, stack, renderContext, maxDistance);
    }

    @Deprecated
    @Override
    @Nonnull
    public final Identifier getTexture(T entity) {
        return findTexture(entity);
    }

    @Override
    public RenderPony<T, M> getInternalRenderer() {
        return renderPony;
    }

    @Override
    public IPony getEntityPony(T entity) {
        return MineLittlePony.getInstance().getManager().getPony(findTexture(entity));
    }

    public abstract static class Caster<T extends MobEntity, M extends ClientPonyModel<T> & IUnicorn<ModelPart>> extends RenderPonyMob<T, M> {

        public Caster(EntityRenderDispatcher manager, ModelKey<? super M> key) {
            super(manager, key);
        }

        @Override
        protected LayerHeldPonyItem<T, M> createItemHoldingLayer() {
            return new LayerHeldPonyItemMagical<>(this);
        }
    }

    public abstract static class Proxy<T extends MobEntity, M extends EntityModel<T> & IPonyModel<T>> extends RenderPonyMob<T, M> {

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Proxy(List exportedLayers, EntityRenderDispatcher manager, ModelKey<M> key) {
            super(manager, key);

            exportedLayers.addAll(features);
        }

        @Override
        protected void addLayers() {
            features.clear();
            super.addLayers();
        }

        public final Identifier getTextureFor(T entity) {
            return super.getTexture(entity);
        }
    }
}
