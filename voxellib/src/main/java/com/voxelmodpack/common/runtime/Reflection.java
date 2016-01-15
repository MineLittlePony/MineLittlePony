package com.voxelmodpack.common.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mumfrey.liteloader.util.ObfuscationUtilities;

public class Reflection {
    private static Field MODIFIERS = null;

    static {
        try {
            Reflection.MODIFIERS = (java.lang.reflect.Field.class).getDeclaredField("modifiers");
            Reflection.MODIFIERS.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * ModLoader function, set a private value on the specified object
     *
     * @param instanceClass Class which the member belongs to (may be a
     *            superclass of the actual instance object's class)
     * @param instance Object instance to set the value in
     * @param fieldName Name of the field to set
     * @param value Value to set for the field
     */
    public static void setPrivateValue(Class<?> instanceClass, Object instance, String fieldName,
            String obfuscatedFieldName, String seargeName, Object value)
                    throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        Reflection.setPrivateValueRaw(instanceClass, instance,
                ObfuscationUtilities.getObfuscatedFieldName(fieldName, obfuscatedFieldName, seargeName), value);
    }

    /**
     * ModLoader function, set a private value on the specified object
     *
     * @param instanceClass Class which the member belongs to (may be a
     *            superclass of the actual instance object's class)
     * @param instance Object instance to set the value in
     * @param fieldName Name of the field to set
     * @param value Value to set for the field
     */
    public static void setPrivateValue(Class<?> instanceClass, Object instance, String fieldName, Object value)
            throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        Reflection.setPrivateValueRaw(instanceClass, instance, fieldName, value);
    }

    /**
     * ModLoader function, get a private value from the specified object
     *
     * @param instanceClass Class which the member belongs to (may be a
     *            superclass of the actual instance object's class)
     * @param instance Object instance to get the value from
     * @param fieldName Name of the field to get the value of
     * @return Value of the field
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPrivateValue(Class<?> instanceClass, Object instance, String fieldName,
            String obfuscatedFieldName, String seargeName)
                    throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        return (T) Reflection.getPrivateValueRaw(instanceClass, instance,
                ObfuscationUtilities.getObfuscatedFieldName(fieldName, obfuscatedFieldName, seargeName));
    }

    /**
     * ModLoader function, get a private value from the specified object
     *
     * @param instanceClass Class which the member belongs to (may be a
     *            superclass of the actual instance object's class)
     * @param instance Object instance to get the value from
     * @param fieldName Name of the field to get the value of
     * @return Value of the field
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPrivateValue(Class<?> instanceClass, Object instance, String fieldName)
            throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        return (T) Reflection.getPrivateValueRaw(instanceClass, instance, fieldName);
    }

    private static void setPrivateValueRaw(Class<?> instanceClass, Object instance, String fieldName, Object value)
            throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        try {
            Field field = instanceClass.getDeclaredField(fieldName);
            int modifiers = Reflection.MODIFIERS.getInt(field);

            if ((modifiers & Modifier.FINAL) != 0) {
                Reflection.MODIFIERS.setInt(field, modifiers & 0xffffffef);
            }

            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException illegalaccessexception) {}
    }

    public static Object getPrivateValueRaw(Class<?> instanceClass, Object instance, String fieldName)
            throws IllegalArgumentException, SecurityException, NoSuchFieldException {
        try {
            Field field = instanceClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException illegalaccessexception) {
            return null;
        }
    }
}
