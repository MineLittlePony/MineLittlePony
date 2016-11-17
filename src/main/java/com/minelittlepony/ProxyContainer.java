package com.minelittlepony;

import com.minelittlepony.forge.IPonyArmor;
import com.minelittlepony.forge.MLPCommonProxy;

public class ProxyContainer extends MLPCommonProxy {

    private IPonyArmor ponyArmors;

    @Override
    public void setPonyArmors(IPonyArmor armors) {
        this.ponyArmors = armors;
    }

    public IPonyArmor getPonyArmors() {
        return ponyArmors;
    }
}
