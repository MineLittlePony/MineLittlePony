package com.minelittlepony.api.events;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.minelittlepony.api.pony.PonyData;

public record MsgPonyData(short apiIdentifier, int apiVersion, PonyData data) {
    private static final short API_IDENTIFIER = (short) 0xABCD;
    // API version - increment this number before any time any data is added/removed/moved in the data stream
    private static final byte API_VERSION = 4;

    public static final StreamCodec<FriendlyByteBuf, MsgPonyData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.SHORT, MsgPonyData::apiIdentifier,
            ByteBufCodecs.INT, MsgPonyData::apiVersion,
            PonyData.STREAM_CODEC, MsgPonyData::data,
            MsgPonyData::new
    );

    public MsgPonyData(PonyData data) {
        this(API_IDENTIFIER, API_VERSION, data);
    }
}
