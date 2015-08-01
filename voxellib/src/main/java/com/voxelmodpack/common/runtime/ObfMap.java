package com.voxelmodpack.common.runtime;

import com.mumfrey.liteloader.core.runtime.Obf;

/**
 * Central list of obfuscation mappings used throughout macros, coalesced here
 * instead of being spread throughout the different reflection mechanisms
 * 
 * @author Adam Mummery-Smith TODO Obfuscation - updated 1.8
 */
public class ObfMap extends Obf {
    public static final ObfMap currentLocale = new ObfMap("field_135054_a", "a", "i18nLocale");
    public static final ObfMap downloadedImage = new ObfMap("field_110560_d", "l", "bufferedImage");
    public static final ObfMap renderZoom = new ObfMap("field_78503_V", "X", "cameraZoom");
    public static final ObfMap renderOfsetX = new ObfMap("field_78502_W", "Y", "cameraYaw");
    public static final ObfMap renderOfsetY = new ObfMap("field_78509_X", "Z", "cameraPitch");
    public static final ObfMap guiScreenSelectedButton = new ObfMap("field_146290_a", "h", "selectedButton");
    public static final ObfMap worldType = new ObfMap("field_76098_b", "c", "terrainType");
    public static final ObfMap soundSystemThread = new ObfMap("field_148620_e", "e", "sndSystem");
    public static final ObfMap lastClicked = new ObfMap("field_148167_s", "p", "lastClicked");
    public static final ObfMap imageUrl = new ObfMap("field_110562_b", "j", "imageUrl");
    public static final ObfMap imageThread = new ObfMap("field_110561_e", "m", "imageThread");
    public static final ObfMap imageBuffer = new ObfMap("field_110563_c", "k", "imageBuffer");
    public static final ObfMap imageFile = new ObfMap("field_152434_e", "i");
    public static final ObfMap resourceToTextureMap = new ObfMap("field_110585_a", "b", "mapTextureObjects");
    public static final ObfMap optionsBackground = new ObfMap("field_110325_k", "b", "optionsBackground");
    public static final ObfMap rainingStrength = new ObfMap("field_73004_o", "p", "rainingStrength");
    public static final ObfMap thunderingStrength = new ObfMap("field_73017_q", "r", "thunderingStrength");
    public static final ObfMap internetServerList = new ObfMap("field_146804_i", "i", "savedServerList");
    public static final ObfMap serverSelectionList = new ObfMap("field_146803_h", "h", "serverListSelector");
    public static final ObfMap guiResourcePacksParentScreen = new ObfMap("field_146965_f", "f");
    public static final ObfMap abstractResourcePackFile = new ObfMap("field_110597_b", "a", "resourcePackFile");
    public static final ObfMap mcFramebuffer = new ObfMap("field_147124_at", "aB", "framebufferMc");
    public static final ObfMap eventSounds = new ObfMap("field_148736_a", "a", "soundPool");

    public static final ObfMap getSlotAtPosition = new ObfMap("func_146975_c", "c", "getSlotAtPosition");
    public static final ObfMap handleMouseClick = new ObfMap("func_146984_a", "a", "handleMouseClick");
    public static final ObfMap selectTab = new ObfMap("func_147050_b", "b", "setCurrentCreativeTab");
    public static final ObfMap scrollTo = new ObfMap("func_148329_a", "a", "scrollTo");
    public static final ObfMap renderSkyBox = new ObfMap("func_73971_c", "c", "renderSkybox");
    public static final ObfMap guiScreenMouseClicked = new ObfMap("func_73864_a", "a", "mouseClicked");
    public static final ObfMap guiScreenMouseMovedOrUp = new ObfMap("func_146286_b", "b", "mouseReleased");
    public static final ObfMap guiScreenKeyTyped = new ObfMap("func_73869_a", "a", "keyTyped");
    public static final ObfMap createPlayer = new ObfMap("func_178892_a", "a");

    public static final ObfMap ContainerCreative = new ObfMap("net.minecraft.client.gui.inventory.GuiContainerCreative$ContainerCreative", "bza");
    public static final ObfMap SlotCreativeInventory = new ObfMap("net.minecraft.client.gui.inventory.GuiContainerCreative$CreativeSlot", "bzb");
    public static final ObfMap PlayerControllerMP = new ObfMap("net.minecraft.client.multiplayer.PlayerControllerMP", "cem");
    public static final ObfMap StatFileWriter = new ObfMap("net.minecraft.stats.StatFileWriter", "tz");
    public static final ObfMap World = new ObfMap("net.minecraft.world.World", "aqu");

    private ObfMap(String seargeName, String obfuscatedName, String mcpName) {
        super(seargeName, obfuscatedName, mcpName);
    }

    private ObfMap(String seargeName, String obfuscatedName) {
        super(seargeName, obfuscatedName, seargeName);
    }
}
