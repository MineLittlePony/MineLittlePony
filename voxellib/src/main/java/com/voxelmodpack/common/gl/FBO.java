package com.voxelmodpack.common.gl;

import static com.mumfrey.liteloader.gl.GL.*;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.ARBFramebufferObject.*;

/**
 * Object-oriented wrapper for basic OpenGL FBOs with only a colour and depth
 * buffer
 *
 * @author Adam Mummery-Smith
 */
public class FBO {
    private static boolean supported = false;

    private static boolean useARB = false;

    /**
     * This FBO is created
     */
    private boolean created;

    /**
     * FBO is bound
     */
    private boolean active;

    /**
     * Handle to the depth buffer resource
     */
    private int depthBuffer;

    /**
     * Handle to the frame buffer resource
     */
    private int frameBuffer;

    /**
     * Handle to the FBOs texture assigned by the renderer
     */
    private DynamicTexture texture;

    /**
     * Width and height for the current frame buffer, used so we know to
     * regenerate it if the frame size changes
     */
    private int frameBufferWidth, frameBufferHeight;

    /**
     * Helper function to check that FBOs are supported
     * 
     * @return True if EXT or ARB framebuffer support is available on the
     *         hardware
     */
    public static boolean detectFBOCapabilities() {
        ContextCapabilities capabilities = GLContext.getCapabilities();

        if (capabilities.GL_ARB_framebuffer_object) {
            supported = true;
            useARB = true;
            return true;
        } else if (capabilities.GL_EXT_framebuffer_object) {
            supported = true;
            return true;
        }

        supported = false;
        return false;
    }

    /**
     * Create a new FBO, the internal FBO itself is not created until the first
     * call to Begin() is made
     * 
     * @param renderEngine Minecraft render engine
     */
    public FBO() {
        detectFBOCapabilities();
    }

    /**
     * Get whether FBO is supported by the graphics hardware
     */
    public static boolean isSupported() {
        return supported;
    }

    /**
     * Begin
     * 
     * @param width
     * @param height
     */
    public void begin(int width, int height) {
        if (!supported) {
            return;
        }

        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("Attempted to create an FBO with zero or negative size");
        }

        if (this.created && (width != this.frameBufferWidth || height != this.frameBufferHeight)) {
            this.dispose();
        }

        if (!this.created) {
            this.created = true;
            this.frameBufferWidth = width;
            this.frameBufferHeight = height;

            BufferedImage textureImage = new BufferedImage(this.frameBufferWidth, this.frameBufferHeight,
                    BufferedImage.TYPE_INT_RGB);
            this.texture = new DynamicTexture(textureImage);

            if (useARB) {
                this.frameBuffer = glGenFramebuffers();
                this.depthBuffer = glGenRenderbuffers();

                glBindFramebuffer(GL_FRAMEBUFFER, this.frameBuffer);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
                        this.texture.getGlTextureId(), 0);

                glBindRenderbuffer(GL_RENDERBUFFER, this.depthBuffer);
                glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, this.frameBufferWidth,
                        this.frameBufferHeight);
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, this.depthBuffer);

                glBindFramebuffer(GL_FRAMEBUFFER, 0);
                glBindRenderbuffer(GL_RENDERBUFFER, 0);
            } else {
                this.frameBuffer = glGenFramebuffersEXT();
                this.depthBuffer = glGenRenderbuffersEXT();

                glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.frameBuffer);
                glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D,
                        this.texture.getGlTextureId(), 0);

                glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, this.depthBuffer);
                glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT24, this.frameBufferWidth,
                        this.frameBufferHeight);
                glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT,
                        this.depthBuffer);

                glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
                glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, 0);
            }
        }

        this.bind();
    }

    /**
     * 
     */
    public void bind() {
        if (!supported) {
            return;
        }

        if (this.created && this.checkFBO()) {
            if (useARB) {
                glBindFramebuffer(GL_FRAMEBUFFER, this.frameBuffer);
                glBindRenderbuffer(GL_RENDERBUFFER, this.depthBuffer);
            } else {
                glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.frameBuffer);
                glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, this.depthBuffer);
            }

            glPushAttrib(GL_VIEWPORT_BIT);
            glViewport(0, 0, this.frameBufferWidth, this.frameBufferHeight);
            glClear(GL_COLOR_BUFFER_BIT);
            this.active = true;
        }
        // else
        // {
        // System.err.println("Bad fbo");
        // }
    }

    public void end() {
        if (supported && this.active) {
            if (useARB) {
                glBindFramebuffer(GL_FRAMEBUFFER, 0);
                glBindRenderbuffer(GL_RENDERBUFFER, 0);
            } else {
                glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
                glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, 0);
            }

            glPopAttrib();
            this.active = false;
        }
    }

    public void dispose() {
        if (!supported) {
            return;
        }

        this.end();

        if (this.texture != null) {
            glDeleteTextures(this.texture.getGlTextureId());
        }

        if (useARB) {
            glDeleteRenderbuffers(this.depthBuffer);
            glDeleteFramebuffers(this.frameBuffer);
        } else {
            glDeleteRenderbuffersEXT(this.depthBuffer);
            glDeleteFramebuffersEXT(this.frameBuffer);
        }

        this.depthBuffer = 0;
        this.texture = null;
        this.frameBuffer = 0;
        this.created = false;
    }

    /**
     * FBO completeness check
     * 
     * @param fboID
     * @return
     */
    private boolean checkFBO() {
        if (useARB) {
            glBindFramebuffer(GL_FRAMEBUFFER, this.frameBuffer);
            glBindRenderbuffer(GL_RENDERBUFFER, this.depthBuffer);
        } else {
            glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.frameBuffer);
            glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, this.depthBuffer);
        }

        int frameBufferStatus = useARB ? glCheckFramebufferStatus(GL_FRAMEBUFFER)
                : glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);

        // status
        switch (frameBufferStatus) {
        case GL_FRAMEBUFFER_COMPLETE_EXT:
            return true;

        case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
        case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
        case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
        case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
        case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
        case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
            return false;

        default:
            throw new RuntimeException("Unexpected reply from glCheckFramebufferStatus: " + frameBufferStatus);
        }
    }

    /**
     * Draw this FBO
     * 
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @param z
     * @param alpha
     */
    public void draw(int x, int y, int x2, int y2, int z, float alpha) {
        this.draw(x, y, x2, y2, z, alpha, 0.0, 0.0, 1.0, 1.0);
    }

    /**
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @param z
     * @param alpha
     * @param u
     * @param v
     * @param u2
     * @param v2
     */
    public void draw(double x, double y, double x2, double y2, double z, float alpha, double u, double v, double u2,
            double v2) {
        if (supported && this.created) {
            glEnableTexture2D();
            glBindTexture2D(this.texture.getGlTextureId());
            glColor4f(1, 1, 1, alpha);

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRender = tessellator.getWorldRenderer();
            worldRender.startDrawingQuads();
            worldRender.addVertexWithUV(x, y2, z, u, v);
            worldRender.addVertexWithUV(x2, y2, z, u2, v);
            worldRender.addVertexWithUV(x2, y, z, u2, v2);
            worldRender.addVertexWithUV(x, y, z, u, v2);
            tessellator.draw();
        }
    }
}