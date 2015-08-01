package com.voxelmodpack.common.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Object which retrieves files from a remote server using http
 * 
 * @author Adam Mummery-Smith
 */
public class HttpFileRetriever extends Thread {
    /**
     * Callback which receives progress notifications
     */
    private final IDownloadProgressWatcher progressWatcher;

    /**
     * Callback which receives succeeded/failed notifications
     */
    private final IDownloadCompletedWatcher completedWatcher;

    /**
     * Source URL for the resource to download
     */
    private final String sourceUrl;

    /**
     * Additional headers to send with the request
     */
    private final Map<String, String> requestHeaders;

    /**
     * File to download to
     */
    private final File localFile;

    /**
     * Maximum file size allowed for this resource
     */
    private final int maxFileSize;

    /**
     * Flag which can be set to cancel the download
     */
    private volatile boolean cancelled;

    /**
     * @param sourceUrl
     * @param headers
     * @param destinationFile
     * @param maxFileSize
     * @param progressWatcher
     * @param completedWatcher
     */
    private HttpFileRetriever(String sourceUrl, Map<String, String> headers, File destinationFile, int maxFileSize,
            IDownloadProgressWatcher progressWatcher, IDownloadCompletedWatcher completedWatcher) {
        this.setDaemon(true);

        this.sourceUrl = sourceUrl;
        this.requestHeaders = headers;
        this.localFile = destinationFile;
        this.maxFileSize = maxFileSize;
        this.progressWatcher = progressWatcher;
        this.completedWatcher = completedWatcher;
    }

    /**
     * Cancel this download
     */
    public synchronized void cancel() {
        this.cancelled = true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        URLConnection http = null;
        InputStream httpInputStream = null;
        DataOutputStream fileOutputStream = null;

        if (this.progressWatcher != null) {
            this.progressWatcher.resetProgressAndMessage("Downloading File");
            this.progressWatcher.resetProgressAndWorkingMessage("Making Request...");
        }

        try {
            try {
                byte[] buffer = new byte[4096];
                http = new URL(this.sourceUrl).openConnection();
                http.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)"); // For
                                                                                                             // CloudFlare

                float progress = 0.0F;
                float progressMax = this.requestHeaders.entrySet().size();
                for (Entry<String, String> header : this.requestHeaders.entrySet()) {
                    http.setRequestProperty(header.getKey(), header.getValue());

                    if (this.progressWatcher != null) {
                        this.progressWatcher.setProgress((int) (++progress / progressMax * 100.0F));
                    }
                }

                if (this.cancelled) {
                    if (this.progressWatcher != null)
                        this.progressWatcher.onCompleted();
                    this.completedWatcher.onFailure(this.localFile);
                    return;
                }

                httpInputStream = http.getInputStream();
                progressMax = http.getContentLength();
                int contentLength = http.getContentLength();

                if (this.progressWatcher != null)
                    this.progressWatcher.resetProgressAndWorkingMessage(String.format("Downloading file (%.2f MB)...",
                            new Object[] { Float.valueOf(progressMax / 1000.0F / 1000.0F) }));

                if (this.localFile.exists()) {
                    long receivedBytes = this.localFile.length();

                    if (receivedBytes == contentLength) {
                        this.completedWatcher.onSuccess(this.localFile);

                        if (this.progressWatcher != null)
                            this.progressWatcher.onCompleted();

                        return;
                    }

                    System.out.println("Deleting " + this.localFile + " as it does not match what we currently have ("
                            + contentLength + " vs our " + receivedBytes + ").");
                    this.localFile.delete();
                }

                fileOutputStream = new DataOutputStream(new FileOutputStream(this.localFile));

                if (this.maxFileSize > 0 && progressMax > this.maxFileSize) {
                    if (this.progressWatcher != null)
                        this.progressWatcher.onCompleted();

                    throw new IOException("Filesize is bigger than maximum allowed (file is " + progress + ", limit is "
                            + this.maxFileSize + ")");
                }

                int readBytes;

                while ((readBytes = httpInputStream.read(buffer)) >= 0 && !this.cancelled) {
                    progress += readBytes;

                    if (this.progressWatcher != null)
                        this.progressWatcher.setProgress((int) (progress / progressMax * 100.0F));

                    if (this.maxFileSize > 0 && progress > this.maxFileSize) {
                        if (this.progressWatcher != null)
                            this.progressWatcher.onCompleted();

                        throw new IOException("Filesize was bigger than maximum allowed (got >= " + progress
                                + ", limit was " + this.maxFileSize + ")");
                    }

                    fileOutputStream.write(buffer, 0, readBytes);
                }

                if (this.cancelled) {
                    try {
                        fileOutputStream.close();
                        this.localFile.delete();
                    } catch (IOException ex) {}

                    if (this.progressWatcher != null)
                        this.progressWatcher.onCompleted();
                    this.completedWatcher.onFailure(this.localFile);
                    return;
                }

                this.completedWatcher.onSuccess(this.localFile);

                if (this.progressWatcher != null) {
                    this.progressWatcher.onCompleted();
                    return;
                }
            } catch (Throwable th) {
                // th.printStackTrace();
            }
        } finally {
            try {
                if (httpInputStream != null)
                    httpInputStream.close();
            } catch (IOException ex) {}

            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException ex) {}
        }
    }

    /**
     * @param sourceUrl
     * @param headers
     * @param destFile
     * @param maxFileSize
     * @param progressWatcher
     * @param completedWatcher
     * @return
     */
    public static HttpFileRetriever beginDownloading(String sourceUrl, Map<String, String> headers, File destFile,
            int maxFileSize, IDownloadProgressWatcher progressWatcher, IDownloadCompletedWatcher completedWatcher) {
        HttpFileRetriever downloadThread = new HttpFileRetriever(sourceUrl, headers, destFile, maxFileSize,
                progressWatcher, completedWatcher);
        downloadThread.start();
        return downloadThread;
    }
}
