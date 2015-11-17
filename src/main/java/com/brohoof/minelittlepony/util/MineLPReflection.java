package com.brohoof.minelittlepony.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.brohoof.minelittlepony.util.MineLPLogger;
import com.brohoof.minelittlepony.util.MineLPRData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

// TODO Don't use reflection during rendering
public class MineLPReflection {
    public static MineLPRData forgeAPI;

    public static void preCall() {}

    private static boolean reflectForgeAPI(MineLPRData data) {
        MineLPLogger.info("Checking ForgeAPI...");
        final String frgPkg = "net.minecraftforge.client.";
        Class<?> ItemRenderType = getClass(frgPkg + "IItemRenderer$ItemRenderType");
        Class<?> ItemRendererHelper = getClass(frgPkg + "IItemRenderer$ItemRendererHelper");
        Class<?> IItemRenderer = getClass(frgPkg + "IItemRenderer");
        Class<?> MinecraftForgeClient = getClass(frgPkg + "MinecraftForgeClient");
        Class<?> ForgeHooksClient = getClass(frgPkg + "ForgeHooksClient");
        Class<?>[] reflectedForgeAPIClasses = new Class[] {
                ItemRenderType,
                ItemRendererHelper,
                IItemRenderer,
                MinecraftForgeClient,
                ForgeHooksClient,
                RenderBiped.class
        };
        data.installed = false;
        int var9 = reflectedForgeAPIClasses.length;
        byte o = 0;
        if (o < var9) {
            Class<?> enumConstants = reflectedForgeAPIClasses[o];
            if (enumConstants == null) {
                return false;
            }
            data.installed = true;
        }
        data.putClass("ForgeHooksClient", ForgeHooksClient);
        data.putClass("IItemRenderer", IItemRenderer);
        data.putClass("MinecraftForgeClient", MinecraftForgeClient);
        data.putClass("IItemRenderer$ItemRenderType", ItemRenderType);
        data.putClass("IItemRenderer$ItemRendererHelper", ItemRendererHelper);
        Method m;
        data.putMethod("ForgeHooksClient.getArmorModel", m = getMethod(ForgeHooksClient, "getArmorModel", EntityLivingBase.class, ItemStack.class, Integer.TYPE, ModelBiped.class));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("ForgeHooksClient.getArmorTexture",
                m = getMethod(ForgeHooksClient, "getArmorTexture", Entity.class, ItemStack.class, String.class, Integer.TYPE, String.class));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("ForgeHooksClient.orientBedCamera", m = getMethod(ForgeHooksClient, "orientBedCamera", Minecraft.class, EntityLivingBase.class));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("ForgeHooksClient.setRenderPass", m = getMethod(ForgeHooksClient, "setRenderPass", Integer.TYPE));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("ForgeHooksClient.onDrawBlockHighlight", m = getMethod(ForgeHooksClient, "onDrawBlockHighlight",
                RenderGlobal.class, EntityPlayer.class, MovingObjectPosition.class, Integer.TYPE, ItemStack.class, Float.TYPE));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("ForgeHooksClient.dispatchRenderLast", m = getMethod(ForgeHooksClient, "dispatchRenderLast", RenderGlobal.class, Float.TYPE));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("MinecraftForgeClient.getItemRenderer",
                m = getMethod(MinecraftForgeClient, "getItemRenderer", ItemStack.class, ItemRenderType));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("IItemRenderer.shouldUseRenderHelper",
                m = getMethod(IItemRenderer, "shouldUseRenderHelper", ItemRenderType, ItemStack.class, ItemRendererHelper));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("Item.getRenderPasses", m = getMethod(Item.class, "getRenderPasses", Integer.TYPE));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("RenderBiped.getArmorResource",
                m = getMethod(RenderBiped.class, "getArmorResource", Entity.class, ItemStack.class, Integer.TYPE, String.class));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("Entity.canRiderInteract", m = getMethod(Entity.class, "canRiderInteract", new Class[0]));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        data.putMethod("RenderGlobal.drawBlockDamageTexture",
                m = getMethod(RenderGlobal.class, "drawBlockDamageTexture", Tessellator.class, EntityLivingBase.class, Float.TYPE));
        if (m == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
        Object[] enumConstants1 = ItemRenderType.getEnumConstants();
        Object o1;
        data.putObject("IItemRenderer$ItemRenderType.ENTITY", o1 = enumConstants1[0]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        data.putObject("IItemRenderer$ItemRenderType.EQUIPPED", o1 = enumConstants1[1]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        data.putObject("IItemRenderer$ItemRenderType.EQUIPPED_FIRST_PERSON", o1 = enumConstants1[2]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        data.putObject("IItemRenderer$ItemRenderType.INVENTORY", o1 = enumConstants1[3]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        data.putObject("IItemRenderer$ItemRenderType.FIRST_PERSON_MAP", o1 = enumConstants1[4]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        enumConstants1 = ItemRendererHelper.getEnumConstants();
        data.putObject("IItemRenderer$ItemRendererHelper.ENTITY_ROTATION", o1 = enumConstants1[0]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        data.putObject("IItemRenderer$ItemRendererHelper.ENTITY_BOBBING", o1 = enumConstants1[1]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        data.putObject("IItemRenderer$ItemRendererHelper.EQUIPPED_BLOCK", o1 = enumConstants1[2]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        data.putObject("IItemRenderer$ItemRendererHelper.BLOCK_3D", o1 = enumConstants1[3]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        data.putObject("IItemRenderer$ItemRendererHelper.INVENTORY_BLOCK", o1 = enumConstants1[4]);
        if (o1 == null) {
            return false;
        }
        MineLPLogger.debug("ForgeAPI Object " + o1.toString());
        if (forgeAPI.removeNullData()) {
            MineLPLogger.warn("ForgeAPI reflection returned some nulls");
        }
        return true;
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (Exception var2) {
            return null;
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... types) {
        try {
            Constructor<?> e = null;

            try {
                if (types != null && types.length != 0) {
                    e = clazz.getConstructor(types);
                } else {
                    e = clazz.getConstructor(new Class[0]);
                }
            } catch (Exception var4) {

            }

            return e;
        } catch (Exception var5) {
            MineLPLogger.error("Failed to match Constructor for class \"%s\"", new Object[] { clazz.getName() });
            var5.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {

        try {
            Field f = null;

            try {
                f = clazz.getField(fieldName);
            } catch (Exception var5) {
                f = null;
            }

            if (f == null) {
                f = clazz.getDeclaredField(fieldName);
            }

            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            MineLPLogger.error("Failed to match Field \"%s\" in %s", new Object[] { fieldName, clazz.getName() });
            e.printStackTrace();
        }

        return null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... types) {
        try {
            Method e = null;

            try {
                e = clazz.getMethod(methodName, types);
            } catch (Exception var5) {

            }

            if (e == null) {
                if (types != null && types.length != 0) {
                    e = clazz.getDeclaredMethod(methodName, types);
                } else {
                    e = clazz.getDeclaredMethod(methodName, new Class[0]);
                }
            }

            e.setAccessible(true);
            return e;
        } catch (Exception var6) {
            MineLPLogger.error("Failed to match method \"%s\" in %s", new Object[] { methodName, clazz.getName() });
            MineLPLogger.error("Types: " + getStringFromTypes(types));
            var6.printStackTrace();
            return null;
        }
    }

    public static String getStringFromTypes(Class<?>... types) {
        String temp = "";
        temp = temp + "(";
        boolean first = true;
        Class<?>[] var3 = types;
        int var4 = types.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Class<?> c = var3[var5];
            if (!first) {
                temp = temp + ",";
            } else {
                first = false;
            }

            temp = temp + c.getName();
        }

        temp = temp + ")";
        return temp;
    }

    public static Class<?>[] getTypesFromObjects(Object... objects) {
        Class<?>[] types = new Class[objects.length];

        for (int i = 0; i < objects.length; ++i) {
            types[i] = objects[i].getClass();
        }

        return types;
    }

    public static String stringMethod(Method m) {
        return Modifier.toString(m.getModifiers()) + " "
                + (m.getReturnType() != null ? m.getReturnType().getName() : "void") + " " + m.getName()
                + getStringFromTypes(m.getParameterTypes())
                + (m.getExceptionTypes().length > 0 ? " throws " + getStringFromTypes(m.getExceptionTypes()) : "");
    }

    static {
        MineLPLogger.info("Checking compatibilities...");
        forgeAPI = new MineLPRData();
        forgeAPI.compatible = reflectForgeAPI(forgeAPI);
        MineLPLogger.info("Compatibility Check Done!");
        if (forgeAPI.installed) {
            MineLPLogger.info("ForgeAPI " + (forgeAPI.compatible ? "Installed and Compatible" : "Installed but Incompatible"));
        }

    }
}
