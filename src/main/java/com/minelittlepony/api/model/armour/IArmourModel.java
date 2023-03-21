package com.minelittlepony.api.model.armour;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.client.model.IPonyModel;

public interface IArmourModel<T extends LivingEntity> {
    /**
     * Called to synchronise this armour's angles with that of another.
     *
     * @param model The other model to mimic
     */
    boolean poseModel(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch,
            EquipmentSlot slot, ArmourLayer layer,
            IPonyModel<T> mainModel);
}
