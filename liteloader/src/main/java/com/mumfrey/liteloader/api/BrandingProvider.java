package com.mumfrey.liteloader.api;

import java.net.URI;

import net.minecraft.util.ResourceLocation;

import com.mumfrey.liteloader.util.render.Icon;

/**
 * LiteLoader Extensible API - Branding Provider
 * 
 * <p>The Branding Provider manages loader branding alterations for a particular
 * API. This is an optional API component which allows an API to specify
 * customisations and additions to the loader environment in order to provide a
 * more comfortable integration for the API.</p>
 * 
 * <p>Since some branding options simply stack (like the API copyright notices
 * for example) all APIs will be allowed to supply this information, however
 * other options (like the main logo image) can only be set by one API. To
 * determine which API should be used to set this information, the getPriority()
 * method will be called and used to sort branding providers by priority, with
 * highest priority winning.</p>
 * 
 * <p>All branding options may return a null/not set value, allowing a branding
 * provider to only override the branding features it wishes. Some options
 * require a non-null value to be returned from a set of methods in order to
 * take effect. eg. the logo option requires non-null return values from BOTH
 * the getLogoResource() and getLogoCoords() methods.</p>
 * 
 * @author Adam Mummery-Smith
 */
public interface BrandingProvider extends CustomisationProvider
{
    /**
     * Get the priority of this provider, higher numbers take precedence. Some
     * brandings can only be set by one provider (eg. the main "about" logo) so
     * the branding provider with the highest priority will be the one which
     * gets control of that feature.
     */
    public abstract int getPriority();

    /**
     * Get the primary branding colour for this API, the branding provider
     * should return 0 if it does not wish to override the branding colour. The
     * branding colour is used for the mod list entries and hyper-links within
     * the about GUI panels, the colour returned should be fully opaque.
     */
    public abstract int getBrandingColour();

    /**
     * Get the resource to use for the main logo, the API with the highest
     * priority gets to define the logo, this method can return null if this API
     * does not want to override the logo.
     */
    public abstract ResourceLocation getLogoResource();

    /**
     * Gets the coordinates of the logo as an IIcon instance, only called if
     * getLogoResource() returns a non-null value and the logo will only be used
     * if BOTH methods return a valid object.
     */
    public abstract Icon getLogoCoords();

    /**
     * Get the resource to use for the icon logo (the chicken in the default
     * setup), the API with the highest priority gets to define the icon logo,
     * this method can return null if this API does not want to override the
     * icon. 
     */
    public abstract ResourceLocation getIconResource();

    /**
     * Gets the coordinates of the icon logo as an IIcon instance, only called
     * if getIconResource() returns a non-null value and the icon will only be
     * used if BOTH methods return a valid object.
     */
    public abstract Icon getIconCoords();

    /**
     * Get the display name for this API, used on the "about" screen, must not
     * return null.
     */
    public abstract String getDisplayName();

    /**
     * Get the copyright text for this API, used on the "about" screen, must not
     * return null.
     */
    public abstract String getCopyrightText();

    /**
     * Get the main home page URL for this API, used on the "about" screen, must
     * not return null.
     */
    public abstract URI getHomepage();

    /**
     * If you wish to display a clickable twitter icon next to the API
     * information in the "about" panel then you must return values from this
     * method as well as getTwitterAvatarResource() and
     * getTwitterAvatarCoords(). Return the twitter user name here.
     */
    public abstract String getTwitterUserName();

    /**
     * If you wish to display a clickable twitter icon next to the API
     * information, return the icon resource here.
     */
    public abstract ResourceLocation getTwitterAvatarResource();

    /**
     * If you wish to display a clickable twitter icon next to the API
     * information, return the icon coordinates here.
     */
    public abstract Icon getTwitterAvatarCoords();
}
