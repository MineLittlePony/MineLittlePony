package com.voxelmodpack.hdskins;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import javax.imageio.ImageIO;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.common.runtime.PrivateFields;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;

class HDSkinDownload extends Thread {
    private final ThreadDownloadImageData image;
    private final IImageBuffer imageBuffer;
    private final String skinUrl;
    private final Thread originalThread;

    HDSkinDownload(ThreadDownloadImageData image, IImageBuffer imageBuffer, String url) {
        this.image = image;
        this.imageBuffer = imageBuffer != null ? imageBuffer : (IImageBuffer) PrivateFields.imageBuffer.get(image);
        this.originalThread = PrivateFields.imageThread.get(image);
        this.skinUrl = url;
    }

    @Override
    public void run() {
        Proxy proxy = Minecraft.getMinecraft().getProxy();
        if (!this.tryDownload(proxy, this.skinUrl) && this.originalThread != null) {
            this.originalThread.run();
        }

    }

    boolean tryDownload(Proxy proxy, String strUrl) {
        HttpURLConnection httpConnection = null;

        try {
            LiteLoaderLogger.debug("Downloading HD Skin from %s", strUrl);
            URL ex = new URL(strUrl);
            httpConnection = (HttpURLConnection) ex.openConnection(proxy);
            httpConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(false);
            httpConnection.connect();
            if (httpConnection.getResponseCode() / 100 == 2) {
                BufferedImage image1 = ImageIO.read(httpConnection.getInputStream());
                if (this.imageBuffer != null) {
                    image1 = this.imageBuffer.parseUserSkin(image1);
                }

                this.image.setBufferedImage(image1);
                return true;
            }
        } catch (Exception var10) {
            return false;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }

        }

        return false;
    }
}
