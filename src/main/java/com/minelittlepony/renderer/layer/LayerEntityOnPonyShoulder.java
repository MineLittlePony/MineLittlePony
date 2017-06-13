package com.minelittlepony.renderer.layer;

import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.pony.ModelPlayerPony;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelParrot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderParrot;
import net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

public class LayerEntityOnPonyShoulder extends AbstractPonyLayer<EntityPlayer> {

    private final RenderManager rm;
    private ShoulderEntity leftEntity;
    private ShoulderEntity rightEntity;

    public LayerEntityOnPonyShoulder(RenderManager rm, RenderLivingBase<AbstractClientPlayer> renderer) {
        super(renderer, new LayerEntityOnShoulder(rm));
        this.rm = rm;
    }

    public void doPonyRender(EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch, float scale) {

        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        NBTTagCompound leftTag = player.func_192023_dk();

        if (!leftTag.hasNoTags()) {
            this.leftEntity = this.renderShoulderEntity(player, leftTag, this.leftEntity,
                    limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, true);
        }

        NBTTagCompound rightTag = player.func_192025_dl();

        if (!rightTag.hasNoTags()) {
            this.rightEntity =
                    this.renderShoulderEntity(player, rightTag, this.rightEntity,
                            limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, false);
        }

        GlStateManager.disableRescaleNormal();
    }

    @Nullable
    private ShoulderEntity renderShoulderEntity(EntityPlayer player, NBTTagCompound tag, @Nullable ShoulderEntity shoulder,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale,
            boolean left) {

        if (shoulder == null || !shoulder.uuid.equals(tag.getUniqueId("UUID"))) {
            UUID uuid = tag.getUniqueId("UUID");
            Class<?> clazz = EntityList.func_192839_a(tag.getString("id"));

            // this wan't an entity
            if (uuid == null || clazz == null) {
                return null;
            }

            if (clazz == EntityParrot.class) {
                RenderLiving<?> renderer = new RenderParrot(this.rm);
                ModelBase model = new ModelParrot();
                ResourceLocation resource = RenderParrot.field_192862_a[tag.getInteger("Variant")];
                shoulder = new ShoulderEntity(uuid, renderer, model, resource, clazz);
            } else {
                // not supported? TODO experiment with other mobs, probably will be changed by forge
                return null;
            }
        }

        shoulder.renderer.bindTexture(shoulder.resource);
        GlStateManager.pushMatrix();
        float f = -1.8F;
        float f1 = left ? 0.2F : -0.2F;
        ModelPlayerPony model = ((ModelPlayerPony)getRenderer().getMainModel());
        model.transform(BodyPart.HEAD);
        model.bipedHead.postRender(scale);
        GlStateManager.translate(f1, f, -0.2F);

        if (shoulder.clazz == EntityParrot.class) {
            ageInTicks = 0.0F;
            netHeadYaw = 0;
        }

        shoulder.model.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
        shoulder.model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, player);
        shoulder.model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GlStateManager.popMatrix();

        return shoulder;
    }

    private class ShoulderEntity {

        public UUID uuid;
        public RenderLivingBase<? extends EntityLivingBase> renderer;
        public ModelBase model;
        public ResourceLocation resource;
        public Class<?> clazz;

        public ShoulderEntity(UUID uuid, RenderLivingBase<? extends EntityLivingBase> renderer,
                ModelBase model, ResourceLocation res, Class<?> clazz) {
            this.uuid = uuid;
            this.renderer = renderer;
            this.model = model;
            this.resource = res;
            this.clazz = clazz;
        }
    }
}
