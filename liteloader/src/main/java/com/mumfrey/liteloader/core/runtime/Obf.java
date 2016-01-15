package com.mumfrey.liteloader.core.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Centralised obfuscation table for LiteLoader
 *
 * @author Adam Mummery-Smith
 * TODO Obfuscation 1.8
 */
public class Obf
{
    // Non-obfuscated references, here for convenience
    // -----------------------------------------------------------------------------------------
    public static final Obf                   EventProxy = new Obf("com.mumfrey.liteloader.core.event.EventProxy"                      );
    public static final Obf                  HandlerList = new Obf("com.mumfrey.liteloader.core.event.HandlerList"                     );
    public static final Obf             BakedHandlerList = new Obf("com.mumfrey.liteloader.core.event.HandlerList$BakedHandlerList"    );
    public static final Obf    BakedProfilingHandlerList = new Obf("com.mumfrey.liteloader.core.event.ProfilingHandlerList$BakedList"  );
    public static final Obf                 PacketEvents = new Obf("com.mumfrey.liteloader.core.PacketEvents"                          );
    public static final Obf           PacketEventsClient = new Obf("com.mumfrey.liteloader.client.PacketEventsClient"                  );
    public static final Obf                   LoadingBar = new Obf("com.mumfrey.liteloader.client.gui.startup.LoadingBar"              );
    public static final Obf                  GameProfile = new Obf("com.mojang.authlib.GameProfile"                                    );
    public static final Obf                MinecraftMain = new Obf("net.minecraft.client.main.Main"                                    );
    public static final Obf              MinecraftServer = new Obf("net.minecraft.server.MinecraftServer"                              );
    public static final Obf                         GL11 = new Obf("org.lwjgl.opengl.GL11"                                             );
    public static final Obf             RealmsMainScreen = new Obf("com.mojang.realmsclient.RealmsMainScreen"                          );
    public static final Obf                         init = new Obf("init"                                                              );
    public static final Obf                     postInit = new Obf("postInit"                                                          );
    public static final Obf                  constructor = new Obf("<init>"                                                            );

    // Overlays and Accessor Interfaces
    // -----------------------------------------------------------------------------------------
    public static final Obf                   IMinecraft = new Obf("com.mumfrey.liteloader.client.overlays.IMinecraft"                 );
    public static final Obf                IGuiTextField = new Obf("com.mumfrey.liteloader.client.overlays.IGuiTextField"              );
    public static final Obf              IEntityRenderer = new Obf("com.mumfrey.liteloader.client.overlays.IEntityRenderer"            );
    public static final Obf                ISoundHandler = new Obf("com.mumfrey.liteloader.client.overlays.ISoundHandler"              );
    
    // CHECKSTYLE:OFF

    // Classes
    // -----------------------------------------------------------------------------------------
    public static final Obf                    Minecraft = new Obf("net.minecraft.client.Minecraft",                             "bsu" );
    public static final Obf               EntityRenderer = new Obf("net.minecraft.client.renderer.EntityRenderer",               "cji" );
    public static final Obf                       Blocks = new Obf("net.minecraft.init.Blocks",                                  "aty" );
    public static final Obf                CrashReport$6 = new Obf("net.minecraft.crash.CrashReport$6",                          "h"   );
    public static final Obf                  INetHandler = new Obf("net.minecraft.network.INetHandler",                          "hg"  );
    public static final Obf                        Items = new Obf("net.minecraft.init.Items",                                   "amk" );

    // Fields
    // -----------------------------------------------------------------------------------------
    public static final Obf     tileEntityNameToClassMap = new Obf("field_145855_i",                                             "f"   );
    public static final Obf     tileEntityClassToNameMap = new Obf("field_145853_j",                                             "g"   );

    // Methods
    // -----------------------------------------------------------------------------------------
    public static final Obf                    startGame = new Obf("func_71384_a",                                               "aj"  );
    public static final Obf                 startSection = new Obf("func_76320_a",                                               "a"   );
    public static final Obf                   endSection = new Obf("func_76319_b",                                               "b"   );
    public static final Obf              endStartSection = new Obf("func_76318_c",                                               "c"   );
    public static final Obf                processPacket = new Obf("func_148833_a",                                              "a"   );

