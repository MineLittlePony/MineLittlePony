package com.voxelmodpack.common.net;

import java.io.File;

/**
 * Interface for objects which want a callback on the success/failure of file
 * downloads
 * 
 * @author Adam Mummery-Smith
 */
public interface IDownloadCompletedWatcher {
    /**
     * Called if the download succeeds
     * 
     * @param destFile
     */
    public abstract void onSuccess(File destFile);

    /**
     * Called if the download fails
     * 
     * @param destFile
     */
    public abstract void onFailure(File destFile);
}
