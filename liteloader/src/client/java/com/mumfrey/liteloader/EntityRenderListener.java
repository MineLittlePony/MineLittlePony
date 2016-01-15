package com.mumfrey.liteloader;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

/**
 * Interface for mods which want to receive callbacks when entities are rendered
 * into the world.
 *  
 * @author Adam Mummery-Smith
 */
public interface EntityRenderListener extends LiteMod
{
    /**
     * Called immediately prior to an entity being rendered
     * 
     * @param render 
     * @param entity
     * @param xPos
     * @param yPos
     * @param zPos
     * @param yaw
     * @param partialTicks
     */
    public abstract void onRenderEntity(Render render, Entity entity, double xPos, double yPos, double zPos, float yaw, float partialTicks);

    /**
     * Called immediately following an entity being rendered
     * 
     * @param render 
     * @param entity
     * @param xPos
     * @param yPos
     * @param zPos
     * @param yaw
     * @param partialTicks
     */
    public abstract void onPostRenderEntity(Render render, Entity entity, double xPos, double yPos, double zPos, float yaw, float partialTicks);
}