    // Legacy
    // -----------------------------------------------------------------------------------------
    @Deprecated public static final Obf                    GuiIngame = new Obf("net.minecraft.client.gui.GuiIngame",                         "btz" );
    @Deprecated public static final Obf                     Profiler = new Obf("net.minecraft.profiler.Profiler",                            "uw"  );
    @Deprecated public static final Obf             IntegratedServer = new Obf("net.minecraft.server.integrated.IntegratedServer",           "cyk" );
    @Deprecated public static final Obf                WorldSettings = new Obf("net.minecraft.world.WorldSettings",                          "arb" );
    @Deprecated public static final Obf   ServerConfigurationManager = new Obf("net.minecraft.server.management.ServerConfigurationManager", "sn"  );
    @Deprecated public static final Obf               EntityPlayerMP = new Obf("net.minecraft.entity.player.EntityPlayerMP",                 "qw"  );
    @Deprecated public static final Obf               NetworkManager = new Obf("net.minecraft.network.NetworkManager",                       "gr"  );
    @Deprecated public static final Obf              DedicatedServer = new Obf("net.minecraft.server.dedicated.DedicatedServer",             "po"  );
    @Deprecated public static final Obf               EntityPlayerSP = new Obf("net.minecraft.client.entity.EntityPlayerSP",                 "cio" );
    @Deprecated public static final Obf                  FrameBuffer = new Obf("net.minecraft.client.shader.Framebuffer",                    "ckw" );
    @Deprecated public static final Obf                   GuiNewChat = new Obf("net.minecraft.client.gui.GuiNewChat",                        "buh" );
    @Deprecated public static final Obf               GlStateManager = new Obf("net.minecraft.client.renderer.GlStateManager",               "cjm" );
    @Deprecated public static final Obf                      Session = new Obf("net.minecraft.util.Session",                                 "btw" );
    @Deprecated public static final Obf               IChatComponent = new Obf("net.minecraft.util.IChatComponent",                          "ho"  );
    @Deprecated public static final Obf             ScreenShotHelper = new Obf("net.minecraft.util.ScreenShotHelper",                        "btt" );
    @Deprecated public static final Obf                 OpenGlHelper = new Obf("net.minecraft.client.renderer.OpenGlHelper",                 "dax" );
    @Deprecated public static final Obf                       Entity = new Obf("net.minecraft.entity.Entity",                                "wv"  );
    @Deprecated public static final Obf                RenderManager = new Obf("net.minecraft.client.renderer.entity.RenderManager",         "cpt" );
    @Deprecated public static final Obf                       Render = new Obf("net.minecraft.client.renderer.entity.Render",                "cpu" );
    @Deprecated public static final Obf                 GuiTextField = new Obf("net.minecraft.client.gui.GuiTextField",                      "bul" );
    @Deprecated public static final Obf                 SoundHandler = new Obf("net.minecraft.client.audio.SoundHandler",                    "czh" );
    @Deprecated public static final Obf                     BlockPos = new Obf("net.minecraft.util.BlockPos",                                "dt"  );
    @Deprecated public static final Obf                   EnumFacing = new Obf("net.minecraft.util.EnumFacing",                              "ej"  );
    @Deprecated public static final Obf           ItemInWorldManager = new Obf("net.minecraft.server.management.ItemInWorldManager",         "qx"  );
    @Deprecated public static final Obf         NetHandlerPlayServer = new Obf("net.minecraft.network.NetHandlerPlayServer",                 "rj"  );
    @Deprecated public static final Obf                 EntityPlayer = new Obf("net.minecraft.entity.player.EntityPlayer",                   "ahd" );
    @Deprecated public static final Obf                        World = new Obf("net.minecraft.world.World",                                  "aqu" );
    @Deprecated public static final Obf                    ItemStack = new Obf("net.minecraft.item.ItemStack",                               "amj" );
    @Deprecated public static final Obf             PacketThreadUtil = new Obf("net.minecraft.network.PacketThreadUtil",                     "ig"  );
    @Deprecated public static final Obf                 RenderGlobal = new Obf("net.minecraft.client.renderer.RenderGlobal",                 "ckn" );
    @Deprecated public static final Obf            minecraftProfiler = new Obf("field_71424_I",                                              "y"   );
    @Deprecated public static final Obf              entityRenderMap = new Obf("field_78729_o",                                              "k"   );
    @Deprecated public static final Obf              reloadListeners = new Obf("field_110546_b",                                             "d"   );
    @Deprecated public static final Obf               networkManager = new Obf("field_147393_d",                                             "d"   );
    @Deprecated public static final Obf              registryObjects = new Obf("field_82596_a",                                              "c"   );
    @Deprecated public static final Obf         underlyingIntegerMap = new Obf("field_148759_a",                                             "a"   );
    @Deprecated public static final Obf                  identityMap = new Obf("field_148749_a",                                             "a"   );
    @Deprecated public static final Obf                   objectList = new Obf("field_148748_b",                                             "b"   );
    @Deprecated public static final Obf          mapSpecialRenderers = new Obf("field_147559_m",                                             "m"   );
    @Deprecated public static final Obf                        timer = new Obf("field_71428_T",                                              "U"   );
    @Deprecated public static final Obf                   mcProfiler = new Obf("field_71424_I",                                              "y"   ); 
    @Deprecated public static final Obf                      running = new Obf("field_71425_J",                                              "z"   ); 
    @Deprecated public static final Obf         defaultResourcePacks = new Obf("field_110449_ao",                                            "aw"  );
    @Deprecated public static final Obf                   serverName = new Obf("field_71475_ae",                                             "am"  );
    @Deprecated public static final Obf                   serverPort = new Obf("field_71477_af",                                             "an"  );
    @Deprecated public static final Obf      shaderResourceLocations = new Obf("field_147712_ad",                                            "ab"  );
    @Deprecated public static final Obf                  shaderIndex = new Obf("field_147713_ae",                                            "ac"  );
    @Deprecated public static final Obf                    useShader = new Obf("field_175083_ad",                                            "ad"  );
    @Deprecated public static final Obf                 viewDistance = new Obf("field_149528_b",                                             "b"   );
    @Deprecated public static final Obf                   entityPosY = new Obf("field_70163_u",                                              "t"   );
    @Deprecated public static final Obf                chatComponent = new Obf("field_148919_a",                                             "a"   );
    @Deprecated public static final Obf                  runGameLoop = new Obf("func_71411_J",                                               "as"  );
    @Deprecated public static final Obf                      runTick = new Obf("func_71407_l",                                               "r"   );
    @Deprecated public static final Obf        updateCameraAndRender = new Obf("func_78480_b",                                               "b"   );
    @Deprecated public static final Obf                  renderWorld = new Obf("func_78471_a",                                               "a"   );
    @Deprecated public static final Obf            renderGameOverlay = new Obf("func_175180_a",                                              "a"   );
    @Deprecated public static final Obf                  spawnPlayer = new Obf("func_148545_a",                                              "f"   );
    @Deprecated public static final Obf                respawnPlayer = new Obf("func_72368_a",                                               "a"   );
    @Deprecated public static final Obf initializeConnectionToPlayer = new Obf("func_72355_a",                                               "a"   );
    @Deprecated public static final Obf               playerLoggedIn = new Obf("func_72377_c",                                               "c"   );
    @Deprecated public static final Obf              playerLoggedOut = new Obf("func_72367_e",                                               "e"   );
    @Deprecated public static final Obf                  startServer = new Obf("func_71197_b",                                               "i"   );
    @Deprecated public static final Obf            startServerThread = new Obf("func_71256_s",                                               "B"   );
    @Deprecated public static final Obf              sendChatMessage = new Obf("func_71165_d",                                               "e"   );
    @Deprecated public static final Obf        updateFramebufferSize = new Obf("func_147119_ah",                                             "av"  );
    @Deprecated public static final Obf            framebufferRender = new Obf("func_147615_c",                                              "c"   );
    @Deprecated public static final Obf         framebufferRenderExt = new Obf("func_178038_a",                                              "a"   );
    @Deprecated public static final Obf       bindFramebufferTexture = new Obf("func_147612_c",                                              "c"   );
    @Deprecated public static final Obf                     drawChat = new Obf("func_146230_a",                                              "a"   );
    @Deprecated public static final Obf                        clear = new Obf("func_179086_m",                                              "m"   );
    @Deprecated public static final Obf              renderWorldPass = new Obf("func_175068_a",                                              "a"   );
    @Deprecated public static final Obf                   getProfile = new Obf("func_148256_e",                                              "a"   );
    @Deprecated public static final Obf               saveScreenshot = new Obf("func_148260_a",                                              "a"   );
    @Deprecated public static final Obf         isFramebufferEnabled = new Obf("func_148822_b",                                              "i"   );
    @Deprecated public static final Obf               doRenderEntity = new Obf("func_147939_a",                                              "a"   );
    @Deprecated public static final Obf                     doRender = new Obf("func_76986_a",                                               "a"   );
    @Deprecated public static final Obf        doRenderShadowAndFire = new Obf("func_76979_b",                                               "b"   );
    @Deprecated public static final Obf                       resize = new Obf("func_71370_a",                                               "a"   );
    @Deprecated public static final Obf                   loadShader = new Obf("func_175069_a",                                              "a"   );
    @Deprecated public static final Obf               getFOVModifier = new Obf("func_78481_a",                                               "a"   );
    @Deprecated public static final Obf         setupCameraTransform = new Obf("func_78479_a",                                               "a"   );
    @Deprecated public static final Obf            loadSoundResource = new Obf("func_147693_a",                                              "a"   );
    @Deprecated public static final Obf               onBlockClicked = new Obf("func_180784_a",                                              "a"   );
    @Deprecated public static final Obf       activateBlockOrUseItem = new Obf("func_180236_a",                                              "a"   );
    @Deprecated public static final Obf  processPlayerBlockPlacement = new Obf("func_147346_a",                                              "a"   );
    @Deprecated public static final Obf              handleAnimation = new Obf("func_175087_a",                                              "a"   );
    @Deprecated public static final Obf         processPlayerDigging = new Obf("func_147345_a",                                              "a"   );
    @Deprecated public static final Obf   updateTimeLightAndEntities = new Obf("func_71190_q",                                               "z"   );
    @Deprecated public static final Obf        checkThreadAndEnqueue = new Obf("func_180031_a",                                              "a"   );
    @Deprecated public static final Obf                processPlayer = new Obf("func_147347_a",                                              "a"   );
    @Deprecated public static final Obf                    renderSky = new Obf("func_174976_a",                                              "a"   );
    @Deprecated public static final Obf            renderCloudsCheck = new Obf("func_180437_a",                                              "a"   );
    @Deprecated public static final Obf                     setupFog = new Obf("func_78468_a",                                               "a"   );

