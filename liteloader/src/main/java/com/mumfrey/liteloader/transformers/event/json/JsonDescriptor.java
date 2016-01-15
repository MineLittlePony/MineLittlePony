package com.mumfrey.liteloader.transformers.event.json;

import java.io.Serializable;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.event.MethodInfo;

/**
 * A JSON method descriptor, 
 *
 * @author Adam Mummery-Smith
 */
public class JsonDescriptor implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Key used to refer to this method descriptor elsewhere
     */
    @SerializedName("id")
    private String key;

    /**
     * Name of the class which owns this method
     */
    @SerializedName("owner")
    private String owner;

    /**
     * Method name
     */
    @SerializedName("name")
    private String name;

    /**
     * Method return type, assumes VOID if none specified
     */
    @SerializedName("return")
    private String returnType;

    /**
     * Argument types for the method
     */
    @SerializedName("args")
    private String[] argumentTypes;

    /**
     * Get the key used to refer to this method descriptor
     */
    public String getKey()
    {
        if (this.key == null)
        {
            this.key = "UserDescriptor" + UUID.randomUUID().toString();
        }

        return this.key;
    }

    /**
     * @param obfTable
     * @return MethodInfo for this descriptor
     */
    public MethodInfo parse(JsonObfuscationTable obfTable)
    {
        if (this.owner == null || this.name == null)
        {
            throw new InvalidEventJsonException("Method descriptor was invalid, must specify owner and name!");
        }

        Obf owner = obfTable.parseClass(this.owner);
        Obf name = obfTable.parseMethod(this.name);

        if (this.argumentTypes == null && this.returnType == null)
        {
            return new MethodInfo(owner, name);
        }

        Object returnType = obfTable.parseType(this.returnType == null ? "VOID" : this.returnType);
        Object[] args = (this.argumentTypes != null ? new Object[this.argumentTypes.length] : new Object[0]);
        if (this.argumentTypes != null)
        {
            for (int arg = 0; arg < this.argumentTypes.length; arg++)
            {
                args[arg] = obfTable.parseType(this.argumentTypes[arg]);
            }
        }

        return new MethodInfo(owner, name, returnType, args);
    }
}
