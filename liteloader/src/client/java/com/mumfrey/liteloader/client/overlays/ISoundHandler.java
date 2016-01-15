package com.mumfrey.liteloader.client.overlays;

import net.minecraft.client.audio.SoundList;
import net.minecraft.util.ResourceLocation;

import com.mumfrey.liteloader.transformers.access.Accessor;
import com.mumfrey.liteloader.transformers.access.Invoker;

@Accessor("SoundHandler")
public interface ISoundHandler
{
    @Invoker("loadSoundResource")
    public abstract void addSound(ResourceLocation sound, SoundList soundList);
}
