package com.mumfrey.liteloader.debug;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.launchwrapper.Launch;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.mumfrey.liteloader.launch.LiteLoaderTweaker;
import com.mumfrey.liteloader.launch.LiteLoaderTweakerServer;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Wrapper class for LaunchWrapper Main class, which logs in using Yggdrasil
 * first so that online shizzle can be tested.
 * 
 * @author Adam Mummery-Smith
 */
public abstract class Start
{
    /**
     * Number of times to retry Yggdrasil login 
     */
    private static final int LOGIN_RETRIES = 5;

    /**
     * Arguments which are allowed to have multiple occurrences
     */
    private static final Set<String> MULTI_VALUE_ARGS = ImmutableSet.<String>of(
            "--tweakClass"
            );

    /**
     * Entry point.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        System.setProperty("mcpenv", "true");
        Launch.main(Start.processArgs(args));
    }

    /**
     * Process the launch-time args, since we may be being launched by
     * GradleStart we need to parse out any values passed in and ensure we
     * replace them with our own.
     */
    private static String[] processArgs(String[] args)
    {
        List<String> unqualifiedArgs = new ArrayList<String>();
        Map<String, Set<String>> qualifiedArgs = new HashMap<String, Set<String>>();

        Start.parseArgs(args, unqualifiedArgs, qualifiedArgs);

        if (Start.hasArg(unqualifiedArgs, "server"))
        {
            Start.addRequiredArgsServer(args, unqualifiedArgs, qualifiedArgs);
        }
        else
        {
            Start.addRequiredArgsClient(args, unqualifiedArgs, qualifiedArgs);
        }

        args = Start.combineArgs(args, unqualifiedArgs, qualifiedArgs);

        return args; 
    }

    private static boolean hasArg(List<String> args, String target)
    {
        for (String arg : args)
        {
            if (target.equalsIgnoreCase(arg))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Read the args from the command line into the qualified and unqualified
     * collections.
     */
    private static void parseArgs(String[] args, List<String> unqualifiedArgs, Map<String, Set<String>> qualifiedArgs)
    {
        String qualifier = null;
        for (String arg : args)
        {
            boolean isQualifier = arg.startsWith("-");

            if (isQualifier)
            {
                if (qualifier != null) unqualifiedArgs.add(qualifier);
                qualifier = arg;
            }
            else if (qualifier != null)
            {
                Start.addArg(qualifiedArgs, qualifier, arg);
                qualifier = null;
            }
            else
            {
                unqualifiedArgs.add(arg);
            }
        }

        if (qualifier != null) unqualifiedArgs.add(qualifier);
    }

    private static void addRequiredArgsClient(String[] args, List<String> unqualifiedArgs, Map<String, Set<String>> qualifiedArgs)
    {
        LoginManager loginManager = Start.doLogin(qualifiedArgs);

        File gameDir = new File(System.getProperty("user.dir"));
        File assetsDir = new File(gameDir, "assets");

        Start.addArg(qualifiedArgs, "--tweakClass",     LiteLoaderTweaker.class.getName());
        Start.addArg(qualifiedArgs, "--username",       loginManager.getProfileName());
        Start.addArg(qualifiedArgs, "--uuid",           loginManager.getUUID());
        Start.addArg(qualifiedArgs, "--accessToken",    loginManager.getAuthenticatedToken());
        Start.addArg(qualifiedArgs, "--userType",       loginManager.getUserType());
        Start.addArg(qualifiedArgs, "--userProperties", loginManager.getUserProperties());
        Start.addArg(qualifiedArgs, "--version",        "mcp");
        Start.addArg(qualifiedArgs, "--gameDir",        gameDir.getAbsolutePath());
        Start.addArg(qualifiedArgs, "--assetIndex",     LiteLoaderTweaker.VERSION);
        Start.addArg(qualifiedArgs, "--assetsDir",      assetsDir.getAbsolutePath());
    }

    private static void addRequiredArgsServer(String[] args, List<String> unqualifiedArgs, Map<String, Set<String>> qualifiedArgs)
    {
        File gameDir = new File(System.getProperty("user.dir"));

        Start.addArg(qualifiedArgs, "--tweakClass", LiteLoaderTweakerServer.class.getName());
        Start.addArg(qualifiedArgs, "--version",    "mcp");
        Start.addArg(qualifiedArgs, "--gameDir",    gameDir.getAbsolutePath());
    }

    private static LoginManager doLogin(Map<String, Set<String>> qualifiedArgs)
    {
        File loginJson = new File(new File(System.getProperty("user.dir")), ".auth.json");
        LoginManager loginManager = new LoginManager(loginJson);

        String usernameFromCmdLine = Start.getArg(qualifiedArgs, "--username");
        String passwordFromCmdLine = Start.getArg(qualifiedArgs, "--password");

        loginManager.login(usernameFromCmdLine, passwordFromCmdLine, Start.LOGIN_RETRIES);

        LiteLoaderLogger.info("Launching game as %s", loginManager.getProfileName());

        return loginManager;
    }

    private static void addArg(Map<String, Set<String>> qualifiedArgs, String qualifier, String arg)
    {
        Set<String> args = qualifiedArgs.get(qualifier);

        if (args == null)
        {
            args =  new HashSet<String>();
            qualifiedArgs.put(qualifier, args);
        }

        if (!Start.MULTI_VALUE_ARGS.contains(qualifier))
        {
            args.clear();
        }

        args.add(arg);
    }

    private static String getArg(Map<String, Set<String>> qualifiedArgs, String arg)
    {
        if (qualifiedArgs.containsKey(arg))
        {
            return qualifiedArgs.get(arg).iterator().next();
        }

        return null;
    }

    private static String[] combineArgs(String[] args, List<String> unqualifiedArgs, Map<String, Set<String>> qualifiedArgs)
    {
        for (Entry<String, Set<String>> qualifiedArg : qualifiedArgs.entrySet())
        {
            for (String argValue : qualifiedArg.getValue())
            {
                unqualifiedArgs.add(qualifiedArg.getKey());
                if (!Strings.isNullOrEmpty(argValue)) unqualifiedArgs.add(argValue);
            }
        }

        return unqualifiedArgs.toArray(args);
    }
}
