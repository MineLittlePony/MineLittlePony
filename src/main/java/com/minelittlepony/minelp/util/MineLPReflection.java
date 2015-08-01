package com.minelittlepony.minelp.util;

import com.minelittlepony.minelp.util.MineLPLogger;
import com.minelittlepony.minelp.util.MineLPRData;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

public class MineLPReflection {
    public static MineLPRData forgeAPI;

    public static void preCall() {}

    private static boolean reflectForgeAPI(MineLPRData data) {
        MineLPLogger.info("Checking ForgeAPI...");
        Class<?> forgeAPIIItemRendererItemRenderType;
        Class<?> forgeAPIIItemRendererItemRendererHelper;
        Class<?> forgeAPIIItemRenderer;
        Class<?> forgeAPIMinecraftForgeClient;
        Class<?> forgeAPIForgeHooksClient;
        Class<?>[] reflectedForgeAPIClasses = new Class[] {
                forgeAPIIItemRendererItemRenderType = getClass(
                        "net.minecraftforge.client.IItemRenderer$ItemRenderType"),
                forgeAPIIItemRendererItemRendererHelper = getClass(
                        "net.minecraftforge.client.IItemRenderer$ItemRendererHelper"),
                forgeAPIIItemRenderer = getClass("net.minecraftforge.client.IItemRenderer"),
                forgeAPIMinecraftForgeClient = getClass("net.minecraftforge.client.MinecraftForgeClient"),
                forgeAPIForgeHooksClient = getClass("net.minecraftforge.client.ForgeHooksClient"),
                getClass("net.minecraft.src.RenderBiped") };
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

        data.putClass("ForgeHooksClient", forgeAPIForgeHooksClient);
        data.putClass("IItemRenderer", forgeAPIIItemRenderer);
        data.putClass("MinecraftForgeClient", forgeAPIMinecraftForgeClient);
        data.putClass("IItemRenderer$ItemRenderType", forgeAPIIItemRendererItemRenderType);
        data.putClass("IItemRenderer$ItemRendererHelper", forgeAPIIItemRendererItemRendererHelper);
        Method m;
        data.putMethod("ForgeHooksClient.getArmorModel", m = getMethod(forgeAPIForgeHooksClient, "getArmorModel",
                new Class[] { EntityLivingBase.class, ItemStack.class, Integer.TYPE, ModelBiped.class }));
        if (m == null) {
            return false;
        } else {
            MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
            data.putMethod("ForgeHooksClient.getArmorTexture",
                    m = getMethod(forgeAPIForgeHooksClient, "getArmorTexture",
                            new Class[] { Entity.class, ItemStack.class, String.class, Integer.TYPE, String.class }));
            if (m == null) {
                return false;
            } else {
                MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                data.putMethod("ForgeHooksClient.orientBedCamera", m = getMethod(forgeAPIForgeHooksClient,
                        "orientBedCamera", new Class[] { Minecraft.class, EntityLivingBase.class }));
                if (m == null) {
                    return false;
                } else {
                    MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                    data.putMethod("ForgeHooksClient.setRenderPass",
                            m = getMethod(forgeAPIForgeHooksClient, "setRenderPass", new Class[] { Integer.TYPE }));
                    if (m == null) {
                        return false;
                    } else {
                        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                        data.putMethod("ForgeHooksClient.onDrawBlockHighlight",
                                m = getMethod(forgeAPIForgeHooksClient, "onDrawBlockHighlight",
                                        new Class[] { RenderGlobal.class, EntityPlayer.class,
                                                MovingObjectPosition.class, Integer.TYPE, ItemStack.class,
                                                Float.TYPE }));
                        if (m == null) {
                            return false;
                        } else {
                            MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                            data.putMethod("ForgeHooksClient.dispatchRenderLast",
                                    m = getMethod(forgeAPIForgeHooksClient, "dispatchRenderLast",
                                            new Class[] { RenderGlobal.class, Float.TYPE }));
                            if (m == null) {
                                return false;
                            } else {
                                MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                                data.putMethod("MinecraftForgeClient.getItemRenderer",
                                        m = getMethod(forgeAPIMinecraftForgeClient, "getItemRenderer",
                                                new Class[] { ItemStack.class, forgeAPIIItemRendererItemRenderType }));
                                if (m == null) {
                                    return false;
                                } else {
                                    MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                                    data.putMethod("IItemRenderer.shouldUseRenderHelper",
                                            m = getMethod(forgeAPIIItemRenderer, "shouldUseRenderHelper",
                                                    new Class[] { forgeAPIIItemRendererItemRenderType, ItemStack.class,
                                                            forgeAPIIItemRendererItemRendererHelper }));
                                    if (m == null) {
                                        return false;
                                    } else {
                                        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                                        data.putMethod("Item.getRenderPasses", m = getMethod(Item.class,
                                                "getRenderPasses", new Class[] { Integer.TYPE }));
                                        if (m == null) {
                                            return false;
                                        } else {
                                            MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                                            data.putMethod("RenderBiped.getArmorResource",
                                                    m = getMethod(RenderBiped.class, "getArmorResource",
                                                            new Class[] { Entity.class, ItemStack.class, Integer.TYPE,
                                                                    String.class }));
                                            if (m == null) {
                                                return false;
                                            } else {
                                                MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                                                data.putMethod("Entity.canRiderInteract",
                                                        m = getMethod(Entity.class, "canRiderInteract", new Class[0]));
                                                if (m == null) {
                                                    return false;
                                                } else {
                                                    MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                                                    data.putMethod("RenderGlobal.drawBlockDamageTexture",
                                                            m = getMethod(RenderGlobal.class, "drawBlockDamageTexture",
                                                                    new Class[] { Tessellator.class,
                                                                            EntityLivingBase.class, Float.TYPE }));
                                                    if (m == null) {
                                                        return false;
                                                    } else {
                                                        MineLPLogger.debug("ForgeAPI Method " + stringMethod(m));
                                                        Object[] enumConstants1 = forgeAPIIItemRendererItemRenderType
                                                                .getEnumConstants();
                                                        Object o1;
                                                        data.putObject("IItemRenderer$ItemRenderType.ENTITY",
                                                                o1 = enumConstants1[0]);
                                                        if (o1 == null) {
                                                            return false;
                                                        } else {
                                                            MineLPLogger.debug("ForgeAPI Object " + o1.toString());
                                                            data.putObject("IItemRenderer$ItemRenderType.EQUIPPED",
                                                                    o1 = enumConstants1[1]);
                                                            if (o1 == null) {
                                                                return false;
                                                            } else {
                                                                MineLPLogger.debug("ForgeAPI Object " + o1.toString());
                                                                data.putObject(
                                                                        "IItemRenderer$ItemRenderType.EQUIPPED_FIRST_PERSON",
                                                                        o1 = enumConstants1[2]);
                                                                if (o1 == null) {
                                                                    return false;
                                                                } else {
                                                                    MineLPLogger
                                                                            .debug("ForgeAPI Object " + o1.toString());
                                                                    data.putObject(
                                                                            "IItemRenderer$ItemRenderType.INVENTORY",
                                                                            o1 = enumConstants1[3]);
                                                                    if (o1 == null) {
                                                                        return false;
                                                                    } else {
                                                                        MineLPLogger.debug(
                                                                                "ForgeAPI Object " + o1.toString());
                                                                        data.putObject(
                                                                                "IItemRenderer$ItemRenderType.FIRST_PERSON_MAP",
                                                                                o1 = enumConstants1[4]);
                                                                        if (o1 == null) {
                                                                            return false;
                                                                        } else {
                                                                            MineLPLogger.debug(
                                                                                    "ForgeAPI Object " + o1.toString());
                                                                            enumConstants1 = forgeAPIIItemRendererItemRendererHelper
                                                                                    .getEnumConstants();
                                                                            data.putObject(
                                                                                    "IItemRenderer$ItemRendererHelper.ENTITY_ROTATION",
                                                                                    o1 = enumConstants1[0]);
                                                                            if (o1 == null) {
                                                                                return false;
                                                                            } else {
                                                                                MineLPLogger.debug("ForgeAPI Object "
                                                                                        + o1.toString());
                                                                                data.putObject(
                                                                                        "IItemRenderer$ItemRendererHelper.ENTITY_BOBBING",
                                                                                        o1 = enumConstants1[1]);
                                                                                if (o1 == null) {
                                                                                    return false;
                                                                                } else {
                                                                                    MineLPLogger
                                                                                            .debug("ForgeAPI Object "
                                                                                                    + o1.toString());
                                                                                    data.putObject(
                                                                                            "IItemRenderer$ItemRendererHelper.EQUIPPED_BLOCK",
                                                                                            o1 = enumConstants1[2]);
                                                                                    if (o1 == null) {
                                                                                        return false;
                                                                                    } else {
                                                                                        MineLPLogger.debug(
                                                                                                "ForgeAPI Object " + o1
                                                                                                        .toString());
                                                                                        data.putObject(
                                                                                                "IItemRenderer$ItemRendererHelper.BLOCK_3D",
                                                                                                o1 = enumConstants1[3]);
                                                                                        if (o1 == null) {
                                                                                            return false;
                                                                                        } else {
                                                                                            MineLPLogger
                                                                                                    .debug("ForgeAPI Object "
                                                                                                            + o1.toString());
                                                                                            data.putObject(
                                                                                                    "IItemRenderer$ItemRendererHelper.INVENTORY_BLOCK",
                                                                                                    o1 = enumConstants1[4]);
                                                                                            if (o1 == null) {
                                                                                                return false;
                                                                                            } else {
                                                                                                MineLPLogger
                                                                                                        .debug("ForgeAPI Object "
                                                                                                                + o1.toString());
                                                                                                if (forgeAPI
                                                                                                        .removeNullData()) {
                                                                                                    MineLPLogger.warn(
                                                                                                            "ForgeAPI reflection returned some nulls");
                                                                                                }

                                                                                                return true;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
                ;
            }

            return e;
        } catch (Exception var5) {
            MineLPLogger.error("Failed to match Constructor for class \"%s\"", new Object[] { clazz.getName() });
            var5.printStackTrace();
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Object ex = null;

        try {
            Field e = null;

            try {
                e = clazz.getField(fieldName);
            } catch (Exception var5) {
                e = null;
            }

            if (e == null) {
                e = clazz.getDeclaredField(fieldName);
            }

            e.setAccessible(true);
            return e;
        } catch (SecurityException var6) {
            ex = var6;
        } catch (NoSuchFieldException var7) {
            ex = var7;
        } catch (Exception var8) {
            ex = var8;
        }

        if (ex != null) {
            MineLPLogger.error("Failed to match Field \"%s\" in %s", new Object[] { fieldName, clazz.getName() });
            ((Exception) ex).printStackTrace();
        }

        return null;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... types) {
        try {
            Method e = null;

            try {
                e = clazz.getMethod(methodName, types);
            } catch (Exception var5) {
                ;
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

    public static void printClass(Class<?> c) {
        printClass(c, true);
    }

    public static void printClass(Class<?> c, boolean declared) {
        printClass(c, declared, 0);
    }

    public static void printClass(Class<?> c, boolean declared, int indent) {
        String indentation = "";
        int i;
        if (indent > 0) {
            char[] lineFormat = new char[indent];

            for (i = 0; i < indent; ++i) {
                lineFormat[i] = 32;
            }

            indentation = String.valueOf(lineFormat);
        }

        String var10 = indentation + "%03d";
        System.out.println(indentation + c.getName());
        System.out.println(indentation + "Nested Classes:");
        Class<?>[] var6 = c.getClasses();
        int var7 = var6.length;

        int var8;
        for (var8 = 0; var8 < var7; ++var8) {
            Class<?> f = var6[var8];
            printClass(f, declared, indent + 1);
        }

        System.out.println(indentation + "Constructors:");
        i = 0;
        Constructor<?>[] var11 = c.getConstructors();
        var7 = var11.length;

        for (var8 = 0; var8 < var7; ++var8) {
            Constructor<?> var14 = var11[var8];
            System.out.println(
                    String.format(var10, new Object[] { Integer.valueOf(i) }) + " " + stringConstructor(var14));
            ++i;
        }

        System.out.println(indentation + "Methods:");
        i = 0;
        Method[] var12 = c.getMethods();
        var7 = var12.length;

        Method var15;
        for (var8 = 0; var8 < var7; ++var8) {
            var15 = var12[var8];
            System.out.println(String.format(var10, new Object[] { Integer.valueOf(i) }) + " " + stringMethod(var15));
            ++i;
        }

        System.out.println(indentation + "Fields:");
        i = 0;
        Field[] var13 = c.getFields();
        var7 = var13.length;

        Field var16;
        for (var8 = 0; var8 < var7; ++var8) {
            var16 = var13[var8];
            System.out.println(String.format(var10, new Object[] { Integer.valueOf(i) }) + " " + stringField(var16));
            ++i;
        }

        if (declared) {
            System.out.println(indentation + "Declared Methods:");
            i = 0;
            var12 = c.getDeclaredMethods();
            var7 = var12.length;

            for (var8 = 0; var8 < var7; ++var8) {
                var15 = var12[var8];
                System.out
                        .println(String.format(var10, new Object[] { Integer.valueOf(i) }) + " " + stringMethod(var15));
                ++i;
            }

            System.out.println(indentation + "Declared Fields:");
            i = 0;
            var13 = c.getDeclaredFields();
            var7 = var13.length;

            for (var8 = 0; var8 < var7; ++var8) {
                var16 = var13[var8];
                System.out
                        .println(String.format(var10, new Object[] { Integer.valueOf(i) }) + " " + stringField(var16));
                ++i;
            }
        }

    }

    public static String stringConstructor(Constructor<?> c) {
        return Modifier.toString(c.getModifiers()) + " " + c.getName() + getStringFromTypes(c.getParameterTypes())
                + (c.getExceptionTypes().length > 0 ? " throws " + c.getExceptionTypes() : "");
    }

    public static String stringMethod(Method m) {
        return Modifier.toString(m.getModifiers()) + " "
                + (m.getReturnType() != null ? m.getReturnType().getName() : "void") + " " + m.getName()
                + getStringFromTypes(m.getParameterTypes())
                + (m.getExceptionTypes().length > 0 ? " throws " + getStringFromTypes(m.getExceptionTypes()) : "");
    }

    public static String stringField(Field f) {
        return Modifier.toString(f.getModifiers()) + " " + f.getType().getName() + " " + f.getName();
    }

    static {
        MineLPLogger.info("Checking compatibilities...");
        forgeAPI = new MineLPRData();
        forgeAPI.compatible = reflectForgeAPI(forgeAPI);
        MineLPLogger.info("Compatibility Check Done!");
        if (forgeAPI.installed) {
            MineLPLogger.info(
                    "ForgeAPI " + (forgeAPI.compatible ? "Installed and Compatible" : "Installed but Incompatible"));
        }

    }
}
