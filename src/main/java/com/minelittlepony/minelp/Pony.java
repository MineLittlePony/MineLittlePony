package com.minelittlepony.minelp;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.minelittlepony.minelp.model.PMAPI;
import com.minelittlepony.minelp.model.PlayerModel;
import com.minelittlepony.minelp.util.MineLPLogger;
import com.voxelmodpack.common.runtime.PrivateFields;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Pony {
    public static PonyManager ponyManager = PonyManager.getInstance();
    public PonyRace race = PonyRace.EARTH;
    public boolean advancedTexturing;
    public ResourceLocation textureResourceLocation;
    public boolean isSpPlayer;
    public boolean isPony;
    public boolean isPonySkin;
    public boolean isPegasus;
    public boolean isUnicorn;
    public boolean isFlying;
    public boolean isGlow;
    public int glowColor = -12303190;
    public boolean isMale;
    public int size = 1;
    public int wantTail;
    public boolean isVillager;
    public int villagerProfession = 1;
    public float defaultYOffset = 1.62F;
    boolean pegasusFlying;
    @SuppressWarnings("unused")
    private final int dangerzone = 2;
    private float previousFallDistance;
    private static int ponyCount = 0;
    private final int ponyId = ponyCount++;
    private int skinCheckCount;
    private boolean skinChecked;
    private boolean newSkinSize;

    public Pony(AbstractClientPlayer player) {
        this.textureResourceLocation = player.getLocationSkin();
        MineLPLogger.debug("+ Initialising new pony #%d for player %s (%s) with resource location %s.", this.ponyId, player.getCommandSenderName(), player.getUniqueID(), this.textureResourceLocation);
        this.checkSkin(this.textureResourceLocation);
    }

    public Pony(ResourceLocation aTextureResourceLocation) {
        this.textureResourceLocation = aTextureResourceLocation;
        MineLPLogger.debug("+ Initialising new pony #%d with resource location %s.", this.ponyId, this.textureResourceLocation);
        this.checkSkin(this.textureResourceLocation);
    }

    public void invalidateSkinCheck() {
        this.resetValues();
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
            skinImage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(textureResourceLocation).getInputStream());
            MineLPLogger.debug("Obtained skin from resource location %s", textureResourceLocation);
            this.checkSkin(skinImage);
        } catch (Exception var6) {
            Exception e = var6;

            try {
                ITextureObject e2 = Minecraft.getMinecraft().getTextureManager().getTexture(textureResourceLocation);
                if (e2 instanceof ThreadDownloadImageData) {
                    skinImage = PrivateFields.downloadedImage.get((ThreadDownloadImageData) e2);
                    if (skinImage != null) {
                        MineLPLogger.debug(e, "Successfully reflected downloadedImage from texture object");
                        this.checkSkin(skinImage);
                    }
                }
            } catch (Exception var5) {
                ;
            }
        }

        return skinImage;
    }

    public void checkSkin(BufferedImage bufferedimage) {
        MineLPLogger.debug("\tStart skin check #%d for pony #%d with image %s.", ++this.skinCheckCount, this.ponyId);
        this.resetValues();
        Color flagPix = new Color(bufferedimage.getRGB(0, 0), true);
        Color applejack = new Color(249, 177, 49, 255);
        Color dashie = new Color(136, 202, 240, 255);
        Color twilight = new Color(209, 159, 228, 255);
        Color celestia = new Color(254, 249, 252, 255);
        Color zecora = new Color(208, 204, 207, 255);
        Color changeling = new Color(40, 43, 41, 255);
        if (flagPix.equals(applejack)) {
            this.isPony = true;
            this.isPonySkin = true;
            this.race = Pony.PonyRace.EARTH;
        }

        if (flagPix.equals(zecora)) {
            this.isPony = true;
            this.isPonySkin = true;
            this.race = Pony.PonyRace.ZEBRA;
        }

        if (flagPix.equals(dashie)) {
            this.isPony = true;
            this.isPonySkin = true;
            this.isPegasus = true;
            this.race = Pony.PonyRace.PEGASUS;
        }

        if (flagPix.equals(twilight)) {
            this.isPony = true;
            this.isPonySkin = true;
            this.isUnicorn = true;
            this.race = Pony.PonyRace.UNICORN;
        }

        if (flagPix.equals(celestia)) {
            this.isPony = true;
            this.isPonySkin = true;
            this.isPegasus = true;
            this.isUnicorn = true;
            this.race = Pony.PonyRace.ALICORN;
        }

        if (flagPix.equals(changeling)) {
            this.isPony = true;
            this.isPonySkin = true;
            this.isPegasus = true;
            this.isUnicorn = true;
            this.race = Pony.PonyRace.CHANGELING;
        }

        Color tailcolor = new Color(bufferedimage.getRGB(1, 0), true);
        Color tailcolor1 = new Color(66, 88, 68, 255);
        Color tailcolor2 = new Color(70, 142, 136, 255);
        Color tailcolor3 = new Color(83, 75, 118, 255);
        Color tailcolor4 = new Color(138, 107, 127, 255);
        if (tailcolor.equals(tailcolor1)) {
            this.wantTail = 4;
        } else if (tailcolor.equals(tailcolor2)) {
            this.wantTail = 3;
        } else if (tailcolor.equals(tailcolor3)) {
            this.wantTail = 2;
        } else if (tailcolor.equals(tailcolor4)) {
            this.wantTail = 1;
        } else {
            this.wantTail = 0;
        }

        Color gendercolor = new Color(bufferedimage.getRGB(2, 0), true);
        Color gendercolor1 = new Color(255, 255, 255, 255);
        if (gendercolor.equals(gendercolor1)) {
            this.isMale = true;
        } else {
            this.isMale = false;
        }

        Color sizecolor = new Color(bufferedimage.getRGB(3, 0), true);
        Color scootaloo = new Color(255, 190, 83);
        Color bigmac = new Color(206, 50, 84);
        Color luna = new Color(42, 60, 120);
        if (ponyManager.getUseSizes() == 1) {
            if (sizecolor.equals(scootaloo)) {
                this.size = 0;
            } else if (sizecolor.equals(bigmac)) {
                this.size = 2;
            } else if (sizecolor.equals(luna)) {
                this.size = 3;
            } else {
                this.size = 1;
            }
        }

        Color black = new Color(0, 0, 0);
        int scaleFactor = bufferedimage.getHeight() / 32;
        int tileSize = 8 * scaleFactor;
        Color advcutiecolor = new Color(bufferedimage.getRGB(tileSize / 2, 0), true);
        if (advcutiecolor.getAlpha() == 0) {
            this.advancedTexturing = false;
        } else {
            this.advancedTexturing = false;

            for (int tempGlowColor = tileSize / 2; tempGlowColor < tileSize; ++tempGlowColor) {
                for (int y = 0; y < tileSize; ++y) {
                    Color aColor = new Color(bufferedimage.getRGB(tempGlowColor, y), true);
                    if (!aColor.equals(black)) {
                        this.advancedTexturing = true;
                    }
                }
            }
        }

        Color var27 = new Color(bufferedimage.getRGB(0, 1), true);
        if (!var27.equals(black) && var27.getAlpha() != 0) {
            this.glowColor = var27.getRGB();
        } else {
            this.glowColor = -12303190;
        }

        this.newSkinSize = bufferedimage.getWidth() == bufferedimage.getHeight();
        this.skinChecked = true;
        MineLPLogger.debug(
                "\tSkin check #%d for pony #%d completed. {IsPony:%b, Race:%s, FlagPixel:%s, AdvancedTexturing:%b}",
                this.skinCheckCount, this.ponyId, this.isPony, this.race, flagPix, this.advancedTexturing);
    }

    protected void resetValues() {
        this.isPony = false;
        this.isPonySkin = false;
        this.isPegasus = false;
        this.isUnicorn = false;
        this.isPonySkin = false;
        this.isMale = false;
        this.wantTail = 0;
        this.size = 1;
    }

    public boolean isPony() {
        return this.isPony;
    }

    public boolean isPonySkin() {
        return this.isPonySkin;
    }

    public boolean isUnicorn() {
        return this.isUnicorn;
    }

    public boolean isPegasus() {
        return this.isPegasus;
    }

    public Pony.PonyRace getRace() {
        return this.race;
    }

    public int wantTail() {
        return this.wantTail;
    }

    public boolean isMale() {
        return this.isMale;
    }

    public int size() {
        return ponyManager.getUseSizes() == 1 ? this.size : 1;
    }

    public boolean advancedTexturing() {
        return this.advancedTexturing;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public boolean isGlow() {
        return this.isGlow;
    }

    public int glowColor() {
        return this.glowColor;
    }

    public int villagerProfession() {
        return this.villagerProfession;
    }

    public boolean isPegasusFlying(double posX, double posY, double posZ, float fallDistance, boolean isJumping,
            boolean onGround, World equestria) {
        if (!this.isPegasus) {
            return pegasusFlying = false;
        } else if (isJumping) {
            return true;
        } else if (onGround) {
            return false;
        } else {
            boolean falling = fallDistance > 0;
            boolean levitating = fallDistance == this.previousFallDistance;
            boolean standingOnAir;
            if (falling && !levitating) {
                standingOnAir = this.standingOnAir(posX, posY, posZ, 1.5F, equestria);
            } else {
                standingOnAir = this.standingOnAir(posX, posY, posZ, 1.0F, equestria);
            }

            if (!standingOnAir) {
                return pegasusFlying = false;
            } else if (this.pegasusFlying) {
                return true;
            } else if (levitating) {
                return pegasusFlying = true;
            } else {
                this.previousFallDistance = fallDistance;
                if (fallDistance < 2.0F) {
                    return false;
                } else {
                    return pegasusFlying = true;
                }
            }
        }
    }

    public boolean standingOnAir(double posX, double posY, double posZ, float range, World equestria) {
        boolean foundSolidBlock = false;
        int y;
        if (this.isSpPlayer) {
            y = MathHelper.floor_double(posY - this.defaultYOffset - 0.009999999776482582D);
        } else {
            y = MathHelper.floor_double(posY - 0.009999999776482582D);
        }

        for (float shiftX = 0.0F - range; shiftX < range * 2.0F; shiftX += range) {
            for (float shiftZ = 0.0F - range; shiftZ < range * 2.0F; shiftZ += range) {
                int x = MathHelper.floor_double(posX + shiftX);
                int z = MathHelper.floor_double(posZ + shiftZ);
                if (!equestria.isAirBlock(new BlockPos(x, y, z))) {
                    foundSolidBlock = true;
                }
            }
        }

        return !foundSolidBlock;
    }

    public PlayerModel getModel() {
        return getModel(false);
    }

    public PlayerModel getModel(boolean ignorePony) {
        boolean is_a_pony = false;
        switch (ignorePony ? PonyLevel.MIXED : ponyManager.getPonyLevel()) {
        case HUMANS:
            is_a_pony = false;
            break;
        case MIXED:
            is_a_pony = isPonySkin;
            break;
        case PONIES:
            is_a_pony = true;
        }

        PlayerModel model;
        if (is_a_pony) {
            model = newSkinSize ? PMAPI.newPonyAdv : PMAPI.newPonyAdv_32;
        } else {
            model = PMAPI.human;
        }
        return model;
    }

    public ResourceLocation getTextureResourceLocation() {
        return this.textureResourceLocation;
    }

    public void setVillager(int profession) {
        this.isVillager = true;
        this.villagerProfession = profession;
    }

    public void setIsPonySkin(boolean b) {
        this.isPonySkin = false;
    }

    public static enum PonyRace {
        EARTH,
        PEGASUS,
        UNICORN,
        ALICORN,
        CHANGELING,
        ZEBRA;
    }
}
