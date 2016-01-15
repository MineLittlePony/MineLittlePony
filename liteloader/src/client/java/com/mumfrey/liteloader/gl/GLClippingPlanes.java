package com.mumfrey.liteloader.gl;

import static org.lwjgl.opengl.GL11.*;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Rectangle;

/**
 * OpenGL clipping plane convenience functions. We prefer to clip rectangular
 * GUI regions in Minecraft using clipping rather than scissor because scissor
 * is a nuisance to work with, primarily because it works in "window" (OpenGL
 * window) coordinates and doesn't respect the current transformation matrix.
 * Using clipping planes we can specify clipping edges in "Minecraft screen
 * coordinates", can optionally clip on only one or two axes, and also don't
 * need to worry about the current transform. 
 * 
 * @author Adam Mummery-Smith
 */
public final class GLClippingPlanes
{
    public enum Plane
    {
        LEFT(GL_CLIP_PLANE0),
        RIGHT(GL_CLIP_PLANE1),
        TOP(GL_CLIP_PLANE2),
        BOTTOM(GL_CLIP_PLANE3);

        final int plane;
        final int sign;

        private Plane(int plane)
        {
            this.plane = plane;
            this.sign = (plane % 2 == 0) ? -1 : 1;
        }
    }

    private static final int STACK_DEPTH = 1;
    private static final int STACK_FRAME_SIZE = 128;

    private static DoubleBuffer doubleBuffer = BufferUtils.createByteBuffer(STACK_FRAME_SIZE * STACK_DEPTH).asDoubleBuffer();

    private static int clippingPlaneFlags = 0;

    private static int totalClippingPlanes = glGetInteger(GL_MAX_CLIP_PLANES);

//    private static int frame = 0;

    static
    {
        for (int f = 0; f < STACK_DEPTH; f++)
        {
            // Clipping normals
            GLClippingPlanes.doubleBuffer.put(1).put(0).put(0).put(0);
            GLClippingPlanes.doubleBuffer.put(-1).put(0).put(0).put(0);
            GLClippingPlanes.doubleBuffer.put(0).put(1).put(0).put(0);
            GLClippingPlanes.doubleBuffer.put(0).put(-1).put(0).put(0);
        }
    }

    private GLClippingPlanes() {}

    /**
     * Get the total number of available clipping planes on the platform
     */
    public static int glGetTotalClippingPlanes()
    {
        return GLClippingPlanes.totalClippingPlanes;
    }

    /**
     * Enable OpenGL clipping planes (uses planes 0, 1, 2 and 3)
     * 
     * @param left Left edge clip or -1 to not use this plane
     * @param right Right edge clip or -1 to not use this plane
     * @param top Top edge clip or -1 to not use this plane
     * @param bottom Bottom edge clip or -1 to not use this plane
     */
    public static void glEnableClipping(int left, int right, int top, int bottom)
    {
        GLClippingPlanes.clippingPlaneFlags = 0;

        glEnableClipping(GL_CLIP_PLANE0, left, -1);
        glEnableClipping(GL_CLIP_PLANE1, right, 1);
        glEnableClipping(GL_CLIP_PLANE2, top, -1);
        glEnableClipping(GL_CLIP_PLANE3, bottom, 1);
    }

    /**
     * Enable OpenGL clipping planes (uses planes 0, 1, 2 and 3)
     * 
     * @param rect Clipping rectangle
     */
    public static void glEnableClipping(Rectangle rect)
    {
        GLClippingPlanes.clippingPlaneFlags = 0;

        glEnableClipping(GL_CLIP_PLANE0, rect.getX(), -1);
        glEnableClipping(GL_CLIP_PLANE1, rect.getX() + rect.getWidth(), 1);
        glEnableClipping(GL_CLIP_PLANE2, rect.getY(), -1);
        glEnableClipping(GL_CLIP_PLANE3, rect.getY() + rect.getHeight(), 1);
    }

    /**
     * Enable horizontal clipping planes (left and right) (uses planes 0, 1)
     * 
     * @param left Left edge clip or -1 to not use this plane
     * @param right Right edge clip or -1 to not use this plane
     */
    public static void glEnableHorizontalClipping(int left, int right)
    {
        glEnableClipping(GL_CLIP_PLANE0, left, -1);
        glEnableClipping(GL_CLIP_PLANE1, right, 1);
    }

    /**
     * Enable vertical clipping planes (top and bottom) (uses planes 2, 3)
     * 
     * @param top Top edge clip or -1 to not use this plane
     * @param bottom Bottom edge clip or -1 to not use this plane
     */
    public static void glEnableVerticalClipping(int top, int bottom)
    {
        glEnableClipping(GL_CLIP_PLANE2, top, -1);
        glEnableClipping(GL_CLIP_PLANE3, bottom, 1);
    }

    /**
     * @param plane
     * @param value
     */
    public static void glEnableClipping(int plane, int value)
    {
        if (plane < GL_CLIP_PLANE0 || plane >= (GL_CLIP_PLANE0 + GLClippingPlanes.totalClippingPlanes))
        {
            throw new IllegalArgumentException("Invalid clipping plane enum specified GL_CLIP_PLANE" + (plane - GL_CLIP_PLANE0));
        }

        glEnableClipping(plane, value, (plane % 2 == 0) ? -1 : 1); 
    }

    /**
     * @param plane
     * @param value
     */
    public static void glEnableClipping(Plane plane, int value)
    {
        glEnableClipping(plane.plane, value, plane.sign); 
    }

    /**
     * Enable clipping on a particular axis
     * 
     * @param plane Clipping plane to enable
     * @param value Clipping plane position
     * @param sign Sign of the position
     */
    private static void glEnableClipping(int plane, int value, int sign)
    {
        if (value == -1) return;

        int offset = (plane - GL_CLIP_PLANE0) << 2;
        GLClippingPlanes.doubleBuffer.put(offset + 3, value * sign).position(offset);
        GLClippingPlanes.clippingPlaneFlags |= plane;

        glClipPlane(plane, GLClippingPlanes.doubleBuffer);
        glEnable(plane);
    }

    /**
     * Enable clipping planes which were previously enabled 
     */
    public static void glEnableClipping()
    {
        if ((GLClippingPlanes.clippingPlaneFlags & GL_CLIP_PLANE0) == GL_CLIP_PLANE0) glEnable(GL_CLIP_PLANE0);
        if ((GLClippingPlanes.clippingPlaneFlags & GL_CLIP_PLANE1) == GL_CLIP_PLANE1) glEnable(GL_CLIP_PLANE1);
        if ((GLClippingPlanes.clippingPlaneFlags & GL_CLIP_PLANE2) == GL_CLIP_PLANE2) glEnable(GL_CLIP_PLANE2);
        if ((GLClippingPlanes.clippingPlaneFlags & GL_CLIP_PLANE3) == GL_CLIP_PLANE3) glEnable(GL_CLIP_PLANE3);
    }

    /**
     * Disable OpenGL clipping planes (uses planes 2, 3, 4 and 5)
     */
    public static void glDisableClipping()
    {
        glDisable(GL_CLIP_PLANE3);
        glDisable(GL_CLIP_PLANE2);
        glDisable(GL_CLIP_PLANE1);
        glDisable(GL_CLIP_PLANE0);
    }
}
