package com.minelittlepony.client.pony;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.network.MsgPonyData;

import net.minecraft.util.Identifier;

public record Pony (
        Identifier texture,
        Memoize<IPonyData> memoizedData,
        boolean defaulted
    ) implements IPony {

    Pony(Identifier resource) {
        this(resource, PonyData.parse(resource), false);
    }

    @Override
    public boolean hasMetadata() {
        return memoizedData.isPresent();
    }

    @Override
    public IPonyData metadata() {
        return memoizedData.get(PonyData.NULL);
    }

    public static IPony snapshot(IPony pony) {
        return new Pony(pony.texture(), Memoize.of(new MsgPonyData(pony.metadata(), pony.defaulted())), pony.defaulted());
    }
}
