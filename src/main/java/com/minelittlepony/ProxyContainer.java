package com.minelittlepony;

import com.minelittlepony.forge.IForgeHooks;
import com.minelittlepony.forge.MLPCommonProxy;

import javax.annotation.Nullable;

public class ProxyContainer extends MLPCommonProxy {

    private IForgeHooks ponyArmors;

    @Override
    public void setForgeHooks(IForgeHooks armors) {
        this.ponyArmors = armors;
    }

    @Nullable
    public IForgeHooks getHooks() {
        return ponyArmors;
    }
}
