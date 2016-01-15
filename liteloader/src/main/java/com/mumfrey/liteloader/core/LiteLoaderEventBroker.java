package com.mumfrey.liteloader.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mumfrey.liteloader.*;
import com.mumfrey.liteloader.PlayerInteractionListener.MouseButton;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.api.ShutdownObserver;
import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.common.LoadingProgress;
import com.mumfrey.liteloader.common.ducks.IPacketClientSettings;
import com.mumfrey.liteloader.core.event.HandlerList;
import com.mumfrey.liteloader.core.event.HandlerList.ReturnLogicOp;
import com.mumfrey.liteloader.interfaces.FastIterable;
import com.mumfrey.liteloader.interfaces.FastIterableDeque;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.Position;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

/**
 * @author Adam Mummery-Smith
 *
 * @param <TClient> Type of the client runtime, "Minecraft" on client and null
 *      on the server
 * @param <TServer> Type of the server runtime, "IntegratedServer" on the client
 *      "MinecraftServer" on the server 
 */
public abstract class LiteLoaderEventBroker<TClient, TServer extends MinecraftServer> implements InterfaceProvider, ShutdownObserver
{
    /**
     * @author Adam Mummery-Smith
     *
     * @param <T>
     */
    public static class ReturnValue<T>
    {
        private T value;
        private boolean isSet;

        public ReturnValue(T value)
        {
            this.value = value;
        }

        public ReturnValue()
        {
        }

        public boolean isSet()
        {
            return this.isSet;
        }

        public T get()
        {
            return this.value;
        }

        public void set(T value)
        {
            this.isSet = true;
            this.value = value;
        }
    }

    public static enum InteractType
    {
        RIGHT_CLICK,
        LEFT_CLICK,
        LEFT_CLICK_BLOCK,
        PLACE_BLOCK_MAYBE,
        DIG_BLOCK_MAYBE
    }

    /**
     * Singleton
     */
    static LiteLoaderEventBroker<?, ?> broker; 

    /**
     * Reference to the loader instance
     */
    protected final LiteLoader loader;

    /**
     * Reference to the game
     */
    protected final GameEngine<TClient, TServer> engine;

    /**
     * Profiler 
     */
    protected final Profiler profiler;

    protected LiteLoaderMods mods;

    private Map<UUID, PlayerEventState> playerStates = new HashMap<UUID, PlayerEventState>();
    private FastIterableDeque<IEventState> playerStateList = new HandlerList<IEventState>(IEventState.class);

    /**
     * List of mods which provide server commands
     */
    private FastIterable<ServerCommandProvider> serverCommandProviders
            = new HandlerList<ServerCommandProvider>(ServerCommandProvider.class);

    /**
     * List of mods which monitor server player events
     */
    private FastIterable<ServerPlayerListener> serverPlayerListeners
            = new HandlerList<ServerPlayerListener>(ServerPlayerListener.class);

    /**
     * List of mods which handle player interaction events 
     */
    private FastIterable<PlayerInteractionListener> playerInteractionListeners
            = new HandlerList<PlayerInteractionListener>(PlayerInteractionListener.class, ReturnLogicOp.AND);

    /**
     * List of mods which handle player movement events
     */
    private FastIterable<PlayerMoveListener> playerMoveListeners
            = new HandlerList<PlayerMoveListener>(PlayerMoveListener.class, ReturnLogicOp.AND_BREAK_ON_FALSE);

    /**
     * List of mods which monitor server ticks
     */
    private FastIterable<ServerTickable> serverTickListeners
            = new HandlerList<ServerTickable>(ServerTickable.class);

    /**
     * List of mods which want to be notified when the game is shutting down
     */
    private FastIterable<ShutdownListener> shutdownListeners
            = new HandlerList<ShutdownListener>(ShutdownListener.class);

