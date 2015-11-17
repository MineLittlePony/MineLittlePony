package com.brohoof.minelittlepony.renderer;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.model.ModelPony;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.model.pony.pm_newPonyAdv;
import com.brohoof.minelittlepony.renderer.layer.LayerHeldPonyItem;
import com.brohoof.minelittlepony.renderer.layer.LayerPonyArmor;
import com.brohoof.minelittlepony.renderer.layer.LayerPonySkull;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class RenderPonyMob<T extends EntityLiving> extends RenderLiving implements IRenderPony {
    protected ModelPony mobModel;
    protected PlayerModel playerModel;

    public RenderPonyMob(RenderManager renderManager, PlayerModel playerModel) {
        super(renderManager, playerModel.model, playerModel.shadowsize);
        this.mobModel = playerModel.model;
        this.playerModel = playerModel;

        this.addLayer(new LayerPonyArmor(this));
        this.addLayer(new LayerHeldPonyItem(this));
        // this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerPonySkull(this));
    }

    @Override
    public void doRender(EntityLiving entity, double xPosition, double yPosition, double zPosition, float yaw,
            float partialTicks) {
        ItemStack heldItem = entity.getHeldItem();
        this.playerModel.armor.modelArmorChestplate.heldItemRight = this.playerModel.armor.modelArmor.heldItemRight = this.playerModel.model.heldItemRight = heldItem == null
                ? 0 : 1;
        if (entity.isChild()) {
            this.playerModel.armor.modelArmorChestplate.size = this.playerModel.armor.modelArmor.size = this.playerModel.model.size = 0;
        } else {
            this.playerModel.armor.modelArmorChestplate.size = this.playerModel.armor.modelArmor.size = this.playerModel.model.size = 1;
        }

        this.playerModel.armor.modelArmorChestplate.issneak = this.playerModel.armor.modelArmor.issneak = this.playerModel.model.issneak = false;
        this.playerModel.armor.modelArmorChestplate.isFlying = this.playerModel.armor.modelArmor.isFlying = this.playerModel.model.isFlying = false;
        this.playerModel.armor.modelArmorChestplate.isPegasus = this.playerModel.armor.modelArmor.isPegasus = this.playerModel.model.isPegasus = false;
        if (this.playerModel.model instanceof pm_newPonyAdv) {
            ((pm_newPonyAdv) this.playerModel.model).setHasWings_Compression(false);
        }

        if (entity instanceof EntitySkeleton) {
            this.playerModel.armor.modelArmorChestplate.glowColor = this.playerModel.armor.modelArmor.glowColor = this.playerModel.model.glowColor = 0;
            switch (entity.getEntityId() % 3) {
            case 0:
                this.playerModel.armor.modelArmorChestplate.glowColor = this.playerModel.armor.modelArmor.glowColor = this.playerModel.model.glowColor = -10066211;
            case 1:
                this.playerModel.armor.modelArmorChestplate.isUnicorn = this.playerModel.armor.modelArmor.isUnicorn = this.playerModel.model.isUnicorn = true;
                break;
            case 2:
                this.playerModel.armor.modelArmorChestplate.isUnicorn = this.playerModel.armor.modelArmor.isUnicorn = this.playerModel.model.isUnicorn = false;
            }
        } else {
            this.playerModel.armor.modelArmorChestplate.isUnicorn = this.playerModel.armor.modelArmor.isUnicorn = this.playerModel.model.isUnicorn = false;
        }

        if (entity instanceof EntityPigZombie) {
            this.playerModel.armor.modelArmorChestplate.isMale = this.playerModel.armor.modelArmor.isMale = this.playerModel.model.isMale = true;
        } else {
            this.playerModel.armor.modelArmorChestplate.isMale = this.playerModel.armor.modelArmor.isMale = this.playerModel.model.isMale = false;
        }

        if (entity instanceof EntitySkeleton) {
            this.playerModel.model.wantTail = 4;
        } else {
            this.playerModel.model.wantTail = 0;
        }

        this.playerModel.armor.modelArmorChestplate.isSleeping = this.playerModel.armor.modelArmor.isSleeping = this.playerModel.model.isSleeping = false;
        this.playerModel.model.isVillager = false;
        if (MineLittlePony.getConfig().getShowScale().get()) {
            this.shadowSize = 0.4F;
        }

        double yOrigin = yPosition;
        if (entity.isSneaking()) {
            yOrigin -= 0.125D;
        }

        super.doRender(entity, xPosition, yOrigin, zPosition, yaw, partialTicks);
        this.playerModel.armor.modelArmorChestplate.aimedBow = this.playerModel.armor.modelArmor.aimedBow = this.playerModel.model.aimedBow = false;
        this.playerModel.armor.modelArmorChestplate.issneak = this.playerModel.armor.modelArmor.issneak = this.playerModel.model.issneak = false;
        this.playerModel.armor.modelArmorChestplate.heldItemRight = this.playerModel.armor.modelArmor.heldItemRight = this.playerModel.model.heldItemRight = 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doRender(Entity entity, double xPosition, double yPosition, double zPosition, float yaw,
            float partialTicks) {
        this.doRender((T) entity, xPosition, yPosition, zPosition, yaw, partialTicks);
    }

    protected abstract ResourceLocation getEntityTexture(T var1);

    @SuppressWarnings("unchecked")
    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return this.getEntityTexture((T) var1);
    }

    protected void preRenderCallback(T entity, float partick) {}

    @SuppressWarnings("unchecked")
    @Override
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
        preRenderCallback((T) entitylivingbaseIn, partialTickTime);
    }

    protected void rotateCorpse(T entity, float xPosition, float yPosition, float zPosition) {}

    @SuppressWarnings("unchecked")
    @Override
    protected void rotateCorpse(EntityLivingBase entity, float xPosition, float yPosition, float zPosition) {
        this.rotateCorpse((T) entity, xPosition, yPosition, zPosition);
        super.rotateCorpse(entity, xPosition, yPosition, zPosition);
    }

    @Override
    public PlayerModel getPony() {
        return playerModel;
    }
}
