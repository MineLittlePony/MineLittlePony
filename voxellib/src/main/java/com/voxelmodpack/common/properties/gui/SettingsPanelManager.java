package com.voxelmodpack.common.properties.gui;

import static com.mumfrey.liteloader.gl.GL.*;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;

/**
 * Manager object that keeps track of the settings panels and manages the tab
 * display and switching between panels
 * 
 * @author Adam Mummery-Smith
 */
public class SettingsPanelManager implements Tickable {
    // Consts
    private static int TAB_WIDTH = 72;
    private static final int TAB_HEIGHT = 16;

    /**
     * Singleton
     */
    private static SettingsPanelManager instance;

    /**
     * Minecraft game instance
     */
    private Minecraft mc;

    /**
     * Settings panels
     */
    private Map<String, Class<? extends GuiVoxelBoxSettingsPanel>> panels = new TreeMap<String, Class<? extends GuiVoxelBoxSettingsPanel>>();

    /**
     * Sorted tabs
     */
    private TreeSet<SettingsPanelMenuTab> tabs = new TreeSet<SettingsPanelMenuTab>();

    /**
     * Scroll bar to display when there are too many tabs
     */
    private SettingsPanelScrollBar scrollbar = new SettingsPanelScrollBar(6, 4, 10, 220, 0);

    /**
     * First panel which was added, this becomes the default panel
     */
    private Class<? extends GuiVoxelBoxSettingsPanel> firstPanel = null;

    /**
     * Key binding used to display the GUI
     */
    public static KeyBinding guiKeyBinding = new KeyBinding("VoxelMods Config", Keyboard.KEY_NONE, "VoxelLib");

    /**
     * Get the singleton instance of the tab manager
     * 
     * @return
     */
    public static SettingsPanelManager getInstance() {
        if (instance == null) {
            instance = new SettingsPanelManager();
            LiteLoader.getInterfaceManager().registerListener(instance);
            LiteLoader.getInput().registerKeyBinding(guiKeyBinding);
        }

        return instance;
    }

    /**
     * Private ctor
     */
    private SettingsPanelManager() {
        this.mc = Minecraft.getMinecraft();
    }

    /**
     * Add a settings panel to the tab list
     * 
     * @param panelName
     * @param panel
     */
    public static void addSettingsPanel(String panelName, Class<? extends GuiVoxelBoxSettingsPanel> panel) {
        SettingsPanelManager.getInstance().addPanel(panelName, panel);
    }

    /**
     * Remove a settings panel from the tab list
     * 
     * @param panelName
     */
    public static void removeSettingsPanel(String panelName) {
        SettingsPanelManager.getInstance().removePanel(panelName);
    }

    public static void displaySettings() {
        SettingsPanelManager.getInstance().displaySettings(Minecraft.getMinecraft());
    }

    public static boolean hasOptions() {
        return SettingsPanelManager.getInstance().panels.size() > 0;
    }

    /**
     * Add a settings panel to the panel pool
     * 
     * @param panelName
     * @param panel
     */
    public void addPanel(String panelName, Class<? extends GuiVoxelBoxSettingsPanel> panel) {
        // Add the panel to the pool
        if (panel != null) {
            this.panels.put(panelName, panel);
            this.tabs.add(new SettingsPanelMenuTab(panelName, GuiVoxelBoxSettingsPanel.PANEL_LEFT, 0));
        }

        this.updateTabs();
    }

    public void addPanel(String panelName, Class<? extends GuiVoxelBoxSettingsPanel> panel, int priority) {
        // Add the panel to the pool
        if (panel != null) {
            this.panels.put(panelName, panel);
            this.tabs.add(new SettingsPanelMenuTab(panelName, 62, priority));
        }

        this.updateTabs();
    }

    /**
     * Remove a settings panel from the tabs set
     * 
     * @param panelName
     */
    public void removePanel(String panelName) {
        this.panels.remove(panelName);
        this.updateTabs();
    }

    /**
     * 
     */
    protected void updateTabs() {

        Iterator<String> iter = this.panels.keySet().iterator();
        if (iter.hasNext())
            this.firstPanel = this.panels.get(iter.next());

        int largest = 0;

        for (SettingsPanelMenuTab tab : this.tabs)
            if (this.mc.fontRendererObj.getStringWidth(tab.getLabel()) > largest)
                largest = this.mc.fontRendererObj.getStringWidth(tab.getLabel());

        if (SettingsPanelManager.TAB_HEIGHT * this.tabs.size() < GuiVoxelBoxSettingsPanel.PANEL_HEIGHT)
            SettingsPanelManager.TAB_WIDTH = largest + 6;
        else
            SettingsPanelManager.TAB_WIDTH = largest + 18 + this.scrollbar.getWidth();

        for (SettingsPanelMenuTab tab : this.tabs)
            tab.setXPos(SettingsPanelManager.TAB_WIDTH);

        GuiVoxelBoxSettingsPanel.PANEL_LEFT = SettingsPanelManager.TAB_WIDTH;
    }

