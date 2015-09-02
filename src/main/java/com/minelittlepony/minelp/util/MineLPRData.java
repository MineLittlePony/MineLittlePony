package com.minelittlepony.minelp.util;

import com.minelittlepony.minelp.util.MineLPLogger;
import com.minelittlepony.minelp.util.MineLPReflection;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MineLPRData {
    public boolean installed = false;
    public boolean compatible = false;
    public HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
    public HashMap<String, Constructor<?>> constructors = new HashMap<String, Constructor<?>>();
    public HashMap<String, Method> methods = new HashMap<String, Method>();
    public HashMap<String, Field> fields = new HashMap<String, Field>();
    public HashMap<String, Object> objects = new HashMap<String, Object>();

    public Object getInstance(String constructorName, Object... params) {
        try {
            if (this.constructors.containsKey(constructorName)) {
                Constructor<?> e = this.constructors.get(constructorName);
                if (params != null && params.length != 0) {
                    return e.newInstance(params);
                }

                return e.newInstance();
            }

            MineLPLogger.error("Unknown requested constructor \"%s\"", constructorName);
        } catch (Exception var4) {
            var4.printStackTrace();
            MineLPLogger.error("Error when trying to get constructor \"%s\"", constructorName);
        }

        return null;
    }

    public boolean isInstance(String className, Object object) {
        return this.getClass(className).isInstance(object);
    }

    public Object getField(String fieldName, Object object) {
        try {
            if (this.fields.containsKey(fieldName)) {
                Field e = this.fields.get(fieldName);
                return e.get(object);
            }

            MineLPLogger.error("Unknown requested field \"%s\"", fieldName);
        } catch (Exception var4) {
            var4.printStackTrace();
            MineLPLogger.error("Error when trying to get field \"%s\"", fieldName);
        }

        return null;
    }

    public boolean setField(String fieldName, Object object, Object value) {
        try {
            if (this.fields.containsKey(fieldName)) {
                Field e = this.fields.get(fieldName);
                e.set(object, value);
                return true;
            }
            MineLPLogger.error("Unknown requested field \"%s\"", fieldName);
            return false;
        } catch (Exception var5) {
            var5.printStackTrace();
            MineLPLogger.error("Error when trying to set field \"%s\"", fieldName);
            return false;
        }
    }

    public Object invokeMethod(String methodName, Object object, Object... params) {
        try {
            if (this.methods.containsKey(methodName)) {
                Method e = this.methods.get(methodName);
                return params != null ? e.invoke(object, params) : e.invoke(object);
            }
            MineLPLogger.error("Unknown requested method \"%s\"", methodName);
            return null;
        } catch (Exception var5) {
            var5.printStackTrace();
            MineLPLogger.error("Method \"%s\" failed to be invoked", methodName);
            MineLPLogger.error("Types:  " + MineLPReflection.getStringFromTypes(MineLPReflection.getTypesFromObjects(params)));
            MineLPLogger.error("Values: " + params);
            return null;
        }
    }

    public Class<?> getClass(String className) {
        return this.classes.containsKey(className) ? this.classes.get(className) : null;
    }

    public Constructor<?> getConstructor(String constructorName) {
        return this.constructors.containsKey(constructorName) ? this.constructors.get(constructorName) : null;
    }

    public Method getMethod(String methodName) {
        return this.methods.containsKey(methodName) ? this.methods.get(methodName) : null;
    }

    public Field getField(String fieldName) {
        return this.fields.containsKey(fieldName) ? this.fields.get(fieldName) : null;
    }

    public Object getObject(String objectName) {
        Object object = null;
        if (this.objects.containsKey(objectName)) {
            object = this.objects.get(objectName);
        }

        if (object == null) {
            MineLPLogger.error("Object \"%s\" is NULL", objectName);
        }

        return object;
    }

    public void putClass(String className, Class<?> clazz) {
        this.classes.put(className, clazz);
    }

    public void putConstructor(String constructorName, Constructor<?> constructor) {
        this.constructors.put(constructorName, constructor);
    }

    public void putMethod(String methodName, Method method) {
        this.methods.put(methodName, method);
    }

    public void putField(String fieldName, Field field) {
        this.fields.put(fieldName, field);
    }

    public void putObject(String objectName, Object object) {
        this.objects.put(objectName, object);
    }

    public void removeClass(String className) {
        this.classes.remove(className);
    }

    public void removeConstructor(String constructorName) {
        this.constructors.remove(constructorName);
    }

    public void removeMethod(String methodName) {
        this.methods.remove(methodName);
    }

    public void removeField(String fieldName) {
        this.fields.remove(fieldName);
    }

    public void removeObject(String objectName) {
        this.objects.remove(objectName);
    }

    public boolean hasClass(String className) {
        return this.classes.containsKey(className);
    }

    public boolean hasConstructor(String constructorName) {
        return this.constructors.containsKey(constructorName);
    }

    public boolean hasMethod(String methodName) {
        return this.methods.containsKey(methodName);
    }

    public boolean hasField(String fieldName) {
        return this.fields.containsKey(fieldName);
    }

    public boolean hasObject(String objectName) {
        return this.objects.containsKey(objectName);
    }

    public boolean removeNullData() {
        boolean nullDataPresent = false;
        nullDataPresent |= this.removeNullData(this.classes);
        nullDataPresent |= this.removeNullData(this.constructors);
        nullDataPresent |= this.removeNullData(this.methods);
        nullDataPresent |= this.removeNullData(this.fields);
        return nullDataPresent;
    }

    private <K> boolean removeNullData(Map<K, ?> map) {
        // boolean nullDataPresent = false;
        Iterator<K> var3 = map.keySet().iterator();

        while (var3.hasNext()) {
            Object key = var3.next();
            if (map.get(key) == null) {
                this.constructors.remove(key);
                // nullDataPresent = true;
            }
        }

        return false;
    }
}
