package com.brohoof.minelittlepony;

import com.voxelmodpack.common.VoxelCommonLiteMod;

public class LiteModMineLittlePony extends VoxelCommonLiteMod {

    public LiteModMineLittlePony() {
        super("com.brohoof.minelittlepony.MineLittlePony");
    }

    @Override
    public String getVersion() {
        return MineLittlePony.MOD_VERSION;
    }

    @Override
    public String getName() {
        return MineLittlePony.MOD_NAME;
    }
}
