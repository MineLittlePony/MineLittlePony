package com.voxelmodpack.common.runtime;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import paulscode.sound.SoundSystem;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.util.ObfuscationUtilities;

/**
 * Wrapper for obf/mcp reflection-accessed private fields, mainly added to
 * centralise the locations I have to update the obfuscated field names
 *
 * @author Adam Mummery-Smith
 * @param
 *            <P>
 *            Parent class type, the type of the class that owns the field
 * @param <T> Field type, the type of the field value
 */
public class PrivateFields<P, T> {
    /**
     * Class to which this field belongs
     */
    public final Class<P> parentClass;

    /**
     * Name used to access the field, determined at init
     */
    private final String fieldName;

    private boolean errorReported;

    /**
     * Creates a new private field entry
     *
     * @param owner
     * @param mcpName
     * @param name
     */
    private PrivateFields(Class<P> owner, Obf mapping) {
        this.parentClass = owner;
        this.fieldName = ObfuscationUtilities.getObfuscatedFieldName(mapping);
    }

    /**
     * Get the current value of this field on the instance class supplied
     *
     * @param instance Class to get the value of
     * @return field value or null if errors occur
     */
    @SuppressWarnings("unchecked")
    public T get(P instance) {
        try {
            return (T) Reflection.getPrivateValue(this.parentClass, instance, this.fieldName);
        } catch (Exception ex) {
            if (!this.errorReported) {
                this.errorReported = true;
                ex.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Set the value of this field on the instance class supplied
     *
     * @param instance Object to set the value of the field on
     * @param value value to set
     * @return value
     */
    public T set(P instance, T value) {
        try {
            Reflection.setPrivateValue(this.parentClass, instance, this.fieldName, value);
        } catch (Exception ex) {
            if (!this.errorReported) {
                this.errorReported = true;
                ex.printStackTrace();
            }
        }

        return value;
    }

    /**
     * Static private fields
     *
     * @param
     *            <P>
     *            Parent class type, the type of the class that owns the field
     * @param <T> Field type, the type of the field value
     */
    public static final class StaticFields<P, T> extends PrivateFields<P, T> {
        @SuppressWarnings("synthetic-access")
        public StaticFields(Class<P> owner, ObfMap mapping) {
            super(owner, mapping);
        }

        public T get() {
            return this.get(null);
        }

        public void set(T value) {
            this.set(null, value);
        }

        public static final StaticFields<I18n, Locale> locale = new StaticFields<I18n, Locale>(I18n.class, ObfMap.currentLocale);
        public static final StaticFields<Gui, ResourceLocation> optionsBackground = new StaticFields<Gui, ResourceLocation>(Gui.class, ObfMap.optionsBackground);
    }

    // If anyone screws up the formatting of this table again I will have them
    // fed to a shark
    public static final PrivateFields<GuiScreen, GuiButton> guiScreenSelectedButton = new PrivateFields<GuiScreen, GuiButton>(GuiScreen.class, ObfMap.guiScreenSelectedButton);
    public static final PrivateFields<WorldInfo, WorldType> worldType = new PrivateFields<WorldInfo, WorldType>(WorldInfo.class, ObfMap.worldType);
    public static final PrivateFields<SoundManager, SoundSystem> soundSystem = new PrivateFields<SoundManager, SoundSystem>(SoundManager.class, ObfMap.soundSystemThread);
    public static final PrivateFields<GuiSlot, Long> lastClicked = new PrivateFields<GuiSlot, Long>(GuiSlot.class, ObfMap.lastClicked);
    public static final PrivateFields<EntityRenderer, Double> renderZoom = new PrivateFields<EntityRenderer, Double>(EntityRenderer.class, ObfMap.renderZoom);
    public static final PrivateFields<EntityRenderer, Double> renderOfsetX = new PrivateFields<EntityRenderer, Double>(EntityRenderer.class, ObfMap.renderOfsetX);
    public static final PrivateFields<EntityRenderer, Double> renderOfsetY = new PrivateFields<EntityRenderer, Double>(EntityRenderer.class, ObfMap.renderOfsetY);
    public static final PrivateFields<World, Float> rainingStrength = new PrivateFields<World, Float>(World.class, ObfMap.rainingStrength);
    public static final PrivateFields<World, Float> thunderingStrength = new PrivateFields<World, Float>(World.class, ObfMap.thunderingStrength);
    public static final PrivateFields<ThreadDownloadImageData, BufferedImage> downloadedImage = new PrivateFields<ThreadDownloadImageData, BufferedImage>(ThreadDownloadImageData.class, ObfMap.downloadedImage);
    public static final PrivateFields<GuiMultiplayer, ServerList> internetServerList = new PrivateFields<GuiMultiplayer, ServerList>(GuiMultiplayer.class, ObfMap.internetServerList);
    public static final PrivateFields<GuiMultiplayer, ServerSelectionList> serverSelectionList = new PrivateFields<GuiMultiplayer, ServerSelectionList>(GuiMultiplayer.class, ObfMap.serverSelectionList);
    public static final PrivateFields<GuiScreenResourcePacks, GuiScreen> guiResourcePacksParentScreen = new PrivateFields<GuiScreenResourcePacks, GuiScreen>(GuiScreenResourcePacks.class, ObfMap.guiResourcePacksParentScreen);
    public static final PrivateFields<AbstractResourcePack, File> abstractResourcePackFile = new PrivateFields<AbstractResourcePack, File>(AbstractResourcePack.class, ObfMap.abstractResourcePackFile);
    public static final PrivateFields<Minecraft, Framebuffer> mcFramebuffer = new PrivateFields<Minecraft, Framebuffer>(Minecraft.class, ObfMap.mcFramebuffer);
    public static final PrivateFields<ThreadDownloadImageData, String> imageUrl = new PrivateFields<ThreadDownloadImageData, String>(ThreadDownloadImageData.class, ObfMap.imageUrl);
    public static final PrivateFields<ThreadDownloadImageData, Thread> imageThread = new PrivateFields<ThreadDownloadImageData, Thread>(ThreadDownloadImageData.class, ObfMap.imageThread);
    public static final PrivateFields<ThreadDownloadImageData, IImageBuffer> imageBuffer = new PrivateFields<ThreadDownloadImageData, IImageBuffer>(ThreadDownloadImageData.class, ObfMap.imageBuffer);
    public static final PrivateFields<ThreadDownloadImageData, File> imageFile = new PrivateFields<ThreadDownloadImageData, File>(ThreadDownloadImageData.class, ObfMap.imageFile);

    public static final PrivateFields<SoundEventAccessorComposite, List<ISoundEventAccessor>> eventSounds = new PrivateFields<SoundEventAccessorComposite, List<ISoundEventAccessor>>(SoundEventAccessorComposite.class, ObfMap.eventSounds);
    public static final PrivateFields<TextureManager, Map<ResourceLocation, ? extends ITextureObject>> resourceToTextureMap = new PrivateFields<TextureManager, Map<ResourceLocation, ? extends ITextureObject>>(
            TextureManager.class, ObfMap.resourceToTextureMap);
}
