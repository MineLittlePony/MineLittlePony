package com.mumfrey.liteloader.core;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Proxy
{
    private static LiteLoaderEventBroker<?, ?> broker;

    protected Proxy() {}

    protected static void onStartupComplete()
    {
        Proxy.broker = LiteLoaderEventBroker.broker;

        if (Proxy.broker == null)
        {
            throw new RuntimeException("LiteLoader failed to start up properly."
                    + " The game is in an unstable state and must shut down now. Check the developer log for startup errors");
        }
    }

    public static void onInitializePlayerConnection(ServerConfigurationManager source, NetworkManager netManager, EntityPlayerMP player)
    {
        Proxy.broker.onInitializePlayerConnection(source, netManager, player);
    }

    public static void onPlayerLogin(ServerConfigurationManager source, EntityPlayerMP player)
    {
        Proxy.broker.onPlayerLogin(source, player);
    }

    public static void onPlayerLogout(ServerConfigurationManager source, EntityPlayerMP player)
    {
        Proxy.broker.onPlayerLogout(source, player);
    }

    public static void onSpawnPlayer(CallbackInfoReturnable<EntityPlayerMP> cir, ServerConfigurationManager source, GameProfile profile)
    {
        Proxy.broker.onSpawnPlayer(source, cir.getReturnValue(), profile);
    }

    public static void onRespawnPlayer(CallbackInfoReturnable<EntityPlayerMP> cir, ServerConfigurationManager source, EntityPlayerMP oldPlayer,
            int dimension, boolean won)
    {
        Proxy.broker.onRespawnPlayer(source, cir.getReturnValue(), oldPlayer, dimension, won);
    }

    public static void onServerTick(MinecraftServer mcServer)
    {
        Proxy.broker.onServerTick(mcServer);
    }

    public static void onPlaceBlock(CallbackInfo ci, NetHandlerPlayServer netHandler, C08PacketPlayerBlockPlacement packet)
    {
        if (!Proxy.broker.onPlaceBlock(netHandler, netHandler.playerEntity, packet.getPosition(),
                EnumFacing.getFront(packet.getPlacedBlockDirection())))
        {
            ci.cancel();
        }
    }

    public static void onClickedAir(CallbackInfo ci, NetHandlerPlayServer netHandler, C0APacketAnimation packet)
    {
        if (!Proxy.broker.onClickedAir(netHandler))
        {
            ci.cancel();
        }
    }

    public static void onPlayerDigging(CallbackInfo ci, NetHandlerPlayServer netHandler, C07PacketPlayerDigging packet)
    {
        if (packet.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK)
        {
            if (!Proxy.broker.onPlayerDigging(netHandler, packet.getPosition(), netHandler.playerEntity))
            {
                ci.cancel();
            }
        }
    }

    public static void onUseItem(CallbackInfoReturnable<Boolean> ci, EntityPlayer player, World world, ItemStack itemStack, BlockPos pos,
            EnumFacing side, float par8, float par9, float par10)
    {
        if (!(player instanceof EntityPlayerMP))
        {
            return;
        }

        if (!Proxy.broker.onUseItem(pos, side, (EntityPlayerMP)player))
        {
            ci.setReturnValue(false);
        }
    }

    public static void onBlockClicked(CallbackInfo ci, ItemInWorldManager manager, BlockPos pos, EnumFacing side)
    {
        if (!Proxy.broker.onBlockClicked(pos, side, manager))
        {
            ci.cancel();
        }
    }

    public static void onPlayerMoved(CallbackInfo ci, NetHandlerPlayServer netHandler, C03PacketPlayer packet, WorldServer world, double oldPosX,
            double oldPosY, double oldPosZ)
    {
        if (!Proxy.broker.onPlayerMove(netHandler, packet, netHandler.playerEntity, world))
        {
            ci.cancel();
        }
    }

    public static void onPlayerMoved(CallbackInfo ci, NetHandlerPlayServer netHandler, C03PacketPlayer packet, WorldServer world, double oldPosX,
            double oldPosY, double oldPosZ, double deltaMoveSq, double deltaX, double deltaY, double deltaZ)
    {
        if (!Proxy.broker.onPlayerMove(netHandler, packet, netHandler.playerEntity, world))
        {
            ci.cancel();
        }
    }
}
