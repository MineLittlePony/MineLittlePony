package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import static com.minelittlepony.model.PonyModelConstants.*;

public class PegasusWings extends ModelBase {

    private final AbstractPonyModel pony;

    public final ModelWing leftWing, rightWing;

    public PegasusWings(AbstractPonyModel pony, float yOffset, float stretch) {
        this.pony = pony;
        
        leftWing = new ModelWing(pony, false, 4f, yOffset, stretch, 32);
        rightWing = new ModelWing(pony, true, -6f, yOffset, stretch, 16);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ticks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {

        float bodySwingRotation = 0.0F;
        if (pony.swingProgress > -9990.0F && !pony.metadata.hasMagic()) {
            bodySwingRotation = MathHelper.sin(MathHelper.sqrt(pony.swingProgress) * 3.1415927F * 2.0F) * 0.2F;
        }
        
        leftWing.updateModelRotation(bodySwingRotation);
        rightWing.updateModelRotation(bodySwingRotation);
        
        if (pony.isSneak && !pony.isFlying) {
            leftWing.rotateSneaked(LEFT_WING_ROTATE_ANGLE_Z_SNEAK);
            rightWing.rotateSneaked(-LEFT_WING_ROTATE_ANGLE_Z_SNEAK);
        } else if (pony.isFlying) {
            float WingRotateAngleZ = (MathHelper.sin(ticks * 0.536F) * 1.0F) + ROTATE_270 + 0.4F;
            
            leftWing.rotateUnsneaked(WingRotateAngleZ);
            rightWing.rotateUnsneaked(-WingRotateAngleZ);
        }
        
        leftWing.rotate(ROTATE_90);
        rightWing.rotate(ROTATE_90);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (pony.metadata.getRace().hasWings()) {
            boolean standing = !pony.isFlying && !pony.isSneak;
            leftWing.render(standing, scale);
            rightWing.render(standing, scale);
        }
    }
}
