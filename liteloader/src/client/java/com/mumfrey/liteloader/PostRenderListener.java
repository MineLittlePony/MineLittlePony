package com.mumfrey.liteloader;

/**
 * Render callback that gets called AFTER entities are rendered
 * 
 * @author Adam Mummery-Smith
 */
public interface PostRenderListener extends LiteMod
{
    /**
     * Called after entities are rendered but before particles
     * 
     * @param partialTicks
     */
    public abstract void onPostRenderEntities(float partialTicks);

    /**
     * Called after all world rendering is completed
     * 
     * @param partialTicks
     */
    public abstract void onPostRender(float partialTicks);
}
