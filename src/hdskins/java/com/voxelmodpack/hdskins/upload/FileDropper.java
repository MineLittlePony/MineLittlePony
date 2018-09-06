package com.voxelmodpack.hdskins.upload;

import org.lwjgl.opengl.Display;

import java.awt.Color;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;

public class FileDropper extends JFrame {
    private static final long serialVersionUID = -2945117328826695659L;

    private static FileDropper instance = null;

    public static FileDropper getAWTContext() {
        if (instance == null) {
            instance = new FileDropper();
        }

        return instance;
    }

    private final DropTarget dt;

    public FileDropper() {
        super("Skin Drop");

        setType(Type.UTILITY);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(false);
        setTitle("Skin Drop");
        setSize(256, 256);
        setAlwaysOnTop(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        panel.setBounds(10, 11, 230, 205);
        getContentPane().add(panel);

        JLabel txtInst = new JLabel("Drop skin files here");
        txtInst.setHorizontalAlignment(SwingConstants.CENTER);
        txtInst.setVerticalAlignment(SwingConstants.CENTER);
        panel.add(txtInst);

        dt = new DropTarget();

        setDropTarget(dt);

        if (InternalDialog.hiddenFrame == null) {
            InternalDialog.hiddenFrame = this;
        }
    }

    public void show(DropTargetListener dtl) throws TooManyListenersException {
        dt.addDropTargetListener(dtl);
        setVisible(true);
        requestFocusInWindow();
        setLocation(Display.getX(), Display.getY());
    }

    public void hide(DropTargetListener dtl) {
        dt.removeDropTargetListener(dtl);
        setVisible(false);
    }
}
