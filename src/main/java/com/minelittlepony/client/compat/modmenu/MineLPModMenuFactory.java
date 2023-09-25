package com.minelittlepony.client.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import com.minelittlepony.client.PonySettingsscreen;

public class MineLPModMenuFactory implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PonySettingsscreen::new;
    }
}
