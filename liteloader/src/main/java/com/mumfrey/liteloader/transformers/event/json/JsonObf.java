package com.mumfrey.liteloader.transformers.event.json;

import java.io.Serializable;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.core.runtime.Obf;

public class JsonObf implements Serializable
{
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private String key;

    @SerializedName("mcp")
    private String mcp;

    @SerializedName("srg")
    private String srg;

    @SerializedName("obf")
    private String obf;

    public String getKey()
    {
        if (this.key == null)
        {
            this.key = "UserObfuscationMapping" + UUID.randomUUID().toString();
        }

        return this.key;
    }

    public Obf parse()
    {
        String seargeName = this.getFirstValidEntry(this.srg, this.mcp, this.obf, this.getKey());
        String obfName = this.getFirstValidEntry(this.obf, this.srg, this.mcp, this.getKey());
        String mcpName = this.getFirstValidEntry(this.mcp, this.srg, this.obf, this.getKey());

        return new Mapping(seargeName, obfName, mcpName);
    }

    private String getFirstValidEntry(String... entries)
    {
        for (String entry : entries)
        {
            if (entry != null) return entry;
        }

        throw new InvalidEventJsonException("No valid entry found in list!");
    }

    public static class Mapping extends Obf
    {
        protected Mapping(String seargeName, String obfName, String mcpName)
        {
            super(seargeName, obfName, mcpName);
        }
    }
}
