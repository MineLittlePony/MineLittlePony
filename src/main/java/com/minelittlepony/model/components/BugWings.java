package com.minelittlepony.model.components;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.capabilities.IModelPegasus;

public class BugWings<T extends AbstractPonyModel & IModelPegasus> extends PegasusWings<T> {

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
        //pegasus.boxList.clear();
        //init(0, 0);
        return rightWing;
    }
}
