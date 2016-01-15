package com.mumfrey.liteloader.client.gui;

import static com.mumfrey.liteloader.gl.GL.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.api.BrandingProvider;
import com.mumfrey.liteloader.api.LiteAPI;
import com.mumfrey.liteloader.api.ModInfoDecorator;
import com.mumfrey.liteloader.client.api.LiteLoaderBrandingProvider;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.core.LiteLoaderVersion;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.core.api.LiteLoaderCoreAPI;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.modconfig.ConfigManager;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.util.render.Icon;

/**
 * GUI screen which displays info about loaded mods and also allows them to be
 * enabled and disabled. An instance of this class is created every time the
 * main menu is displayed and is drawn as an overlay until the tab is clicked,
 * at which point it becomes the active GUI screen and draws the parent main
 * menu screen as its background to give the appearance of being overlaid on the
 * main menu.
 *
 * @author Adam Mummery-Smith
 */
public class GuiLiteLoaderPanel extends GuiScreen
{
    static final int WHITE                           = 0xFFFFFFFF;
    static final int OPAQUE                          = 0xFF000000;
    static final int NOTIFICATION_TOOLTIP_FOREGROUND = 0xFFFFFF;
    static final int NOTIFICATION_TOOLTIP_BACKGROUND = 0xB0000099;
    static final int ERROR_TOOLTIP_FOREGROUND        = 0xFF5555;
    static final int ERROR_TOOLTIP_BACKGROUND        = 0xB0330000;
    static final int HEADER_HR_COLOUR                = 0xFF999999;
    static final int HEADER_TEXT_COLOUR              = GuiLiteLoaderPanel.WHITE;
    static final int HEADER_TEXT_COLOUR_SUB          = 0xFFAAAAAA;
    static final int TOOLTIP_FOREGROUND              = 0xFFFFFF;
    static final int TOOLTIP_FOREGROUND_SUB          = 0xCCCCCC;
    static final int TOOLTIP_BACKGROUND              = 0xB0000000;
    
    static final int LEFT_EDGE                       = 80;
    static final int MARGIN                          = 12;
    static final int TAB_WIDTH                       = 20;
    static final int TAB_HEIGHT                      = 40;
    static final int TAB_TOP                         = 20;
    static final int PANEL_TOP                       = 83;
    static final int PANEL_BOTTOM                    = 26;

    private static final double TWEEN_RATE = 0.08;

    private static boolean displayErrorToolTip = true;

    /**
     * Reference to the main menu which this screen is either overlaying or
     * using as its background.
     */
    private GuiScreen parentScreen;

    @SuppressWarnings("unused")
    private final LoaderEnvironment environment;

    private final LoaderProperties properties;

    /**
     * Tick number (update counter) used for tweening
     */
    private long tickNumber;

    /**
     * Last tick number, for tweening
     */
    private double lastTick;

    /**
     * Current tween percentage (0.0 -> 1.0)
     */
    private double tweenAmount = 0.0;

    /**
     * Since we don't get real mouse events we have to simulate them by tracking
     * the mouse state.
     */
    private boolean mouseDown, toggled, toggleable;

    /**
     * Hover opacity for the tab
     */
    private float tabOpacity = 0.0F;

    private boolean showTab = true;

    /**
     * Text to display under the header
     */
    private String activeModText, versionText;

    /**
     * Configuration panel
     */
    private GuiPanel currentPanel;

    private final GuiPanelMods modsPanel;
    private final GuiPanelSettings settingsPanel;

    private int brandColour = LiteLoaderBrandingProvider.BRANDING_COLOUR;

    private ResourceLocation logoResource = LiteLoaderBrandingProvider.ABOUT_TEXTURE;
    private Icon logoCoords = LiteLoaderBrandingProvider.LOGO_COORDS;

    private ResourceLocation iconResource = LiteLoaderBrandingProvider.ABOUT_TEXTURE;
    private Icon iconCoords = LiteLoaderBrandingProvider.ICON_COORDS;

    private List<ModInfoDecorator> modInfoDecorators = new ArrayList<ModInfoDecorator>();

    private boolean mouseOverLogo = false;

    private int startupErrorCount = 0, criticalErrorCount = 0;

    private String notification;

    private boolean isSnapshot;

