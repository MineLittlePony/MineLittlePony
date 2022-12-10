package com.minelittlepony.client.model.gear;

import net.minecraft.client.model.ModelPart;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;

public class Muffin extends AbstractWearableGear implements IStackable {

    public Muffin(ModelPart tree) {
        super(Wearable.MUFFIN, BodyPart.HEAD);
        addPart(tree.getChild("crown"));
    }

    @Override
    public float getStackingHeight() {
        return 0.45F;
    }
}
