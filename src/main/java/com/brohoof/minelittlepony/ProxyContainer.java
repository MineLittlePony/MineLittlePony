package com.brohoof.minelittlepony;

import com.brohoof.minelittlepony.common.IPonyArmor;
import com.brohoof.minelittlepony.common.MLPCommonProxy;

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
