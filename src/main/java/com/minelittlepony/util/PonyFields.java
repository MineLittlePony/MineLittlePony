package com.minelittlepony.util;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.util.PrivateFields;
import net.minecraft.client.renderer.ThreadDownloadImageData;

import java.awt.image.BufferedImage;

public class PonyFields<P, T> extends PrivateFields<P, T> {

    public static final PrivateFields<ThreadDownloadImageData, BufferedImage> downloadedImage = field(ThreadDownloadImageData.class, PonyObf.downloadedImage);

    protected PonyFields(Class<P> owner, Obf obf) {
        super(owner, obf);
    }

    private static <P, T> PrivateFields<P, T> field(Class<P> c, Obf o) {
        return new PonyFields<>(c, o);
    }

    private static class PonyObf extends Obf {

        public static Obf downloadedImage = new PonyObf("field_110560_d", "l", "bufferedImage");

        protected PonyObf(String seargeName, String obfName, String mcpName) {
            super(seargeName, obfName, mcpName);
        }

    }
}
