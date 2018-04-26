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

public class LayerEntityOnPonyShoulder extends AbstractPonyLayer<EntityPlayer> {

    private final RenderManager rm;
    private EntityLivingBase leftEntity;
    private EntityLivingBase rightEntity;

    public LayerEntityOnPonyShoulder(RenderManager rm, RenderLivingBase<AbstractClientPlayer> renderer) {
        super(renderer, getForgeLayer(rm));
        this.rm = rm;
    }

    public void doPonyRender(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1, 1, 1, 1);

        NBTTagCompound leftTag = player.getLeftShoulderEntity();

        if (!leftTag.hasNoTags()) {
            this.leftEntity = this.renderShoulderEntity(player, this.leftEntity, leftTag,
                    limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, true);
        }

        NBTTagCompound rightTag = player.getRightShoulderEntity();

        if (!rightTag.hasNoTags()) {
            this.rightEntity = this.renderShoulderEntity(player, this.rightEntity, rightTag,
                    limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, false);
        }

        GlStateManager.disableRescaleNormal();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private EntityLivingBase renderShoulderEntity(EntityPlayer player, @Nullable EntityLivingBase entity, NBTTagCompound tag, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, boolean left) {

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

        Render<Entity> render = rm.getEntityRenderObject(entity);
        if (render != null) {
            render.doRender(entity, 0, 0, 0, 0, 0);
            GlStateManager.popMatrix();
        }
        return entity;
    }

    private static LayerRenderer<EntityPlayer> getForgeLayer(RenderManager rm) {
        return ForgeProxy.createShoulderLayer()
                .orElse(LayerEntityOnShoulder::new)
                .apply(rm);
    }
}
