package com.minelittlepony.model.ponies;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;

import static com.minelittlepony.model.PonyModelConstants.HEAD_RP_X;
import static com.minelittlepony.model.PonyModelConstants.HEAD_RP_Y;
import static com.minelittlepony.model.PonyModelConstants.HEAD_RP_Z;

import com.minelittlepony.render.PonyRenderer;

public class ModelWitchPony extends ModelVillagerPony {

    private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("textures/entity/witch.png");

    private PonyRenderer witchHat;

    public ModelWitchPony() {
        super();
    }

    public void setRotationAngles(float move, float swing, float age, float headYaw, float headPitch, float scale, Entity entity) {
        rightArmPose = ArmPose.EMPTY;
        leftArmPose = ((EntityWitch) entity).getHeldItemMainhand().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;

        super.setRotationAngles(move, swing, age, headYaw, headPitch, scale, entity);
        if (leftArmPose != ArmPose.EMPTY) {
            if (!canCast()) {
                bipedRightArm.rotateAngleX = -2 * (float)Math.PI/3;
                bipedRightArm.offsetZ = 0.1f;
            }
            unicornArmRight.offsetZ = -0.1f;
        }
    }

    @Override
    public void render(Entity entityIn, float move, float swing, float age, float headYaw, float headPitch, float scale) {
        super.render(entityIn, move, swing, age, headYaw, headPitch, scale);

        copyModelAngles(bipedHead, witchHat);

        TextureManager tex = Minecraft.getMinecraft().getRenderManager().renderEngine;
        tex.bindTexture(WITCH_TEXTURES);
        witchHat.render(scale * 1.3f);
    }

    @Override
    protected void initTextures() {
        super.initTextures();
        witchHat = new PonyRenderer(this).size(64, 128);
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        super.initPositions(yOffset, stretch);
        witchHat.offset(-5, -6, -7)
                .around(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z - 2)
                .tex(0, 64).box(0, 0, 0, 10, 2, 10, stretch)
                    .child(0).offset(-5, -5, -7).around(1.75F, -4, 2)
                        .tex(0, 76).box(0, 0, 0, 7, 4, 7, stretch)
                        .rotate(-0.05235988F, 0, 0.02617994F)
                        .child(0).offset(-5, -4, -7).around(1.75F, -4, 2)
                            .tex(0, 87).box(0, 0, 0, 4, 4, 4, stretch)
                            .rotate(-0.10471976F, 0, 0.05235988F)
                            .child(0).offset(-5, -2, -7).around(1.75F, -2, 2)
                                .tex(0, 95).box(0, 0, 0, 1, 2, 1, stretch)
                                .rotate(-0.20943952F, 0, 0.10471976F);
    }
}