    // CHECKSTYLE:ON

    public static final int MCP = 0;
    public static final int SRG = 1;
    public static final int OBF = 2;

    private static Properties mcpNames;

    private static final Map<String, Obf> obfs = new HashMap<String, Obf>(); 

    static
    {
        try
        {
            for (Field fd : Obf.class.getFields())
            {
                if (fd.getType().equals(Obf.class))
                {
                    Obf.obfs.put(fd.getName(), (Obf)fd.get(null));
                }
            }
        }
        catch (IllegalAccessException ex) {}
    }

    /**
     * Array of names, indexed by MCP, SRG, OBF constants
     */
    public final String[] names;

    /**
     * Class, field or method name in unobfuscated (MCP) format
     */
    public final String name;

    /**
     * Class name in bytecode notation with slashes instead of dots
     */
    public final String ref;

    /**
     * Class, field or method name in searge format
     */
    public final String srg;

    /**
     * Class, field or method name in obfuscated (original) format
     */
    public final String obf;

    /**
     * @param mcpName
     */
    protected Obf(String mcpName)
    {
        this(mcpName, mcpName, mcpName);
    }

    /**
     * @param seargeName
     * @param obfName
     */
    protected Obf(String seargeName, String obfName)
    {
        this(seargeName, obfName, null);
    }