    /**
     * @param minecraft
     * @param parentScreen
     * @param mods
     */
    public GuiLiteLoaderPanel(Minecraft minecraft, GuiScreen parentScreen, LiteLoaderMods mods, LoaderEnvironment environment,
            LoaderProperties properties, ConfigManager configManager, boolean showTab)
    {
        this.mc              = minecraft;
        this.fontRendererObj = minecraft.fontRendererObj;
        this.parentScreen    = parentScreen;
        this.showTab         = showTab;

        this.environment     = environment;
        this.properties      = properties;

        this.toggleable      = true;

        this.versionText = I18n.format("gui.about.versiontext", LiteLoader.getVersion());
        this.activeModText = I18n.format("gui.about.modsloaded", mods.getLoadedMods().size());

        this.initBranding();

        this.currentPanel = this.modsPanel = new GuiPanelMods(this, minecraft, mods, environment, configManager,
                this.brandColour, this.modInfoDecorators);
        this.settingsPanel = new GuiPanelSettings(this, minecraft);

        this.startupErrorCount = mods.getStartupErrorCount();
        this.criticalErrorCount = mods.getCriticalErrorCount();

        String branding = LiteLoader.getBranding();
        if (branding != null && branding.contains("SNAPSHOT"))
        {
            this.isSnapshot = true;
            this.versionText = "\247c" + branding;
        }
    }

    /**
     * 
     */
    private void initBranding()
    {
        LiteAPI logoProvider = null;

        int brandingColourProviderPriority = Integer.MIN_VALUE;
        int logoProviderPriority = Integer.MIN_VALUE;
        int iconProviderPriority = Integer.MIN_VALUE;

        for (LiteAPI api : LiteLoader.getAPIs())
        {
            BrandingProvider brandingProvider = LiteLoader.getCustomisationProvider(api, BrandingProvider.class);
            if (brandingProvider != null)
            {
                if (brandingProvider.getBrandingColour() != 0 && brandingProvider.getPriority() > brandingColourProviderPriority)
                {
                    brandingColourProviderPriority = brandingProvider.getPriority();
                    this.brandColour = GuiLiteLoaderPanel.OPAQUE | brandingProvider.getBrandingColour();
                }

                ResourceLocation logoResource = brandingProvider.getLogoResource();
                Icon logoCoords = brandingProvider.getLogoCoords();
                if (logoResource != null && logoCoords != null && brandingProvider.getPriority() > logoProviderPriority)
                {
                    logoProvider = api;
                    logoProviderPriority = brandingProvider.getPriority();
                    this.logoResource = logoResource;
                    this.logoCoords = logoCoords;
                }

                ResourceLocation iconResource = brandingProvider.getIconResource();
                Icon iconCoords = brandingProvider.getIconCoords();
                if (iconResource != null && iconCoords != null && brandingProvider.getPriority() > iconProviderPriority)
                {
                    iconProviderPriority = brandingProvider.getPriority();
                    this.iconResource = iconResource;
                    this.iconCoords = iconCoords;
                }
            }

            ModInfoDecorator modInfoDecorator = LiteLoader.getCustomisationProvider(api, ModInfoDecorator.class);
            if (modInfoDecorator != null)
            {
                this.modInfoDecorators.add(modInfoDecorator);
            }
        }

        if (logoProvider != null && !LiteLoaderCoreAPI.class.isAssignableFrom(logoProvider.getClass()))
        {
            this.versionText = I18n.format("gui.about.poweredbyversion", logoProvider.getVersion(), LiteLoader.getVersion());
        }
    }

    /**
     * Get the computed branding colour
     */
    public int getBrandColour()
    {
        return this.brandColour;
    }

    /**
     * Release references prior to being disposed
     */
    public void release()
    {
        this.parentScreen = null;
    }

    /**
     * Get the parent menu
     */
    public GuiScreen getScreen()
    {
        return this.parentScreen;
    }

    /**
     * Return true if the panel is not fully closed (tweening or open)
     */
    public boolean isOpen()
    {
        return this.tweenAmount > 0.0;
    }

    public void setToggleable(boolean toggleable)
    {
        this.toggleable = toggleable;
    }

