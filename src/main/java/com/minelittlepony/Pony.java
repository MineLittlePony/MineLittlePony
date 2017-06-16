package com.minelittlepony;

import com.google.common.base.MoreObjects;
import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.PlayerModel;
import com.minelittlepony.util.PonyFields;
import com.voxelmodpack.hdskins.DynamicTextureImage;
import com.voxelmodpack.hdskins.ThreadDownloadImageETag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Pony {

    private static final AtomicInteger ponyCount = new AtomicInteger();
    private final int ponyId = ponyCount.getAndIncrement();

    private final ResourceLocation texture;
    private final PonyData metadata;

    public Pony(AbstractClientPlayer player) {
        this.texture = player.getLocationSkin();
        this.metadata = this.checkSkin(this.texture);

        MineLittlePony.logger.debug("+ Initialising new pony #{} for player {} ({}) with resource location {}.",
                this.ponyId, player.getName(), player.getUniqueID(), this.texture);
    }

    public Pony(ResourceLocation resourceLocation) {
        this(resourceLocation, null);
    }

    public Pony(ResourceLocation aTextureResourceLocation, @Nullable PonyData meta) {
        this.texture = aTextureResourceLocation;
        this.metadata = meta != null ? meta : this.checkSkin(this.texture);

        MineLittlePony.logger.debug("+ Initialising new pony #{} with resource location {}.", this.ponyId, this.texture);
    }

    private PonyData checkSkin(ResourceLocation textureResourceLocation) {
        PonyData data = checkPonyMeta(textureResourceLocation);
        if (data == null) {
            BufferedImage skinImage = this.getBufferedImage(textureResourceLocation);
            if (skinImage != null) {
                data = this.checkSkin(skinImage);
            } else {
                data = new PonyData();
            }
        }
        return data;
    }

    @Nullable
    private PonyData checkPonyMeta(ResourceLocation location) {
        try {
            IResource res = Minecraft.getMinecraft().getResourceManager().getResource(location);
            if (res.hasMetadata()) {
                PonyData data = res.getMetadata(PonyDataSerialzier.NAME);
                if (data != null) {
                    return data;
                }
            }
        } catch (FileNotFoundException e) {
            // Ignore uploaded texture
        } catch (IOException e) {
            MineLittlePony.logger.warn("Unable to read {} metadata", location, e);
        }
        return null;
    }

    @Nullable
    private BufferedImage getBufferedImage(@Nonnull ResourceLocation textureResourceLocation) {
        BufferedImage skinImage = null;
        try {
            IResource skin = Minecraft.getMinecraft().getResourceManager().getResource(textureResourceLocation);
            skinImage = TextureUtil.readBufferedImage(skin.getInputStream());
            MineLittlePony.logger.debug("Obtained skin from resource location {}", textureResourceLocation);
            // this.checkSkin(skinImage);
        } catch (IOException e) {

            try {
                ITextureObject e2 = Minecraft.getMinecraft().getTextureManager().getTexture(textureResourceLocation);
                if (e2 instanceof ThreadDownloadImageData) {

                    skinImage = PonyFields.downloadedImage.get((ThreadDownloadImageData) e2);
                    if (skinImage != null) {
                        MineLittlePony.logger.debug("Successfully reflected downloadedImage from texture object", e);
                        // this.checkSkin(skinImage);
                    }
                } else if (e2 instanceof ThreadDownloadImageETag) {
                    skinImage = ((ThreadDownloadImageETag) e2).getBufferedImage();
                } else if (e2 instanceof DynamicTextureImage) {
                    skinImage = ((DynamicTextureImage) e2).getImage();
                }
            } catch (Exception ignored) {

            }
        }

        return skinImage;
    }

    private PonyData checkSkin(BufferedImage bufferedimage) {
        MineLittlePony.logger.debug("\tStart skin check for pony #{} with image {}.", this.ponyId, bufferedimage);
        return PonyData.parse(bufferedimage);
    }

    public boolean isPegasusFlying(EntityPlayer player) {
        //noinspection SimplifiableIfStatement
        if (!this.metadata.getRace().hasWings()) {
            return false;
        }
        return player.capabilities.isFlying || !(player.onGround || player.isRiding() || player.isOnLadder() || player.isInWater() || player.isElytraFlying());
    }

    public PlayerModel getModel(boolean ignorePony, boolean smallArms) {
        boolean is_a_pony = false;
        switch (ignorePony ? PonyLevel.BOTH : MineLittlePony.getConfig().getPonyLevel()) {
            case HUMANS:
                is_a_pony = false;
                break;
            case BOTH:
                is_a_pony = metadata.getRace() != PonyRace.HUMAN;
                break;
            case PONIES:
                is_a_pony = true;
        }

        PlayerModel model;
        if (is_a_pony) {
            model = smallArms ? PMAPI.ponySmall : PMAPI.pony;
        } else {
            model = smallArms ? PMAPI.humanSmall : PMAPI.human;
        }
        return model;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public PonyData getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("texture", texture)
                .add("metadata", metadata)
                .toString();
    }
}
