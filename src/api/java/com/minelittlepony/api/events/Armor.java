package com.minelittlepony.api.events;

import java.util.ArrayList;
import java.util.List;

import com.minelittlepony.model.capabilities.IModelArmor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;

class Armor implements EventBus<IPreArmorEventHandler>, IPreArmorEventHandler {

    private final List<IPreArmorEventHandler> handlers = new ArrayList<IPreArmorEventHandler>();

    @Override
    public IPreArmorEventHandler dispatcher() {
        return this;
    }

    public void addEventListener(IPreArmorEventHandler handler) {
        if (handler == this) return;

        handlers.add(handler);
    }

    private boolean cancel = false;

    @Override
    public <T extends ModelBiped & IModelArmor> boolean preRenderPonyArmor(T armour, Entity entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EntityEquipmentSlot armourSlot) {
        if (handlers.size() == 0) return false;

        handlers.forEach(handler -> {
           cancel |= handler.preRenderPonyArmor(armour, entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, armourSlot);
        });

        return cancel;
    }

    @Override
    public <T extends ModelBiped & IModelArmor> void postRenderPonyArmor(T armour, Entity entity, float move, float swing, float partialTicks, float ticks, float headYaw, float headPitch, float scale, EntityEquipmentSlot armourSlot) {
        if (handlers.size() == 0) return;

        handlers.forEach(handler -> {
           handler.postRenderPonyArmor(armour, entity, move, swing, partialTicks, ticks, headYaw, headPitch, scale, armourSlot);
        });
    }
}
