package com.mumfrey.liteloader.debug;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.JOptionPane;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * Manages login requests against Yggdrasil for use in MCP
 *
 * @author Adam Mummery-Smith
 */
public class LoginManager
{
    /**
     * Gson instance for serialising and deserialising the authentication data
     */
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Authentication service
     */
    private YggdrasilAuthenticationService authService;

    /**
     * Authentication agent
     */
    private YggdrasilUserAuthentication authentication;

    /**
     * JSON file to load/save auth data from
     */
    private File jsonFile;

    /**
     * Username read from the auth JSON file, we use this as the default in the
     * login dialog in case login fails. This is stored in the JSON even if
     * authentication is not successful so that we can display the same username
     * next time.
     */
    private String defaultUsername;

    /**
     * Minecraft screen name read from the auth JSON file. Use this as default
     * in case the login fails or is skipped (when offline) so that at least the
     * Minecraft client has a sensible display name.
     * 
     * <p>Defaults to user.name when not specified</p>
     */
    private String defaultDisplayName = System.getProperty("user.name");

    /**
     * True if login should not be attempted, skips the authentication attempt
     * and the login dialog.
     */
    private boolean offline = false;

    /**
     * If authentication fails with token then the first attempt will be to use
     * the user/pass specified on the command line (if any). This flag is set
     * <b>after</b> that first attempt so that we know to display the login
     * dialog anyway (eg. the login on the command line was bad).
     */
    private boolean forceShowLoginDialog = false;

    /**
     * ctor
     * 
     * @param jsonFile
     */
    public LoginManager(File jsonFile)
    {
        this.jsonFile = jsonFile;

        this.resetAuth();
        this.load();
    }

    /**
     * When authenticaion fails, we regenerate the auth service and agent
     * because trying again with the same client data will fail.
     */
    public void resetAuth()
    {
        this.authService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        this.authentication = new YggdrasilUserAuthentication(this.authService, Agent.MINECRAFT);
    }

