package com.voxelmodpack.hdskins.upload;

import javax.swing.JFileChooser;

/**
 * Interface for objects which can receive a callback from ThreadOpenFile
 *
 * @author Adam Mummery-Smith
 */
@FunctionalInterface
public interface IOpenFileCallback {

    /**
     * Callback method called when the "open file" dialog is closed
     */
    void onFileOpenDialogClosed(JFileChooser fileDialog, int dialogResult);

}
