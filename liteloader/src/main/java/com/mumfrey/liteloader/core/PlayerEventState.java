package com.mumfrey.liteloader.core;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import com.mumfrey.liteloader.PlayerInteractionListener.MouseButton;
import com.mumfrey.liteloader.core.LiteLoaderEventBroker.InteractType;
import com.mumfrey.liteloader.util.EntityUtilities;

public class PlayerEventState implements IEventState
{
    private static long MISS = new BlockPos(-1, -1, -1).toLong();

    private WeakReference<EntityPlayerMP> playerRef;

    private final LiteLoaderEventBroker<?, ?> broker;

    private double traceDistance = 256.0;

    private int suppressLeftTicks; 
    private int suppressRightTicks; 
    private boolean leftClick;
    private boolean rightClick;

    private MovingObjectPosition hit;

    private String locale = "en_US";

    public PlayerEventState(EntityPlayerMP player, LiteLoaderEventBroker<?, ?> broker)
    {
        this.playerRef = new WeakReference<EntityPlayerMP>(player);
        this.broker = broker;
    }

    public void setTraceDistance(int renderDistance)
    {
        this.traceDistance = renderDistance * 16.0;
    }

    public double getTraceDistance()
    {
        return this.traceDistance;
    }

    public void setLocale(String lang)
    {
        if (lang.matches("^[a-z]{2}_[A-Z]{2}$"))
        {
            this.locale = lang;
        }
    }

    public String getLocale()
    {
        return this.locale;
    }

    public EntityPlayerMP getPlayer()
    {
        return this.playerRef.get();
    }

    public void onSpawned()
    {
    }

    @Override
    public void onTick(MinecraftServer server)
    {
        if (this.leftClick && this.suppressLeftTicks == 0)
        {
            this.broker.onPlayerClickedAir(this.getPlayer(), MouseButton.LEFT, this.hit.getBlockPos(), this.hit.sideHit, this.hit.typeOfHit);
        }

        if (this.rightClick && this.suppressRightTicks == 0)
        {
            this.broker.onPlayerClickedAir(this.getPlayer(), MouseButton.RIGHT, this.hit.getBlockPos(), this.hit.sideHit,  this.hit.typeOfHit);
        }

        if (this.suppressLeftTicks > 0) this.suppressLeftTicks--;
        if (this.suppressRightTicks > 0) this.suppressRightTicks--;

        this.leftClick = false;
        this.rightClick = false;
    }

    public boolean onPlayerInteract(InteractType action, EntityPlayerMP player, BlockPos position, EnumFacing side)
    {
        this.hit = EntityUtilities.rayTraceFromEntity(player, this.traceDistance, 0.0F);

        if (action == InteractType.LEFT_CLICK)
        {
            this.leftClick = true;
            return true;
        }

        if (action == InteractType.RIGHT_CLICK)
        {
            this.rightClick = true;
            return true;
        }

        if ((action == InteractType.LEFT_CLICK_BLOCK || action == InteractType.DIG_BLOCK_MAYBE) && this.suppressLeftTicks == 0)
        {
            this.suppressLeftTicks += 2;
            return this.broker.onPlayerClickedBlock(player, MouseButton.LEFT, position, side);
        }

        if (action == InteractType.PLACE_BLOCK_MAYBE)
        {
            if (this.suppressRightTicks > 0)
            {
                return true;
            }

            if (position.toLong() == PlayerEventState.MISS)
            {
                MovingObjectPosition actualHit = EntityUtilities.rayTraceFromEntity(player, player.capabilities.isCreativeMode ? 5.0 : 4.5, 0.0F);
                if (actualHit.typeOfHit == MovingObjectType.MISS)
                {
                    this.rightClick = true;
                    return true;
                }
            }

            this.suppressRightTicks++;
            this.suppressLeftTicks++;
            return this.broker.onPlayerClickedBlock(player, MouseButton.RIGHT, position, side);
        }

        return true;
    }
}