    /**
     * @param seargeName
     * @param obfName
     * @param mcpName
     */
    protected Obf(String seargeName, String obfName, String mcpName)
    {
        this.name = mcpName != null ? mcpName : this.getDeobfuscatedName(seargeName);
        this.ref = this.name.replace('.', '/');
        this.srg = seargeName;
        this.obf = obfName;

        this.names = new String[] { this.name, this.srg, this.obf };
    }

    /**
     * @param type
     */
    public String getDescriptor(int type)
    {
        return String.format("L%s;", this.names[type].replace('.', '/'));
    }

    /**
     * Test whether any of this Obf's dimensions match the supplied name
     * 
     * @param name
     */
    public boolean matches(String name)
    {
        return this.obf.equals(name) || this.srg.equals(name)|| this.name.equals(name);
    }

    /**
     * Test whether any of this Obf's dimensions match the supplied name or
     * ordinal
     * 
     * @param name
     * @param ordinal
     */
    public boolean matches(String name, int ordinal)
    {
        if (this.isOrdinal() && ordinal > -1)
        {
            return this.getOrdinal() == ordinal;
        }

        return this.matches(name);
    }

    /**
     * Returns true if this is an ordinal pointer
     */
    public boolean isOrdinal()
    {
        return false;
    }

    /**
     * Get the ordinal for this entry
     */
    public int getOrdinal()
    {
        return -1;
    }

