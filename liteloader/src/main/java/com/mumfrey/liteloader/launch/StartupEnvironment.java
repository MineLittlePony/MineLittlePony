package com.mumfrey.liteloader.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.launchwrapper.Launch;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Container for startup environment state which also parses the command line
 * options.
 * 
 * @author Adam Mummery-Smith
 */
public abstract class StartupEnvironment implements GameEnvironment
{
    private List<String> singularLaunchArgs = new ArrayList<String>();
    private Map<String, String> launchArgs;

    private ArgumentAcceptingOptionSpec<String> modsDirOption;
    private ArgumentAcceptingOptionSpec<String> modsOption;
    private ArgumentAcceptingOptionSpec<String> apisOption;
    private NonOptionArgumentSpec<String> unparsedOptions;
    private OptionSet parsedOptions;

    private final File gameDirectory;
    private final File assetsDirectory;
    private final String profile;

    public StartupEnvironment(List<String> args, File gameDirectory, File assetsDirectory, String profile)
    {
        this.gameDirectory = gameDirectory;
        this.assetsDirectory = assetsDirectory;
        this.profile = profile;

        this.initArgs(args);
    }

    public abstract void registerCoreAPIs(List<String> apisToLoad);

    public abstract int getEnvironmentTypeId();

    /**
     * @param args
     */
    @SuppressWarnings("unchecked")
    public void initArgs(List<String> args)
    {
        // Get the launchArgs map from the blackboard, or create it if it's not there
        this.launchArgs = (Map<String, String>)Launch.blackboard.get("launchArgs");
        if (this.launchArgs == null)
        {
            this.launchArgs = new HashMap<String, String>();
            Launch.blackboard.put("launchArgs", this.launchArgs);
        }

        // Parse liteloader options using joptsimple
        this.parseOptions(args.toArray(new String[args.size()]));

        // Parse out the arguments ourself because joptsimple doesn't really provide a good way to
        // add arguments to the unparsed argument list after parsing
        this.parseArgs(this.parsedOptions.valuesOf(this.unparsedOptions));

        // Put required arguments to the blackboard if they don't already exist there
        this.provideRequiredArgs();
    }

    private void parseOptions(String[] args)
    {
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();

        this.modsOption = optionParser.accepts("mods", "Comma-separated list of mods to load")
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        this.apisOption = optionParser.accepts("api", "Additional API classes to load")
                .withRequiredArg().ofType(String.class);
        this.modsDirOption = optionParser.accepts("modsDir", "Path to 'mods' folder to use instead of default")
                .withRequiredArg().ofType(String.class);

        this.unparsedOptions = optionParser.nonOptions();
        this.parsedOptions = optionParser.parse(args);
    }

    private void parseArgs(List<String> args)
    {
        String classifier = null;

        for (String arg : args)
        {
            if (arg.startsWith("-"))
            {
                if (classifier != null)
                {
                    this.addClassifiedArg(classifier, "");
                    classifier = null;
                }
                else if (arg.contains("="))
                {
                    this.addClassifiedArg(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
                }
                else
                {
                    classifier = arg;
                }
            }
            else
            {
                if (classifier != null)
                {
                    this.addClassifiedArg(classifier, arg);
                    classifier = null;
                }
                else
                {
                    this.singularLaunchArgs.add(arg);
                }
            }
        }

        if (classifier != null) this.singularLaunchArgs.add(classifier);
    }

    public void addClassifiedArg(String classifiedArg, String arg)
    {
        this.launchArgs.put(classifiedArg, arg);
    }

    public void provideRequiredArgs()
    {
        if (this.launchArgs.get("--version") == null)
        {
            this.addClassifiedArg("--version", LiteLoaderTweaker.VERSION);
        }

        if (this.launchArgs.get("--gameDir") == null && this.gameDirectory != null)
        {
            this.addClassifiedArg("--gameDir", this.gameDirectory.getAbsolutePath());
        }

        if (this.launchArgs.get("--assetsDir") == null && this.assetsDirectory != null)
        {
            this.addClassifiedArg("--assetsDir", this.assetsDirectory.getAbsolutePath());
        }
    }

    public String[] getLaunchArguments()
    {
        List<String> args = new ArrayList<String>();

        for (String singularArg : this.singularLaunchArgs)
            args.add(singularArg);

        for (Entry<String, String> launchArg : this.launchArgs.entrySet())
        {
            args.add(launchArg.getKey().trim());
            args.add(launchArg.getValue().trim());
        }

        this.singularLaunchArgs.clear();
        this.launchArgs.clear();

        return args.toArray(new String[args.size()]);
    }

    /**
     * Get the mod filter list
     */
    public List<String> getModFilterList()
    {
        return (this.parsedOptions.has(this.modsOption)) ? this.modsOption.values(this.parsedOptions) : null;
    }

    /**
     * Get API classes to load
     */
    public List<String> getAPIsToLoad()
    {
        List<String> apisToLoad = new ArrayList<String>();
        this.registerCoreAPIs(apisToLoad);
        if (this.parsedOptions.has(this.apisOption))
        {
            apisToLoad.addAll(this.apisOption.values(this.parsedOptions));
        }

        return apisToLoad;
    }

    public File getOptionalDirectory(File baseDirectory, ArgumentAcceptingOptionSpec<String> option, String defaultDir)
    {
        if (this.parsedOptions.has(option))
        {
            String path = option.value(this.parsedOptions);
            File dir = new File(path);
            if (dir.isAbsolute())
            {
                return dir;
            }

            return new File(baseDirectory, path);
        }

        return new File(baseDirectory, defaultDir);
    }

    @Override
    public File getGameDirectory()
    {
        return this.gameDirectory;
    }

    @Override
    public File getAssetsDirectory()
    {
        return this.assetsDirectory;
    }

    @Override
    public String getProfile()
    {
        return this.profile;
    }

    @Override
    public File getModsFolder()
    {
        return this.getOptionalDirectory(this.gameDirectory, this.modsDirOption, "mods");
    }
}
