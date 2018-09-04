package com.voxelmodpack.hdskins.util;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

public class PlayerUtil {

    private static final Field playerInfo = FieldUtils.getAllFields(AbstractClientPlayer.class)[0];

    public static NetworkPlayerInfo getInfo(AbstractClientPlayer player) {
        try {
            if (!playerInfo.isAccessible()) {
                playerInfo.setAccessible(true);
            }
            return (NetworkPlayerInfo) FieldUtils.readField(playerInfo, player);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
