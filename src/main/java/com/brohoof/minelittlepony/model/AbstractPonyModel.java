package com.brohoof.minelittlepony.model;

import static net.minecraft.client.renderer.GlStateManager.rotate;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import java.util.List;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.PonySize;
import com.brohoof.minelittlepony.model.part.IPonyPart;
import com.brohoof.minelittlepony.model.pony.ModelPlayerPony;
import com.brohoof.minelittlepony.renderer.AniParams;
import com.brohoof.minelittlepony.renderer.PlaneRenderer;
import com.google.common.collect.Lists;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public abstract class AbstractPonyModel extends ModelPlayer {

    protected float scale = 0.0625F;

    public ModelRenderer steveLeftArm;
    public ModelRenderer steveRightArm;
    public ModelRenderer steveLeftArmwear;
    public ModelRenderer steveRightArmwear;
    
    public boolean isArmour = false;
    public boolean isVillager;
    public boolean isFlying;
    public boolean isSleeping;

    public PonyData metadata = new PonyData();

    protected List<IPonyPart> modelParts = Lists.newArrayList();

    public AbstractPonyModel(boolean arms) {
        super(0, arms);
        this.steveLeftArm = this.bipedLeftArm;
        this.steveRightArm = this.bipedRightArm;
        this.steveLeftArmwear = this.bipedLeftArmwear;
        this.steveRightArmwear = this.bipedLeftArmwear;
    }

    public final void init(float yOffset, float stretch) {
        this.initTextures();
        this.initPositions(yOffset, stretch);
        for (IPonyPart part : modelParts) {
            part.init(this, yOffset, stretch);
        }
    }

    protected void initTextures() {}

    protected void initPositions(float yOffset, float stretch) {}

    protected void animate(AniParams var1) {}

    protected void render() {}

    @Override
    public void render(Entity player, float Move, float Moveswing, float Loop, float Right, float Down, float Scale) {
        if (player instanceof AbstractClientPlayer) {
            setModelVisibilities((AbstractClientPlayer) player);
        }
        if (!doCancelRender()) {
            AniParams ani = new AniParams(Move, Moveswing, Loop, Right, Down);
            this.animate(ani);
            for (IPonyPart part : modelParts) {
                part.animate(metadata, ani);
            }
            GlStateManager.pushMatrix();
            this.render();
            GlStateManager.popMatrix();
            for (IPonyPart part : modelParts) {
                GlStateManager.pushMatrix();
                part.render(metadata, scale);
                GlStateManager.popMatrix();
            }
        } else {
            super.render(player, Move, Moveswing, Loop, Right, Down, Scale);
        }
    }

    @Override
    public void renderRightArm() {
        this.steveRightArm.render(0.0625F);
        this.steveRightArmwear.render(0.0625F);
    }

    @Override
    public void renderLeftArm() {
        this.steveLeftArm.render(0.0625f);
        this.steveLeftArmwear.render(0.0625f);
    }

    protected void setModelVisibilities(AbstractClientPlayer clientPlayer) {
        ModelPlayer modelplayer = this;

        if (clientPlayer.isSpectator()) {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        } else {
            ItemStack itemstack = clientPlayer.inventory.getCurrentItem();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.heldItemLeft = 0;
            modelplayer.aimedBow = false;
            modelplayer.isSneak = clientPlayer.isSneaking();

            if (itemstack == null) {
                modelplayer.heldItemRight = 0;
            } else {
                modelplayer.heldItemRight = 1;

                if (clientPlayer.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3;
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true;
                    }
                }
            }
        }
    }

    protected boolean doCancelRender() {
        return false;
    }

    public void setRotationPoint(ModelRenderer aRenderer, float setX, float setY, float setZ) {
        aRenderer.rotationPointX = setX;
        aRenderer.rotationPointY = setY;
        aRenderer.rotationPointZ = setZ;
    }

    public void setRotationPoint(PlaneRenderer aPlaneRenderer, float setX, float setY, float setZ) {
        aPlaneRenderer.rotationPointX = setX;
        aPlaneRenderer.rotationPointY = setY;
        aPlaneRenderer.rotationPointZ = setZ;
    }

    public void shiftRotationPoint(PlaneRenderer aPlaneRenderer, float shiftX, float shiftY, float shiftZ) {
        aPlaneRenderer.rotationPointX += shiftX;
        aPlaneRenderer.rotationPointY += shiftY;
        aPlaneRenderer.rotationPointZ += shiftZ;
    }

    public void shiftRotationPoint(ModelRenderer aRenderer, float shiftX, float shiftY, float shiftZ) {
        aRenderer.rotationPointX += shiftX;
        aRenderer.rotationPointY += shiftY;
        aRenderer.rotationPointZ += shiftZ;
    }

    public void transform(BodyPart part) {
        if (this.isRiding && !this.isArmour) {
            translate(0.0F, -0.56F, -0.46F);
        }

        if (this.isSleeping && !this.isArmour) {
            rotate(90.0F, 0.0F, 1.0F, 0.0F);
            rotate(270.0F, 0.0F, 0.0F, 1.0F);
            rotate(90.0F, 0.0F, 1.0F, 0.0F);
            rotate(180.0F, 0.0F, 0.0F, 1.0F);
            rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }

        if (this.metadata.getSize() == PonySize.FOAL || isChild) {
            if (this.isSneak && !this.isFlying && !this.isArmour) {
                translate(0.0F, -0.12F, 0.0F);
            }

            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, -1.0F, 0.25F);
            }
            switch (part) {
            case NECK:
            case HEAD:
                translate(0.0F, 0.76F, 0.0F);
                scale(0.9F, 0.9F, 0.9F);
                if (part == BodyPart.HEAD)
                    break;
                if (this.isSneak && !this.isFlying) {
                    translate(0.0F, -0.01F, 0.15F);
                }
                break;
            case BODY:
            case TAIL:
                translate(0.0F, 0.76F, -0.04F);
                scale(0.6F, 0.6F, 0.6F);
                break;
            case LEGS:
                translate(0.0F, 0.89F, 0.0F);
                scale(0.6F, 0.41F, 0.6F);
                if (this.isSneak && !this.isFlying) {
                    translate(0.0F, 0.12F, 0.0F);
                }

                if (this instanceof ModelPlayerPony && ((ModelPlayerPony) this).rainboom) {
                    translate(0.0F, -0.08F, 0.0F);
                }

                break;
            }

        } else if (this.metadata.getSize() == PonySize.LARGE) {
            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, -0.47F, 0.2F);
            }

            switch (part) {
            case HEAD:

                translate(0.0F, -0.17F, -0.04F);
                if (this.isSleeping && !this.isArmour) {
                    translate(0.0F, 0.0F, -0.1F);
                }

                if (this.isSneak && !this.isFlying) {
                    translate(0.0F, 0.15F, 0.0F);
                }

                break;
            case NECK:
                translate(0.0F, -0.15F, -0.07F);
                if (this.isSneak && !this.isFlying) {
                    translate(0.0F, 0.0F, -0.05F);
                }

                break;
            case BODY:
                translate(0.0F, -0.2F, -0.04F);
                scale(1.15F, 1.2F, 1.2F);
                break;
            case TAIL:
                translate(0.0F, -0.2F, 0.08F);
                break;
            case LEGS:
                translate(0.0F, -0.14F, 0.0F);
                scale(1.15F, 1.12F, 1.15F);
                break;
            }
        } else if (this.metadata.getSize() == PonySize.TALL) {
            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, -0.43F, 0.25F);
            }

            switch (part) {
            case HEAD:
                translate(0.0F, -0.15F, 0.01F);
                if (this.isSneak && !this.isFlying) {
                    translate(0.0F, 0.05F, 0.0F);
                }
                break;
            case NECK:
                translate(0.0F, -0.19F, -0.01F);
                scale(1.0F, 1.1F, 1.0F);
                if (this.isSneak && !this.isFlying) {
                    translate(0.0F, -0.06F, -0.04F);
                }
                break;
            case BODY:
            case TAIL:
                translate(0.0F, -0.1F, 0.0F);
                scale(1.0F, 1.0F, 1.0F);
                break;
            case LEGS:
                translate(0.0F, -0.25F, 0.03F);
                scale(1.0F, 1.18F, 1.0F);
                if (this instanceof ModelPlayerPony && ((ModelPlayerPony) this).rainboom) {
                    translate(0.0F, 0.05F, 0.0F);
                }
                break;
            }
        } else {
            if (this.isSleeping && !this.isArmour) {
                translate(0.0F, -0.535F, 0.25F);
            }
        }
    }

    @Override
    public void setModelAttributes(ModelBase model) {
        super.setModelAttributes(model);
        if (model instanceof AbstractPonyModel) {
            AbstractPonyModel pony = (AbstractPonyModel) model;
            this.isFlying = pony.isFlying;
            this.isSleeping = pony.isSleeping;
            this.isVillager = pony.isVillager;
        }
    }

}
