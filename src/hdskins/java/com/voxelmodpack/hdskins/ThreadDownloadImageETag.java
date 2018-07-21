package com.voxelmodpack.hdskins;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.voxelmodpack.hdskins.util.NetClient;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadDownloadImageETag extends SimpleTexture {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger THREAD_ID = new AtomicInteger(0);

    @Nonnull
    private final File cacheFile;

    private final File eTagFile;
    private final String imageUrl;

    @Nullable
    private final IImageBuffer imageBuffer;

    @Nullable
    private BufferedImage bufferedImage;

    @Nullable
    private Thread imageThread;
    private boolean textureUploaded;

    public ThreadDownloadImageETag(@Nonnull File cacheFileIn, String imageUrlIn, ResourceLocation defLocation, @Nullable IImageBuffer imageBufferIn) {
        super(defLocation);
        cacheFile = cacheFileIn;
        eTagFile = new File(cacheFile.getParentFile(), cacheFile.getName() + ".etag");
        imageUrl = imageUrlIn;
        imageBuffer = imageBufferIn;
    }

    public int getGlTextureId() {
        if (!textureUploaded) {
            if (bufferedImage != null) {
                if (textureLocation != null) {
                    deleteGlTexture();
                }

                TextureUtil.uploadTextureImage(super.getGlTextureId(), bufferedImage);
                textureUploaded = true;
            }
        }

        return super.getGlTextureId();
    }

    private void setBufferedImage(@Nonnull BufferedImage bufferedImageIn) {
        bufferedImage = bufferedImageIn;

        if (imageBuffer != null) {
            imageBuffer.skinAvailable();
        }
    }

    @Nullable
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (bufferedImage == null && textureLocation != null) {
            super.loadTexture(resourceManager);
        }

        if (imageThread == null) {
            imageThread = new Thread(this::loadTexture, "Texture Downloader #" + THREAD_ID.incrementAndGet());
            imageThread.setDaemon(true);
            imageThread.start();
        }
    }

    private void loadTexture() {
        try (NetClient client = new NetClient("GET", imageUrl)) {
            CloseableHttpResponse response = client.getResponse();

            if (client.getResponseCode() == HttpStatus.SC_NOT_FOUND) {
                // delete the cache files in case we can't connect in the future
                clearCache();
            } else if (checkETag(response)) {
                LOGGER.debug("Loading http texture from local cache ({})", cacheFile);

                try {
                    // e-tag check passed. Load the local file
                    setLocalCache();
                } catch (IOException ioexception) {
                    // Nope. Local cache is corrupt. Re-download it.
                    LOGGER.error("Couldn't load skin {}", cacheFile, ioexception);
                    loadTextureFromServer(response);
                }
            } else {
                // there's an updated file. Download it again.
                loadTextureFromServer(response);
            }

        } catch (IOException e) {
            // connection failed
            if (cacheFile.isFile()) {
                try {
                    // try to load from cache anyway
                    setLocalCache();
                    return;
                } catch (IOException ignored) {
                }
            }
            LOGGER.error("Couldn't load skin {} ", imageUrl, e);
        }
    }

    private void setLocalCache() throws IOException {
        if (cacheFile.isFile()) {
            BufferedImage image = ImageIO.read(cacheFile);
            if (imageBuffer != null) {
                image = imageBuffer.parseUserSkin(image);
            }
            setBufferedImage(image);
        }
    }

    private void clearCache() {
        FileUtils.deleteQuietly(cacheFile);
        FileUtils.deleteQuietly(eTagFile);
    }

    private boolean checkETag(HttpResponse response) {
        try {
            if (cacheFile.isFile()) {
                String localETag = Files.readFirstLine(eTagFile, Charsets.UTF_8);
                Header remoteETag = response.getFirstHeader(HttpHeaders.ETAG);

                // true if no remote etag or does match
                return remoteETag == null || localETag.equals(remoteETag.getValue());
            }
        } catch (IOException e) {

        }

        return false; // it failed, so re-fetch.
    }

    private void loadTextureFromServer(HttpResponse response) {
        LOGGER.debug("Downloading http texture from {} to {}", imageUrl, cacheFile);
        try {
            if (response.getStatusLine().getStatusCode() / 100 == 2) {
                BufferedImage bufferedimage;

                // write the image to disk
                FileUtils.copyInputStreamToFile(response.getEntity().getContent(), cacheFile);
                bufferedimage = ImageIO.read(cacheFile);

                // maybe write the etag to disk
                Header eTag = response.getFirstHeader(HttpHeaders.ETAG);
                if (eTag != null) {
                    FileUtils.write(eTagFile, eTag.getValue(), Charsets.UTF_8);
                }

                if (imageBuffer != null) {
                    bufferedimage = imageBuffer.parseUserSkin(bufferedimage);
                }
                setBufferedImage(bufferedimage);
            }
        } catch (Exception exception) {
            LOGGER.error("Couldn\'t download http texture", exception);
        }
    }
}

