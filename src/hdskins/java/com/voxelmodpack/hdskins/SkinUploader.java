package com.voxelmodpack.hdskins;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.voxelmodpack.hdskins.gui.EntityPlayerModel;
import com.voxelmodpack.hdskins.resources.PreviewTextureManager;
import com.voxelmodpack.hdskins.server.SkinServer;
import com.voxelmodpack.hdskins.server.SkinUpload;
import com.voxelmodpack.hdskins.util.MoreHttpResponses;
import com.voxelmodpack.hdskins.util.NetClient;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class SkinUploader implements Closeable {

    private final Iterator<SkinServer> skinServers;

    public static final String ERR_NO_SERVER = "hdskins.error.noserver";
    public static final String ERR_OFFLINE = "hdskins.error.offline";

    public static final String ERR_MOJANG = "hdskins.error.mojang";
    public static final String ERR_WAIT = "hdskins.error.mojang.wait";

    public static final String STATUS_FETCH = "hdskins.fetch";

    private SkinServer gateway;

    private String status;

    private Type skinType;

    private Map<String, String> skinMetadata = new HashMap<String, String>();

    private volatile boolean fetchingSkin = false;
    private volatile boolean throttlingNeck = false;
    private volatile boolean offline = false;

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

    private static <T> Iterator<T> cycle(List<T> list, Predicate<T> filter) {
        return Iterables.cycle(Iterables.filter(list, filter::test)).iterator();
    }

    public SkinUploader(List<SkinServer> servers, EntityPlayerModel local, EntityPlayerModel remote, ISkinUploadHandler listener) {

        localPlayer = local;
        remotePlayer = remote;

        skinType = Type.SKIN;
        skinMetadata.put("model", "default");

        this.listener = listener;
        skinServers = cycle(servers, SkinServer::verifyGateway);
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

        return gateway.uploadSkin(new SkinUpload(mc.getSession(), skinType, localSkin == null ? null : localSkin.toURI(), skinMetadata)).handle((response, throwable) -> {
            if (throwable == null) {
                LiteLoaderLogger.info("Upload completed with: %s", response);
                setError(null);
            } else {
                setError(Throwables.getRootCause(throwable).toString());
            }

            fetchRemote();
            return null;
        });
    }

    public CompletableFuture<MoreHttpResponses> downloadSkin() {
        String loc = remotePlayer.getLocal(skinType).getRemote().getUrl();

        return new NetClient("GET", loc).async(HDSkinManager.skinDownloadExecutor);
    }

    protected void fetchRemote() {
        fetchingSkin = true;
        throttlingNeck = false;
        offline = false;

        remotePlayer.reloadRemoteSkin(this, (type, location, profileTexture) -> {
            fetchingSkin = false;
            listener.onSetRemoteSkin(type, location, profileTexture);
        }).handle((a, throwable) -> {
            fetchingSkin = false;

            if (throwable != null) {
                throwable = throwable.getCause();

                throwable.printStackTrace();

                if (throwable instanceof AuthenticationUnavailableException) {
                    offline = true;
                } else {
                    throttlingNeck = true;
                }
            }
            return a;
        });
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
                System.out.println("Set " + skinType + " " + pendingLocalSkin);
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
        }
    }

    public CompletableFuture<PreviewTextureManager> loadTextures(GameProfile profile) {
        return gateway.getPreviewTextures(profile).thenApply(PreviewTextureManager::new);
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
