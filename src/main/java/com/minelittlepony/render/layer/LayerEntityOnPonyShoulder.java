package com.minelittlepony.render.layer;

import com.minelittlepony.model.BodyPart;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * TODO: Looks like {@link LayerEntityOnShoulder}
 */
public class LayerEntityOnPonyShoulder extends AbstractPonyLayer<AbstractClientPlayer> {

    private final RenderManager renderManager;

    private EntityLivingBase leftEntity;
    private EntityLivingBase rightEntity;

    public LayerEntityOnPonyShoulder(RenderManager manager, RenderLivingBase<AbstractClientPlayer> renderer) {
        super(renderer);
        renderManager = manager;
    }

    @Override
    public void doPonyRender(AbstractClientPlayer player, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale) {

        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1, 1, 1, 1);

        NBTTagCompound leftTag = player.getLeftShoulderEntity();

        if (!leftTag.hasNoTags()) {
            leftEntity = renderShoulderEntity(player, leftEntity, leftTag, partialTicks, true);
        }

        NBTTagCompound rightTag = player.getRightShoulderEntity();

        if (!rightTag.hasNoTags()) {
            rightEntity = renderShoulderEntity(player, rightEntity, rightTag, partialTicks, false);
        }

        GlStateManager.disableRescaleNormal();
    }

    @Nullable
    private EntityLivingBase renderShoulderEntity(AbstractClientPlayer player, @Nullable EntityLivingBase entity, NBTTagCompound shoulderTag, float partialTicks, boolean left) {

        if (entity == null || !entity.getUniqueID().equals(shoulderTag.getUniqueId("UUID"))) {
            entity = (EntityLivingBase) EntityList.createEntityFromNBT(shoulderTag, player.world);
            // this isn't an entity.
            if (entity == null) {
                return null;
            }
        }

        Render<Entity> render = renderManager.getEntityRenderObject(entity);

        if (render == null) {
            return entity;
        }

        GlStateManager.pushMatrix();

        getPonyModel().transform(BodyPart.BODY);

        // render on the haunches
        GlStateManager.translate(left ? 0.25F : -0.25F, 0.25F, 0.35F);
        GlStateManager.scale(1, -1, -1);
        GlStateManager.rotate(left ? -5 : 5, 0, 0, 1);

        render.doRender(entity, 0, 0, 0, 0, partialTicks);

        GlStateManager.popMatrix();
        return entity;
    }
}
