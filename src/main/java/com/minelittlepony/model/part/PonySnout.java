package com.minelittlepony.model.part;

import com.minelittlepony.PonyData;
import com.minelittlepony.PonyGender;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.PonyModelConstants;
import com.minelittlepony.renderer.PlaneRenderer;

public class PonySnout extends AbstractHeadPart implements PonyModelConstants {

    private PlaneRenderer mare;
    private PlaneRenderer stallion;

    public PonySnout(AbstractPonyModel pony) {
        super(pony);
    }

    @Override
    public void init(float yOffset, float stretch) {

        mare = new PlaneRenderer(this.pony);
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

        stallion = new PlaneRenderer(this.pony);
        stallion.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);

        stallion.setTextureOffset(10, 13).addBackPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 3, 0, stretch);
        stallion.setTextureOffset(10, 13).addTopPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        stallion.setTextureOffset(18, 7).addBottomPlane(-2.0F + HEAD_CENTRE_X, 4.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 4, 0, 1, stretch);
        stallion.setTextureOffset(10, 13).addWestPlane(-2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 3, 1, stretch);
        stallion.setTextureOffset(13, 13).addEastPlane(2.0F + HEAD_CENTRE_X, 1.0F + HEAD_CENTRE_Y, -5.0F + HEAD_CENTRE_Z, 0, 3, 1, stretch);
    }

    @Override
    public void animate(PonyData metadata, float move, float swing, float tick, float horz, float vert) {
        mare.isHidden = metadata.getGender() != PonyGender.MARE;
        stallion.isHidden = metadata.getGender() != PonyGender.STALLION;
    }

//    @Override
//    public void render(PonyData data, float scale) {
//        super.render(data, scale);
//        if (MineLittlePony.getConfig().snuzzles && data.getGender() != null) {
//            PlaneRenderer[] muzzle = MUZZLES.get(data.getGender());
//            for (int i = 0; i < muzzle.length; i++) {
//                muzzle[i].render(scale);
//            }
//        }
//    }

}
