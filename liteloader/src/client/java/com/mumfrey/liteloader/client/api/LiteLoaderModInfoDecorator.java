package com.mumfrey.liteloader.client.api;

import java.util.List;

import net.minecraft.client.resources.I18n;

import com.mumfrey.liteloader.api.ModInfoDecorator;
import com.mumfrey.liteloader.client.gui.GuiLiteLoaderPanel;
import com.mumfrey.liteloader.client.gui.modlist.GuiModListPanel;
import com.mumfrey.liteloader.client.util.render.IconAbsolute;
import com.mumfrey.liteloader.client.util.render.IconAbsoluteClickable;
import com.mumfrey.liteloader.core.ModInfo;
import com.mumfrey.liteloader.util.render.IconTextured;

/**
 * ModInfo decorator
 * 
 * @author Adam Mummery-Smith
 */
public class LiteLoaderModInfoDecorator implements ModInfoDecorator
{
    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ModInfoDecorator
     *      #addIcons(com.mumfrey.liteloader.core.ModInfo, java.util.List)
     */
    @Override
    public void addIcons(final ModInfo<?> mod, List<IconTextured> icons)
    {
        if (mod.hasTweakClass())
        {
            icons.add(new IconAbsoluteClickable(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                    I18n.format("gui.mod.providestweak"), 12, 12, 158, 80, 170, 92)
            {
                @Override
                public void onClicked(Object source, Object container)
                {
                    if (container instanceof GuiModListPanel)
                    {
                        ((GuiModListPanel)container).displayModHelpMessage(mod, "gui.mod.providestweak", "gui.mod.help.tweak");
                    }
                }
            });
        }

        if (mod.hasEventTransformers())
        {
            icons.add(new IconAbsoluteClickable(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                    I18n.format("gui.mod.providesevents"), 12, 12, 170, 92, 182, 104)
            {
                @Override
                public void onClicked(Object source, Object container)
                {
                    if (container instanceof GuiModListPanel)
                    {
                        ((GuiModListPanel)container).displayModHelpMessage(mod, "gui.mod.providesevents", "gui.mod.help.events");
                    }
                }
            });
        }

        if (mod.hasClassTransformers())
        {
            icons.add(new IconAbsoluteClickable(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                    I18n.format("gui.mod.providestransformer"), 12, 12, 170, 80, 182, 92)
            {
                @Override
                public void onClicked(Object source, Object container)
                {
                    if (container instanceof GuiModListPanel)
                    {
                        ((GuiModListPanel)container).displayModHelpMessage(mod, "gui.mod.providestransformer", "gui.mod.help.transformer");
                    }
                }
            });
        }

        if (mod.hasMixins())
        {
            icons.add(new IconAbsoluteClickable(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                    I18n.format("gui.mod.providesmixins"), 12, 12, 122, 104, 134, 116)
            {
                @Override
                public void onClicked(Object source, Object container)
                {
                    if (container instanceof GuiModListPanel)
                    {
                        ((GuiModListPanel)container).displayModHelpMessage(mod, "gui.mod.providesmixins", "gui.mod.help.mixins");
                    }
                }
            });
        }
        
        if (mod.usesAPI())
        {
            icons.add(new IconAbsolute(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                    I18n.format("gui.mod.usingapi"), 12, 12, 122, 92, 134, 104));
        }

        List<Throwable> startupErrors = mod.getStartupErrors();
        if (startupErrors != null && startupErrors.size() > 0)
        {
            icons.add(new IconAbsoluteClickable(LiteLoaderBrandingProvider.ABOUT_TEXTURE,
                    I18n.format("gui.mod.startuperror", startupErrors.size()), 12, 12, 134, 92, 146, 104)
            {
                @Override
                public void onClicked(Object source, Object container)
                {
                    if (source instanceof GuiLiteLoaderPanel)
                    {
                        ((GuiLiteLoaderPanel)source).showErrorPanel(mod);
                    }
                }
            });
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ModInfoDecorator
     *      #modifyStatusText(com.mumfrey.liteloader.core.ModInfo,
     *      java.lang.String)
     */
    @Override
    public String modifyStatusText(ModInfo<?> mod, String statusText)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.ModInfoDecorator
     *      #onDrawListEntry(int, int, float, int, int, int, int, boolean,
     *      com.mumfrey.liteloader.core.ModInfo, int, int, int)
     */
    @Override
    public void onDrawListEntry(int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, int height, boolean selected,
            ModInfo<?> mod, int gradientColour, int titleColour, int statusColour)
    {
    }
}
