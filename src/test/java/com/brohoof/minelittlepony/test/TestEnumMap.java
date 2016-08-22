package com.brohoof.minelittlepony.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.brohoof.minelittlepony.PonyManager;

import net.minecraft.entity.monster.ZombieType;

public class TestEnumMap {

    @Test
    public void testMapCompletion() {
        int size = PonyManager.ZOMBIES.size();

        assertEquals(size, ZombieType.values().length);
    }

}
