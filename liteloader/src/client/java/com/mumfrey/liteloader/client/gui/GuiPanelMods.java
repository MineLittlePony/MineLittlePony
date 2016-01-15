package com.mumfrey.liteloader.client.gui;

import static com.mumfrey.liteloader.gl.GL.*;
import static com.mumfrey.liteloader.gl.GLClippingPlanes.*;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.api.ModInfoDecorator;
import com.mumfrey.liteloader.client.gui.modlist.ModList;
import com.mumfrey.liteloader.client.gui.modlist.ModListContainer;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.modconfig.ConfigManager;
import com.mumfrey.liteloader.modconfig.ConfigPanel;

/**
 * Mods panel
 * 
 * @author Adam Mummery-Smith
 */
public class GuiPanelMods extends GuiPanel implements ModListContainer
{
    private static final int SCROLLBAR_WIDTH = 5;

    private final GuiLiteLoaderPanel parentScreen;

    private final ConfigManager configManager;

    /**
     * List of enumerated mods
     */
    private ModList modList;

    /**
     * Enable / disable button
     */
    private GuiButton btnToggle;

    /**
     * Config button 
     */
    private GuiButton btnConfig;

    /**
     * Height of all the items in the list
     */
    private int listHeight = 100;

    /**
     * Scroll bar control for the mods list
     */
    private GuiSimpleScrollBar scrollBar = new GuiSimpleScrollBar();

    public GuiPanelMods(GuiLiteLoaderPanel parentScreen, Minecraft minecraft, LiteLoaderMods mods, LoaderEnvironment environment,
            ConfigManager configManager, int brandColour, List<ModInfoDecorator> decorators)
    {
        super(minecraft);

        this.parentScreen = parentScreen;
        this.configManager = configManager;

        this.modList = new ModList(this, minecraft, mods, environment, configManager, brandColour, decorators);
    }

    @Override
    public GuiLiteLoaderPanel getParentScreen()
    {
        return this.parentScreen;
    }

    @Override
    public void setConfigButtonVisible(boolean visible)
    {
        this.btnConfig.visible = visible;
    }

    @Override
    public void setEnableButtonVisible(boolean visible)
    {
        this.btnToggle.visible = visible;
    }

    @Override
    public void setEnableButtonText(String displayString)
    {
        this.btnToggle.displayString = displayString;
    }

    @Override
    boolean stealFocus()
    {
        return false;
    }

    @Override
    void setSize(int width, int height)
    {
        super.setSize(width, height);

        int rightPanelLeftEdge = MARGIN + 4 + (this.width - MARGIN - MARGIN - 4) / 2;

        this.controls.clear();
        this.controls.add(this.btnToggle = new GuiButton(0, rightPanelLeftEdge, this.height - GuiLiteLoaderPanel.PANEL_BOTTOM - 24, 90, 20,
                I18n.format("gui.enablemod")));
        this.controls.add(this.btnConfig = new GuiButton(1, rightPanelLeftEdge + 92, this.height - GuiLiteLoaderPanel.PANEL_BOTTOM - 24, 69, 20,
                I18n.format("gui.modsettings")));

        this.modList.setSize(width, height);
    }

    @Override
    void onTick()
    {
        this.modList.onTick();
    }

    @Override
    void onHidden()
    {
    }

    @Override
    void onShown()
    {
    }

    @Override
    void mousePressed(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            if (this.scrollBar.wasMouseOver())
            {
                this.scrollBar.setDragging(true);
            }

            if (mouseY > GuiLiteLoaderPanel.PANEL_TOP && mouseY < this.height - GuiLiteLoaderPanel.PANEL_BOTTOM)
            {
                this.modList.mousePressed(mouseX, mouseY, mouseButton);
            }
        }

