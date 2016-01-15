package com.mumfrey.liteloader.launch;

import java.io.File;
import java.util.List;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger.Verbosity;

public class LiteLoaderTweakerServer extends LiteLoaderTweaker
{
    public LiteLoaderTweakerServer()
    {
        LiteLoaderLogger.verbosity = Verbosity.REDUCED;
    }

    @Override
    protected StartupEnvironment spawnStartupEnvironment(List<String> args, File gameDirectory, File assetsDirectory, String profile)
    {
        return new StartupEnvironment(args, gameDirectory, assetsDirectory, profile)
        {
            @Override
            public void registerCoreAPIs(List<String> apisToLoad)
            {
                apisToLoad.add(0, "com.mumfrey.liteloader.server.api.LiteLoaderCoreAPIServer");
            }

            @Override
            public int getEnvironmentTypeId()
            {
                return LiteLoaderTweaker.ENV_TYPE_DEDICATEDSERVER;
            }
        };
    }

    @Override
    public String getLaunchTarget()
    {
        super.getLaunchTarget();

        return "net.minecraft.server.MinecraftServer";
    }
}