    /**
     * ctor
     * 
     * @param loader
     * @param engine
     * @param properties
     */
    public LiteLoaderEventBroker(LiteLoader loader, GameEngine<TClient, TServer> engine, LoaderProperties properties)
    {
        this.loader   = loader;
        this.engine   = engine;
        this.profiler = engine.getProfiler();

        LiteLoaderEventBroker.broker = this;
    }

    /**
     * @param mods
     */
    void setMods(LiteLoaderMods mods)
    {
        this.mods = mods;
    }

    /**
     * 
     */
    protected void onStartupComplete()
    {
        LoadingProgress.setMessage("Checking mods...");
        this.mods.onStartupComplete();

        LoadingProgress.setMessage("Initialising CoreProviders...");
        this.loader.onStartupComplete();

        LoadingProgress.setMessage("Starting Game...");
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.InterfaceProvider#getListenerBaseType()
     */
    @Override
    public Class<? extends Listener> getListenerBaseType()
    {
        return LiteMod.class;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.InterfaceProvider#registerInterfaces(
     *      com.mumfrey.liteloader.core.InterfaceRegistrationDelegate)
     */
    @Override
    public void registerInterfaces(InterfaceRegistrationDelegate delegate)
    {
        delegate.registerInterface(ServerCommandProvider.class);
        delegate.registerInterface(ServerPlayerListener.class);
        delegate.registerInterface(PlayerInteractionListener.class);
        delegate.registerInterface(PlayerMoveListener.class);
        delegate.registerInterface(CommonPluginChannelListener.class);
        delegate.registerInterface(ServerTickable.class);
        delegate.registerInterface(ShutdownListener.class);
    }

    /**
     * Add a listener to the relevant listener lists
     * 
     * @param listener
     */
    public void addCommonPluginChannelListener(CommonPluginChannelListener listener)
    {
        if (!(listener instanceof PluginChannelListener) && !(listener instanceof ServerPluginChannelListener))
        {
            LiteLoaderLogger.warning("Interface error for mod '%1s'. Implementing CommonPluginChannelListener has no effect! "
                    + "Use PluginChannelListener or ServerPluginChannelListener instead", listener.getName());
        }
    }

    /**
     * @param serverCommandProvider
     */
    public void addServerCommandProvider(ServerCommandProvider serverCommandProvider)
    {
        this.serverCommandProviders.add(serverCommandProvider);
    }

    /**
     * @param serverPlayerListener
     */
    public void addServerPlayerListener(ServerPlayerListener serverPlayerListener)
    {
        this.serverPlayerListeners.add(serverPlayerListener);
    }

    /**
     * @param playerInteractionListener
     */
    public void addPlayerInteractionListener(PlayerInteractionListener playerInteractionListener)
    {
        this.playerInteractionListeners.add(playerInteractionListener);
    }

    /**
     * @param playerMoveListener
     */
    public void addPlayerMoveListener(PlayerMoveListener playerMoveListener)
    {
        this.playerMoveListeners.add(playerMoveListener);
    }

    /**
     * @param serverTickable
     */
    public void addServerTickable(ServerTickable serverTickable)
    {
        this.serverTickListeners.add(serverTickable);
    }

    /**
     * @param shutdownListener
     */
    public void addShutdownListener(ShutdownListener shutdownListener)
    {
        this.shutdownListeners.add(shutdownListener);
    }

    /**
     * @param instance
     * @param folderName
     * @param worldName
     * @param worldSettings
     */
    public void onStartServer(MinecraftServer instance, String folderName, String worldName, WorldSettings worldSettings)
    {
        ICommandManager commandManager = instance.getCommandManager();

        if (commandManager instanceof ServerCommandManager)
        {
            ServerCommandManager serverCommandManager = (ServerCommandManager)commandManager;
            this.serverCommandProviders.all().provideCommands(serverCommandManager);
        }

        LiteLoader.getServerPluginChannels().onServerStartup();

        this.playerStates.clear();
    }

    /**
     * @param scm
     * @param player
     * @param profile
     */
    public void onSpawnPlayer(ServerConfigurationManager scm, EntityPlayerMP player, GameProfile profile)
    {
        this.serverPlayerListeners.all().onPlayerConnect(player, profile);
        PlayerEventState playerState = this.getPlayerState(player);
        playerState.onSpawned();
    }

    /**
     * @param scm
     * @param player
     */
    public void onPlayerLogin(ServerConfigurationManager scm, EntityPlayerMP player)
    {
        LiteLoader.getServerPluginChannels().onPlayerJoined(player);
    }

    /**
     * @param scm
     * @param netManager
     * @param player
     */
    public void onInitializePlayerConnection(ServerConfigurationManager scm, NetworkManager netManager, EntityPlayerMP player)
    {
        this.serverPlayerListeners.all().onPlayerLoggedIn(player);
    }

    /**
     * @param scm
     * @param player
     * @param oldPlayer
     * @param dimension
     * @param won
     */
    public void onRespawnPlayer(ServerConfigurationManager scm, EntityPlayerMP player, EntityPlayerMP oldPlayer, int dimension, boolean won)
    {
        this.serverPlayerListeners.all().onPlayerRespawn(player, oldPlayer, dimension, won);
    }

    /**
     * @param scm
     * @param player
     */
    public void onPlayerLogout(ServerConfigurationManager scm, EntityPlayerMP player)
    {
        this.serverPlayerListeners.all().onPlayerLogout(player);
        this.removePlayer(player);
    }

    /**
     * @param clock
     * @param partialTicks
     * @param inGame
     */
    protected void onTick(boolean clock, float partialTicks, boolean inGame)
    {
        this.loader.onTick(clock, partialTicks, inGame);
    }

    /**
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    protected void onPostRender(int mouseX, int mouseY, float partialTicks)
    {
        this.loader.onPostRender(mouseX, mouseY, partialTicks);
    }

    protected void onWorldChanged(World world)
    {
        this.loader.onWorldChanged(world);
    }

    public void onServerTick(MinecraftServer server)
    {
        this.playerStateList.all().onTick(server);
        this.serverTickListeners.all().onTick(server);
    }

    public boolean onPlaceBlock(NetHandlerPlayServer netHandler, EntityPlayerMP playerMP, BlockPos pos, EnumFacing facing)
    {
        if (!this.onPlayerInteract(InteractType.PLACE_BLOCK_MAYBE, playerMP, pos, facing))
        {
            S23PacketBlockChange cancellation = new S23PacketBlockChange(playerMP.worldObj, pos.offset(facing));
            netHandler.playerEntity.playerNetServerHandler.sendPacket(cancellation);
            playerMP.sendContainerToPlayer(playerMP.inventoryContainer);
            return false;
        }

        return true;
    }

    public boolean onClickedAir(NetHandlerPlayServer netHandler)
    {
        return this.onPlayerInteract(InteractType.LEFT_CLICK, netHandler.playerEntity, null, EnumFacing.SOUTH);
    }

    public boolean onPlayerDigging(NetHandlerPlayServer netHandler, BlockPos pos, EntityPlayerMP playerMP)
    {
        if (!this.onPlayerInteract(InteractType.DIG_BLOCK_MAYBE, playerMP, pos, EnumFacing.SOUTH))
        {
            S23PacketBlockChange cancellation = new S23PacketBlockChange(playerMP.worldObj, pos);
            netHandler.playerEntity.playerNetServerHandler.sendPacket(cancellation);
            return false;
        }

        return true;
    }

    public boolean onUseItem(BlockPos pos, EnumFacing side, EntityPlayerMP playerMP)
    {
        if (!this.onPlayerInteract(InteractType.PLACE_BLOCK_MAYBE, playerMP, pos, side))
        {
            S23PacketBlockChange cancellation = new S23PacketBlockChange(playerMP.worldObj, pos);
            playerMP.playerNetServerHandler.sendPacket(cancellation);
            return false;
        }

        return true;
    }

    public boolean onBlockClicked(BlockPos pos, EnumFacing side, ItemInWorldManager manager)
    {
        if (!this.onPlayerInteract(InteractType.LEFT_CLICK_BLOCK, manager.thisPlayerMP, pos, side))
        {
            S23PacketBlockChange cancellation = new S23PacketBlockChange(manager.theWorld, pos);
            manager.thisPlayerMP.playerNetServerHandler.sendPacket(cancellation);
            return false;
        }

        return true;
    }

    public boolean onPlayerInteract(InteractType action, EntityPlayerMP player, BlockPos position, EnumFacing side)
    {
        PlayerEventState eventState = this.getPlayerState(player);
        return eventState.onPlayerInteract(action, player, position, side);
    }

    void onPlayerClickedAir(EntityPlayerMP player, MouseButton button, BlockPos tracePos, EnumFacing traceSideHit, MovingObjectType traceHitType)
    {
        this.playerInteractionListeners.all().onPlayerClickedAir(player, button, tracePos, traceSideHit, traceHitType);
    }

    boolean onPlayerClickedBlock(EntityPlayerMP player, MouseButton button, BlockPos hitPos, EnumFacing sideHit)
    {
        return this.playerInteractionListeners.all().onPlayerClickedBlock(player, button, hitPos, sideHit);
    }

    public boolean onPlayerMove(NetHandlerPlayServer netHandler, C03PacketPlayer packet, EntityPlayerMP playerMP, WorldServer world)
    {
        Position from = new Position(playerMP, true);

        double toX = playerMP.posX;
        double toY = playerMP.posY;
        double toZ = playerMP.posZ;
        float toYaw = playerMP.rotationYaw;
        float toPitch = playerMP.rotationPitch;

        if (packet.isMoving())
        {
            toX = packet.getPositionX();
            toY = packet.getPositionY();
            toZ = packet.getPositionZ();
        }

        if (packet.getRotating())
        {
            toYaw = packet.getYaw();
            toPitch = packet.getPitch();
        }

        Position to = new Position(toX, toY, toZ, toYaw, toPitch);
        ReturnValue<Position> pos = new ReturnValue<Position>(to);

        if (!this.playerMoveListeners.all().onPlayerMove(playerMP, from, to, pos))
        {
            playerMP.setPositionAndRotation(from.xCoord, from.yCoord, from.zCoord, playerMP.prevRotationYaw, playerMP.prevRotationPitch);
            playerMP.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(from.xCoord, from.yCoord, from.zCoord,
                    playerMP.prevRotationYaw, playerMP.prevRotationPitch, Collections.emptySet()));
            return false;
        }

        if (pos.isSet())
        {
            Position newPos = pos.get();
            netHandler.setPlayerLocation(newPos.xCoord, newPos.yCoord, newPos.zCoord, newPos.yaw, newPos.pitch);
            return false;
        }

        return true;
    }

    void onPlayerSettingsReceived(EntityPlayerMP player, C15PacketClientSettings packet)
    {
        PlayerEventState playerState = this.getPlayerState(player);
        playerState.setTraceDistance(((IPacketClientSettings)packet).getViewDistance());
        playerState.setLocale(packet.getLang());
    }

    public PlayerEventState getPlayerState(EntityPlayerMP player)
    {
        PlayerEventState playerState = this.playerStates.get(player.getUniqueID());
        if (playerState == null)
        {
            playerState = new PlayerEventState(player, this);
            this.playerStates.put(player.getUniqueID(), playerState);
            this.playerStateList.add(playerState);
        }
        return playerState;
    }

    protected void removePlayer(EntityPlayerMP player)
    {
        PlayerEventState playerState = this.playerStates.remove(player.getUniqueID());
        if (playerState != null)
        {
            this.playerStateList.remove(playerState);
        }
    }

    @Override
    public void onShutDown()
    {
        for (ShutdownListener listener : this.shutdownListeners)
        {
            try
            {
                listener.onShutDown();
            }
            catch (Throwable th)
            {
                th.printStackTrace();
            }
        }
    }
}
