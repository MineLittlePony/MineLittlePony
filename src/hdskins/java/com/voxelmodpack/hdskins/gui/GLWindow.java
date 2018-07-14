package com.voxelmodpack.hdskins.gui;

import com.google.common.collect.Lists;
import com.voxelmodpack.hdskins.upload.awt.FileDropper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Experimental window to control file drop. It kind of sucks.
 *
 */
public class GLWindow extends DropTarget {

    // Serial version because someone decided to extend DropTarget
    private static final long serialVersionUID = -8891327070920541481L;

    @Nullable
    private static GLWindow instance = null;

    /**
     * Gets or creates the current GLWindow context.
     */
    public static synchronized GLWindow current() {
        if (instance == null) {
            instance = new GLWindow();
        }
        return instance;
    }

    public static void create() {
        try {
            current().open();
        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Destroys the current GLWindow context and restores default behaviour.
     */
    public static synchronized void dispose() {
        if (instance != null) {
            instance.close();
        }
    }

    private static int getScaledPixelUnit(int i) {
        return (int)Math.floor(i * Display.getPixelScaleFactor());
    }

    private final Minecraft mc = Minecraft.getMinecraft();

    private JFrame frame;
    private Canvas canvas;

    private DropTargetListener dropListener = null;

    private int windowState = 0;

    private boolean isFullscreen;

    private boolean ready = false;
    private boolean closeRequested = false;

    private GLWindow() {

    }

    public JFrame getFrame() {
        return frame;
    }

    private synchronized void open() throws LWJGLException {
        // Dimensions from LWJGL may have a non 1:1 scale on high DPI monitors.
        int x = getScaledPixelUnit(Display.getX());
        int y = getScaledPixelUnit(Display.getY());

        int w = getScaledPixelUnit(Display.getWidth());
        int h = getScaledPixelUnit(Display.getHeight());

        isFullscreen = mc.isFullScreen();

        canvas = new Canvas();

        frame = new JFrame(Display.getTitle());
        frame.add(canvas);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                if (!closeRequested) {
                    for (Window w : Frame.getWindows()) {
                        w.dispose();
                    }

                    mc.shutdown();
                }
                closeRequested = false;
            }

            @Override
            public void windowStateChanged(WindowEvent event) {
                windowState = event.getNewState();
                onResize();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                // Once the window has opened compare the content and window dimensions to get
                // the OS's frame size then reassign adjusted dimensions to match LWJGL's window.
                float frameFactorX = frame.getWidth() - frame.getContentPane().getWidth();
                float frameFactorY = frame.getHeight() - frame.getContentPane().getHeight();

                frame.setSize((int)Math.floor(w + frameFactorX), (int)Math.floor(h + frameFactorY));
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                onResize();
            }
        });

        // TODO: (Unconfirmed) reports say the icon appears small on some OSs.
        //       I've yet to reproduce this.
        setIcons();

        // Order here is important. Size is set _before_ displaying but
        // after events to ensure the window and canvas both get correct dimensions.
        frame.setResizable(Display.isResizable());
        frame.setLocation(x, y);
        frame.setSize(w, h);
        frame.setVisible(true);

        Display.setParent(canvas);
        Display.setFullscreen(isFullscreen);

        ready = true;
    }

    private synchronized void close() {
        closeRequested = true;

        try {
            Display.setParent(null);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        try {
            if (isFullscreen) {
                Display.setFullscreen(true);
            } else {
                if ((windowState & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                    Display.setLocation(0, 0);
                    Display.setDisplayMode(Display.getDesktopDisplayMode());
                } else {
                    Display.setDisplayMode(new DisplayMode(frame.getContentPane().getWidth(), frame.getContentPane().getHeight()));
                    Display.setLocation(frame.getX(), frame.getY());
                }

                // https://bugs.mojang.com/browse/MC-68754
                Display.setResizable(false);
                Display.setResizable(true);
            }
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        frame.setVisible(false);
        frame.dispose();

        for (Window w : Frame.getWindows()) {
            w.dispose();
        }

        instance = null;
    }

    private void setIcons() {
        // VanillaTweakInjector.loadIconsOnFrames();
        try {
            //
            // The icons are stored in Display#cached_icons. However they're not the _original_ values.
            // LWJGL copies the initial byte streams and then reverses them. The result is a stream that's not
            // only already consumed, but somehow invalid when you try to parse it through ImageIO.read.
            //
            DefaultResourcePack pack = (DefaultResourcePack) mc.getResourcePackRepository().rprDefaultResourcePack;

            ArrayList<Image> images = Lists.newArrayList(
                    ImageIO.read(pack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"))),
                    ImageIO.read(pack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png")))
            );

            Frame[] frames = Frame.getFrames();

            if (frames != null) {
                for (Frame frame : frames) {
                    try {
                        frame.setIconImages(images);
                    } catch (Throwable t) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onResize() {
        canvas.setBounds(0, 0, frame.getContentPane().getWidth(), frame.getContentPane().getHeight());
    }

    public void refresh(boolean fullscreen) {
        if (ready && fullscreen != isFullscreen) {
            // Repaint the canvas, not the window.
            // The former strips the window of its state. The latter fixes a viewport scaling bug.
            canvas.setBounds(0, 0, 0, 0);
            onResize();
            isFullscreen = fullscreen;
        }
    }

    public synchronized void clearDropTargetListener() {
        if (dropListener != null) {
            if (!ready) {
                FileDropper.getAWTContext().hide(dropListener);
                return;
            } else {
                frame.setDropTarget(null);
                removeDropTargetListener(dropListener);
            }
            dropListener = null;
        }
    }

    public synchronized void setDropTargetListener(DropTargetListener dtl) {
        try {
            clearDropTargetListener();

            dropListener = dtl;

            if (!ready) {
                FileDropper.getAWTContext().show(dtl);
                return;
            }

            frame.setDropTarget(this);
            addDropTargetListener(dtl);
        } catch (TooManyListenersException ignored) { }
    }
}
