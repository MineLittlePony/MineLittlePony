package com.brohoof.minelittlepony.util;

import com.mumfrey.liteloader.core.runtime.Obf;

public class MineLPObf extends Obf {

    public static final MineLPObf isJumping = new MineLPObf("field_70703_bu", "aW", "isJumping");
    public static final MineLPObf ARMOR_TEXTURE_RES_MAP = new MineLPObf("field_177191_j", "j", "ARMOR_TEXTURE_RES_MAP");

    protected MineLPObf(String seargeName, String obfName, String mcpName) {
        super(seargeName, obfName, mcpName);
    }

    protected MineLPObf(String seargeName, String obfName) {
        super(seargeName, obfName, seargeName);
    }
}
