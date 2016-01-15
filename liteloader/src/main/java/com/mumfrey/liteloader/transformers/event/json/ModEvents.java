package com.mumfrey.liteloader.transformers.event.json;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.launchwrapper.Launch;

import com.google.common.base.Charsets;
import com.mumfrey.liteloader.api.ContainerRegistry.DisabledReason;
import com.mumfrey.liteloader.api.EnumerationObserver;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.core.api.LoadableModFile;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import com.mumfrey.liteloader.interfaces.LoaderEnumerator;
import com.mumfrey.liteloader.interfaces.TweakContainer;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

public class ModEvents implements EnumerationObserver
{
    public static class ModEventDefinition
    {
        private final LoadableModFile file;

        private final String identifier;

        private final String json;

        public ModEventDefinition(LoadableModFile file, String json)
        {
            this.file = file;
            this.identifier = file.getIdentifier();
            this.json = json;
        }

        public String getIdentifier()
        {
            return this.identifier;
        }

        public String getJson()
        {
            return this.json;
        }

        public void onEventsInjected()
        {
            this.file.onEventsInjected();
        }

        public void injectIntoClassPath()
        {
            try
            {
                this.file.injectIntoClassPath(Launch.classLoader, true);
            }
            catch (MalformedURLException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private static final String DEFINITION_FILENAME = "events.json";

    private static Map<String, ModEventDefinition> events = new HashMap<String, ModEventDefinition>();

    @Override
    public void onRegisterEnabledContainer(LoaderEnumerator enumerator, LoadableMod<?> container)
    {
        if (container instanceof LoadableModFile)
        {
            LoadableModFile file = (LoadableModFile)container;
            if (!file.exists()) return;

            String json = file.getFileContents(ModEvents.DEFINITION_FILENAME, Charsets.UTF_8);
            if (json == null) return;

            LiteLoaderLogger.info("Registering %s for mod with id %s", ModEvents.DEFINITION_FILENAME, file.getIdentifier());
            ModEvents.events.put(file.getIdentifier(), new ModEventDefinition(file, json));
        }
    }

    @Override
    public void onRegisterDisabledContainer(LoaderEnumerator enumerator, LoadableMod<?> container, DisabledReason reason)
    {
    }

    @Override
    public void onRegisterTweakContainer(LoaderEnumerator enumerator, TweakContainer<File> container)
    {
    }

    @Override
    public void onModAdded(LoaderEnumerator enumerator, ModInfo<LoadableMod<?>> mod)
    {
    }

    static Map<String, ModEventDefinition> getEvents()
    {
        return events;
    }
}
