package com.minelittlepony.client.model.armour;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.armorstand.ArmorStandModel;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;

public class PonyArmourStandModel extends ArmorStandModel {
    public PonyArmourStandModel(ModelPart tree) {
        super(tree);
    }

    @Override
    public void setupAnim(final ArmorStandRenderState state) {
        super.setupAnim(state);
        float scale = 1.12F;
        root().xScale = scale;
        root().yScale = scale;
        root().zScale = scale;
        root().y -= 4F;
        root().z -= 0.5F;
        var baseplate = root().getChild("base_plate");
        baseplate.xScale = scale;
        baseplate.yScale = scale;
        baseplate.zScale = scale;
        baseplate.y -= 0.5F;
        baseplate.z += 1;
    }
}
