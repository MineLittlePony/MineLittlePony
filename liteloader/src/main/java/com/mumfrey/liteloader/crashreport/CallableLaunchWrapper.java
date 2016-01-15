package com.mumfrey.liteloader.crashreport;

import java.util.List;
import java.util.concurrent.Callable;

import net.minecraft.crash.CrashReport;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class CallableLaunchWrapper implements Callable<String>
{
    final CrashReport crashReport;

    public CallableLaunchWrapper(CrashReport report)
    {
        this.crashReport = report;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public String call() throws Exception
    {
        return CallableLaunchWrapper.generateTransformerList();
    }

    /**
     * Generates a list of active transformers to display in the crash report
     */
    public static String generateTransformerList()
    {
        final List<IClassTransformer> transformers = Launch.classLoader.getTransformers();

        StringBuilder sb = new StringBuilder();
        sb.append(transformers.size());
        sb.append(" active transformer(s)");

        for (IClassTransformer transformer : transformers)
        {
            sb.append("\n          - Transformer: ");
            sb.append(transformer.getClass().getName());
        }

        return sb.toString();
    }
}