    public void setNotification(String notification)
    {
        this.notification = notification;
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen#initGui()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        // Hide the tooltip once the user opens the panel
        GuiLiteLoaderPanel.displayErrorToolTip = false;

        this.currentPanel.setSize(this.width - LEFT_EDGE, this.height);

        this.buttonList.add(new GuiHoverLabel(2, LEFT_EDGE + MARGIN, this.height - PANEL_BOTTOM + 9, this.fontRendererObj,
                I18n.format("gui.about.taboptions"), this.brandColour));

        if (LiteLoaderVersion.getUpdateSite().canCheckForUpdate() && this.mc.theWorld == null && !this.isSnapshot)
        {
            this.buttonList.add(new GuiHoverLabel(3, LEFT_EDGE + MARGIN + 38 + this.fontRendererObj.getStringWidth(this.versionText) + 6, 50,
                    this.fontRendererObj, I18n.format("gui.about.checkupdates"), this.brandColour));
        }

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen
     *      #setWorldAndResolution(net.minecraft.client.Minecraft, int, int)
     */
    @Override
    public void setWorldAndResolution(Minecraft minecraft, int width, int height)
    {
        if (this.mc.currentScreen == this)
        {
            // Set res in parent screen if we are the active GUI
            this.parentScreen.setWorldAndResolution(minecraft, width, height);
        }

        super.setWorldAndResolution(minecraft, width, height);
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen#updateScreen()
     */
    @Override
    public void updateScreen()
    {
        this.currentPanel.onTick();

        this.tickNumber++;

        if (this.mc.currentScreen == this)
        {
            this.mc.currentScreen = this.parentScreen;
            this.parentScreen.updateScreen();
            this.mc.currentScreen = this;
        }

        if (this.toggled && this.toggleable)
        {
            this.onToggled();
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen#drawScreen(int, int, float)
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawScreen(mouseX, mouseY, partialTicks, false);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param alwaysExpandTab
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks, boolean alwaysExpandTab)
    {
        boolean active = this.mc.currentScreen == this;

        if (active)
        {
            // Draw the parent screen as our background if we are the active screen
            glClear(GL_DEPTH_BUFFER_BIT);
            this.parentScreen.drawScreen(-10, -10, partialTicks);
            glClear(GL_DEPTH_BUFFER_BIT);
        }
        else
        {
            // If this is not the active screen, copy the width and height from the parent GUI
            this.width = this.parentScreen.width;
            this.height = this.parentScreen.height;
        }

        // Calculate the current tween position
        float xOffset = (this.width - LEFT_EDGE) * this.calcTween(partialTicks, active) + 16.0F + (this.tabOpacity * -32.0F);
        int offsetMouseX = mouseX - (int)xOffset;

        // Handle mouse stuff here since we won't get mouse events when not the active GUI
        boolean mouseOverTab = this.showTab && (offsetMouseX > LEFT_EDGE - TAB_WIDTH
                                                && offsetMouseX < LEFT_EDGE
                                                && mouseY > TAB_TOP
                                                && mouseY < TAB_TOP + TAB_HEIGHT);
        this.handleMouseClick(offsetMouseX, mouseY, partialTicks, active, mouseOverTab);

        // Calculate the tab opacity, not framerate adjusted because we don't really care
        this.tabOpacity = mouseOverTab || alwaysExpandTab || this.startupErrorCount > 0 || this.notification != null
                || this.isOpen() ? 0.5F : Math.max(0.0F, this.tabOpacity - partialTicks * 0.1F);

        // Draw the panel contents
        this.drawPanel(offsetMouseX, mouseY, partialTicks, active, xOffset);
        this.drawTooltips(mouseX, mouseY, partialTicks, active, xOffset, mouseOverTab);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param active
     * @param xOffset
     */
    private void drawPanel(int mouseX, int mouseY, float partialTicks, boolean active, float xOffset)
    {
        this.mouseOverLogo = false;

        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        glPushMatrix();
        glTranslatef(xOffset, 0.0F, 0.0F);

        // Draw the background and left edge
        drawRect(LEFT_EDGE, 0, this.width, this.height, GuiLiteLoaderPanel.TOOLTIP_BACKGROUND);

        if (this.showTab)
        {
            drawRect(LEFT_EDGE, 0, LEFT_EDGE + 1, TAB_TOP, GuiLiteLoaderPanel.WHITE);
            drawRect(LEFT_EDGE, TAB_TOP + TAB_HEIGHT, LEFT_EDGE + 1, this.height, GuiLiteLoaderPanel.WHITE);

            this.mc.getTextureManager().bindTexture(LiteLoaderBrandingProvider.ABOUT_TEXTURE);
            glDrawTexturedRect(LEFT_EDGE - TAB_WIDTH, TAB_TOP, TAB_WIDTH + 1, TAB_HEIGHT, 80, 80, 122, 160, 0.5F + this.tabOpacity);
            if (this.startupErrorCount > 0)
            {
                glDrawTexturedRect(LEFT_EDGE - TAB_WIDTH + 7, TAB_TOP + 2, 12, 12, 134, 92, 134 + 12, 92 + 12, 0.5F + this.tabOpacity);
            }
            else if (this.notification != null)
            {
                glDrawTexturedRect(LEFT_EDGE - TAB_WIDTH + 7, TAB_TOP + 2, 12, 12, 134 + 12, 92, 134 + 24, 92 + 12, 0.5F + this.tabOpacity);
            }
        }
        else
        {
            drawRect(LEFT_EDGE, 0, LEFT_EDGE + 1, this.height, GuiLiteLoaderPanel.WHITE);
        }

        // Only draw the panel contents if we are actually open
        if (this.isOpen())
        {
            if (this.currentPanel.isCloseRequested())
            {
                this.closeCurrentPanel();
            }

            this.drawCurrentPanel(mouseX, mouseY, partialTicks);

            if (!this.currentPanel.stealFocus())
            {
                // Draw other controls inside the transform so that they slide properly
                super.drawScreen(mouseX, mouseY, partialTicks);
            }
        }
        else
        {
            this.closeCurrentPanel();
        }

        glPopMatrix();
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    private void drawCurrentPanel(int mouseX, int mouseY, float partialTicks)
    {
        glPushMatrix();
        glTranslatef(LEFT_EDGE, 0, 0);

        this.currentPanel.draw(mouseX - LEFT_EDGE, mouseY, partialTicks);

        glPopMatrix();
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    protected boolean drawInfoPanel(int mouseX, int mouseY, float partialTicks, int left, int bottom)
    {
        int right = this.width - MARGIN - LEFT_EDGE + left;
        left += MARGIN;

        // Draw the header pieces
        this.mc.getTextureManager().bindTexture(this.logoResource);
        glDrawTexturedRect(left, MARGIN, this.logoCoords, 1.0F);
        this.mc.getTextureManager().bindTexture(this.iconResource);
        glDrawTexturedRect(right - this.iconCoords.getIconWidth(), MARGIN, this.iconCoords, 1.0F);

        // Draw header text
        this.fontRendererObj.drawString(this.versionText, left + 38, 50, GuiLiteLoaderPanel.HEADER_TEXT_COLOUR);
        this.fontRendererObj.drawString(this.activeModText, left + 38, 60, GuiLiteLoaderPanel.HEADER_TEXT_COLOUR_SUB);

        // Draw top and bottom horizontal rules
        drawRect(left, 80, right, 81, GuiLiteLoaderPanel.HEADER_HR_COLOUR);
        drawRect(left, this.height - bottom + 2, right, this.height - bottom + 3, GuiLiteLoaderPanel.HEADER_HR_COLOUR);

        this.mouseOverLogo = (mouseY > MARGIN && mouseY < MARGIN + this.logoCoords.getIconHeight()
                && mouseX > left && mouseX < left + this.logoCoords.getIconWidth());
        return this.mouseOverLogo;
    }

    private void drawTooltips(int mouseX, int mouseY, float partialTicks, boolean active, float xOffset, boolean mouseOverTab)
    {
        boolean annoyingTip = this.startupErrorCount > 0 || this.notification != null;

        if (mouseOverTab && this.tweenAmount < 0.01)
        {
            GuiLiteLoaderPanel.drawTooltip(this.fontRendererObj, LiteLoader.getVersionDisplayString(), mouseX, mouseY, this.width, this.height,
                    GuiLiteLoaderPanel.TOOLTIP_FOREGROUND, GuiLiteLoaderPanel.TOOLTIP_BACKGROUND);
            GuiLiteLoaderPanel.drawTooltip(this.fontRendererObj, this.activeModText, mouseX, mouseY + 13, this.width, this.height,
                    GuiLiteLoaderPanel.TOOLTIP_FOREGROUND_SUB, GuiLiteLoaderPanel.TOOLTIP_BACKGROUND);

            if (annoyingTip)
            {
                this.drawNotificationTooltip(mouseX, mouseY - 13);
            }
        }
        else if (GuiLiteLoaderPanel.displayErrorToolTip && annoyingTip && !active && this.parentScreen instanceof GuiMainMenu)
        {
            this.drawNotificationTooltip((int)xOffset + LEFT_EDGE - 12, TAB_TOP + 2);
        }
    }

    private void drawNotificationTooltip(int left, int top)
    {
        if (this.startupErrorCount > 0)
        {
            GuiLiteLoaderPanel.drawTooltip(this.fontRendererObj, I18n.format("gui.error.tooltip", this.startupErrorCount, this.criticalErrorCount),
                    left, top, this.width, this.height, GuiLiteLoaderPanel.ERROR_TOOLTIP_FOREGROUND, GuiLiteLoaderPanel.ERROR_TOOLTIP_BACKGROUND);
        }
        else if (this.notification != null)
        {
            GuiLiteLoaderPanel.drawTooltip(this.fontRendererObj, this.notification, left, top, this.width, this.height,
                    GuiLiteLoaderPanel.NOTIFICATION_TOOLTIP_FOREGROUND, GuiLiteLoaderPanel.NOTIFICATION_TOOLTIP_BACKGROUND);
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen
     *      #actionPerformed(net.minecraft.client.gui.GuiButton)
     */
    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 2)
        {
            this.setCurrentPanel(this.settingsPanel);
        }

        if (button.id == 3)
        {
            this.setCurrentPanel(new GuiPanelUpdateCheck(this, this.mc, LiteLoaderVersion.getUpdateSite(), "LiteLoader", this.properties));
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen#keyTyped(char, int)
     */
    @Override
    protected void keyTyped(char keyChar, int keyCode)
    {
        this.currentPanel.keyPressed(keyChar, keyCode);
    }

    /**
     * 
     */
    void showLogPanel()
    {
        this.setCurrentPanel(new GuiPanelLiteLoaderLog(this.mc, this));
    }

    /**
     * 
     */
    void showAboutPanel()
    {
        this.setCurrentPanel(new GuiPanelAbout(this.mc, this));
    }

    public void showErrorPanel(ModInfo<?> mod)
    {
        this.setCurrentPanel(new GuiPanelError(this.mc, this, mod));
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen#mouseClicked(int, int, int)
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException
    {
        this.currentPanel.mousePressed(mouseX - LEFT_EDGE, mouseY, button);

        if (button == 0 && this.mouseOverLogo && !this.currentPanel.stealFocus())
        {
            this.showAboutPanel();
        }

        if (!this.currentPanel.stealFocus())
        {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen#mouseReleased(int, int, int)
     */
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button)
    {
        if (button == -1)
        {
            this.currentPanel.mouseMoved(mouseX - LEFT_EDGE, mouseY);
        }
        else
        {
            this.currentPanel.mouseReleased(mouseX - LEFT_EDGE, mouseY, button);
        }

        if (!this.currentPanel.stealFocus())
        {
            super.mouseReleased(mouseX, mouseY, button);
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.client.gui.GuiScreen#handleMouseInput()
     */
    @Override
    public void handleMouseInput() throws IOException
    {
        int mouseWheelDelta = Mouse.getEventDWheel();
        if (mouseWheelDelta != 0)
        {
            this.currentPanel.mouseWheelScrolled(mouseWheelDelta);
        }

        super.handleMouseInput();
    }

    /**
     * @param mouseX
     * @param active
     * @param mouseOverTab
     */
    public void handleMouseClick(int mouseX, int mouseY, float partialTicks, boolean active, boolean mouseOverTab)
    {
        boolean mouseDown = Mouse.isButtonDown(0);
        if (((active && mouseX < LEFT_EDGE && this.tweenAmount > 0.75) || mouseOverTab) && !this.mouseDown && mouseDown)
        {
            this.mouseDown = true;
            this.toggled = true;
        }
        else if (this.mouseDown && !mouseDown)
        {
            this.mouseDown = false;
        }
    }

    /**
     * @param partialTicks
     * @param active
     */
    private float calcTween(float partialTicks, boolean active)
    {
        double tickValue = this.tickNumber + partialTicks;

        if (active && this.tweenAmount < 1.0)
        {
            this.tweenAmount = Math.min(1.0, this.tweenAmount + ((tickValue - this.lastTick) * TWEEN_RATE));
        }
        else if (!active && this.isOpen())
        {
            this.tweenAmount = Math.max(0.0, this.tweenAmount - ((tickValue - this.lastTick) * TWEEN_RATE));
        }

        this.lastTick = tickValue;
        return 1.0F - (float)Math.sin(this.tweenAmount * 0.5 * Math.PI);
    }

    /**
     * Called when the tab is clicked
     */
    void onToggled()
    {
        this.toggled = false;
        this.mc.displayGuiScreen(this.mc.currentScreen == this ? this.parentScreen : this);
    }

    /**
     * Callback for the "config" button, display the config panel for the
     * currently selected mod.
     */
    void openConfigPanel(ConfigPanel panel, LiteMod mod)
    {
        if (panel != null)
        {
            this.setCurrentPanel(new GuiPanelConfigContainer(this.mc, panel, mod));
        }
    }

    /**
     * @param newPanel
     */
    private void setCurrentPanel(GuiPanel newPanel)
    {
        this.closeCurrentPanel();

        this.currentPanel = newPanel;
        this.currentPanel.setSize(this.width - LEFT_EDGE, this.height);
        this.currentPanel.onShown();
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.modconfig.ConfigPanelHost#close()
     */
    private void closeCurrentPanel()
    {
        this.currentPanel.onHidden();
        this.currentPanel = this.modsPanel;
        this.modsPanel.setSize(this.width - LEFT_EDGE, this.height);
    }

    /**
     * Draw a tooltip at the specified location and clip to screenWidth and
     * screenHeight
     * 
     * @param fontRenderer
     * @param tooltipText
     * @param mouseX
     * @param mouseY
     * @param screenWidth
     * @param screenHeight
     * @param colour
     * @param backgroundColour
     */
    public static void drawTooltip(FontRenderer fontRenderer, String tooltipText, int mouseX, int mouseY, int screenWidth, int screenHeight,
            int colour, int backgroundColour)
    {
        int textSize = fontRenderer.getStringWidth(tooltipText);
        mouseX = Math.max(0, Math.min(screenWidth - 4, mouseX - 4));
        mouseY = Math.max(0, Math.min(screenHeight - 16, mouseY));
        drawRect(mouseX - textSize - 2, mouseY, mouseX + 2, mouseY + 12, backgroundColour);
        fontRenderer.drawStringWithShadow(tooltipText, mouseX - textSize, mouseY + 2, colour);
    }


    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param u
     * @param v
     * @param u2
     * @param v2
     * @param alpha
     */
    static void glDrawTexturedRect(int x, int y, int width, int height, int u, int v, int u2, int v2, float alpha)
    {
        float texMapScale = 0.00390625F; // 256px
        glDrawTexturedRect(x, y, width, height, u * texMapScale, v * texMapScale, u2 * texMapScale, v2 * texMapScale, alpha);
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param u
     * @param v
     * @param u2
     * @param v2
     * @param alpha
     */
    static void glDrawTexturedRect(int x, int y, int width, int height, float u, float v, float u2, float v2, float alpha)
    {
        glDisableLighting();
        glEnableBlend();
        glAlphaFunc(GL_GREATER, 0.0F);
        glEnableTexture2D();
        glColor4f(1.0F, 1.0F, 1.0F, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertexWithUV(x + 0,     y + height, 0, u , v2);
        worldRenderer.addVertexWithUV(x + width, y + height, 0, u2, v2);
        worldRenderer.addVertexWithUV(x + width, y + 0,      0, u2, v );
        worldRenderer.addVertexWithUV(x + 0,     y + 0,      0, u , v );
        tessellator.draw();

        glDisableBlend();
        glAlphaFunc(GL_GREATER, 0.01F);
    }

    /**
     * @param x
     * @param y
     * @param icon
     * @param alpha
     */
    static void glDrawTexturedRect(int x, int y, Icon icon, float alpha)
    {
        glDrawTexturedRect(x, y, icon.getIconWidth(), icon.getIconHeight(), icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), alpha);
    }
}