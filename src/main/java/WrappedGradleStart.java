/*
TODO: ForgeGradle can't understand test sourcesets. Sad.
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import com.google.common.base.Strings;
import com.mumfrey.liteloader.launch.LiteLoaderTweaker;
import com.mumfrey.liteloader.launch.LiteLoaderTweakerServer;

import net.minecraft.launchwrapper.Launch;

// MixinBootstrap#start contains a hardcoded reference to GradleStart so we have to have it in our class name for mixins to initialise correctly.
public class WrappedGradleStart extends GradleStart {

    public static void main(String[] args) throws Throwable {
        // hack natives.
        Method m = GradleStart.class.getDeclaredMethod("hackNatives");
        m.setAccessible(true);
        m.invoke(null);

        // launch
        (new WrappedGradleStart()).launch(args);
    }

    @Override
    protected String getBounceClass() {
        return "WrappedGradleStart$Start";
    }

    @Override
    protected void preLaunch(Map<String, String> argMap, List<String> extras) {
        super.preLaunch(argMap, extras);
    }

    public static class Start {
        public static void main(String[] args) throws Throwable {
            System.setProperty("mcpenv", "true");
            Launch.main(processArgs(args));
        }

        private static String[] processArgs(String[] args) {
            File gameDir = new File(System.getProperty("user.dir"));

            Args arguments = new Args(args);

            if (arguments.contains("server")) {
                arguments
                    .add("--tweakClass", LiteLoaderTweakerServer.class.getName())
                    .put("--version", "mcp")
                    .put("--gameDir", gameDir.getAbsolutePath());
            } else {
                arguments
                    .add("--tweakClass", LiteLoaderTweaker.class.getName())
                    .addIfNotPresent("--username", "User" + (System.currentTimeMillis() % 20))
                    .addIfNotPresent("--uuid", UUID.randomUUID().toString())
                    .addIfNotPresent("--userType", "mojang")
                    .addIfNotPresent("--userProperties", "{}")
                    .addIfNotPresent("--version", "mcp")
                    .addIfNotPresent("--gameDir", gameDir.getAbsolutePath())
                    .addIfNotPresent("--assetIndex", LiteLoaderTweaker.VERSION);

                File assetsDir = new File(gameDir, "assets");
                File assetIndexDir = new File(assetsDir, "indexes");

                if (assetIndexDir.exists()) {
                    arguments.put("--assetsDir", assetsDir.getAbsolutePath());
                } else {
                    arguments.addIfNotPresent("--assetsDir", assetsDir.getAbsolutePath());
                }
            }

            return arguments.toArray(args);
        }
    }

    static class Args {
        private final Map<String, Set<String>> qualified = new HashMap<>();
        private final List<String> unqualified = new ArrayList<>();

        public Args(String[] args) {
            String qualifier = null;
            for (String arg : args) {

                if (arg.startsWith("-")) {
                    if (qualifier != null) {
                        unqualified.add(qualifier);
                    }
                    qualifier = arg;
                } else if (qualifier != null) {
                    add(qualifier, arg);
                    qualifier = null;
                } else {
                    unqualified.add(arg);
                }
            }
        }

        public Args put(String key, String value) {
            qualified.remove(key);
            return add(key, value);
        }

        public Args addIfNotPresent(String key, String value) {
            return contains(key) ? this : add(key, value);
        }

        public Args add(String key, String value) {
            qualified.computeIfAbsent(key, s -> new HashSet<>()).add(value);
            return this;
        }

        public boolean contains(String key) {
            return qualified.containsKey(key) || unqualified.contains(key);
        }

        public String[] toArray(String[] array) {
            List<String> entries = new ArrayList<>(unqualified);

            for (Entry<String, Set<String>> entry : qualified.entrySet()) {
                for (String value : entry.getValue()) {
                    entries.add(entry.getKey());
                    if (!Strings.isNullOrEmpty(value)) {
                        entries.add(value);
                    }
                }
            }

            return entries.toArray(array);
        }
    }
}
*/