package com.voxelmodpack.common.util;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility for opening the users default file/internet browser
 * 
 * @author thatapplefreak
 */
public abstract class BrowserOpener {
    /**
     * Open via url
     */
    public static void openURLinBrowser(URL url) {
        try {
            URI uri = url.toURI();
            Desktop.getDesktop().browse(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open via uri
     */
    public static void openURIinBrowser(URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open via string
     */
    public static void openURLstringInBrowser(String url) {
        try {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
