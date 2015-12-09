package com.brohoof.minelittlepony;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.brohoof.minelittlepony.model.PMAPI;
import com.brohoof.minelittlepony.model.PlayerModel;
import com.brohoof.minelittlepony.util.MineLPLogger;
import com.voxelmodpack.common.runtime.PrivateFields;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class Pony {

    private static PonyConfig config = MineLittlePony.getConfig();

    private static int ponyCount = 0;
    private final int ponyId = ponyCount++;

    public ResourceLocation textureResourceLocation;
    public PonyData metadata = new PonyData();

    private int skinCheckCount;
    private boolean skinChecked;

    public Pony(AbstractClientPlayer player) {
        this.textureResourceLocation = player.getLocationSkin();
        MineLPLogger.debug("+ Initialising new pony #%d for player %s (%s) with resource location %s.", this.ponyId,
                player.getCommandSenderName(), player.getUniqueID(), this.textureResourceLocation);
        this.checkSkin(this.textureResourceLocation);
    }

    public Pony(ResourceLocation aTextureResourceLocation) {
        this.textureResourceLocation = aTextureResourceLocation;
        MineLPLogger.debug("+ Initialising new pony #%d with resource location %s.", this.ponyId,
                this.textureResourceLocation);
        this.checkSkin(this.textureResourceLocation);
    }

    public void invalidateSkinCheck() {
        this.skinChecked = false;
    }

    public void checkSkin() {
        if (!this.skinChecked) {
            this.checkSkin(this.textureResourceLocation);
        }

    }

    public void checkSkin(ResourceLocation textureResourceLocation) {
        BufferedImage skinImage = this.getBufferedImage(textureResourceLocation);
        if (skinImage != null) {
            this.checkSkin(skinImage);
        }

    }

    public BufferedImage getBufferedImage(ResourceLocation textureResourceLocation) {
        BufferedImage skinImage = null;

        try {
            skinImage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(textureResourceLocation)
                    .getInputStream());
            MineLPLogger.debug("Obtained skin from resource location %s", textureResourceLocation);
            // this.checkSkin(skinImage);
        } catch (Exception var6) {
            Exception e = var6;

            try {
                ITextureObject e2 = Minecraft.getMinecraft().getTextureManager().getTexture(textureResourceLocation);
                if (e2 instanceof ThreadDownloadImageData) {
                    skinImage = PrivateFields.downloadedImage.get((ThreadDownloadImageData) e2);
                    if (skinImage != null) {
                        MineLPLogger.debug(e, "Successfully reflected downloadedImage from texture object");
                        // this.checkSkin(skinImage);
                    }
                }
            } catch (Exception var5) {

            }
        }

        return skinImage;
    }

    public void checkSkin(BufferedImage bufferedimage) {
        MineLPLogger.debug("\tStart skin check #%d for pony #%d with image %s.", ++this.skinCheckCount, this.ponyId);
        metadata = PonyData.parse(bufferedimage);
        this.skinChecked = true;
    }

    public boolean isPegasusFlying(EntityPlayer player) {
        if (this.metadata.getRace() == null || !this.metadata.getRace().hasWings()) {
            return false;
        }
        return player.capabilities.isFlying || !(player.onGround || player.isOnLadder() || player.isInWater());

        //@formatter:off
//            boolean falling = player.fallDistance > 0;
//            boolean levitating = player.fallDistance == this.previousFallDistance;
//            boolean standingOnAir;
//            if (falling && !levitating) {
//                standingOnAir = this.standingOnAir(posX, posY, posZ, 1.5F, equestria);
//            } else {
//                standingOnAir = this.standingOnAir(posX, posY, posZ, 1.0F, equestria);
//            }
//
//            if (!standingOnAir) {
//                return pegasusFlying = false;
//            } else if (this.pegasusFlying) {
//                return true;
//            } else if (levitating) {
//                return pegasusFlying = true;
//            } else {
//                this.previousFallDistance = fallDistance;
//                if (fallDistance < 2.0F) {
//                    return false;
//                }
//                return pegasusFlying = true;
//            }
        }
    

//    public boolean standingOnAir(BlockPos pos, float range, World equestria) {
//        boolean foundSolidBlock = false;
//        int y;
//        if (this.isSpPlayer) {
//            y = MathHelper.floor_double(posY - this.defaultYOffset - 0.009999999776482582D);
//        } else {
//            y = MathHelper.floor_double(posY - 0.009999999776482582D);
//        }
//
//        for (float shiftX = 0.0F - range; shiftX < range * 2.0F; shiftX += range) {
//            for (float shiftZ = 0.0F - range; shiftZ < range * 2.0F; shiftZ += range) {
//                int x = MathHelper.floor_double(posX + shiftX);
//                int z = MathHelper.floor_double(posZ + shiftZ);
//                if (!equestria.isAirBlock(new BlockPos(x, y, z))) {
//                    foundSolidBlock = true;
//                }
//            }
//        }
//
//        return !foundSolidBlock;
//    }
    // @formatter:on

    public PlayerModel getModel() {
        return getModel(false);
    }

    public PlayerModel getModel(boolean ignorePony) {
        boolean is_a_pony = false;
        switch (ignorePony ? PonyLevel.BOTH : config.getPonyLevel().get()) {
        case HUMANS:
            is_a_pony = false;
            break;
        case BOTH:
            is_a_pony = metadata.getRace() != null;
            break;
        case PONIES:
            is_a_pony = true;
        }

        PlayerModel model;
        if (is_a_pony) {
            model = PMAPI.pony;
        } else {
            model = PMAPI.human;
        }
        return model;
    }

    public ResourceLocation getTextureResourceLocation() {
        return this.textureResourceLocation;
    }

}
