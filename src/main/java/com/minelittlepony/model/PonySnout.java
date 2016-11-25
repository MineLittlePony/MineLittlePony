package com.minelittlepony.model;

import com.minelittlepony.PonyGender;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.renderer.PlaneRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public class PonySnout extends ModelBase implements PonyModelConstants {

    private PonyGender gender;

    private PlaneRenderer mare;
    private PlaneRenderer stallion;

    public PonySnout(AbstractPonyModel pony, float yOffset, float stretch) {
        mare = new PlaneRenderer(pony);
        mare.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        mare.setTextureOffset(10, 14).addBackPlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 2, 0, stretch);
        mare.setTextureOffset(11, 13).addBackPlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 2, 1, 0, stretch);
        mare.setTextureOffset(9, 14).addTopPlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 1, 0, 1, stretch);
        mare.setTextureOffset(14, 14).addTopPlane(1.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 1, 0, 1, stretch);
        mare.setTextureOffset(11, 12).addTopPlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 2, 0, 1, stretch);
        mare.setTextureOffset(18, 7).addBottomPlane(-2.0F + HEAD_CENTRE_X, 4.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        mare.setTextureOffset(9, 14).addWestPlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 2, 1, stretch);
        mare.setTextureOffset(14, 14).addEastPlane(2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 2, 1, stretch);
        mare.setTextureOffset(11, 12).addWestPlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 1, 1, stretch);
        mare.setTextureOffset(12, 12).addEastPlane(1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 1, 1, stretch);

        pony.bipedHead.addChild(mare);

        stallion = new PlaneRenderer(pony);
        stallion.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        stallion.setTextureOffset(10, 13).addBackPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 3, 0, stretch);
        stallion.setTextureOffset(10, 13).addTopPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        stallion.setTextureOffset(18, 7).addBottomPlane(-2.0F + HEAD_CENTRE_X, 4.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        stallion.setTextureOffset(10, 13).addWestPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 3, 1, stretch);
        stallion.setTextureOffset(13, 13).addEastPlane(2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 3, 1, stretch);

        pony.bipedHead.addChild(stallion);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        mare.isHidden = gender != PonyGender.MARE;
        stallion.isHidden = gender != PonyGender.STALLION;
    }

    public void setGender(PonyGender gender) {
        this.gender = gender;
    }
}
