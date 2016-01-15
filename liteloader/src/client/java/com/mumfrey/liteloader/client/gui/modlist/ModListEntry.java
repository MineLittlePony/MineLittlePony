package com.mumfrey.liteloader.client.gui.modlist;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.api.ModInfoDecorator;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.interfaces.Loadable;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.launch.LoaderEnvironment;

/**
 * Represents a mod in the mod info screen, keeps track of mod information and
 * provides methods for displaying the mod in the mod list and drawing the
 * selected mod info.
 *
 * @author Adam Mummery-Smith
 */
public class ModListEntry
{
    private final ModList modList;

    private final LiteLoaderMods mods;

    private final ModInfo<?> modInfo;

    private GuiModListPanel listPanel;

    private GuiModInfoPanel infoPanel;

    /**
     * Whether the mod is currently active
     */
    private boolean isActive;

    private boolean isValid;

    private boolean isMissingDependencies;

    private boolean isMissingAPIs;

    private boolean isErrored;

    /**
     * True if the mod is missing a dependency which has caused it not to load
     */
    private Set<String> missingDependencies;

    /**
     * True if the mod is missing an API which has caused it not to load
     */
    private Set<String> missingAPIs;

    /**
     * Whether the mod can be toggled, not all mods support this, eg. internal
     * mods
     */
    private boolean canBeToggled;

    /**
     * Whether the mod WILL be enabled on the next startup, if the mod is active
     * and has been disabled this will be false, and if it's currently disabled
     * by has been toggled then it will be true. 
     */
    private boolean willBeEnabled;

    /**
     * True if this is not a mod but an external jar 
     */
    private boolean isExternal;

    /**
     * Timer used to handle double-clicking on a mod
     */
    private int doubleClickTime = 0;

    /**
     * @param modList
     * @param mods
     * @param environment
     * @param fontRenderer
     * @param brandColour
     * @param decorators
     * @param modInfo
     */
    ModListEntry(ModList modList, LiteLoaderMods mods, LoaderEnvironment environment, FontRenderer fontRenderer, int brandColour,
            List<ModInfoDecorator> decorators, ModInfo<?> modInfo)
    {
        this.modList       = modList;
        this.mods          = mods;
        this.modInfo       = modInfo;

        this.isActive      = modInfo.isActive();
        this.isValid       = modInfo.isValid();
        this.canBeToggled  = modInfo.isToggleable() && mods.getEnabledModsList().saveAllowed();
        this.willBeEnabled = mods.isModEnabled(this.modInfo.getIdentifier());
        this.isExternal    = modInfo.getContainer().isExternalJar();
        this.isErrored     = modInfo.getStartupErrors() != null && modInfo.getStartupErrors().size() > 0;

        if (!modInfo.isActive() && this.isValid)
        {
            this.isActive = modInfo.getContainer().isEnabled(environment);

            Loadable<?> modContainer = modInfo.getContainer();
            if (modContainer instanceof LoadableMod<?>)
            {
                LoadableMod<?> loadableMod = (LoadableMod<?>)modContainer;

                this.missingDependencies   = loadableMod.getMissingDependencies();
                this.missingAPIs           = loadableMod.getMissingAPIs();
                this.isMissingDependencies = this.missingDependencies.size() > 0;
                this.isMissingAPIs         = this.missingAPIs.size() > 0;
            }
        }

        this.initPanels(fontRenderer, brandColour, decorators, modInfo);
    }

    /**
     * @param fontRenderer
     * @param brandColour
     * @param decorators
     * @param modInfo
     */
    protected void initPanels(FontRenderer fontRenderer, int brandColour, List<ModInfoDecorator> decorators, ModInfo<?> modInfo)
    {
        this.infoPanel = new GuiModInfoPanel(this, fontRenderer, brandColour, modInfo);

        if (this.isValid)
        {
            this.listPanel = new GuiModListPanel(this, fontRenderer, brandColour, modInfo, decorators);
        }
        else
        {
            this.listPanel = new GuiModListPanelInvalid(this, fontRenderer, brandColour, modInfo, decorators);
        }
    }

    public void onTick()
    {
        if (this.doubleClickTime > 0)
        {
            this.doubleClickTime--;
        }
    }

