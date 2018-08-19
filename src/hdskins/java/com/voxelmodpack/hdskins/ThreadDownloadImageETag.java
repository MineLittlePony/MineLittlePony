package com.voxelmodpack.hdskins;

import com.voxelmodpack.hdskins.skins.MoreHttpResponses;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

public class ThreadDownloadImageETag extends SimpleTexture {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
    private static CloseableHttpClient client = HttpClients.createSystem();

    @Nonnull
    private final Path cacheFile;
    private final Path eTagFile;
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
        this.cacheFile = cacheFileIn.toPath();
        this.eTagFile = cacheFile.resolveSibling(cacheFile.getFileName() + ".etag");
        this.imageUrl = imageUrlIn;
        this.imageBuffer = imageBufferIn;
    }

    private void checkTextureUploaded() {
        if (!this.textureUploaded) {
            if (this.bufferedImage != null) {
                if (this.textureLocation != null) {
                    this.deleteGlTexture();
                }

                TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
                this.textureUploaded = true;
            }
        }
    }

    @Override
    public int getGlTextureId() {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }

    private void setBufferedImage(@Nonnull BufferedImage bufferedImageIn) {
        this.bufferedImage = bufferedImageIn;

        if (this.imageBuffer != null) {
            this.imageBuffer.skinAvailable();
        }
    }

    @Nullable
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (this.bufferedImage == null && this.textureLocation != null) {
            super.loadTexture(resourceManager);
        }

        if (this.imageThread == null) {
            this.imageThread = new Thread(this::loadTexture, "Texture Downloader #" + THREAD_ID.incrementAndGet());
            this.imageThread.setDaemon(true);
            this.imageThread.start();
        }
    }

    private void loadTexture() {
        switch(checkLocalCache()) {
            case GONE:
                clearCache();
                break;
            case OK:
            case NOPE:
                LOGGER.debug("Loading http texture from local cache ({})", cacheFile);
                try {
                    // e-tag check passed. Load the local file
                    setLocalCache();
                    break;
                } catch (IOException e) {
                    // Nope. Local cache is corrupt. Re-download it.
                    // fallthrough to load from network
                    LOGGER.error("Couldn't load skin {}", cacheFile, e);
                }
            case OUTDATED:
                loadTextureFromServer();
        }
    }


    private void setLocalCache() throws IOException {
        if (Files.isRegularFile(cacheFile)) {
            try (InputStream in = Files.newInputStream(cacheFile)) {
                BufferedImage image = ImageIO.read(in);
                if (imageBuffer != null) {
                    image = imageBuffer.parseUserSkin(image);
                }
                setBufferedImage(image);
            }
        }
    }

    private void clearCache() {
        try {
            Files.deleteIfExists(this.cacheFile);
            Files.deleteIfExists(this.eTagFile);
        } catch (IOException e) {
            // ignore
        }
    }

    private enum State {
        OUTDATED,
        GONE,
        NOPE,
        OK
    }

    private State checkLocalCache() {
        try (CloseableHttpResponse response = client.execute(new HttpHead(imageUrl))) {
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_NOT_FOUND) {
                return State.GONE;
            }
            if (code != HttpStatus.SC_OK) {
                return State.NOPE;
            }
            return checkETag(response) ? State.OK : State.OUTDATED;
        } catch (IOException e) {
            LOGGER.error("Couldn't load skin {} ", imageUrl, e);
            return State.NOPE;
        }
    }

    private boolean checkETag(HttpResponse response) {
        try {
            if (Files.isRegularFile(cacheFile)) {
                String localETag = Files.lines(eTagFile).limit(1).findFirst().orElse("");
                Header remoteETag = response.getFirstHeader(HttpHeaders.ETAG);
                // true if no remote etag or does match
                return remoteETag == null || localETag.equals(remoteETag.getValue());
            }
            return false;
        } catch (IOException e) {
            // it failed, so re-fetch.
            return false;
        }
    }

    private void loadTextureFromServer() {
        LOGGER.debug("Downloading http texture from {} to {}", imageUrl, cacheFile);
        try (MoreHttpResponses resp = MoreHttpResponses.execute(client, new HttpGet(imageUrl))) {
            if (resp.ok()) {
                // write the image to disk
                Files.copy(resp.getInputStream(), cacheFile);

                BufferedImage bufferedimage = ImageIO.read(Files.newInputStream(cacheFile));

                // maybe write the etag to disk
                Header eTag = resp.getResponse().getFirstHeader(HttpHeaders.ETAG);
                if (eTag != null) {
                    Files.write(eTagFile, Collections.singleton(eTag.getValue()));
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

