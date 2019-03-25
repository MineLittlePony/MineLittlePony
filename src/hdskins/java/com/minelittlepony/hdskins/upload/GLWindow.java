package com.minelittlepony.hdskins.upload;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.util.TooManyListenersException;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * Experimental window to control file drop. It kind of sucks.
 *
 * @deprecated TODO: Merge GLFW branch
 */
@Deprecated
public class GLWindow extends DropTarget {

    // Serial version because someone decided to extend DropTarget
    private static final long serialVersionUID = -8891327070920541481L;

    @Nullable
    private static GLWindow instance = null;

    private static final Logger logger = LogManager.getLogger();

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

    }

    /**
     * Destroys the current GLWindow context and restores default behaviour.
     */
    public static synchronized void dispose() {

    }

    private final Minecraft mc = Minecraft.getInstance();

    private JFrame frame;
    private volatile DropTargetListener dropListener = null;

    private boolean ready = false;
    private GLWindow() {

    }

    public JFrame getFrame() {
        return frame;
    }


    public void refresh(boolean fullscreen) {

    }

    public synchronized void clearDropTargetListener() {
        SwingUtilities.invokeLater(this::syncClearDropTargetListener);
    }

    private void syncClearDropTargetListener() {
        if (dropListener != null) {
            if (!ready) {
                FileDropper.getAWTContext().hide(dropListener);
            } else {
                frame.setDropTarget(null);
                removeDropTargetListener(dropListener);
            }

            dropListener = null;
        }
    }

    public synchronized void setDropTargetListener(FileDropListener dtl) {
        SwingUtilities.invokeLater(() -> syncSetDropTargetListener(dtl));
    }

    private void syncSetDropTargetListener(FileDropListener dtl) {
        try {
            syncClearDropTargetListener();

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
