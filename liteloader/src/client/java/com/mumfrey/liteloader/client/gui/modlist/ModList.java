package com.mumfrey.liteloader.client.gui.modlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.api.ModInfoDecorator;
import com.mumfrey.liteloader.client.gui.GuiLiteLoaderPanel;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.interfaces.Loadable;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.modconfig.ConfigManager;

public class ModList
{
    private final ModListContainer container;

    private final ConfigManager configManager;

    /**
     * List of enumerated mods
     */
    private final List<ModListEntry> mods = new ArrayList<ModListEntry>();

    /**
     * Currently selected mod
     */
    private ModListEntry selectedMod = null;

    private boolean hasConfig = false;

    public ModList(ModListContainer container, Minecraft minecraft, LiteLoaderMods mods, LoaderEnvironment environment, ConfigManager configManager,
            int brandColour, List<ModInfoDecorator> decorators)
    {
        this.container = container;
        this.configManager = configManager;

        this.populate(minecraft, mods, environment, brandColour, decorators);
    }

    /**
     * @param minecraft
     * @param mods
     * @param environment
     * @param brandColour
     * @param decorators
     */
    protected void populate(Minecraft minecraft, LiteLoaderMods mods, LoaderEnvironment environment, int brandColour,
            List<ModInfoDecorator> decorators)
    {
        // Add mods to this treeset first, in order to sort them
        Map<String, ModListEntry> sortedMods = new TreeMap<String, ModListEntry>();

        // Active mods
        for (ModInfo<LoadableMod<?>> mod : mods.getLoadedMods())
        {
            ModListEntry modListEntry = new ModListEntry(this, mods, environment, minecraft.fontRendererObj, brandColour, decorators, mod);
            sortedMods.put(modListEntry.getKey(), modListEntry);
        }

        // Disabled mods
        for (ModInfo<?> disabledMod : mods.getDisabledMods())
        {
            ModListEntry modListEntry = new ModListEntry(this, mods, environment, minecraft.fontRendererObj, brandColour, decorators, disabledMod);
            sortedMods.put(modListEntry.getKey(), modListEntry);
        }

        // Show bad containers if no other containers are found, should help users realise they have the wrong mod version!
        if (sortedMods.size() == 0)
        {
            for (ModInfo<?> badMod : mods.getBadContainers())
            {
                ModListEntry modListEntry = new ModListEntry(this, mods, environment, minecraft.fontRendererObj, brandColour, decorators, badMod);
                sortedMods.put(modListEntry.getKey(), modListEntry);
            }
        }

        // Injected tweaks
        for (ModInfo<Loadable<?>> injectedTweak : mods.getInjectedTweaks())
        {
            ModListEntry modListEntry = new ModListEntry(this, mods, environment, minecraft.fontRendererObj, brandColour, decorators, injectedTweak);
            sortedMods.put(modListEntry.getKey(), modListEntry);
        }

        // Add the sorted mods to the mods list
        this.mods.addAll(sortedMods.values());

        // Select the first mod in the list
        if (this.mods.size() > 0)
        {
            this.selectedMod = this.mods.get(0);
        }
    }

    public GuiLiteLoaderPanel getParentScreen()
    {
        return this.container.getParentScreen();
    }

    public LiteMod getSelectedModInstance()
    {
        return this.selectedMod != null ? this.selectedMod.getModInstance() : null;
    }

    public Class<? extends LiteMod> getSelectedModClass()
    {
        return this.selectedMod != null ? this.selectedMod.getModClass() : null;
    }

    public void setSize(int width, int height)
    {
        this.selectMod(this.selectedMod);
    }

    public void onTick()
    {
        for (ModListEntry mod : this.mods)
        {
            mod.onTick();
        }
    }

    public void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        ModListEntry lastSelectedMod = this.selectedMod;

        for (ModListEntry mod : this.mods)
        {
            mod.mousePressed(mouseX, mouseY, mouseButton);
        }

