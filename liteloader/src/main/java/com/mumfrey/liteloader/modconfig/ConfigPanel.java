package com.mumfrey.liteloader.modconfig;


/**
 * Interface for mod config panels to implement
 * 
 * @author Adam Mummery-Smith
 */
public interface ConfigPanel
{
    /**
     * Panels should return the text to display at the top of the config panel
     * window.
     */
    public abstract String getPanelTitle();

    /**
     * Get the height of the content area for scrolling purposes, return -1 to
     * disable scrolling.
     */
    public abstract int getContentHeight();

    /**
     * Called when the panel is displayed, initialise the panel (read settings,
     * etc)
     * 
     * @param host panel host
     */
    public abstract void onPanelShown(ConfigPanelHost host);

    /**
     * Called when the window is resized whilst the panel is active
     * 
     * @param host panel host
     */
    public abstract void onPanelResize(ConfigPanelHost host);

    /**
     * Called when the panel is closed, panel should save settings
     */
    public abstract void onPanelHidden();

    /**
     * Called every tick
     */
    public abstract void onTick(ConfigPanelHost host);

    /**
     * Draw the configuration panel
     * 
     * @param host
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    public abstract void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks);

    /**
     * Called when a mouse button is pressed
     * 
     * @param host
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    public abstract void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton);

    /**
     * Called when a mouse button is released
     * 
     * @param host
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    public abstract void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton);

    /**
     * Called when the mouse is moved
     * 
     * @param host
     * @param mouseX
     * @param mouseY
     */
    public abstract void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY);

    /**
     * Called when a key is pressed
     * 
     * @param host
     * @param keyChar
     * @param keyCode
     */
    public abstract void keyPressed(ConfigPanelHost host, char keyChar, int keyCode);
}
