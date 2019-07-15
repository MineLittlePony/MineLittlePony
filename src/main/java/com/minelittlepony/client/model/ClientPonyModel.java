package com.minelittlepony.client.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.AbsoluteHand;

import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.model.ModelAttributes;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.IPonyData;
import com.minelittlepony.pony.meta.Size;

import java.util.Random;

/**
 * The raw pony model without any implementations.
 * Will act effectively the same as a normal player model without any hints
 * of being cute and adorable.
 *
 * Modders can extend this class to make their own pony models if they wish.
 */
public abstract class ClientPonyModel<T extends LivingEntity> extends PlayerEntityModel<T> implements IPonyModel<T> {

    /**
     * The model attributes.
     */
    protected ModelAttributes<T> attributes = new ModelAttributes<>();

    /**
     * Associated pony data.
     */
    protected IPonyData metadata = new PonyData();

    public ClientPonyModel(float float_1, boolean boolean_1) {
        super(float_1, boolean_1);
    }

    @Override
    public void updateLivingState(T entity, IPony pony) {
        isChild = entity.isBaby();
        isSneaking = entity.isInSneakingPose();
        attributes.updateLivingState(entity, pony);
    }

    @Override
    public ModelAttributes<?> getAttributes() {
        return attributes;
    }

    @Override
    public IPonyData getMetadata() {
        return metadata;
    }

    @Override
    public Size getSize() {
        return isChild ? Size.FOAL : getMetadata().getSize();
    }

    @Override
    public void apply(IPonyData meta) {
        metadata = meta;
    }

    @Override
    public Cuboid getHead() {
        return head;
    }

    @Override
    public boolean isRiding() {
        return isRiding;
    }

    @Override
    public float getSwingAmount() {
        return handSwingProgress;
    }


    @Override
    public Cuboid getArm(AbsoluteHand side) {
        return super.getArm(side);
    }

    /**
     * Copies this model's attributes into the passed model.
     */
    @Override
    public void setAttributes(BipedEntityModel<T> model) {
        super.setAttributes(model);

        if (model instanceof ClientPonyModel) {
            ((ClientPonyModel<T>)model).attributes = attributes;
            ((ClientPonyModel<T>)model).metadata = metadata;
        }
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
