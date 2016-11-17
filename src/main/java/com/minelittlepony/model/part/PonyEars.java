package com.minelittlepony.model.part;

import com.minelittlepony.PonyData;
import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.PonyModelConstants;

import net.minecraft.client.model.ModelRenderer;

public class PonyEars extends AbstractHeadPart implements PonyModelConstants {

    private ModelRenderer left;
    private ModelRenderer right;

    public PonyEars(AbstractPonyModel pony) {
        super(pony);
    }

    @Override
    public void init(float yOffset, float stretch) {
        this.left = new ModelRenderer(pony, 12, 16);
        this.right = new ModelRenderer(pony, 12, 16);
        this.right.mirror = true;

        this.left.addBox(-4.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);
        this.left.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
        this.right.addBox(2.0F + HEAD_CENTRE_X, -6.0F + HEAD_CENTRE_Y, 1.0F + HEAD_CENTRE_Z, 2, 2, 2, stretch);
        this.right.setRotationPoint(HEAD_RP_X, HEAD_RP_Y + yOffset, HEAD_RP_Z);
    }

    @Override
    public void render(PonyData data, float scale) {
        super.render(data, scale);
        left.render(scale);
        right.render(scale);
    }

}