    public void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        if (this.getListPanel().isMouseOver())
        {
            this.modList.selectMod(this);

            if (this.getListPanel().isMouseOver())
            {
                this.getListPanel().mousePressed(mouseX, mouseY, mouseButton);
            }

            if (this.getListPanel().isMouseOverIcon())
            {
                this.getListPanel().iconClick(this.modList.getParentScreen());
            }
            else
            {
                // handle double-click
                if (this.doubleClickTime > 0)
                {
                    this.onDoubleClicked();
                }
            }

            this.doubleClickTime = 5;
        }
    }

    protected void onDoubleClicked()
    {
        this.modList.showConfig(this);
    }

    protected String getTitleText()
    {
        return this.modInfo.getDisplayName();
    }

    protected String getVersionText()
    {
        return I18n.format("gui.about.versiontext", this.modInfo.getVersion());
    }

    protected String getStatusText()
    {
        String statusText = this.isExternal ? I18n.format("gui.status.loaded") : I18n.format("gui.status.active");

        if (this.isMissingAPIs)
        {
            statusText = "\2475" + I18n.format("gui.status.missingapis");
            if (this.canBeToggled && !this.willBeEnabled) statusText = "\247c" + I18n.format("gui.status.pending.disabled");
        }
        else if (this.isMissingDependencies)
        {
            statusText = "\247e" + I18n.format("gui.status.missingdeps");
            if (this.canBeToggled && !this.willBeEnabled) statusText = "\247c" + I18n.format("gui.status.pending.disabled");
        }
        else if (this.isErrored)
        {
            statusText = "\247c" + I18n.format("gui.status.startuperror");
        }
        else if (this.canBeToggled)
        {
            if (!this.isActive && !this.willBeEnabled) statusText = "\2477" + I18n.format("gui.status.disabled");
            if (!this.isActive &&  this.willBeEnabled) statusText = "\247a" + I18n.format("gui.status.pending.enabled"); 
            if ( this.isActive && !this.willBeEnabled) statusText = "\247c" + I18n.format("gui.status.pending.disabled");
        }

        return statusText;
    }

    /**
     * Toggle the enablement status of this mod, if supported
     */
    public void toggleEnabled()
    {
        if (this.canBeToggled)
        {
            this.willBeEnabled = !this.willBeEnabled;
            this.mods.setModEnabled(this.modInfo.getIdentifier(), this.willBeEnabled);
        }
    }

    protected void displayHelpMessage(String title, String text)
    {
        this.infoPanel.displayHelpMessage(title, text);
    }

    public void clearHelpMessage()
    {
        this.infoPanel.clearHelpMessage();
    }

    public String getKey()
    {
        return (this.isErrored ? "0000" : "") + this.modInfo.getIdentifier() + Integer.toHexString(this.hashCode());
    }

    public ModInfo<?> getModInfo()
    {
        return this.modInfo;
    }

    public LiteMod getModInstance()
    {
        return this.modInfo.getMod();
    }

    public Class<? extends LiteMod> getModClass()
    {
        return this.modInfo.getModClass();
    }

    public String getName()
    {
        return this.modInfo.getDisplayName();
    }

    public String getVersion()
    {
        return this.modInfo.getVersion();
    }

    public String getAuthor()
    {
        return this.modInfo.getAuthor();
    }

    public String getDescription()
    {
        return this.modInfo.getDescription();
    }

    public boolean isEnabled()
    {
        return this.isActive;
    }

    public boolean canBeToggled()
    {
        return this.canBeToggled;
    }

    public boolean willBeEnabled()
    {
        return this.willBeEnabled;
    }

    public boolean isActive()
    {
        return this.isActive;
    }

    public boolean isErrored()
    {
        return this.isErrored;
    }

    public boolean isExternal()
    {
        return this.isExternal;
    }

    public boolean isMissingAPIs()
    {
        return this.isMissingAPIs;
    }

    public boolean isMissingDependencies()
    {
        return this.isMissingDependencies;
    }

    public GuiModListPanel getListPanel()
    {
        return this.listPanel;
    }

    public GuiModInfoPanel getInfoPanel()
    {
        return this.infoPanel;
    }
}
