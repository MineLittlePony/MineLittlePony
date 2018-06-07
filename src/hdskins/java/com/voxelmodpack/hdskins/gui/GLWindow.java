package com.voxelmodpack.hdskins.gui;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.Closeable;
import java.io.IOException;
import java.util.TooManyListenersException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.google.common.collect.Lists;
import com.voxelmodpack.hdskins.Later;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GLWindow implements Closeable {

    static GLWindow instance = null;

    public static GLWindow current() {
        if (instance == null) {
            instance = new GLWindow();
        }
        return instance;
    }

    private final DropTarget dt;

    private final JFrame frame;

    private DropTargetListener saved = null;

    private final int frameX = 15;
    private final int frameY = 36;

    private final Minecraft mc = Minecraft.getMinecraft();

    private int state = 0;

    private GLWindow() {
        int x = Display.getX();
        int y = Display.getY();

        int w = Display.getWidth() + frameX;

        int h = Display.getHeight() + frameY;

        Canvas canvas = new Canvas();

        frame = new JFrame(Display.getTitle());
        frame.setResizable(Display.isResizable());
        frame.setLocation(x, y);
        frame.setSize(w, h);
        frame.getContentPane().setLayout(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                Minecraft.getMinecraft().shutdown();
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                canvas.setBounds(0, 0, frame.getWidth() - frameX, frame.getHeight() - frameY);
            }
        });
        frame.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent event) {
                state = event.getNewState();
                Later.performLater(1, () -> {
                    canvas.setBounds(0, 0, frame.getWidth() - frameX, frame.getHeight() - frameY);
                });
            }
        });
        setIcons(frame);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);

        try {
            Display.setParent(canvas);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        if (Display.getWidth() == Display.getDesktopDisplayMode().getWidth()) {
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }

        state = frame.getExtendedState();

        if (mc.isFullScreen()) {
            try {
                Display.setFullscreen(true);
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }

        dt = new DropTarget();
        canvas.setDropTarget(dt);
    }

    private final void setIcons(JFrame frame) {
        try {
            frame.setIconImages(Lists.newArrayList(
                    ImageIO.read(mc.getResourceManager().getResource(new ResourceLocation("icons/icon_16x16.png")).getInputStream()),
                    ImageIO.read(mc.getResourceManager().getResource(new ResourceLocation("icons/icon_32x32.png")).getInputStream())
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDropTargetListener(DropTargetListener dtl) {
        if (saved != null) {
            dt.removeDropTargetListener(saved);
        }
        if (dtl != null) {
            try {
                dt.addDropTargetListener(dtl);
            } catch (TooManyListenersException e) { }
            saved = dtl;
        }
    }

    public static void dispose() {
        if (instance != null) {
            try {
                instance.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void close() throws IOException {
        mc.addScheduledTask(() -> {
            try {
                Display.setParent(null);
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            try {
                if (mc.isFullScreen()) {
                    Display.setFullscreen(true);
                } else {
                    if ((state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
                        Display.setLocation(0, 0);
                        Display.setDisplayMode(Display.getDesktopDisplayMode());
                    } else {
                        Display.setDisplayMode(new DisplayMode(mc.displayWidth, mc.displayHeight));
                        Display.setLocation(frame.getX(), frame.getY());
                    }
                    Display.setResizable(false);
                    Display.setResizable(true);
                }
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            frame.setVisible(false);
            frame.dispose();

            instance = null;
        });
    }
}