    @Override
    public String toString()
    {
        return String.format("%s[%s,%s,%s]@%d", this.getClass().getSimpleName(), this.name, this.srg, this.obf, this.getOrdinal());
    }

    /**
     * @param seargeName
     */
    protected String getDeobfuscatedName(String seargeName)
    {
        return Obf.getDeobfName(seargeName);
    }

    /**
     * @param seargeName
     */
    static String getDeobfName(String seargeName)
    {
        if (Obf.mcpNames == null)
        {
            Obf.mcpNames = new Properties();
            InputStream is = Obf.class.getResourceAsStream("/obfuscation.properties");
            if (is != null)
            {
                try
                {
                    Obf.mcpNames.load(is);
                }
                catch (IOException ex) {}

                try
                {
                    is.close();
                }
                catch (IOException ex) {}
            }
        }

        return Obf.mcpNames.getProperty(seargeName, seargeName);
    }

    /**
     * @param name
     */
    public static Obf getByName(String name)
    {
        return Obf.obfs.get(name);
    }

    public static Obf getByName(Class<? extends Obf> obf, String name)
    {
        try
        {
            for (Field fd : obf.getFields())
            {
                if (Obf.class.isAssignableFrom(fd.getType()))
                {
                    String fieldName = fd.getName();
                    Obf entry = (Obf)fd.get(null);
                    if (name.equals(fieldName) || name.equals(entry.name))
                    {
                        return entry;
                    }
                }
            }
        }
        catch (Exception ex) {}

        return Obf.getByName(name);
    }

    public static String lookupMCPName(String obfName)
    {
        for (Obf obf : Obf.obfs.values())
        {
            if (obfName.equals(obf.obf))
            {
                return obf.name;
            }
        }

        return obfName;
    }

    /**
     * Ordinal reference, can be passed to some methods which accept an
     * {@link Obf} to indicate an offset into a class rather than a named
     * reference.
     * 
     * @author Adam Mummery-Smith
     */
    public static class Ord extends Obf
    {
        /**
         * Field/method offset 
         */
        private final int ordinal;

        /**
         * @param name Field/method name
         * @param ordinal Field/method ordinal
         */
        public Ord(String name, int ordinal)
        {
            super(name);
            this.ordinal = ordinal;
        }

        /**
         * @param ordinal Field ordinal
         */
        public Ord(int ordinal)
        {
            super("ord#" + ordinal);
            this.ordinal = ordinal;
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.runtime.Obf#isOrdinal()
         */
        @Override
        public boolean isOrdinal()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.runtime.Obf#getOrdinal()
         */
        @Override
        public int getOrdinal()
        {
            return this.ordinal;
        }
    }
}
