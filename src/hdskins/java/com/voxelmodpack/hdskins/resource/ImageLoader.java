package com.voxelmodpack.hdskins.resource;

import com.voxelmodpack.hdskins.DynamicTextureImage;
import com.voxelmodpack.hdskins.ImageBufferDownloadHD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class ImageLoader implements Supplier<ResourceLocation> {

    private static Minecraft mc = Minecraft.getMinecraft();

    private final ResourceLocation original;

    public ImageLoader(ResourceLocation loc) {
        original = loc;
    }

    @Override
    @Nullable
    public ResourceLocation get() {
        BufferedImage image = getImage(original);
        final BufferedImage updated = new ImageBufferDownloadHD().parseUserSkin(image);
        if (updated == null) {
            return null;
        }
        if (updated == image) {
            // don't load a new image
            return this.original;
        }
        return addTaskAndGet(() -> loadSkin(updated));
    }

    private static <V> V addTaskAndGet(Callable<V> callable) {
        try {
            return mc.addScheduledTask(callable).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private static BufferedImage getImage(ResourceLocation res) {
        try (InputStream in = mc.getResourceManager().getResource(res).getInputStream()) {
            return TextureUtil.readBufferedImage(in);
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private ResourceLocation loadSkin(BufferedImage image) {
        ResourceLocation conv = new ResourceLocation(original.getResourceDomain() + "-converted", original.getResourcePath());
        boolean success = mc.getTextureManager().loadTexture(conv, new DynamicTextureImage(image));

        return success ? conv : null;
    }

}
