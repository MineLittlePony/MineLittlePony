package com.brohoof.minelittlepony.model.part;

import com.brohoof.minelittlepony.PonyData;
import com.brohoof.minelittlepony.model.ModelPony;
import com.brohoof.minelittlepony.model.PonyModelConstants;

import net.minecraft.client.model.ModelRenderer;

public class PonyEars extends AbstractHeadPart implements PonyModelConstants {

    private ModelRenderer left;
    private ModelRenderer right;

    @Override
    public void init(ModelPony pony, float yOffset, float stretch) {
        super.init(pony, yOffset, stretch);
        this.left = new ModelRenderer(pony, 12, 16);
        this.right = new ModelRenderer(pony, 12, 16);
        this.right.mirror = true;

        this.left.addBox(-4.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);
        this.left.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.right.addBox(2.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);
        this.right.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
    }

    @Override
    protected void position(float posX, float posY, float posZ) {
        getPony().setRotationPoint(left, posX, posY, posZ);
        getPony().setRotationPoint(right, posX, posY, posZ);
    }

    @Override
    protected void rotate(float rotX, float rotY) {
        this.left.rotateAngleX = rotX;
        this.left.rotateAngleY = rotY;
        this.right.rotateAngleX = rotX;
        this.right.rotateAngleY = rotY;
    }

    @Override
    public void render(PonyData data, float scale) {
        super.render(data, scale);
        left.render(scale);
        right.render(scale);
    }

}
