package com.minelittlepony.model.components;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

import com.minelittlepony.model.AbstractPonyModel;

public class PegasusWings extends ModelBase {

    private final AbstractPonyModel pony;

    public final ModelWing leftWing;
    public final ModelWing rightWing;

    public PegasusWings(AbstractPonyModel pony, float yOffset, float stretch) {
        this.pony = pony;
        
        leftWing = new ModelWing(pony, false, 4f, yOffset, stretch, 32);
        rightWing = new ModelWing(pony, true, -6f, yOffset, stretch, 16);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ticks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        if (!isVisible()) return;
        
        float swing = 0;
        
        if (pony.swingProgress > 0) {
            swing = MathHelper.sin(MathHelper.sqrt(pony.swingProgress) * PI * 2);
        } else {
            float pi = PI * (float) Math.pow(limbSwingAmount, 16);
            
            float mve = limbSwing * 0.6662f; // magic number ahoy
            float srt = limbSwingAmount / 4;
            
            swing = MathHelper.cos(mve + pi) * srt;
        }
        
        leftWing.rotateWalking(swing);
        rightWing.rotateWalking(-swing);
        
        if (isExtended()) {
            float flapAngle = getWingRotationFactor(ticks);
            leftWing.rotateFlying(flapAngle);
            rightWing.rotateFlying(-flapAngle);
        }
        
    }
    
    public float getWingRotationFactor(float ticks) {
        if (pony.isFlying) {
            return (MathHelper.sin(ticks * 0.536f) * 1) + ROTATE_270 + 0.4f;
        }
        return LEFT_WING_ROTATE_ANGLE_Z_SNEAK;
    }
    
    public boolean isVisible() {
        return pony.metadata.getRace().hasWings();
    }
    
    public boolean isExtended() {
        return pony.isFlying || pony.isSneak;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!isVisible()) return;
        boolean standing = isExtended();
        leftWing.render(standing, scale);
        rightWing.render(standing, scale);
    }
}
