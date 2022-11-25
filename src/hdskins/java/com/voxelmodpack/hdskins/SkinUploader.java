package com.voxelmodpack.hdskins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterators;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.*;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.gui.Feature;
import com.voxelmodpack.hdskins.resources.PreviewTexture;
import com.voxelmodpack.hdskins.resources.PreviewTextureManager;
import com.voxelmodpack.hdskins.server.HttpException;
import com.voxelmodpack.hdskins.server.SkinServer;
import com.voxelmodpack.hdskins.server.SkinUpload;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SkinUploader implements Closeable {

    private static final Logger logger = LogManager.getLogger();

    private final Iterator<SkinServer> skinServers;

    public static final String ERR_NO_SERVER = "hdskins.error.noserver";
    public static final String ERR_OFFLINE = "hdskins.error.offline";

    public static final String ERR_MOJANG = "hdskins.error.mojang";
    public static final String ERR_WAIT = "hdskins.error.mojang.wait";

    public static final String STATUS_FETCH = "hdskins.fetch";

    private SkinServer gateway;

    private String status;

    private Type skinType;

    private Map<String, String> skinMetadata = new HashMap<>();

    private volatile boolean fetchingSkin = false;
    private volatile boolean throttlingNeck = false;
    private volatile boolean offline = false;
    private volatile boolean pending = false;

    private volatile boolean sendingSkin = false;

    private int reloadCounter = 0;
    private int retries = 1;

    private final EntityPlayerModel remotePlayer;
    private final EntityPlayerModel localPlayer;

    private final Object skinLock = new Object();

    private File pendingLocalSkin;
    private File localSkin;

    private final ISkinUploadHandler listener;

    private final Minecraft mc = Minecraft.getMinecraft();

    public SkinUploader(List<SkinServer> servers, EntityPlayerModel local, EntityPlayerModel remote, ISkinUploadHandler listener) {

        localPlayer = local;
        remotePlayer = remote;

        skinType = Type.SKIN;
        skinMetadata.put("model", "default");

        this.listener = listener;
        skinServers = Iterators.cycle(servers);
        cycleGateway();
    }

    public void cycleGateway() {
        if (skinServers.hasNext()) {
            gateway = skinServers.next();
            fetchRemote();
        } else {
            setError(ERR_NO_SERVER);
        }
    }

    public String getGateway() {
        return gateway == null ? "" : gateway.toString();
    }

    public Set<Feature> getFeatures() {
        return gateway == null ? Collections.emptySet() : gateway.getFeatures();
    }

    protected void setError(String er) {
        status = er;
        sendingSkin = false;
    }

    public void setSkinType(Type type) {
        skinType = type;

        ItemStack stack = type == Type.ELYTRA ? new ItemStack(Items.ELYTRA) : ItemStack.EMPTY;
        // put on or take off the elytra
        localPlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
        remotePlayer.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);

        listener.onSkinTypeChanged(type);
    }

    public boolean uploadInProgress() {
        return sendingSkin;
    }

    public boolean downloadInProgress() {
        return fetchingSkin;
    }

    public boolean isThrottled() {
        return throttlingNeck;
    }

    public boolean isOffline() {
        return offline;
    }

    public int getRetries() {
        return retries;
    }

    public boolean canUpload() {
        return !isOffline() && !hasStatus() && !uploadInProgress() && pendingLocalSkin == null && localSkin != null && localPlayer.isUsingLocalTexture();
    }

    public boolean canClear() {
        return !isOffline() && !hasStatus() && !downloadInProgress() && remotePlayer.isUsingRemoteTexture();
    }

    public boolean hasStatus() {
        return status != null;
    }

    public String getStatusMessage() {
        return hasStatus() ? status : "";
    }

    public void setMetadataField(String field, String value) {
        localPlayer.releaseTextures();
        skinMetadata.put(field, value);
    }

    public String getMetadataField(String field) {
        return skinMetadata.getOrDefault(field, "");
    }

    public Type getSkinType() {
        return skinType;
    }

    public boolean tryClearStatus() {
        if (!hasStatus() || !uploadInProgress()) {
            status = null;
            return true;
        }

        return false;
    }

    public CompletableFuture<Void> uploadSkin(String statusMsg) {
        sendingSkin = true;
        status = statusMsg;

        return CompletableFuture.runAsync(() -> {
            try {
                gateway.performSkinUpload(new SkinUpload(mc.getSession(), skinType, localSkin == null ? null : localSkin.toURI(), skinMetadata));
                setError("");
            } catch (IOException | AuthenticationException e) {
                handleException(e);
            }
        }, HDSkinManager.skinUploadExecutor).thenRunAsync(this::fetchRemote);
    }

    public PreviewTexture getServerTexture() {
        return remotePlayer.getLocal(skinType).getRemote();
    }

    protected void fetchRemote() {
        boolean wasPending = pending;
        pending = false;
        fetchingSkin = true;
        throttlingNeck = false;
        offline = false;

        remotePlayer.reloadRemoteSkin(this, (type, location, profileTexture) -> {
            fetchingSkin = false;
            if (type == skinType) {
                fetchingSkin = false;
                if (wasPending) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(net.minecraft.init.SoundEvents.ENTITY_VILLAGER_YES, 1));
                }
            }
            listener.onSetRemoteSkin(type, location, profileTexture);
        }).handleAsync((a, throwable) -> {
            fetchingSkin = false;

            if (throwable != null) {
                handleException(throwable.getCause());
            } else {
                retries = 1;
            }
            return a;
        }, Minecraft.getMinecraft()::addScheduledTask);
    }

    private void handleException(Throwable throwable) {
        throwable = Throwables.getRootCause(throwable);

        fetchingSkin = false;

        if (throwable instanceof AuthenticationUnavailableException) {
            offline = true;
        } else if (throwable instanceof InvalidCredentialsException) {
            setError("Invalid session: Please try restarting Minecraft");
        } else if (throwable instanceof AuthenticationException) {
            throttlingNeck = true;
        } else if (throwable instanceof HttpException) {
            HttpException ex = (HttpException)throwable;

            int code = ex.getStatusCode();

            if (code >= 500) {
                logger.error(ex.getReasonPhrase(), ex);
                setError("A fatal server error has ocurred (check logs for details): \n" + ex.getReasonPhrase());
            } else if (code >= 400 && code != 403 && code != 404) {
                logger.error(ex.getReasonPhrase(), ex);
                setError(ex.getReasonPhrase());
            }
        } else {
            logger.error("Unhandled exception", throwable);
            setError(throwable.toString());
        }
    }

    @Override
    public void close() throws IOException {
        localPlayer.releaseTextures();
        remotePlayer.releaseTextures();
    }

    public void setLocalSkin(File skinFile) {
        mc.addScheduledTask(localPlayer::releaseTextures);

        synchronized (skinLock) {
            pendingLocalSkin = skinFile;
        }
    }

    public void update() {
        localPlayer.updateModel();
        remotePlayer.updateModel();

        synchronized (skinLock) {
            if (pendingLocalSkin != null) {
                logger.debug("Set %s %s", skinType, pendingLocalSkin);
                localPlayer.setLocalTexture(pendingLocalSkin, skinType);
                localSkin = pendingLocalSkin;
                pendingLocalSkin = null;
                listener.onSetLocalSkin(skinType);
            }
        }

        if (isThrottled()) {
            reloadCounter = (reloadCounter + 1) % (200 * retries);
            if (reloadCounter == 0) {
                retries++;
                fetchRemote();
            }
        } else if (pending) {
            fetchRemote();
        }
    }

    public CompletableFuture<PreviewTextureManager> loadTextures(GameProfile profile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new PreviewTextureManager(gateway.getPreviewTextures(profile));
            } catch (IOException | AuthenticationException e) {
                throw new RuntimeException(e);
            }
        }, HDSkinManager.skinDownloadExecutor); // run on main thread
    }

    public interface ISkinUploadHandler {
        default void onSetRemoteSkin(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
        }

        default void onSetLocalSkin(Type type) {
        }

        default void onSkinTypeChanged(Type newType) {

        }
    }
}
