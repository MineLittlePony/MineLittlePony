package com.voxelmodpack.common.gui.interfaces;

public interface IMouseEventListener {
    /**
     * Called when a mouse button is pressed
     * 
     * @param provider
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    public abstract void mousePressed(IMouseEventProvider provider, int mouseX, int mouseY, int mouseButton);

    /**
     * Called when a mouse button is released
     * 
     * @param provider
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    public abstract void mouseReleased(IMouseEventProvider provider, int mouseX, int mouseY, int mouseButton);

    /**
     * Called when the mouse is moved
     * 
     * @param provider
     * @param mouseX
     * @param mouseY
     */
    public abstract void mouseMoved(IMouseEventProvider provider, int mouseX, int mouseY);

}
