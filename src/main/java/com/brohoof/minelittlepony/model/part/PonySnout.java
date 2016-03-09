package com.brohoof.minelittlepony.model.part;

import java.util.Map;

import com.brohoof.minelittlepony.MineLittlePony;
import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.PonyGender;
import com.brohoof.minelittlepony.model.AbstractPonyModel;
import com.brohoof.minelittlepony.model.PonyModelConstants;
import com.brohoof.minelittlepony.renderer.PlaneRenderer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ObjectArrays;

import net.minecraft.client.model.ModelRenderer;

public class PonySnout extends AbstractHeadPart implements PonyModelConstants {

    private static final Map<PonyGender, PlaneRenderer[]> MUZZLES = ImmutableMap.<PonyGender, PlaneRenderer[]> builder()
            .put(PonyGender.MARE, new PlaneRenderer[10])
            .put(PonyGender.STALLION, new PlaneRenderer[5])
            .build();

    @Override
    public void init(AbstractPonyModel pony, float yOffset, float stretch) {
        super.init(pony, yOffset, stretch);

        PlaneRenderer[] muzzle = MUZZLES.get(PonyGender.MARE);
        muzzle[0] = new PlaneRenderer(pony, 10, 14);
        muzzle[1] = new PlaneRenderer(pony, 11, 13);
        muzzle[2] = new PlaneRenderer(pony, 9, 14);
        muzzle[3] = new PlaneRenderer(pony, 14, 14);
        muzzle[4] = new PlaneRenderer(pony, 11, 12);
        muzzle[5] = new PlaneRenderer(pony, 18, 7);
        muzzle[6] = new PlaneRenderer(pony, 9, 14);
        muzzle[7] = new PlaneRenderer(pony, 14, 14);
        muzzle[8] = new PlaneRenderer(pony, 11, 12);
        muzzle[9] = new PlaneRenderer(pony, 12, 12);

        muzzle[0].addBackPlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 2, 0, stretch);
        muzzle[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[1].addBackPlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 2, 1, 0, stretch);
        muzzle[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[2].addTopPlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 1, 0, 1, stretch);
        muzzle[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[3].addTopPlane(1.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 1, 0, 1, stretch);
        muzzle[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[4].addTopPlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 2, 0, 1, stretch);
        muzzle[4].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[5].addBottomPlane(-2.0F + HEAD_CENTRE_X, 4.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        muzzle[5].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[6].addSidePlane(-2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 2, 1, stretch);
        muzzle[6].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[7].addSidePlane(2.0F + HEAD_CENTRE_X, 2.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 2, 1, stretch);
        muzzle[7].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[8].addSidePlane(-1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 1, 1, stretch);
        muzzle[8].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[9].addSidePlane(1.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 1, 1, stretch);
        muzzle[9].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        muzzle = MUZZLES.get(PonyGender.STALLION);
        muzzle[0] = new PlaneRenderer(pony, 10, 13);
        muzzle[1] = new PlaneRenderer(pony, 10, 13);
        muzzle[2] = new PlaneRenderer(pony, 18, 7);
        muzzle[3] = new PlaneRenderer(pony, 10, 13);
        muzzle[4] = new PlaneRenderer(pony, 13, 13);

        muzzle[0].addBackPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 3, 0, stretch);
        muzzle[0].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[1].addTopPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        muzzle[1].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[2].addBottomPlane(-2.0F + HEAD_CENTRE_X, 4.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        muzzle[2].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[3].addSidePlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 3, 1, stretch);
        muzzle[3].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        muzzle[4].addSidePlane(2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 3, 1, stretch);
        muzzle[4].setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
    }

    @Override
    public void render(PonyData data, float scale) {
        super.render(data, scale);
        if (MineLittlePony.getConfig().snuzzles && data.getGender() != null) {
            PlaneRenderer[] muzzle = MUZZLES.get(data.getGender());
            for (int i = 0; i < muzzle.length; i++) {
                muzzle[i].render(scale);
            }
        }
    }

    @Override
    protected void position(float posX, float posY, float posZ) {
        for (PlaneRenderer[] pr : MUZZLES.values()) {
            for (PlaneRenderer p : pr) {
                AbstractPonyModel.setRotationPoint(p, posX, posY, posZ);
            }
        }
    }

    @Override
    protected void rotate(float rotX, float rotY) {
        for (PlaneRenderer[] pr : MUZZLES.values()) {
            for (PlaneRenderer p : pr) {
                p.rotateAngleX = rotX;
                p.rotateAngleY = rotY;
            }
        }
    }

    @Override
    protected ModelRenderer[] getModels() {
        ModelRenderer[] male = MUZZLES.get(PonyGender.STALLION);
        ModelRenderer[] female =  MUZZLES.get(PonyGender.MARE);
        return ObjectArrays.concat(male, female, ModelRenderer.class);
    }

}