        super.mousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    void keyPressed(char keyChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.parentScreen.onToggled();
            return;
        }
        else if (this.modList.keyPressed(keyChar, keyCode))
        {
            // Suppress further handling
        }
        else if (keyCode == Keyboard.KEY_F3)
        {
            this.parentScreen.showLogPanel();
        }
        else if (keyCode == Keyboard.KEY_F1)
        {
            this.parentScreen.showAboutPanel();
        }
    }

    @Override
    void mouseMoved(int mouseX, int mouseY)
    {
    }

    @Override
    void mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            this.scrollBar.setDragging(false);
            this.modList.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    void mouseWheelScrolled(int mouseWheelDelta)
    {
        if (!this.modList.mouseWheelScrolled(mouseWheelDelta))
        {
            this.scrollBar.offsetValue(-mouseWheelDelta / 8);
        }
    }

    @Override
    public void showConfig()
    {
        this.actionPerformed(this.btnConfig);
    }

    @Override
    void actionPerformed(GuiButton control)
    {
        if (control.id == 0)
        {
            this.modList.toggleSelectedMod();
        }

        if (control.id == 1)
        {
            Class<? extends LiteMod> modClass = this.modList.getSelectedModClass();

            if (modClass != null)
            {
                ConfigPanel panel = this.configManager.getPanel(modClass);
                LiteMod mod = this.modList.getSelectedModInstance();
                this.parentScreen.openConfigPanel(panel, mod);
            }
        }
    }

    @Override
    void draw(int mouseX, int mouseY, float partialTicks)
    {
        this.parentScreen.drawInfoPanel(mouseX, mouseY, partialTicks, 0, GuiLiteLoaderPanel.PANEL_BOTTOM);

        int innerWidth = this.width - MARGIN - MARGIN - 4;
        int panelWidth = innerWidth / 2;
        int panelHeight = this.height - GuiLiteLoaderPanel.PANEL_BOTTOM - GuiLiteLoaderPanel.PANEL_TOP;

        this.drawModsList(mouseX, mouseY, partialTicks, panelWidth, panelHeight);

        int left = MARGIN + panelWidth;
        int top = GuiLiteLoaderPanel.PANEL_TOP;
        int spaceForButtons = (this.btnConfig.visible || this.btnToggle.visible ? 28 : 0);
        int bottom = this.height - GuiLiteLoaderPanel.PANEL_BOTTOM - spaceForButtons;

        glEnableClipping(left, this.width - MARGIN, top, bottom);
        this.modList.drawModPanel(mouseX, mouseY, partialTicks, left, top, this.width - MARGIN - left, panelHeight - spaceForButtons);
        glDisableClipping();

        super.draw(mouseX, mouseY, partialTicks);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param width
     * @param height
     */
    private void drawModsList(int mouseX, int mouseY, float partialTicks, int width, int height)
    {
        this.scrollBar.drawScrollBar(mouseX, mouseY, partialTicks, MARGIN + width - SCROLLBAR_WIDTH, GuiLiteLoaderPanel.PANEL_TOP, SCROLLBAR_WIDTH,
                height, this.listHeight);

        // clip outside of scroll area
        glEnableClipping(MARGIN, MARGIN + width - SCROLLBAR_WIDTH - 1, GuiLiteLoaderPanel.PANEL_TOP, this.height - GuiLiteLoaderPanel.PANEL_BOTTOM);

        // handle scrolling
        glPushMatrix();
        glTranslatef(0.0F, GuiLiteLoaderPanel.PANEL_TOP - this.scrollBar.getValue(), 0.0F);

        mouseY -= (GuiLiteLoaderPanel.PANEL_TOP - this.scrollBar.getValue());

        this.listHeight = this.modList.drawModList(mouseX, mouseY, partialTicks, MARGIN, 0, width - SCROLLBAR_WIDTH - 1, height);
        this.scrollBar.setMaxValue(this.listHeight - height);

        glPopMatrix();
        glDisableClipping();
    }

    @Override
    public void scrollTo(int yPosTop, int yPosBottom)
    {
        // Mod is above the top of the visible window
        if (yPosTop < this.scrollBar.getValue())
        {
            this.scrollBar.setValue(yPosTop);
            return;
        }

        int panelHeight = this.height - GuiLiteLoaderPanel.PANEL_BOTTOM - GuiLiteLoaderPanel.PANEL_TOP;

        // Mod is below the bottom of the visible window
        if (yPosBottom - this.scrollBar.getValue() > panelHeight)
        {
            this.scrollBar.setValue(yPosBottom - panelHeight);
        }
    }
}
