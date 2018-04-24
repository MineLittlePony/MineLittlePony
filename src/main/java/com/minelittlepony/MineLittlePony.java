package com.minelittlepony;

import com.google.common.collect.Maps;
import com.minelittlepony.gui.PonySettingPanel;
import com.minelittlepony.hdskins.gui.GuiSkinsMineLP;
import com.minelittlepony.model.PlayerModels;
import com.minelittlepony.renderer.RenderPonyEvoker;
import com.minelittlepony.renderer.RenderPonyIllusionIllager;
import com.minelittlepony.renderer.RenderPonyPigman;
import com.minelittlepony.renderer.RenderPonySkeleton;
import com.minelittlepony.renderer.RenderPonyVex;
import com.minelittlepony.renderer.RenderPonyVillager;
import com.minelittlepony.renderer.RenderPonyVindicator;
import com.minelittlepony.renderer.RenderPonyZombie;
import com.minelittlepony.renderer.RenderPonyZombieVillager;
import com.minelittlepony.renderer.player.RenderPonyPlayer;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.util.ModUtilities;
import com.voxelmodpack.hdskins.HDSkinManager;
import com.voxelmodpack.hdskins.gui.GuiSkins;
import com.voxelmodpack.hdskins.skins.SkinServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.Map;

public class MineLittlePony {

    public static final Logger logger = LogManager.getLogger("MineLittlePony");

    public static final String MOD_NAME = "Mine Little Pony";
    public static final String MOD_VERSION = "@VERSION@";

    @SuppressWarnings("unused")
	private static final String SKIN_SERVER_URL = "minelpskins.voxelmodpack.com";
    @SuppressWarnings("unused")
	private static final String GATEWAY_URL = "minelpskinmanager.voxelmodpack.com";
    private static final KeyBinding SETTINGS_GUI = new KeyBinding("Settings", Keyboard.KEY_F9, "Mine Little Pony");

    private static MineLittlePony instance;

    private PonyConfig config;
    private PonyManager ponyManager;

    private Map<Class<? extends Entity>, Render<?>> renderMap = Maps.newHashMap();

    MineLittlePony() {
        instance = this;
    }

    void init() {
        LiteLoader.getInput().registerKeyBinding(SETTINGS_GUI);

        this.config = new PonyConfig();
        this.ponyManager = new PonyManager(config);

        LiteLoader.getInstance().registerExposable(config, null);

        IReloadableResourceManager irrm = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        irrm.registerReloadListener(this.ponyManager);

        MetadataSerializer ms = Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer;
        ms.registerMetadataSectionType(new PonyDataSerialzier(), IPonyData.class);

        // This also makes it the default gateway server.
        SkinServer.defaultServers.add("legacy:http://minelpskins.voxelmodpack.com;http://minelpskinmanager.voxelmodpack.com");
    }

    void postInit(Minecraft minecraft) {

        HDSkinManager manager = HDSkinManager.INSTANCE;
//        manager.setSkinUrl(SKIN_SERVER_URL);
//        manager.setGatewayURL(GATEWAY_URL);
        manager.addSkinModifier(new PonySkinModifier());
//        logger.info("Set MineLP skin server URL.");

        RenderManager rm = minecraft.getRenderManager();
        this.saveCurrentRenderers(rm);
        //ModUtilities.addRenderer(EntityPonyModel.class, new RenderPonyModel(rm));

        this.initialisePlayerRenderers(rm);
        this.initializeMobRenderers(rm);

    }

    private void saveCurrentRenderers(RenderManager rm) {
        // villagers
        saveRenderer(rm, EntityVillager.class);
        saveRenderer(rm, EntityZombieVillager.class);
        // zombies
        saveRenderer(rm, EntityZombie.class);
        saveRenderer(rm, EntityGiantZombie.class);
        saveRenderer(rm, EntityHusk.class);
        // pig zombie
        saveRenderer(rm, EntityPigZombie.class);
        // skeletons
        saveRenderer(rm, EntitySkeleton.class);
        saveRenderer(rm, EntityStray.class);
        saveRenderer(rm, EntityWitherSkeleton.class);
        // illagers
        saveRenderer(rm, EntityVex.class);
        saveRenderer(rm, EntityEvoker.class);
        saveRenderer(rm, EntityVindicator.class);
        saveRenderer(rm, EntityIllusionIllager.class);
    }

