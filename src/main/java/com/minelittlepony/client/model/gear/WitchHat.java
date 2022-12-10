package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;

public class WitchHat extends AbstractWearableGear implements IStackable {

    public WitchHat(ModelPart tree) {
        super(Wearable.HAT, BodyPart.HEAD);
        addPart(tree.getChild("hat"));
    }

    @Override
    public float getStackingHeight() {
        return 0.7F;
    }
}
