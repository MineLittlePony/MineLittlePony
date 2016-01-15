package com.mumfrey.liteloader.transformers.event.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mumfrey.liteloader.core.runtime.Methods;
import com.mumfrey.liteloader.transformers.event.MethodInfo;

/**
 * A simple registry of MethodInfo objects parsed from the JSON, objects which
 * consume the specified MethodInfo objects will be passed an instance of this
 * object at parse time.
 * 
 * @author Adam Mummery-Smith
 */
public class JsonMethods
{
    /**
     * Serialised obfusctation entries
     */
    private final JsonObfuscationTable obfuscation;

    /**
     * Method descriptors
     */
    private final List<JsonDescriptor> descriptors;

    /**
     * Method descriptors which have been parsed from the descriptors collection
     */
    private Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();

    /**
     * @param obfuscation
     * @param descriptors
     */
    public JsonMethods(JsonObfuscationTable obfuscation, List<JsonDescriptor> descriptors)
    {
        this.obfuscation = obfuscation;
        this.descriptors = descriptors;

        this.parse();
    }

    /**
     * 
     */
    private void parse()
    {
        if (this.descriptors != null)
        {
            for (JsonDescriptor descriptor : this.descriptors)
            {
                this.methods.put(descriptor.getKey(), descriptor.parse(this.obfuscation));
            }
        }
    }

    /**
     * Fetches a method descriptor by token
     * 
     * @param token
     */
    public MethodInfo get(String token)
    {
        String key = JsonEvents.parseToken(token);
        if (key == null)
        {
            throw new InvalidEventJsonException("\"" + token + "\" is not a valid token");
        }

        MethodInfo method = this.methods.get(key);
        if (method != null)
        {
            return method;
        }

        MethodInfo builtinMethod = Methods.getByName(key);
        if (builtinMethod != null)
        {
            return builtinMethod;
        }

        throw new InvalidEventJsonException("Could not locate method descriptor with token " + token);
    }
}