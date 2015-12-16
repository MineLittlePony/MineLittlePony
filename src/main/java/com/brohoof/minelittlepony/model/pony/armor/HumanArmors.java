package com.brohoof.minelittlepony.model.pony.armor;

import com.brohoof.minelittlepony.model.AbstractArmor;
import com.brohoof.minelittlepony.model.pony.ModelHumanPlayer;

public class HumanArmors extends AbstractArmor {

    public HumanArmors() {
        this.modelArmorChestplate = new ModelHumanPlayer();
        this.modelArmor = new ModelHumanPlayer();
    }

}
