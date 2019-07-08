package com.minelittlepony.client.render.entities;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.IPonyRender;
import com.minelittlepony.client.render.RenderPony;
import com.minelittlepony.client.render.layer.LayerGear;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;
import com.minelittlepony.client.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.layer.LayerPonyArmor;
import com.minelittlepony.client.render.layer.LayerPonyCustomHead;
import com.minelittlepony.client.render.layer.LayerPonyElytra;
import com.minelittlepony.client.util.render.PonyRenderer;
import com.minelittlepony.model.IUnicorn;
import com.minelittlepony.pony.IPony;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import javax.annotation.Nonnull;

public abstract class RenderPonyMob<T extends MobEntity, M extends EntityModel<T> & IPonyModel<T>> extends MobEntityRenderer<T, M> implements IPonyRender<T, M> {

    protected RenderPony<T, M> renderPony = new RenderPony<>(this);

    public RenderPonyMob(EntityRenderDispatcher manager, M model) {
        super(manager, model, 0.5F);

        this.model = renderPony.setPonyModel(new ModelWrapper<>(model));

        addLayers();
    }

    protected void addLayers() {
        addFeature(new LayerPonyArmor<>(this));
        addFeature(createItemHoldingLayer());
        addFeature(new StuckArrowsFeatureRenderer<>(this));
        addFeature(new LayerPonyCustomHead<>(this));
        addFeature(new LayerPonyElytra<>(this));
        addFeature(new LayerGear<>(this));
    }

    protected abstract LayerHeldPonyItem<T, M> createItemHoldingLayer();

    @Override
    public void render(T entity, double xPosition, double yPosition, double zPosition, float yaw, float ticks) {
        if (entity.isSneaking()) {
            yPosition -= 0.125D;
        }

        super.render(entity, xPosition, yPosition, zPosition, yaw, ticks);

        DebugBoundingBoxRenderer.instance.render(renderPony.getPony(entity), entity, ticks);
    }

    @Override
    protected void setupTransforms(T entity, float ageInTicks, float rotationYaw, float partialTicks) {
        rotationYaw = renderPony.getRenderYaw(entity, rotationYaw, partialTicks);
        super.setupTransforms(entity, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public boolean isVisible(T entity, VisibleRegion camera, double camX, double camY, double camZ) {
        return super.isVisible(entity, renderPony.getFrustrum(entity, camera), camX, camY, camZ);
    }

    @Override
    public void scale(T entity, float ticks) {
        renderPony.preRenderCallback(entity, ticks);
        // shadowRadius
        field_4673 = renderPony.getShadowScale();

        if (entity.isBaby()) {
            field_4673 *= 3; // undo vanilla shadow scaling
        }

        if (!entity.hasVehicle()) {
            GlStateManager.translatef(0, 0, -entity.getWidth() / 2); // move us to the center of the shadow
        } else {
            GlStateManager.translated(0, entity.getHeightOffset(), 0);
        }
    }

    @Override
    public ModelWrapper<T, M> getModelWrapper() {
        return renderPony.playerModel;
    }

    @Override
    protected void renderLabel(T entity, String name, double x, double y, double z, int maxDistance) {
        super.renderLabel(entity, name, x, renderPony.getNamePlateYOffset(entity, y), z, maxDistance);
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

    public abstract static class Caster<T extends MobEntity, M extends ClientPonyModel<T> & IUnicorn<PonyRenderer>> extends RenderPonyMob<T, M> {

        public Caster(EntityRenderDispatcher manager, M model) {
            super(manager, model);
        }

        @Override
        protected LayerHeldPonyItem<T, M> createItemHoldingLayer() {
            return new LayerHeldPonyItemMagical<>(this);
        }
    }

    public abstract static class Proxy<T extends MobEntity, M extends EntityModel<T> & IPonyModel<T>> extends RenderPonyMob<T, M> {

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Proxy(List exportedLayers, EntityRenderDispatcher manager, M model) {
            super(manager, model);

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
