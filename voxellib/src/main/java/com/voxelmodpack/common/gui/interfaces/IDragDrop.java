package com.voxelmodpack.common.gui.interfaces;

/**
 * Interface for controls or other objects which support drag/drop sourcing and
 * destination capabilities
 * 
 * @author Adam Mummery-Smith
 */
public interface IDragDrop {
    /**
     * Get whether this object is a valid drag target
     * 
     * @return true if the object can have draggable items dropped into it
     */
    public abstract boolean getIsValidDragTarget();

    /**
     * Get whether this object is a valid drag source
     * 
     * @return true if this object can source drag events
     */
    public abstract boolean getIsValidDragSource();

    /**
     * Add the specified target to this object's list of targets
     * 
     * @param target DragTarget to add, the object MUST support drag targets
     */
    public abstract void addDragTarget(IDragDrop target);

    /**
     * Add the specified target to this object's list of targets and create a
     * mutual (bi-directional) linkage if specified
     * 
     * @param target Target to add
     * @param mutual Set true to create a mutual (bi-directional) linkage
     */
    public abstract void addDragTarget(IDragDrop target, boolean mutual);

    /**
     * Remove the specified object from this object's list of targets
     * 
     * @param target Drag Target object to remove
     */
    public abstract void removeDragTarget(IDragDrop target);

    /**
     * Remove the specified object from this object's list of targets and this
     * object from the other object's list of targets if specified
     * 
     * @param target Drag Target object to remove
     * @param mutual Set true to break the link mutually
     */
    public abstract void removeDragTarget(IDragDrop target, boolean mutual);

    /**
     * Perform a drag operation to this object
     * 
     * @param source Object sourcing the drag event
     * @param object Object being dragged
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @return True if the object was accepted
     */
    public abstract boolean dragDrop(IDragDrop source, IListObject object, int mouseX, int mouseY);
}
