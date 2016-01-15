package com.mumfrey.liteloader;

import net.minecraft.client.shader.Framebuffer;

/**
 * Interface for mods which want to interact with Minecraft's main Frame Buffer
 * Object.
 *
 * @author Adam Mummery-Smith
 */
public interface FrameBufferListener extends LiteMod
{
    /**
     * Called before the FBO is rendered. Useful if you want to interact with
     * the FBO before it is drawn to the screen. 
     */
    public abstract void preRenderFBO(Framebuffer fbo);

    /**
     * Called immediately before the FBO is rendered to the screen, after the
     * appropriate IGL modes and matrix transforms have been set but before the
     * FBO is actually rendered into the main output buffer.
     *   
     * @param fbo FBO instance
     * @param width FBO width
     * @param height FBO height
     */
    public abstract void onRenderFBO(Framebuffer fbo, int width, int height);

    /**
     * Called after the FBO is rendered whilst still inside the FBO transform
     */
    public abstract void postRenderFBO(Framebuffer fbo);
}