        if (this.selectedMod != null && this.selectedMod == lastSelectedMod)
        {
            this.selectedMod.getInfoPanel().mousePressed();
        }
    }

    public boolean keyPressed(char keyChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_UP)
        {
            int selectedIndex = this.mods.indexOf(this.selectedMod) - 1;
            if (selectedIndex > -1) this.selectMod(this.mods.get(selectedIndex));
            this.scrollSelectedModIntoView();
            return true;
        }
        else if (keyCode == Keyboard.KEY_DOWN)
        {
            int selectedIndex = this.mods.indexOf(this.selectedMod);
            if (selectedIndex > -1 && selectedIndex < this.mods.size() - 1) this.selectMod(this.mods.get(selectedIndex + 1));
            this.scrollSelectedModIntoView();
            return true;
        }
        else if (keyCode == Keyboard.KEY_SPACE
                || keyCode == Keyboard.KEY_RETURN
                || keyCode == Keyboard.KEY_NUMPADENTER
                || keyCode == Keyboard.KEY_RIGHT)
        {
            this.toggleSelectedMod();
            return true;
        }

        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.selectedMod != null)
        {
            this.selectedMod.getInfoPanel().mouseReleased();
        }
    }

    public boolean mouseWheelScrolled(int mouseWheelDelta)
    {
        return this.selectedMod != null && this.selectedMod.getInfoPanel().mouseWheelScrolled(mouseWheelDelta);
    }

    public int drawModList(int mouseX, int mouseY, float partialTicks, int left, int top, int width, int height)
    {
        this.drawModListPass(mouseX, mouseY, partialTicks, left, top, width, 0);
        return this.drawModListPass(mouseX, mouseY, partialTicks, left, top, width, 1);
    }

    protected int drawModListPass(int mouseX, int mouseY, float partialTicks, int left, int top, int width, int pass)
    {
        int yPos = top;
        for (ModListEntry mod : this.mods)
        {
            GuiModListPanel panel = mod.getListPanel();
            if (panel.isVisible())
            {
                if (yPos > 0) yPos += panel.getSpacing();
                panel.draw(mouseX, mouseY, partialTicks, left, yPos, width, mod == this.selectedMod, pass);
                yPos += panel.getHeight();
            }
        }
        return yPos;
    }

    public void drawModPanel(int mouseX, int mouseY, float partialTicks, int left, int top, int width, int height)
    {
        if (this.selectedMod != null)
        {
            this.selectedMod.getInfoPanel().draw(mouseX, mouseY, partialTicks, left, top, width, height);
        }
    }

    /**
     * @param mod Mod list entry to select
     */
    void selectMod(ModListEntry mod)
    {
        if (this.selectedMod != null)
        {
            this.selectedMod.getInfoPanel().mouseReleased();
        }

        this.selectedMod = mod;
        this.hasConfig = false;
        this.container.setEnableButtonVisible(false);
        this.container.setConfigButtonVisible(false);

        if (this.selectedMod != null && this.selectedMod.canBeToggled())
        {
            this.container.setEnableButtonVisible(true);
            this.container.setEnableButtonText(this.selectedMod.willBeEnabled() ? I18n.format("gui.disablemod") : I18n.format("gui.enablemod"));
            this.hasConfig = this.configManager.hasPanel(this.selectedMod.getModClass());
            this.container.setConfigButtonVisible(this.hasConfig);
        }
    }

    /**
     * Toggle the selected mod's enabled status
     */
    public void toggleSelectedMod()
    {
        if (this.selectedMod != null)
        {
            this.selectedMod.toggleEnabled();
            this.selectMod(this.selectedMod);
        }
    }

    private void scrollSelectedModIntoView()
    {
        if (this.selectedMod == null) return;

        int yPos = 0;
        for (ModListEntry mod : this.mods)
        {
            if (mod == this.selectedMod) break;
            yPos += mod.getListPanel().getTotalHeight();
        }

        int modHeight = this.selectedMod.getListPanel().getTotalHeight();
        this.container.scrollTo(yPos, yPos + modHeight);
    }

    public void showConfig(ModListEntry modListEntry)
    {
        this.container.showConfig();
    }
}
