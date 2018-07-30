package com.minelittlepony.settings;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.minelittlepony.gui.IGuiCallback;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.Exposable;

/**
 * A sensible config container that actually lets us programatically index values by a key.
 *
 */
// Mumfrey pls.
public abstract class ValueConfig implements Exposable {

    // TODO: Mumfrey pls. ValueConfig cannot extend from HashMap directly.
    @Expose
    private Map<String, Object> properties = new HashMap<String, Object>();

    protected void write() {
        LiteLoader.getInstance().writeConfig(this);
    }

    public interface Flag extends Setting<Boolean> {
        default Boolean initial() {
            return true;
        }
    }

    public interface Setting<T> extends IGuiCallback<T> {

        ValueConfig config();

        String name();

        T initial();

        default String key() {
            return name().toLowerCase();
        }

        @SuppressWarnings("unchecked")
        default T get() {
            if (!config().properties.containsKey(key())) {
                set(initial());
            }
            return (T)config().properties.get(key());
        }

        default void set(T value) {
            config().properties.put(key(), value);
            config().write();
        }

        @Override
        default T perform(T in) {
            set(in);
            return get();
        }
    }
}
