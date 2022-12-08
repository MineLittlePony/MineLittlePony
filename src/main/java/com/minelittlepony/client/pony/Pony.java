package com.minelittlepony.client.pony;

import com.google.common.base.MoreObjects;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.network.MsgPonyData;
import com.minelittlepony.settings.PonyConfig;

import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class Pony implements IPony {
    private final Identifier texture;
    private final Memoize<IPonyData> metadata;

    private boolean defaulted;

    Pony(Identifier resource, Memoize<IPonyData> data) {
        texture = resource;
        metadata = data;
    }

    Pony(Identifier resource) {
        this(resource, PonyData.parse(resource));
    }

    public IPony markDefaulted() {
        defaulted = true;
        return this;
    }

    @Override
    public boolean defaulted() {
        return defaulted;
    }

    @Override
    public boolean hasMetadata() {
        return metadata.isPresent();
    }

    @Override
    public Race race() {
        return PonyConfig.getEffectiveRace(metadata().getRace());
    }

    @Override
    public Identifier texture() {
        return texture;
    }

    @Override
    public IPonyData metadata() {
        return metadata.get(PonyData.NULL);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("texture", texture)
                .add("metadata", metadata)
                .add("defaulted", defaulted)
                .toString();
    }

    public interface RegistrationHandler {
        boolean shouldUpdateRegistration(IPony pony);
    }

    public static IPony snapshot(IPony pony) {
        return new Pony(pony.texture(), Memoize.of(new MsgPonyData(pony.metadata(), pony.defaulted())));
    }
}
