package com.minelittlepony.render.layer;

import com.minelittlepony.ForgeProxy;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.ponies.ModelPlayerPony;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class LayerEntityOnPonyShoulder extends AbstractPonyLayer<AbstractClientPlayer> {

    private final RenderManager renderManager;

    private EntityLivingBase leftEntity;
    private EntityLivingBase rightEntity;

    public LayerEntityOnPonyShoulder(RenderManager manager, RenderLivingBase<AbstractClientPlayer> renderer) {
        super(renderer, getForgeLayer(manager));
        renderManager = manager;
    }

    @Override
    public void doPonyRender(AbstractClientPlayer player, float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale) {

        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1, 1, 1, 1);

        NBTTagCompound leftTag = player.getLeftShoulderEntity();

        if (!leftTag.hasNoTags()) {
            leftEntity = renderShoulderEntity(player, leftEntity,
                    leftTag, move, swing, ticks, age, headYaw, headPitch, scale, true);
        }

        NBTTagCompound rightTag = player.getRightShoulderEntity();

        if (!rightTag.hasNoTags()) {
            rightEntity = renderShoulderEntity(player, rightEntity,
                    rightTag, move, swing, ticks, age, headYaw, headPitch, scale, false);
        }

        GlStateManager.disableRescaleNormal();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private EntityLivingBase renderShoulderEntity(AbstractClientPlayer player, @Nullable EntityLivingBase entity, NBTTagCompound tag,
            float move, float swing, float ticks, float age, float headYaw, float headPitch, float scale, boolean left) {

        if (entity == null || !entity.getUniqueID().equals(tag.getUniqueId("UUID"))) {
            entity = (EntityLivingBase) EntityList.createEntityFromNBT(tag, player.world);
            // this isn't an entity.
            if (entity == null) {
                return null;
            }
        }

        GlStateManager.pushMatrix();

        ModelPlayerPony model = ((ModelPlayerPony) getRenderer().getMainModel());
        model.transform(BodyPart.BODY);

        // render on the haunches
        GlStateManager.translate(left ? 0.25F : -0.25F, 0.25F, 0.35F);
        GlStateManager.scale(1, -1, -1);
        GlStateManager.rotate(5 * (left ? -1 : 1), 0, 0, 1);

        Render<Entity> render = renderManager.getEntityRenderObject(entity);
        if (render != null) {
            render.doRender(entity, 0, 0, 0, 0, 0);
            GlStateManager.popMatrix();
        }
        return entity;
    }

    private static LayerRenderer<EntityPlayer> getForgeLayer(RenderManager manager) {
        return ForgeProxy.createShoulderLayer()
                .orElse(LayerEntityOnShoulder::new)
                .apply(manager);
    }
}
