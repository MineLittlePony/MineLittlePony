package com.mumfrey.liteloader.api;

import java.util.List;

import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.util.render.IconTextured;

/**
 * LiteLoader Extensible API - Branding Provider
 *
 * Decorator for ModInfo classes, to alter the appearance of ModInfo entries in
 * the mod list.
 * 
 * @author Adam Mummery-Smith
 */
public interface ModInfoDecorator extends CustomisationProvider
{
    /**
     * Add icons to the mod list entry for this mod
     * 
     * @param mod
     * @param icons
     */
    public abstract void addIcons(ModInfo<?> mod, List<IconTextured> icons);

    /**
     * Allows this decorator to modify the status text for the specified mod,
     * return null if no modification required.
     * 
     * @param statusText
     * @return new status text or NULL to indicate the text should remain
     *      default
     */
    public abstract String modifyStatusText(ModInfo<?> mod, String statusText);

    /**
     * Allow decorators to draw custom content on the mod list entries
     * 
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param partialTicks
     * @param xPosition Panel X position
     * @param yPosition Panel Y position
     * @param width Panel width
     * @param selected Panel height
     * @param mod ModInfo
     * @param gradientColour
     * @param titleColour
     * @param statusColour
     */
    public abstract void onDrawListEntry(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, int height,
            boolean selected, ModInfo<?> mod, int gradientColour, int titleColour, int statusColour);
}
