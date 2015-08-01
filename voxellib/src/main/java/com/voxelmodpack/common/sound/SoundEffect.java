package com.voxelmodpack.common.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public class SoundEffect extends PositionedSound {
    public SoundEffect(ResourceLocation soundLocation, float volume, float pitch) {
        super(soundLocation);
        this.volume = volume;
        this.pitch = pitch;
        this.xPosF = 0.0F;
        this.yPosF = 0.0F;
        this.zPosF = 0.0F;
        this.repeat = false;
        this.repeatDelay = 0;
        this.attenuationType = ISound.AttenuationType.NONE;
    }
}
