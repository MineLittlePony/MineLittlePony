package com.mumfrey.liteloader;

import net.minecraft.client.renderer.RenderGlobal;

/**
 * Render callbacks that get called before certain render events
 * 
 * @author Adam Mummery-Smith
 */
public interface PreRenderListener extends LiteMod
{
    /**
     * Called immediately before rendering of the world (including the sky) is
     * started.
     * 
     * @param partialTicks
     */
    public abstract void onRenderWorld(float partialTicks);

    /**
     * Called <b>after</b> the world camera transform is initialised, may be
     * called more than once per frame if anaglyph is enabled.
     * 
     * @param partialTicks
     * @param pass
     * @param timeSlice
     */
    public abstract void onSetupCameraTransform(float partialTicks, int pass, long timeSlice);

    /**
     * Called when the sky is rendered, may be called more than once per frame
     * if anaglyph is enabled.
     * 
     * @param partialTicks
     * @param pass
     */
    public abstract void onRenderSky(float partialTicks, int pass);

    /**
     * Called immediately before the clouds are rendered, may be called more
     * than once per frame if anaglyph is enabled.
     * 
     * @param renderGlobal
     * @param partialTicks
     * @param pass
     */
    public abstract void onRenderClouds(float partialTicks, int pass, RenderGlobal renderGlobal);

    /**
     * Called before the terrain is rendered, may be called more than once per
     * frame if anaglyph is enabled.
     * 
     * @param partialTicks
     * @param pass
     */
    public abstract void onRenderTerrain(float partialTicks, int pass);
}