    /*
     * (non-Javadoc)
     * @see com.mumfrey.liteloader.Tickable#onTick(net.minecraft.src.Minecraft,
     * float, boolean, boolean)
     */
    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        // Handle GUI key being pressed
        if (minecraft.currentScreen == null && guiKeyBinding.isKeyDown() && this.firstPanel != null) {
            this.displaySettings(minecraft);
        }
    }

    /**
     * @param minecraft
     */
    public void displaySettings(Minecraft minecraft) {
        try {
            // If it was pressed display the default panel
            GuiVoxelBoxSettingsPanel panel = this.firstPanel.newInstance();
            minecraft.displayGuiScreen(panel);
        } catch (InstantiationException ex) {} catch (IllegalAccessException ex) {}
    }

    /**
     * Callback from the panel, checks whether a tab was clicked, returns true
     * if a tab click was handled
     * 
     * @param panel Source panel
     * @param mouseX
     * @param mouseY
     * @param button
     * @param xPos tab x pos
     * @param yPos tab y pos
     * @param spacing tab spacing
     * @return
     */
    public boolean mouseClicked(GuiVoxelBoxSettingsPanel panel, int mouseX, int mouseY, int button, int xPos, int yPos,
            int spacing) {
        if (button == 0) {

            int newWidth = SettingsPanelManager.TAB_WIDTH;

            if (SettingsPanelManager.TAB_HEIGHT * this.tabs.size() > GuiVoxelBoxSettingsPanel.PANEL_HEIGHT)
                newWidth -= 18 + this.scrollbar.getWidth();

            for (SettingsPanelMenuTab tab : this.tabs)
                if (tab.isMouseOver(newWidth, mouseX, mouseY)) {
                    try {
                        GuiVoxelBoxSettingsPanel newPanel = this.panels.get(tab.getLabel()).newInstance();
                        this.mc.displayGuiScreen(newPanel);
                        return true;

                    } catch (InstantiationException e) {} catch (IllegalAccessException e) {}
                }

            boolean intersects = this.scrollbar.mouseIn(mouseX, mouseY);

            if (intersects) {
                this.scrollbar.mouseHeld = true;
                return true;
            }

            this.scrollbar.mouseHeld = false;
            return false;
        }
        return false;
    }

    /**
     * Callback from the panel, renders the tabs
     * 
     * @param panel
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param xPos
     * @param tabYPosition
     * @param spacing
     */
    public void renderTabs(GuiVoxelBoxSettingsPanel panel, int mouseX, int mouseY, float partialTicks, int xPos,
            int yPos, int spacing, boolean mask) {
        int tabYPosition = yPos;

        glEnableDepthTest();

        this.updateTabs();

        int newWidth = SettingsPanelManager.TAB_WIDTH;
        int newY = 0;

        if (TAB_HEIGHT * this.tabs.size() > GuiVoxelBoxSettingsPanel.PANEL_HEIGHT) {
            newWidth -= 12 + this.scrollbar.getWidth();
            newY = (int) (TAB_HEIGHT * this.tabs.size() * this.scrollbar.getValue());
        }

        tabYPosition += 8;
        tabYPosition -= newY;

        for (SettingsPanelMenuTab tab : this.tabs) {
            tab.setActive(panel.getClass().equals(this.panels.get(tab.getLabel())));
            tab.renderTab(panel, newWidth, mouseX, mouseY, tabYPosition, mask);

            tabYPosition += TAB_HEIGHT;
        }

        if (TAB_HEIGHT * this.tabs.size() > GuiVoxelBoxSettingsPanel.PANEL_HEIGHT) {
            this.scrollbar.setHeight(GuiVoxelBoxSettingsPanel.PANEL_HEIGHT - 8);
            this.scrollbar.render(panel, mouseY);
        }

        glDisableDepthTest();
    }

    /*
     * (non-Javadoc)
     * @see com.mumfrey.liteloader.LiteMod#init()
     */
    @Override
    public void init(File f) {}

    /*
     * (non-Javadoc)
     * @see com.mumfrey.liteloader.LiteMod#getName()
     */
    @Override
    public String getName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.mumfrey.liteloader.LiteMod#getVersion()
     */
    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {}
}
