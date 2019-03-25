package com.minelittlepony.hdskins.upload;

import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final class InternalDialog {

    private static final Logger LOGGER = LogManager.getLogger();

    static JFrame hiddenFrame;

    public static JFrame getAWTContext() {
        JFrame context = GLWindow.current().getFrame();

        if (context != null) {
            return context;
        }

        if (hiddenFrame == null) {
            hiddenFrame = new JFrame("InternalDialogue");
            hiddenFrame.setVisible(false);
            hiddenFrame.requestFocusInWindow();
            hiddenFrame.requestFocus();

            try {
                if (hiddenFrame.isAlwaysOnTopSupported()) {
                    hiddenFrame.setAlwaysOnTop(true);
                }
            } catch (SecurityException e) {
                LOGGER.fatal("Could not set window on top state. This is probably Forge's fault.", e);
            }
        }

        return hiddenFrame;
    }
}
