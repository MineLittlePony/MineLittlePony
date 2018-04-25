package com.minelittlepony.pony.data;

import net.minecraft.client.resources.data.IMetadataSection;

/**
 * Dummy interface so gson won't go crazy
 */
public interface IPonyData extends IMetadataSection {
    PonyRace getRace();

    TailLengths getTail();

    PonyGender getGender();

    PonySize getSize();

    int getGlowColor();

    boolean hasMagic();
}
