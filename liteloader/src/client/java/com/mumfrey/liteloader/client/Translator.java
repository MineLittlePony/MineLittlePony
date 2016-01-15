package com.mumfrey.liteloader.client;

import net.minecraft.client.resources.I18n;

import com.mumfrey.liteloader.api.TranslationProvider;

public class Translator implements TranslationProvider
{
    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.TranslationProvider#translate(
     *      java.lang.String, java.lang.Object[])
     */
    @Override
    public String translate(String key, Object... args)
    {
        // TODO doesn't currently honour the contract of TranslationProvider::translate, should return null if translation is missing
        return I18n.format(key, args);
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.api.TranslationProvider#translate(
     *      java.lang.String, java.lang.String, java.lang.Object[])
     */
    @Override
    public String translate(String locale, String key, Object... args)
    {
        // TODO doesn't currently honour the contract of TranslationProvider::translate, should return null if translation is missing
        return I18n.format(key, args);
    }
}
