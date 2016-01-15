package com.mumfrey.liteloader.transformers.event.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ObfProvider;

/**
 * Serialisable class which represents a set of event injection definitions.
 * Instances of this class are created by deserialising with JSON. The JSON
 * string should be passed to the static {@link #parse} method which returns an
 * instance of the class.
 * 
 * <p>After parsing, the events defined here can be injected into an event
 * transformer instance by calling the {@link #register} method.</p>
 * 
 * @author Adam Mummery-Smith
 */
public class JsonEvents implements Serializable, ObfProvider
{
    private static final long serialVersionUID = 1L;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Tokens are an instruction to the parser to look up a value rather than
     * using a literal.
     */
    private static final Pattern tokenPattern = Pattern.compile("^\\$\\{([a-zA-Z0-9_\\-\\.\\$]+)\\}$");

    /**
     * Serialised obfusctation entries
     */
    @SerializedName("obfuscation")
    private JsonObfuscationTable obfuscation;

    /**
     * Serialised method descriptors
     */
    @SerializedName("descriptors")
    private List<JsonDescriptor> descriptors;

    /**
     * Serialised events
     */
    @SerializedName("events")
    private List<JsonEvent> events;

    /**
     * List of accessor interfaces 
     */
    @SerializedName("accessors")
    private List<String> accessors;

    /**
     * Parsed method descriptors 
     */
    private transient JsonMethods methods;

    /**
     * Parsed accessors
     */
    private transient List<String> accessorInterfaces = new ArrayList<String>();

    /**
     * Attempts to parse the information in this object
     */
    private void parse()
    {
        if (this.obfuscation == null)
        {
            this.obfuscation = new JsonObfuscationTable();
        }

        try
        {
            // Parse the obfuscation table
            this.obfuscation.parse();

            // Parse the descriptor list
            this.methods = new JsonMethods(this.obfuscation, this.descriptors);

            if (this.events != null)
            {
                // Parse the events
                for (JsonEvent event : this.events)
                {
                    event.parse(this.methods);
                }
            }

            if (this.accessors != null)
            {
                for (String accessor : this.accessors)
                {
                    if (accessor != null)
                    {
                        Obf accessorName = this.obfuscation.parseClass(accessor);
                        this.accessorInterfaces.add(accessorName.name);
                    }
                }
            }
        }
        catch (InvalidEventJsonException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new InvalidEventJsonException("An error occurred whilst parsing the event definition: " + ex.getClass().getSimpleName()
                    + ": " + ex.getMessage(), ex);
        }
    }

    public boolean hasAccessors()
    {
        return this.accessorInterfaces.size() > 0;
    }

    /**
     * Parse a token name, returns the token name as a string if the token is
     * valid, or null if the token is not valid
     * 
     * @param token
     */
    static String parseToken(String token)
    {
        token = token.replace(" ", "").trim();

        Matcher tokenPatternMatcher = JsonEvents.tokenPattern.matcher(token);
        if (tokenPatternMatcher.matches())
        {
            return tokenPatternMatcher.group(1);
        }

        return null;
    }

    /**
     * Called to register all events defined in this object into the specified
     * transformer.
     * 
     * @param transformer
     */
    public void register(ModEventInjectionTransformer transformer)
    {
        for (JsonEvent event : this.events)
        {
            event.register(transformer);
        }

        for (String interfaceName : this.accessorInterfaces)
        {
            transformer.registerAccessor(interfaceName, this);
        }
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.transformers.ObfProvider
     *      #getByName(java.lang.String)
     */
    @Override
    public Obf getByName(String name)
    {
        return this.obfuscation.getByName(name);
    }

//    public String toJson()
//    {
//        return JsonEvents.gson.toJson(this);
//    }

    /**
     * Parse a new JsonEvents object from the supplied JSON string
     * 
     * @param json
     * @return new JsonEvents instance
     * @throws InvalidEventJsonException if the JSON ins invalid
     */
    public static JsonEvents parse(String json) throws InvalidEventJsonException
    {
        try
        {
            JsonEvents newJsonEvents = JsonEvents.gson.fromJson(json, JsonEvents.class);
            newJsonEvents.parse();
            return newJsonEvents;
        }
        catch (InvalidEventJsonException ex)
        {
            throw ex;
        }
        catch (Throwable th)
        {
            throw new InvalidEventJsonException("An error occurred whilst parsing the event definition: " + th.getClass().getSimpleName()
                    + ": " + th.getMessage(), th);
        }
    }
}
