package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;

public class Stetson extends AbstractWearableGear implements IStackable {

    public Stetson(ModelPart tree) {
        super(Wearable.STETSON, BodyPart.HEAD);
        addPart(tree.getChild("rim"));
    }

    @Override
    public float getStackingHeight() {
        return 0.15F;
    }
}
