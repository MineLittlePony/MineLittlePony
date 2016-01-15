package com.mumfrey.liteloader.crashreport;

import java.util.concurrent.Callable;

import net.minecraft.crash.CrashReport;

import com.mumfrey.liteloader.core.LiteLoader;

public class CallableLiteLoaderMods implements Callable<String>
{
    final CrashReport crashReport;

    public CallableLiteLoaderMods(CrashReport report)
    {
        this.crashReport = report;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public String call() throws Exception
    {
        try
        {
            return LiteLoader.getInstance().getLoadedModsList();
        }
        catch (Exception ex)
        {
            return "LiteLoader startup failed";
        }
    }
}
