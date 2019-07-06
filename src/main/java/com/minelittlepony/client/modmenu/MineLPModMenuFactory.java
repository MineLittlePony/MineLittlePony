package com.minelittlepony.client.modmenu;

import com.minelittlepony.client.gui.GuiPonySettings;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class MineLPModMenuFactory implements ModMenuApi {

    @Override
    public String getModId() {
        return "minelp";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> new GuiPonySettings();
    }
}
