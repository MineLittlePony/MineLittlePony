package com.minelittlepony.api.events;

public interface EventBus<T> {

    public static final EventBus<IPreArmorEventHandler> ARMOR = new Armor();

    T dispatcher();

    void addEventListener(T handler);
}
