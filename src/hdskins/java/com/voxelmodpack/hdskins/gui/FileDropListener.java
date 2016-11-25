package com.voxelmodpack.hdskins.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileDropListener extends DropTargetListener {

    @Override
    default void dragEnter(DropTargetDragEvent dtde) {
    }

    @Override
    default void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    default void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    default void dragExit(DropTargetEvent dte) {
    }

    @SuppressWarnings("unchecked")
    @Override
    default void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_LINK);
        try {
            onDrop((List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
            dtde.getDropTargetContext().dropComplete(true);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

    }

    void onDrop(List<File> files);

}
