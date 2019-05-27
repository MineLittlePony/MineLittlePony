package com.minelittlepony.util.resources;

import net.minecraft.village.VillagerData;

import java.util.function.Function;

public class ProfessionStringMapper implements Function<VillagerData, String> {

    @Override
    public String apply(VillagerData t) {
        return String.format("level_%d_%s", t.getLevel(), t.getProfession().toString());
    }

}
