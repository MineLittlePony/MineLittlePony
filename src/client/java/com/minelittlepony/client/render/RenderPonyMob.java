package com.minelittlepony.client.render;

import com.minelittlepony.client.ducks.IRenderPony;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.render.layer.LayerGear;
import com.minelittlepony.client.render.layer.LayerHeldPonyItem;
import com.minelittlepony.client.render.layer.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.layer.LayerPonyArmor;
import com.minelittlepony.client.render.layer.LayerPonyCustomHead;
import com.minelittlepony.client.render.layer.LayerPonyElytra;
import com.minelittlepony.common.MineLittlePony;
import com.minelittlepony.common.pony.IPony;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import javax.annotation.Nonnull;

public abstract class RenderPonyMob<T extends EntityLiving> extends RenderLiving<T> implements IRenderPony<T> {

    protected RenderPony<T> renderPony = new RenderPony<T>(this);

    public RenderPonyMob(RenderManager manager, ModelWrapper model) {
        super(manager, model.getBody(), 0.5F);

        mainModel = renderPony.setPonyModel(model);

        addLayers();
    }

    protected void addLayers() {
        addLayer(new LayerPonyArmor<>(this));
        addLayer(createItemHoldingLayer());
        addLayer(new LayerArrow(this));
        addLayer(new LayerPonyCustomHead<>(this));
        addLayer(new LayerPonyElytra<>(this));
        addLayer(new LayerGear<>(this));
    }

    protected LayerHeldPonyItem<T> createItemHoldingLayer() {
        return new LayerHeldPonyItemMagical<>(this);
    }

    @Override
    public void doRender(T entity, double xPosition, double yPosition, double zPosition, float yaw, float ticks) {
        if (entity.isSneaking()) {
            yPosition -= 0.125D;
        }

        super.doRender(entity, xPosition, yPosition, zPosition, yaw, ticks);

        DebugBoundingBoxRenderer.instance.render(renderPony.getPony(entity), entity, ticks);
    }

    @Override
    protected void applyRotations(T entity, float ageInTicks, float rotationYaw, float partialTicks) {
        rotationYaw = renderPony.getRenderYaw(entity, rotationYaw, partialTicks);
        super.applyRotations(entity, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public boolean shouldRender(T entity, ICamera camera, double camX, double camY, double camZ) {
        return super.shouldRender(entity, renderPony.getFrustrum(entity, camera), camX, camY, camZ);
    }

    @Override
    public void preRenderCallback(T entity, float ticks) {
        renderPony.preRenderCallback(entity, ticks);
        shadowSize = renderPony.getShadowScale();

        if (entity.isChild()) {
            shadowSize *= 3; // undo vanilla shadow scaling
        }

        if (!entity.isRiding()) {
            GlStateManager.translate(0, 0, -entity.width / 2); // move us to the center of the shadow
        } else {
            GlStateManager.translate(0, entity.getYOffset(), 0);
        }
    }

    @Override
    public ModelWrapper getModelWrapper() {
        return renderPony.playerModel;
    }

    @Override
    public IPony getEntityPony(T entity) {
        return MineLittlePony.getInstance().getManager().getPony(getEntityTexture(entity));
    }

    @Override
    protected void renderLivingLabel(T entity, String name, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entity, name, x, renderPony.getNamePlateYOffset(entity, y), z, maxDistance);
    }

    @Override
    @Nonnull
    protected final ResourceLocation getEntityTexture(T entity) {
        return HDSkinManager.INSTANCE.getConvertedSkin(getTexture(entity));
    }

    @Override
    public RenderPony<T> getInternalRenderer() {
        return renderPony;
    }

    public abstract static class Proxy<T extends EntityLiving> extends RenderPonyMob<T> {

        public Proxy(List<LayerRenderer<T>> exportedLayers, RenderManager manager, ModelWrapper model) {
            super(manager, model);

            exportedLayers.addAll(layerRenderers);
        }

        @Override
        protected void addLayers() {
            layerRenderers.clear();
            super.addLayers();
        }

        public final ResourceLocation getTextureFor(T entity) {
            return super.getEntityTexture(entity);
        }
    }
}
