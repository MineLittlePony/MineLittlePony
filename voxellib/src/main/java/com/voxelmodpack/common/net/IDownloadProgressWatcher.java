package com.voxelmodpack.common.net;

/**
 * Interface for objects which want a callback to monitor the progress of file
 * downloads
 *
 * @author Adam Mummery-Smith
 */
public interface IDownloadProgressWatcher {
    /**
     * @param message
     */
    public abstract void setMessage(String message);

    /**
     * @param message
     */
    public abstract void resetProgressAndMessage(String message);

    /**
     * @param message
     */
    public abstract void resetProgressAndWorkingMessage(String message);

    /**
     * @param progress
     */
    public abstract void setProgress(int progress);

    /**
     * 
     */
    public abstract void onCompleted();
}