    /**
     * Load auth data from the json file 
     */
    private void load()
    {
        if (this.jsonFile != null && this.jsonFile.exists())
        {
            FileReader fileReader = null;

            try
            {
                fileReader = new FileReader(this.jsonFile);
                AuthData authData = LoginManager.gson.fromJson(fileReader, AuthData.class);

                if (authData != null && authData.validate())
                {
                    LiteLoaderLogger.info("Initialising Yggdrasil authentication service with client token: %s", authData.getClientToken());
                    this.authService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, authData.getClientToken());
                    this.authentication = new YggdrasilUserAuthentication(this.authService, Agent.MINECRAFT);
                    authData.loadFromStorage(this.authentication);
                    this.offline = authData.workOffline();
                    this.defaultUsername = authData.getUsername();
                    this.defaultDisplayName = authData.getDisplayName();
                }
            }
            catch (IOException ex) {}
            finally
            {
                try
                {
                    if (fileReader != null) fileReader.close();
                }
                catch (IOException ex) {}
            }
        }
    }

    /**
     * Save auth data to the JSON file
     */
    private void save()
    {
        if (this.jsonFile != null)
        {
            FileWriter fileWriter = null;

            try
            {
                fileWriter = new FileWriter(this.jsonFile);

                AuthData authData = new AuthData(this.authService, this.authentication, this.offline, this.defaultUsername, this.defaultDisplayName);
                LoginManager.gson.toJson(authData, fileWriter);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                try
                {
                    if (fileWriter != null) fileWriter.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Attempt to login. If authentication data are found on disk then tries
     * first to log in with the stored token. If the token login fails then
     * attempts to log in with the username and password specified. If no user
     * or pass are specified or if they fail then displays a login dialog to
     * allow the user to login. If login succeeds then the token is stored on
     * disk and the method returns.
     * 
     * <p>If the user presses cancel in the login dialog then the method returns
     * false.</p>
     * 
     * @param username User name to log in with if token login fails, if null
     *      displays the login dialog immediately
     * @param password Password to log in with if token login fails, if null
     *      displays the login dialog immediately
     * @param remainingTries Number of loops to go through before giving up,
     *      decremented for each try, specify -1 for unlimited
     * @return false if the user presses cancel in the login dialog, otherwise
     *      returns true
     */
    public boolean login(String username, String password, int remainingTries)
    {
        if (this.offline || remainingTries == 0)
        {
            LiteLoaderLogger.info("LoginManager is set to work offline, skipping login");
            return false;
        }

        LiteLoaderLogger.info("Remaining login tries: %s", remainingTries > 0 ? remainingTries : "unlimited");

        try
        {
            LiteLoaderLogger.info("Attempting login, contacting Mojang auth servers...");

            this.authentication.logIn();

            if (this.authentication.isLoggedIn())
            {
                LiteLoaderLogger.info("LoginManager logged in successfully. Can play online = %s", this.authentication.canPlayOnline());
                this.save();
                return true;
            }

            LiteLoaderLogger.info("LoginManager failed to log in, unspecified status.");
        }
        catch (InvalidCredentialsException ex)
        {
            LiteLoaderLogger.info("Authentication agent reported invalid credentials: %s", ex.getMessage());
            this.resetAuth();

            if (remainingTries > 1)
            {
                if (username == null)
                {
                    username = this.defaultUsername;
                }

                if (this.forceShowLoginDialog || username == null || password == null)
                {
                    LoginPanel loginPanel = LoginPanel.getLoginPanel(username, password, this.forceShowLoginDialog ? ex.getMessage() : null);
                    boolean dialogResult = loginPanel.showModalDialog();
                    this.offline = loginPanel.workOffline();
                    this.defaultUsername = loginPanel.getUsername();

                    if (!dialogResult)
                    {
                        LiteLoaderLogger.info("User cancelled login dialog");
                        return false;
                    }

                    if (this.offline)
                    {
                        if (JOptionPane.showConfirmDialog(null, "<html>You have chosen to work offline. "
                                + "You will never be prompted to log in again.<br /><br />"
                                + "If you would like to re-enable login please delete the file <span style=\"color: #0000FF\">.auth.json</span> "
                                + "from the working dir<br />"
                                + "or press Cancel to return to the login dialog.</html>",
                                "Confirm work offline",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.INFORMATION_MESSAGE) == JOptionPane.CANCEL_OPTION)
                        {
                            this.offline = false;
                            remainingTries = Math.max(remainingTries, 3);
                        }
                    }

                    username = loginPanel.getUsername();
                    password = loginPanel.getPassword();
                    this.save();
                }

                if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password))
                {
                    this.authentication.setUsername(username);
                    this.authentication.setPassword(password);
                }

                this.forceShowLoginDialog = true;
                this.login(username, password, --remainingTries);
            }
        }
        catch (AuthenticationException ex)
        {
            ex.printStackTrace();
        }

        this.save();
        return false;
    }

    /**
     * Get whether user logged in
     */
    public boolean isLoggedIn()
    {
        return this.authentication.isLoggedIn();
    }

    /**
     * Get whether we are able to play online or not
     */
    public boolean canPlayOnline()
    {
        return this.authentication.canPlayOnline();
    }

    /**
     * Get the profile name (minecraft player name) from login
     */
    public String getProfileName()
    {
        GameProfile selectedProfile = this.authentication.getSelectedProfile();
        return selectedProfile != null ? selectedProfile.getName() : this.defaultDisplayName;
    }

    /**
     * Get the profile name (minecraft player name) from login
     */
    public String getUUID()
    {
        GameProfile selectedProfile = this.authentication.getSelectedProfile();
        return selectedProfile != null ? selectedProfile.getId().toString().replace("-", "") : this.defaultDisplayName;
    }

    /**
     * Get the session token
     */
    public String getAuthenticatedToken()
    {
        String accessToken = this.authentication.getAuthenticatedToken();
        return accessToken != null ? accessToken : "-";
    }

    public String getUserType()
    {
        UserType userType = this.authentication.getUserType();
        return (userType != null ? userType : UserType.LEGACY).toString().toLowerCase();
    }

    public String getUserProperties()
    {
        PropertyMap userProperties = this.authentication.getUserProperties();
        return userProperties != null ? (new GsonBuilder()).registerTypeAdapter(PropertyMap.class,
                new UserPropertiesSerializer()).create().toJson(userProperties) : "{}";
    }

    class UserPropertiesSerializer implements JsonSerializer<PropertyMap>
    {
        @Override
        public JsonElement serialize(PropertyMap propertyMap, Type argType, JsonSerializationContext context)
        {
            JsonObject result = new JsonObject();

            for (String key : propertyMap.keySet())
            {
                JsonArray values = new JsonArray();
                for (Property property : propertyMap.get(key))
                {
                    values.add(new JsonPrimitive(property.getValue()));
                }

                result.add(key, values);
            }

            return result;
        }
    }


    /**
     * Struct for Gson serialisation of authenticaion settings
     * 
     * @author Adam Mummery-Smith
     */
    class AuthData
    {
        @SerializedName("clientToken")
        private String clientToken;

        @SerializedName("workOffline")
        private boolean workOffline;

        @SerializedName("authData")
        private Map<String, Object> credentials;

        public AuthData()
        {
            // default ctor for Gson
        }

        public AuthData(YggdrasilAuthenticationService authService, YggdrasilUserAuthentication authentication, boolean workOffline,
                String defaultUserName, String defaultDisplayName)
        {
            this.clientToken = authService.getClientToken();
            this.credentials = authentication.saveForStorage();
            this.workOffline = workOffline;

            if (defaultUserName != null && !this.credentials.containsKey("username"))
            {
                this.credentials.put("username", defaultUserName);
            }

            if (defaultDisplayName != null && !this.credentials.containsKey("displayName"))
            {
                this.credentials.put("displayName", defaultDisplayName);
            }
        }

        /**
         * Called after Gson deserialisation to check that deserialisation was
         * successful.
         */
        public boolean validate()
        {
            if (this.clientToken == null) this.clientToken = UUID.randomUUID().toString();
            if (this.credentials == null) this.credentials = new HashMap<String, Object>();
            return true;
        }

        public String getClientToken()
        {
            return this.clientToken;
        }

        public void setClientToken(String clientToken)
        {
            this.clientToken = clientToken;
        }

        public void loadFromStorage(YggdrasilUserAuthentication authentication)
        {
            authentication.loadFromStorage(this.credentials);
        }

        public boolean workOffline()
        {
            return this.workOffline;
        }

        public String getUsername()
        {
            return this.credentials != null ? this.credentials.get("username").toString() : null;
        }

        public String getDisplayName()
        {
            return this.credentials != null && this.credentials.containsKey("displayName") 
                    ? this.credentials.get("displayName").toString() : System.getProperty("user.name");
        }
    }
}
