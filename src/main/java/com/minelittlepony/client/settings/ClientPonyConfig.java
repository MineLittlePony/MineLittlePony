package com.minelittlepony.client.settings;

import com.minelittlepony.client.render.entities.MobRenderers;
import com.minelittlepony.hdskins.HDSkins;
import com.minelittlepony.settings.PonyConfig;
import com.minelittlepony.settings.PonyLevel;

public class ClientPonyConfig extends PonyConfig {

    public ClientPonyConfig() {
        initWith(MobRenderers.values());
    }

    @Override
    public void setPonyLevel(PonyLevel ponylevel) {
        // only trigger reloads when the value actually changes
        if (ponylevel != getPonyLevel()) {
            HDSkins.getInstance().parseSkins();
        }

        super.setPonyLevel(ponylevel);
    }
}
