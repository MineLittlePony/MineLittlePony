package com.minelittlepony.model.pony.armor;

import com.minelittlepony.model.AbstractArmor;
import com.minelittlepony.model.pony.ModelHumanPlayer;

public class HumanArmors extends AbstractArmor {

    public HumanArmors() {
        this.modelArmorChestplate = new ModelHumanPlayer(false);
        this.modelArmor = new ModelHumanPlayer(false);
    }

}
