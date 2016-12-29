package com.minelittlepony.model.pony;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;

public class ModelHumanPlayer extends AbstractPonyModel {

    public ModelHumanPlayer(boolean smallArms) {
        super(smallArms);
    }

    @Override
    protected boolean doCancelRender() {
        return true;
    }

    @Override
    public void transform(BodyPart part) {
    }
}
