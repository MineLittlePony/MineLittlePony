package com.voxelmodpack.common.gui.interfaces;

import net.minecraft.client.Minecraft;

import com.mumfrey.liteloader.util.render.Icon;

/**
 * Interface for items that can be added to listboxes
 * 
 * @author Adam Mummery-Smith
 */
public interface IListObject {
    /**
     * Custom draw behaviours
     */
    public enum CustomDrawBehaviour {
        /**
         * Don't custom draw, this object is completely owner-drawn
         */
        NoCustomDraw,

        /**
         * Combined custom draw, custom draw is called AFTER owner draw
         */
        CustomDrawExtra,

        /**
         * Custom draw only, no owner draw is performed and the item draws
         * itself
         */
        FullCustomDraw
    }

    /**
     * Get the custom draw behaviour for this object
     * 
     * @return custom draw behaviour
     */
    public abstract CustomDrawBehaviour getCustomDrawBehaviour();

    /**
     * Callback which is called if GetCustomDrawBegaviour returns
     * CustomDrawExtra or FullCustomDraw
     * 
     * @param iconEnabled True if icons are enabled in the container (owner)
     * @param minecraft Minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param XPosition() X location of the control
     * @param yPosition Y location of the control
     * @param width control width
     * @param height control height
     */
    public abstract void drawCustom(boolean iconEnabled, Minecraft minecraft, int mouseX, int mouseY, int xPosition,
            int yPosition, int width, int height, int updateCounter);

    /**
     * @param iconEnabled True if icons are enabled in the container
     * @param minecraft Minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param XPosition() X location of the control
     * @param yPosition Y location of the control
     * @param width control width
     * @param height control height
     * @return true if a custom action was performed
     */
    public abstract boolean mousePressed(boolean iconEnabled, Minecraft minecraft, int mouseX, int mouseY,
            int xPosition, int yPosition, int width, int height);

    /**
     * Called when the mouse is released after a MousePressed event
     * 
     * @param mouseX
     * @param mouseY
     */
    public abstract void mouseReleased(int mouseX, int mouseY);

    /**
     * If a custom action was flagged after calling MousePressed, this function
     * is called to retrieve the custom action
     * 
     * @param bClear Set true to clear the custom action after it is read
     * @return Custom action which was performed
     */
    public abstract String getCustomAction(boolean bClear);

    /**
     * True if the object has an icon
     * 
     * @return True if the object has an icon
     */
    public abstract boolean hasIcon();

    /**
     * Bind the icon texture for this object
     */
    public abstract void bindIconTexture();

    /**
     * Get the icon ID for this object
     * 
     * @return icon id
     */
    public abstract Icon getIcon();

    /**
     * Get the icon's size on the texture
     * 
     * @return
     */
    public abstract int getIconSize();

    /**
     * Get the size of the texture map used for the object's icons
     * 
     * @return
     */
    public abstract int getIconTexmapSize();

    /**
     * Get the display text for the object
     * 
     * @return Display text
     */
    public abstract String getText();

    /**
     * Display name if different to text
     * 
     * @return
     */
    public abstract String getDisplayName();

    /**
     * Get the object's ID, object-specific identifier
     * 
     * @return
     */
    public abstract int getID();

    /**
     * Get the arbitrary data associated with the object
     * 
     * @return object's data or NULL if no data
     */
    public abstract Object getData();

    /**
     * Return true if this item is draggable
     * 
     * @return true if this item is draggable
     */
    public abstract boolean getDraggable();

    /**
     * True if this object supports in-place editing
     * 
     * @return
     */
    public abstract boolean getCanEditInPlace();

    /**
     * True if this object is currently in-place editing
     * 
     * @return
     */
    public abstract boolean getEditingInPlace();

    /**
     * Tell this object to begin editing in-place
     */
    public abstract void beginEditInPlace();

    /**
     * Tell this object to end editing in-place
     */
    public abstract void endEditInPlace();

    /**
     * Key typed handler for editing in-place
     * 
     * @param keyChar Key character that was entered
     * @param keyCode Key scan code
     * @return
     */
    public abstract boolean editInPlaceKeyTyped(char keyChar, int keyCode);

    /**
     * @param iconEnabled True if icons are enabled in the container
     * @param minecraft Minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param XPosition() X location of the control
     * @param yPosition Y location of the control
     * @param width control width
     * @param height control height
     * @return true if the mouse was captured
     */
    public abstract boolean editInPlaceMousePressed(boolean iconEnabled, Minecraft minecraft, int mouseX, int mouseY,
            int xPosition, int yPosition, int width, int height);

    /**
     * Callback which is called if GetEditingInPlace is true
     * 
     * @param iconEnabled True if icons are enabled in the container (owner)
     * @param minecraft Minecraft game instance
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param XPosition() X location of the control
     * @param yPosition Y location of the control
     * @param width control width
     * @param height control height
     */
    public abstract void editInPlaceDraw(boolean iconEnabled, Minecraft minecraft, int mouseX, int mouseY,
            int xPosition, int yPosition, int width, int height, int updateCounter);

    /**
     * Set the texture name of the icon for this object.
     */
    public abstract void setIconTexture(String newTexture);

    /**
     * Set the icon ID for this object
     */
    public abstract void setIconID(int newIconId);

    /**
     * Set the display text for the object
     */
    public abstract void setText(String newText);

    /**
     * Set the display name for this object
     * 
     * @param newDisplayName
     */
    public abstract void setDisplayName(String newDisplayName);

    /**
     * Set the object's ID, object-specific identifier
     */
    public abstract void setID(int newId);

    /**
     * Set the arbitrary data associated with the object
     */
    public abstract void setData(Object newData);

    /**
     * Get the serialised representation of this list entry
     * 
     * @return
     */
    public abstract String serialise();
}
