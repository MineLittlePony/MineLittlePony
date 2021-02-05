package com.minelittlepony.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

import com.minelittlepony.model.fabric.PonyModelPrepareCallback;
import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.client.pony.PonyData;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.model.ModelAttributes;
import com.minelittlepony.mson.api.model.biped.MsonPlayer;

/**
 * The raw pony model without any implementations.
 * Will act effectively the same as a normal player model without any hints
 * of being cute and adorable.
 *
 * Modders can extend this class to make their own pony models if they wish.
 */
public abstract class ClientPonyModel<T extends LivingEntity> extends MsonPlayer<T> implements IPonyModel<T> {

    /**
     * The model attributes.
     */
    protected ModelAttributes<T> attributes = new ModelAttributes<>();

    /**
     * Associated pony data.
     */
    protected IPonyData metadata = PonyData.NULL;

    public ClientPonyModel(ModelPart tree) {
        super(tree);
    }

    protected Arm getPreferredArm(T livingEntity) {
        Arm arm = livingEntity.getMainArm();
        return livingEntity.preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
    }

    @Override
    public void updateLivingState(T entity, IPony pony, EquineRenderManager.Mode mode) {
        child = entity.isBaby();
        attributes.updateLivingState(entity, pony, mode);
        PonyModelPrepareCallback.EVENT.invoker().onPonyModelPrepared(entity, this, mode);
        sneaking = attributes.isCrouching;
        riding = attributes.isSitting;
    }

    @Override
    public void copyAttributes(BipedEntityModel<T> other) {
        setAttributes(other);
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
        return child ? Sizes.FOAL : getMetadata().getSize();
    }

    @Override
    public void apply(IPonyData meta) {
        metadata = meta;
    }

    @Override
    public ModelPart getHead() {
        return head;
    }

    @Override
    public boolean isRiding() {
        return riding;
    }

    @Override
    public float getSwingAmount() {
        return handSwingProgress;
    }


    @Override
    public ModelPart getArm(Arm side) {
        return super.getArm(side);
    }

    public ArmPose getArmPoseForSide(Arm side) {
        return side == Arm.RIGHT ? rightArmPose : leftArmPose;
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
}
