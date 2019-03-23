package com.minelittlepony.client.model.components;

import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.common.model.IPegasus;

public class BugWings<T extends AbstractPonyModel & IPegasus> extends PegasusWings<T> {

    public BugWings(T model, float yOffset, float stretch) {
        super(model, yOffset, stretch);
    }

    @Override
    public void init(float yOffset, float stretch) {
        leftWing = new ModelBugWing<>(pegasus, false, false, yOffset, stretch, 16);
        rightWing = new ModelBugWing<>(pegasus, true, false, yOffset, stretch, 16);
    }

    @Override
    public ModelWing<T> getRight() {
        return rightWing;
    }
}
