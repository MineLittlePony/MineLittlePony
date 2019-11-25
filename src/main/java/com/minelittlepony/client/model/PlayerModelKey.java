package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.util.Identifier;

import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.mson.api.MsonModel;

import java.util.function.Function;

public class PlayerModelKey<T extends Model & MsonModel> {

    private final ModelKey<T> key;

    private boolean slim;

    public final ModelKey<T> steveKey;
    public final ModelKey<T> alexKey;

    PlayerModelKey(Identifier id, Function<Boolean, T> factory) {
        this.key = Mson.getInstance().registerModel(id, () -> factory.apply(slim));

        steveKey = new Key(false);
        alexKey = new Key(true);
    }

    public T createModel(boolean slimArms) {
        return (slimArms ? alexKey : steveKey).createModel();
    }

    private class Key implements ModelKey<T> {

        private final boolean slim;

        public Key(boolean slim) {
            this.slim = slim;
        }

        @Override
        public Identifier getId() {
            return key.getId();
        }

        @Override
        public T createModel() {
            PlayerModelKey.this.slim = this.slim;
            return key.createModel();
        }

    }
}