    private void saveRenderer(RenderManager rm, Class<? extends Entity> cl) {
        this.renderMap.put(cl, rm.getEntityClassRenderObject(cl));
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> Render<T> getRenderer(Class<T> cl) {
        Render<T> render = (Render<T>) this.renderMap.get(cl);
        if (render == null) throw new MissingRendererException(cl);
        return render;
    }

    public void initialisePlayerRenderers(RenderManager rm) {
      new RenderPonyPlayer(rm, false, PlayerModels.PONY);
      new RenderPonyPlayer(rm, true, PlayerModels.PONY);
    }

    public void initializeMobRenderers(RenderManager rm) {
        if (this.config.villagers) {
            ModUtilities.addRenderer(EntityVillager.class, new RenderPonyVillager(rm));
            ModUtilities.addRenderer(EntityZombieVillager.class, new RenderPonyZombieVillager(rm));
            logger.info("Villagers are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityVillager.class, getRenderer(EntityVillager.class));
            ModUtilities.addRenderer(EntityZombieVillager.class, getRenderer(EntityZombieVillager.class));
        }

        if (this.config.zombies) {
            ModUtilities.addRenderer(EntityZombie.class, new RenderPonyZombie<>(rm));
            ModUtilities.addRenderer(EntityHusk.class, new RenderPonyZombie.Husk(rm));
            ModUtilities.addRenderer(EntityGiantZombie.class, new RenderPonyZombie.Giant(rm));
            logger.info("Zombies are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityZombie.class, getRenderer(EntityZombie.class));
            ModUtilities.addRenderer(EntityHusk.class, getRenderer(EntityHusk.class));
            ModUtilities.addRenderer(EntityGiantZombie.class, getRenderer(EntityGiantZombie.class));
        }

        if (this.config.pigzombies) {
            ModUtilities.addRenderer(EntityPigZombie.class, new RenderPonyPigman(rm));
            logger.info("Zombie pigmen are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityPigZombie.class, getRenderer(EntityPigZombie.class));
        }

        if (this.config.skeletons) {
            ModUtilities.addRenderer(EntitySkeleton.class, new RenderPonySkeleton<>(rm));
            ModUtilities.addRenderer(EntityStray.class, new RenderPonySkeleton.Stray(rm));
            ModUtilities.addRenderer(EntityWitherSkeleton.class, new RenderPonySkeleton.Wither(rm));
            logger.info("Skeletons are now ponies.");
        } else {
            ModUtilities.addRenderer(EntitySkeleton.class, getRenderer(EntitySkeleton.class));
            ModUtilities.addRenderer(EntityStray.class, getRenderer(EntityStray.class));
            ModUtilities.addRenderer(EntityWitherSkeleton.class, getRenderer(EntityWitherSkeleton.class));
        }

        if (this.config.illagers) {
            ModUtilities.addRenderer(EntityVex.class, new RenderPonyVex(rm));
            ModUtilities.addRenderer(EntityEvoker.class, new RenderPonyEvoker(rm));
            ModUtilities.addRenderer(EntityVindicator.class, new RenderPonyVindicator(rm));
            ModUtilities.addRenderer(EntityIllusionIllager.class, new RenderPonyIllusionIllager(rm));
            logger.info("Illagers are now ponies.");
        } else {
            ModUtilities.addRenderer(EntityVex.class, getRenderer(EntityVex.class));
            ModUtilities.addRenderer(EntityEvoker.class, getRenderer(EntityEvoker.class));
            ModUtilities.addRenderer(EntityVindicator.class, getRenderer(EntityVindicator.class));
            ModUtilities.addRenderer(EntityIllusionIllager.class, getRenderer(EntityIllusionIllager.class));
        }
    }

    void onTick(Minecraft minecraft, boolean inGame) {

        if (inGame && minecraft.currentScreen == null && SETTINGS_GUI.isPressed()) {
            minecraft.displayGuiScreen(new PonySettingPanel());
        }

        boolean skins = minecraft.currentScreen instanceof GuiSkins
                && !(minecraft.currentScreen instanceof GuiSkinsMineLP);
        if (skins) {
            minecraft.displayGuiScreen(new GuiSkinsMineLP(ponyManager));
        }
        HDSkinManager.INSTANCE.setEnabled(config.hd);

    }

    public static MineLittlePony getInstance() {
        return instance;
    }

    public PonyManager getManager() {
        return ponyManager;
    }

    public static PonyConfig getConfig() {
        return getInstance().config;
    }

}
