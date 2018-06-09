package com.voxelmodpack.hdskins.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

import java.awt.Canvas;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.TooManyListenersException;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Experimental window to control file drop. It kind of sucks.
 *
 * This has to be enabled using the {@code experimentalSkinDrop} config.
 */
public class GLWindow extends DropTarget {

    private static GLWindow instance = null;

    public static void create() {
        if (instance == null)
            instance = new GLWindow();
    }

    @Nullable
    public static GLWindow current() {
        return instance;
    }

    private final JFrame frame;

    private DropTargetListener saved = null;

    // What's so special about these numbers? Are they the same on all systems?
    private final int frameX = 15;
    private final int frameY = 36;

    private final Minecraft mc = Minecraft.getMinecraft();

    private GLWindow() {

        setDefaultActions(DnDConstants.ACTION_LINK);
        try {

            int x = Display.getX();
            int y = Display.getY();

            int w = Display.getWidth() + frameX;

            int h = Display.getHeight() + frameY;

            Canvas canvas = new AWTGLCanvas(new PixelFormat().withDepthBits(24));

            frame = new JFrame(Display.getTitle());
            frame.setResizable(Display.isResizable());
            frame.setLocation(x, y);
            frame.setSize(w, h);

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            // FIXME: icon is super small on the task bar
            setIcons(frame);

            frame.add(canvas);
            frame.setVisible(true);

            Display.setParent(canvas);

            Display.setFullscreen(mc.isFullScreen());

        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }


    }

    public void refresh() {
        // trigger an update
        frame.setSize(frame.getWidth(), frame.getHeight()+1);
        frame.setSize(frame.getWidth(), frame.getHeight()-1);
//        frame.pack();
    }

    private void setIcons(JFrame frame) {
        try {
            // This should be using reflection. No need for this.
            DefaultResourcePack pack = (DefaultResourcePack) mc.getResourcePackRepository().rprDefaultResourcePack;

            frame.setIconImages(Lists.newArrayList(
                    ImageIO.read(pack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"))),
                    ImageIO.read(pack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png")))
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setDropTargetListener(@Nullable DropTargetListener dtl) {
        if (saved != null) {
            removeDropTargetListener(saved);
        }
        if (dtl == null)
            frame.setDropTarget(null);
        else {
            frame.setDropTarget(this);
            try {
                addDropTargetListener(dtl);
            } catch (TooManyListenersException e) { }
            saved = dtl;
        }
    }
}
