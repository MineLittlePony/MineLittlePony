package com.minelittlepony.model.ponies;

import com.minelittlepony.model.AbstractPonyModel;
import com.minelittlepony.model.BodyPart;
import com.minelittlepony.model.armour.PonyArmor;

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

    @Override
    protected void initTextures() {
        
    }

    @Override
    protected void initPositions(float yOffset, float stretch) {
        
    }

    @Override
    public PonyArmor createArmour() {
        return new PonyArmor(new ModelHumanPlayer(false), new ModelHumanPlayer(false));
    }
}
