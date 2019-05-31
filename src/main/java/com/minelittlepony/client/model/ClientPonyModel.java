package com.minelittlepony.client.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.model.IPonyModel;

import java.util.Random;

public abstract class ClientPonyModel<T extends LivingEntity> extends PlayerEntityModel<T> implements IPonyModel<T> {

    public ClientPonyModel(float float_1, boolean boolean_1) {
        super(float_1, boolean_1);
    }

    @Override
    public Cuboid getRandomCuboid(Random rand) {
        // grab one at random, but cycle through the list until you find one that's filled.
        // Return if you find one, or if you get back to where you started in which case there isn't any.

        int randomI = rand.nextInt(cuboidList.size());
        int index = randomI;

        Cuboid result;
        do {
            result = cuboidList.get(randomI);
            if (!result.boxes.isEmpty()) return result;

            index = (index + 1) % cuboidList.size();
        } while (index != randomI);

        if (result.boxes.isEmpty()) {
            result.addBox(0, 0, 0, 0, 0, 0);
        }

        if (result.boxes.isEmpty()) {
            throw new IllegalStateException("This model contains absolutely no boxes and a box could not be added!");
        }

        return result;
    }
}
