package com.mumfrey.liteloader.util.render;

/**
 * Icon with an onClicked handler
 * 
 * @author Adam Mummery-Smith
 */
public interface IconClickable extends IconTextured
{
    /**
     * @param source Source of the event, usually the outermost gui screen
     * @param container Container of this icon, the actual component hosting the
     *      icon
     */
    public void onClicked(Object source, Object container);
}
