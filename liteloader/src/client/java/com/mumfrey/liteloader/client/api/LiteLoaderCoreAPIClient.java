package com.mumfrey.liteloader.client.api;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.mumfrey.liteloader.api.CoreProvider;
import com.mumfrey.liteloader.api.CustomisationProvider;
import com.mumfrey.liteloader.api.InterfaceProvider;
import com.mumfrey.liteloader.api.Observer;
import com.mumfrey.liteloader.client.LiteLoaderCoreProviderClient;
import com.mumfrey.liteloader.client.ResourceObserver;
import com.mumfrey.liteloader.client.Translator;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.api.LiteLoaderCoreAPI;
import com.mumfrey.liteloader.interfaces.ObjectFactory;
import com.mumfrey.liteloader.messaging.MessageBus;
import com.mumfrey.liteloader.transformers.event.json.ModEvents;

/**
 * Client side of the core API
 *
 * @author Adam Mummery-Smith
 */
public class LiteLoaderCoreAPIClient extends LiteLoaderCoreAPI
{
    private static final String PKG_LITELOADER_CLIENT = LiteLoaderCoreAPI.PKG_LITELOADER + ".client";

    private static final String[] requiredTransformers = {
            LiteLoaderCoreAPI.PKG_LITELOADER + ".transformers.event.EventProxyTransformer",
            LiteLoaderCoreAPI.PKG_LITELOADER + ".launch.LiteLoaderTransformer",
            LiteLoaderCoreAPIClient.PKG_LITELOADER_CLIENT + ".transformers.CrashReportTransformer"
    };

    private static final String[] requiredDownstreamTransformers = {
            LiteLoaderCoreAPI.PKG_LITELOADER_COMMON + ".transformers.LiteLoaderPacketTransformer",
            LiteLoaderCoreAPIClient.PKG_LITELOADER_CLIENT + ".transformers.MinecraftTransformer",
            LiteLoaderCoreAPI.PKG_LITELOADER + ".transformers.event.json.ModEventInjectionTransformer"
    };

    private ObjectFactory<Minecraft, IntegratedServer> objectFactory;
    
    @Override
    public String[] getMixinConfigs()
    {
        String[] commonConfigs = super.getMixinConfigs();
        return ObjectArrays.concat(commonConfigs, new String[] {
            "mixins.liteloader.client.json"
        }, String.class);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getRequiredTransformers()
     */
    @Override
    public String[] getRequiredTransformers()
    {
        return LiteLoaderCoreAPIClient.requiredTransformers;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI
     *      #getRequiredDownstreamTransformers()
     */
    @Override
    public String[] getRequiredDownstreamTransformers()
    {
        return LiteLoaderCoreAPIClient.requiredDownstreamTransformers;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getCustomisationProviders()
     */
    @Override
    public List<CustomisationProvider> getCustomisationProviders()
    {
        return ImmutableList.<CustomisationProvider>of
        (
            new LiteLoaderBrandingProvider(),
            new LiteLoaderModInfoDecorator(),
            new Translator()
        );
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getCoreProviders()
     */
    @Override
    public List<CoreProvider> getCoreProviders()
    {
        return ImmutableList.<CoreProvider>of
        (
            new LiteLoaderCoreProviderClient(this.properties),
            LiteLoader.getInput()
        );
    }


    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getInterfaceProviders()
     */
    @Override
    public List<InterfaceProvider> getInterfaceProviders()
    {
        ObjectFactory<?, ?> objectFactory = this.getObjectFactory();

        return ImmutableList.<InterfaceProvider>of
        (
            objectFactory.getEventBroker(),
            objectFactory.getPacketEventBroker(),
            objectFactory.getClientPluginChannels(),
            objectFactory.getServerPluginChannels(),
            MessageBus.getInstance()
        );
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getPreInitObservers()
     */
    @Override
    public List<Observer> getPreInitObservers()
    {
        return ImmutableList.<Observer>of
        (
            new ModEvents()
        );
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.LiteAPI#getObservers()
     */
    @Override
    public List<Observer> getObservers()
    {
        ObjectFactory<?, ?> objectFactory = this.getObjectFactory();

        return ImmutableList.<Observer>of
        (
            new ResourceObserver(),
            objectFactory.getPanelManager(),
            objectFactory.getEventBroker()
        );
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.api.LiteLoaderCoreAPI#getObjectFactory()
     */
    @Override
    public ObjectFactory<?, ?> getObjectFactory()
    {
        if (this.objectFactory == null)
        {
            this.objectFactory = new ObjectFactoryClient(this.environment, this.properties);
        }

        return this.objectFactory;
    }
}
