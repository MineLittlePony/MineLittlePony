package com.mumfrey.liteloader.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.mumfrey.liteloader.client.ducks.*;
import com.mumfrey.liteloader.client.overlays.IMinecraft;
import com.mumfrey.liteloader.client.util.PrivateFieldsClient;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;

/**
 * A small collection of useful functions for mods
 * 
 * @author Adam Mummery-Smith
 */
public abstract class ModUtilities
{
    /**
     * @return true if FML is present in the current environment
     */
    public static boolean fmlIsPresent()
    {
        return ObfuscationUtilities.fmlIsPresent();
    }

    public static void setWindowSize(int width, int height)
    {
        try
        {
            Minecraft mc = Minecraft.getMinecraft();
            Display.setDisplayMode(new DisplayMode(width, height));
            ((IMinecraft)mc).onResizeWindow(width, height);
            Display.setVSyncEnabled(mc.gameSettings.enableVsync);
        }
        catch (LWJGLException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Add a renderer map entry for the specified entity class
     * 
     * @param entityClass
     * @param renderer
     */
    public static void addRenderer(Class<? extends Entity> entityClass, Render renderer)
    {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        Map<Class<? extends Entity>, Render> entityRenderMap = ((IRenderManager)renderManager).getRenderMap();
        if (entityRenderMap != null)
        {
            entityRenderMap.put(entityClass, renderer);
        }
        else
        {
            LiteLoaderLogger.warning("Attempted to set renderer %s for entity class %s but the operation failed",
                    renderer.getClass().getSimpleName(), entityClass.getSimpleName());
        }
    }

    public static void addRenderer(Class<? extends TileEntity> tileEntityClass, TileEntitySpecialRenderer renderer)
    {
        TileEntityRendererDispatcher tileEntityRenderer = TileEntityRendererDispatcher.instance;

        try
        {
            Map<Class<? extends TileEntity>, TileEntitySpecialRenderer> specialRendererMap
                    = ((ITileEntityRendererDispatcher)tileEntityRenderer).getSpecialRenderMap();
            specialRendererMap.put(tileEntityClass, renderer);
            renderer.setRendererDispatcher(tileEntityRenderer);
        }
        catch (Exception ex)
        {
            LiteLoaderLogger.warning("Attempted to set renderer %s for tile entity class %s but the operation failed",
                    renderer.getClass().getSimpleName(), tileEntityClass.getSimpleName());
        }
    }

    /**
     * Add a block to the blocks registry
     * 
     * @param blockId Block ID to insert
     * @param blockName Block identifier
     * @param block Block to register
     * @param force Force insertion even if the operation is blocked by FMl
     */
    public static void addBlock(int blockId, ResourceLocation blockName, Block block, boolean force)
    {
        Block existingBlock = (Block)Block.blockRegistry.getObject(blockName);

        try
        {
            Block.blockRegistry.register(blockId, blockName, block);
        }
        catch (IllegalArgumentException ex)
        {
            if (!force) throw new IllegalArgumentException("Could not register block '" + blockName + "', the operation was blocked by FML.", ex);

            ModUtilities.removeObjectFromRegistry(Block.blockRegistry, blockName);
            Block.blockRegistry.register(blockId, blockName, block);
        }

        if (existingBlock != null)
        {
            try
            {
                for (Field field : Blocks.class.getDeclaredFields())
                {
                    field.setAccessible(true);
                    if (field.isAccessible() && Block.class.isAssignableFrom(field.getType()))
                    {
                        Block fieldValue = (Block)field.get(null);
                        if (fieldValue == existingBlock)
                        {
                            ModUtilities.setFinalStaticField(field, block);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Add an item to the items registry
     * 
     * @param itemId Item ID to insert
     * @param itemName Item identifier
     * @param item Item to register
     * @param force Force insertion even if the operation is blocked by FMl
     */
    public static void addItem(int itemId, ResourceLocation itemName, Item item, boolean force)
    {
        Item existingItem = (Item)Item.itemRegistry.getObject(itemName);

        try
        {
            Item.itemRegistry.register(itemId, itemName, item);
        }
        catch (IllegalArgumentException ex)
        {
            if (!force) throw new IllegalArgumentException("Could not register item '" + itemName + "', the operation was blocked by FML.", ex);

            ModUtilities.removeObjectFromRegistry(Block.blockRegistry, itemName);
            Item.itemRegistry.register(itemId, itemName, item);
        }

        if (existingItem != null)
        {
            try
            {
                for (Field field : Items.class.getDeclaredFields())
                {
                    field.setAccessible(true);
                    if (field.isAccessible() && Item.class.isAssignableFrom(field.getType()))
                    {
                        Item fieldValue = (Item)field.get(null);
                        if (fieldValue == existingItem)
                        {
                            ModUtilities.setFinalStaticField(field, item);
                        }
                    }
                }
            }
            catch (Exception ex) {}
        }
    }

    @SuppressWarnings("unchecked")
    public static void addTileEntity(String entityName, Class<? extends TileEntity> tileEntityClass)
    {
        try
        {
            Map<String, Class<? extends TileEntity>> nameToClassMap = PrivateFieldsClient.tileEntityNameToClassMap.get(null);
            Map<Class<? extends TileEntity>, String> classToNameMap = PrivateFieldsClient.tileEntityClassToNameMap.get(null);
            nameToClassMap.put(entityName, tileEntityClass);
            classToNameMap.put(tileEntityClass, entityName);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static <K, V> V removeObjectFromRegistry(RegistrySimple registry, K key)
    {
        if (registry == null) return null;

        IObjectIntIdentityMap underlyingIntegerMap = null;

        if (registry instanceof INamespacedRegistry)
        {
            underlyingIntegerMap = ((INamespacedRegistry)registry).getUnderlyingMap(); 
        }

        Map<K, V> registryObjects = ((IRegistrySimple)registry).<K, V>getRegistryObjects();
        if (registryObjects != null)
        {
            V existingValue = registryObjects.get(key);
            if (existingValue != null)
            {
                registryObjects.remove(key);

                if (underlyingIntegerMap != null)
                {
                    IdentityHashMap<V, Integer> identityMap = underlyingIntegerMap.<V>getIdentityMap();
                    List<V> objectList = underlyingIntegerMap.<V>getObjectList();
                    if (identityMap != null) identityMap.remove(existingValue);
                    if (objectList != null) objectList.remove(existingValue);
                }

                return existingValue;
            }
        }

        return null;
    }

    private static void setFinalStaticField(Field field, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, value);
    }
}
