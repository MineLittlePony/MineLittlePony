package com.mumfrey.liteloader.core.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * Packet obfuscation table
 *
 * @author Adam Mummery-Smith
 * TODO Obfuscation 1.8
 */
public final class Packets extends Obf
{
    /**
     * Since we need to catch and deal with the fact that a packet is first
     * marshalled across threads via PacketThreadUtil, we will need to know
     * which owner object to check against the current thread in order to detect
     * when the packet instance is being processed by the main message loop. The
     * Context object describes in which context (client or server) that a
     * particular packet will be processed in on the <em>receiving</em> end, and
     * thus which object to check threading against.
     * 
     * @author Adam Mummery-Smith
     */
    public enum Context
    {
        CLIENT,
        SERVER
    }
    
    // CHECKSTYLE:OFF

    private static Map<String, Packets> packetMap = new HashMap<String, Packets>();

    public static Packets S08PacketPlayerPosLook           = new Packets("net.minecraft.network.play.server.S08PacketPlayerPosLook",                    "ii", Context.CLIENT);
    public static Packets S0EPacketSpawnObject             = new Packets("net.minecraft.network.play.server.S0EPacketSpawnObject",                      "il", Context.CLIENT);
    public static Packets S11PacketSpawnExperienceOrb      = new Packets("net.minecraft.network.play.server.S11PacketSpawnExperienceOrb",               "im", Context.CLIENT);
    public static Packets S2CPacketSpawnGlobalEntity       = new Packets("net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity",                "in", Context.CLIENT);
    public static Packets S0FPacketSpawnMob                = new Packets("net.minecraft.network.play.server.S0FPacketSpawnMob",                         "io", Context.CLIENT);
    public static Packets S10PacketSpawnPainting           = new Packets("net.minecraft.network.play.server.S10PacketSpawnPainting",                    "ip", Context.CLIENT);
    public static Packets S0CPacketSpawnPlayer             = new Packets("net.minecraft.network.play.server.S0CPacketSpawnPlayer",                      "iq", Context.CLIENT);
    public static Packets S0BPacketAnimation               = new Packets("net.minecraft.network.play.server.S0BPacketAnimation",                        "ir", Context.CLIENT);
    public static Packets S37PacketStatistics              = new Packets("net.minecraft.network.play.server.S37PacketStatistics",                       "is", Context.CLIENT);
    public static Packets S25PacketBlockBreakAnim          = new Packets("net.minecraft.network.play.server.S25PacketBlockBreakAnim",                   "it", Context.CLIENT);
    public static Packets S35PacketUpdateTileEntity        = new Packets("net.minecraft.network.play.server.S35PacketUpdateTileEntity",                 "iu", Context.CLIENT);
    public static Packets S24PacketBlockAction             = new Packets("net.minecraft.network.play.server.S24PacketBlockAction",                      "iv", Context.CLIENT);
    public static Packets S23PacketBlockChange             = new Packets("net.minecraft.network.play.server.S23PacketBlockChange",                      "iw", Context.CLIENT);
    public static Packets S41PacketServerDifficulty        = new Packets("net.minecraft.network.play.server.S41PacketServerDifficulty",                 "ix", Context.CLIENT);
    public static Packets S3APacketTabComplete             = new Packets("net.minecraft.network.play.server.S3APacketTabComplete",                      "iy", Context.CLIENT);
    public static Packets S02PacketChat                    = new Packets("net.minecraft.network.play.server.S02PacketChat",                             "iz", Context.CLIENT);
    public static Packets S22PacketMultiBlockChange        = new Packets("net.minecraft.network.play.server.S22PacketMultiBlockChange",                 "ja", Context.CLIENT);
    public static Packets S32PacketConfirmTransaction      = new Packets("net.minecraft.network.play.server.S32PacketConfirmTransaction",               "jc", Context.CLIENT);
    public static Packets S2EPacketCloseWindow             = new Packets("net.minecraft.network.play.server.S2EPacketCloseWindow",                      "jd", Context.CLIENT);
    public static Packets S2DPacketOpenWindow              = new Packets("net.minecraft.network.play.server.S2DPacketOpenWindow",                       "je", Context.CLIENT);
    public static Packets S30PacketWindowItems             = new Packets("net.minecraft.network.play.server.S30PacketWindowItems",                      "jf", Context.CLIENT);
    public static Packets S31PacketWindowProperty          = new Packets("net.minecraft.network.play.server.S31PacketWindowProperty",                   "jg", Context.CLIENT);
    public static Packets S2FPacketSetSlot                 = new Packets("net.minecraft.network.play.server.S2FPacketSetSlot",                          "jh", Context.CLIENT);
    public static Packets S3FPacketCustomPayload           = new Packets("net.minecraft.network.play.server.S3FPacketCustomPayload",                    "ji", Context.CLIENT);
    public static Packets S40PacketDisconnect              = new Packets("net.minecraft.network.play.server.S40PacketDisconnect",                       "jj", Context.CLIENT);
    public static Packets S19PacketEntityStatus            = new Packets("net.minecraft.network.play.server.S19PacketEntityStatus",                     "jk", Context.CLIENT);
    public static Packets S49PacketUpdateEntityNBT         = new Packets("net.minecraft.network.play.server.S49PacketUpdateEntityNBT",                  "jl", Context.CLIENT);
    public static Packets S27PacketExplosion               = new Packets("net.minecraft.network.play.server.S27PacketExplosion",                        "jm", Context.CLIENT);
    public static Packets S46PacketSetCompressionLevel     = new Packets("net.minecraft.network.play.server.S46PacketSetCompressionLevel",              "jn", Context.CLIENT);
    public static Packets S2BPacketChangeGameState         = new Packets("net.minecraft.network.play.server.S2BPacketChangeGameState",                  "jo", Context.CLIENT);
    public static Packets S00PacketKeepAlive               = new Packets("net.minecraft.network.play.server.S00PacketKeepAlive",                        "jp", Context.CLIENT);
    public static Packets S21PacketChunkData               = new Packets("net.minecraft.network.play.server.S21PacketChunkData",                        "jq", Context.CLIENT);
    public static Packets S26PacketMapChunkBulk            = new Packets("net.minecraft.network.play.server.S26PacketMapChunkBulk",                     "js", Context.CLIENT);
    public static Packets S28PacketEffect                  = new Packets("net.minecraft.network.play.server.S28PacketEffect",                           "jt", Context.CLIENT);
    public static Packets S2APacketParticles               = new Packets("net.minecraft.network.play.server.S2APacketParticles",                        "ju", Context.CLIENT);
    public static Packets S29PacketSoundEffect             = new Packets("net.minecraft.network.play.server.S29PacketSoundEffect",                      "jv", Context.CLIENT);
    public static Packets S01PacketJoinGame                = new Packets("net.minecraft.network.play.server.S01PacketJoinGame",                         "jw", Context.CLIENT);
    public static Packets S34PacketMaps                    = new Packets("net.minecraft.network.play.server.S34PacketMaps",                             "jx", Context.CLIENT);
    public static Packets S14PacketEntity                  = new Packets("net.minecraft.network.play.server.S14PacketEntity",                           "jy", Context.CLIENT);
    public static Packets S15PacketEntityRelMove           = new Packets("net.minecraft.network.play.server.S14PacketEntity$S15PacketEntityRelMove",    "jz", Context.CLIENT);
    public static Packets S17PacketEntityLookMove          = new Packets("net.minecraft.network.play.server.S14PacketEntity$S17PacketEntityLookMove",   "ka", Context.CLIENT);
    public static Packets S16PacketEntityLook              = new Packets("net.minecraft.network.play.server.S14PacketEntity$S16PacketEntityLook",       "kb", Context.CLIENT);
    public static Packets S36PacketSignEditorOpen          = new Packets("net.minecraft.network.play.server.S36PacketSignEditorOpen",                   "kc", Context.CLIENT);
    public static Packets S39PacketPlayerAbilities         = new Packets("net.minecraft.network.play.server.S39PacketPlayerAbilities",                  "kd", Context.CLIENT);
    public static Packets S42PacketCombatEvent             = new Packets("net.minecraft.network.play.server.S42PacketCombatEvent",                      "ke", Context.CLIENT);
    public static Packets S38PacketPlayerListItem          = new Packets("net.minecraft.network.play.server.S38PacketPlayerListItem",                   "kh", Context.CLIENT);
    public static Packets S0APacketUseBed                  = new Packets("net.minecraft.network.play.server.S0APacketUseBed",                           "kl", Context.CLIENT);
    public static Packets S13PacketDestroyEntities         = new Packets("net.minecraft.network.play.server.S13PacketDestroyEntities",                  "km", Context.CLIENT);
    public static Packets S1EPacketRemoveEntityEffect      = new Packets("net.minecraft.network.play.server.S1EPacketRemoveEntityEffect",               "kn", Context.CLIENT);
    public static Packets S48PacketResourcePackSend        = new Packets("net.minecraft.network.play.server.S48PacketResourcePackSend",                 "ko", Context.CLIENT);
    public static Packets S07PacketRespawn                 = new Packets("net.minecraft.network.play.server.S07PacketRespawn",                          "kp", Context.CLIENT);
    public static Packets S19PacketEntityHeadLook          = new Packets("net.minecraft.network.play.server.S19PacketEntityHeadLook",                   "kq", Context.CLIENT);
    public static Packets S44PacketWorldBorder             = new Packets("net.minecraft.network.play.server.S44PacketWorldBorder",                      "kr", Context.CLIENT);
    public static Packets S43PacketCamera                  = new Packets("net.minecraft.network.play.server.S43PacketCamera",                           "ku", Context.CLIENT);
    public static Packets S09PacketHeldItemChange          = new Packets("net.minecraft.network.play.server.S09PacketHeldItemChange",                   "kv", Context.CLIENT);
    public static Packets S3DPacketDisplayScoreboard       = new Packets("net.minecraft.network.play.server.S3DPacketDisplayScoreboard",                "kw", Context.CLIENT);
    public static Packets S1CPacketEntityMetadata          = new Packets("net.minecraft.network.play.server.S1CPacketEntityMetadata",                   "kx", Context.CLIENT);
    public static Packets S1BPacketEntityAttach            = new Packets("net.minecraft.network.play.server.S1BPacketEntityAttach",                     "ky", Context.CLIENT);
    public static Packets S12PacketEntityVelocity          = new Packets("net.minecraft.network.play.server.S12PacketEntityVelocity",                   "kz", Context.CLIENT);
    public static Packets S04PacketEntityEquipment         = new Packets("net.minecraft.network.play.server.S04PacketEntityEquipment",                  "la", Context.CLIENT);
    public static Packets S1FPacketSetExperience           = new Packets("net.minecraft.network.play.server.S1FPacketSetExperience",                    "lb", Context.CLIENT);
    public static Packets S06PacketUpdateHealth            = new Packets("net.minecraft.network.play.server.S06PacketUpdateHealth",                     "lc", Context.CLIENT);
    public static Packets S3BPacketScoreboardObjective     = new Packets("net.minecraft.network.play.server.S3BPacketScoreboardObjective",              "ld", Context.CLIENT);
    public static Packets S3EPacketTeams                   = new Packets("net.minecraft.network.play.server.S3EPacketTeams",                            "le", Context.CLIENT);
    public static Packets S3CPacketUpdateScore             = new Packets("net.minecraft.network.play.server.S3CPacketUpdateScore",                      "lf", Context.CLIENT);
    public static Packets S05PacketSpawnPosition           = new Packets("net.minecraft.network.play.server.S05PacketSpawnPosition",                    "lh", Context.CLIENT);
    public static Packets S03PacketTimeUpdate              = new Packets("net.minecraft.network.play.server.S03PacketTimeUpdate",                       "li", Context.CLIENT);
    public static Packets S45PacketTitle                   = new Packets("net.minecraft.network.play.server.S45PacketTitle",                            "lj", Context.CLIENT);
    public static Packets S33PacketUpdateSign              = new Packets("net.minecraft.network.play.server.S33PacketUpdateSign",                       "ll", Context.CLIENT);
    public static Packets S47PacketPlayerListHeaderFooter  = new Packets("net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter",           "lm", Context.CLIENT);
    public static Packets S0DPacketCollectItem             = new Packets("net.minecraft.network.play.server.S0DPacketCollectItem",                      "ln", Context.CLIENT);
    public static Packets S18PacketEntityTeleport          = new Packets("net.minecraft.network.play.server.S18PacketEntityTeleport",                   "lo", Context.CLIENT);
    public static Packets S20PacketEntityProperties        = new Packets("net.minecraft.network.play.server.S20PacketEntityProperties",                 "lp", Context.CLIENT);
    public static Packets S1DPacketEntityEffect            = new Packets("net.minecraft.network.play.server.S1DPacketEntityEffect",                     "lr", Context.CLIENT);
    public static Packets C14PacketTabComplete             = new Packets("net.minecraft.network.play.client.C14PacketTabComplete",                      "lt", Context.SERVER);
    public static Packets C01PacketChatMessage             = new Packets("net.minecraft.network.play.client.C01PacketChatMessage",                      "lu", Context.SERVER);
    public static Packets C16PacketClientStatus            = new Packets("net.minecraft.network.play.client.C16PacketClientStatus",                     "lv", Context.SERVER);
    public static Packets C15PacketClientSettings          = new Packets("net.minecraft.network.play.client.C15PacketClientSettings",                   "lx", Context.SERVER);
    public static Packets C0FPacketConfirmTransaction      = new Packets("net.minecraft.network.play.client.C0FPacketConfirmTransaction",               "ly", Context.SERVER);
    public static Packets C11PacketEnchantItem             = new Packets("net.minecraft.network.play.client.C11PacketEnchantItem",                      "lz", Context.SERVER);
    public static Packets C0EPacketClickWindow             = new Packets("net.minecraft.network.play.client.C0EPacketClickWindow",                      "ma", Context.SERVER);
    public static Packets C0DPacketCloseWindow             = new Packets("net.minecraft.network.play.client.C0DPacketCloseWindow",                      "mb", Context.SERVER);
    public static Packets C17PacketCustomPayload           = new Packets("net.minecraft.network.play.client.C17PacketCustomPayload",                    "mc", Context.SERVER);
    public static Packets C02PacketUseEntity               = new Packets("net.minecraft.network.play.client.C02PacketUseEntity",                        "md", Context.SERVER);
    public static Packets C00PacketKeepAlive               = new Packets("net.minecraft.network.play.client.C00PacketKeepAlive",                        "mf", Context.SERVER);
    public static Packets C03PacketPlayer                  = new Packets("net.minecraft.network.play.client.C03PacketPlayer",                           "mg", Context.SERVER);
    public static Packets C04PacketPlayerPosition          = new Packets("net.minecraft.network.play.client.C03PacketPlayer$C04PacketPlayerPosition",   "mh", Context.SERVER);
    public static Packets C06PacketPlayerPosLook           = new Packets("net.minecraft.network.play.client.C03PacketPlayer$C06PacketPlayerPosLook",    "mi", Context.SERVER);
    public static Packets C05PacketPlayerLook              = new Packets("net.minecraft.network.play.client.C03PacketPlayer$C05PacketPlayerLook",       "mj", Context.SERVER);
    public static Packets C13PacketPlayerAbilities         = new Packets("net.minecraft.network.play.client.C13PacketPlayerAbilities",                  "mk", Context.SERVER);
    public static Packets C07PacketPlayerDigging           = new Packets("net.minecraft.network.play.client.C07PacketPlayerDigging",                    "ml", Context.SERVER);
    public static Packets C0BPacketEntityAction            = new Packets("net.minecraft.network.play.client.C0BPacketEntityAction",                     "mn", Context.SERVER);
    public static Packets C0CPacketInput                   = new Packets("net.minecraft.network.play.client.C0CPacketInput",                            "mp", Context.SERVER);
    public static Packets C19PacketResourcePackStatus      = new Packets("net.minecraft.network.play.client.C19PacketResourcePackStatus",               "mq", Context.SERVER);
    public static Packets C09PacketHeldItemChange          = new Packets("net.minecraft.network.play.client.C09PacketHeldItemChange",                   "ms", Context.SERVER);
    public static Packets C10PacketCreativeInventoryAction = new Packets("net.minecraft.network.play.client.C10PacketCreativeInventoryAction",          "mt", Context.SERVER);
    public static Packets C12PacketUpdateSign              = new Packets("net.minecraft.network.play.client.C12PacketUpdateSign",                       "mu", Context.SERVER);
    public static Packets C0APacketAnimation               = new Packets("net.minecraft.network.play.client.C0APacketAnimation",                        "mv", Context.SERVER);
    public static Packets C18PacketSpectate                = new Packets("net.minecraft.network.play.client.C18PacketSpectate",                         "mw", Context.SERVER);
    public static Packets C08PacketPlayerBlockPlacement    = new Packets("net.minecraft.network.play.client.C08PacketPlayerBlockPlacement",             "mx", Context.SERVER);
    public static Packets C00Handshake                     = new Packets("net.minecraft.network.handshake.client.C00Handshake",                         "mz", Context.SERVER);
    public static Packets S02PacketLoginSuccess            = new Packets("net.minecraft.network.login.server.S02PacketLoginSuccess",                    "nd", Context.CLIENT);
    public static Packets S01PacketEncryptionRequest       = new Packets("net.minecraft.network.login.server.S01PacketEncryptionRequest",               "ne", Context.CLIENT);
    public static Packets S03PacketEnableCompression       = new Packets("net.minecraft.network.login.server.S03PacketEnableCompression",               "nf", Context.CLIENT);
    public static Packets S00PacketDisconnect              = new Packets("net.minecraft.network.login.server.S00PacketDisconnect",                      "ng", Context.CLIENT);
    public static Packets C00PacketLoginStart              = new Packets("net.minecraft.network.login.client.C00PacketLoginStart",                      "ni", Context.SERVER);
    public static Packets C01PacketEncryptionResponse      = new Packets("net.minecraft.network.login.client.C01PacketEncryptionResponse",              "nj", Context.SERVER);
    public static Packets S01PacketPong                    = new Packets("net.minecraft.network.status.server.S01PacketPong",                           "nn", Context.CLIENT);
    public static Packets S00PacketServerInfo              = new Packets("net.minecraft.network.status.server.S00PacketServerInfo",                     "no", Context.CLIENT);
    public static Packets C01PacketPing                    = new Packets("net.minecraft.network.status.client.C01PacketPing",                           "nw", Context.SERVER);
    public static Packets C00PacketServerQuery             = new Packets("net.minecraft.network.status.client.C00PacketServerQuery",                    "nx", Context.SERVER);

    // CHECKSTYLE:ON

    public static final Packets[] packets = new Packets[] {
            S08PacketPlayerPosLook,
            S0EPacketSpawnObject,
            S11PacketSpawnExperienceOrb,
            S2CPacketSpawnGlobalEntity,
            S0FPacketSpawnMob,
            S10PacketSpawnPainting,
            S0CPacketSpawnPlayer,
            S0BPacketAnimation,
            S37PacketStatistics,
            S25PacketBlockBreakAnim,
            S35PacketUpdateTileEntity,
            S24PacketBlockAction,
            S23PacketBlockChange,
            S41PacketServerDifficulty,
            S3APacketTabComplete,
            S02PacketChat,
            S22PacketMultiBlockChange,
            S32PacketConfirmTransaction,
            S2EPacketCloseWindow,
            S2DPacketOpenWindow,
            S30PacketWindowItems,
            S31PacketWindowProperty,
            S2FPacketSetSlot,
            S3FPacketCustomPayload,
            S40PacketDisconnect,
            S19PacketEntityStatus,
            S49PacketUpdateEntityNBT,
            S27PacketExplosion,
            S46PacketSetCompressionLevel,
            S2BPacketChangeGameState,
            S00PacketKeepAlive,
            S21PacketChunkData,
            S26PacketMapChunkBulk,
            S28PacketEffect,
            S2APacketParticles,
            S29PacketSoundEffect,
            S01PacketJoinGame,
            S34PacketMaps,
            S14PacketEntity,
            S15PacketEntityRelMove,
            S17PacketEntityLookMove,
            S16PacketEntityLook,
            S36PacketSignEditorOpen,
            S39PacketPlayerAbilities,
            S42PacketCombatEvent,
            S38PacketPlayerListItem,
            S0APacketUseBed,
            S13PacketDestroyEntities,
            S1EPacketRemoveEntityEffect,
            S48PacketResourcePackSend,
            S07PacketRespawn,
            S19PacketEntityHeadLook,
            S44PacketWorldBorder,
            S43PacketCamera,
            S09PacketHeldItemChange,
            S3DPacketDisplayScoreboard,
            S1CPacketEntityMetadata,
            S1BPacketEntityAttach,
            S12PacketEntityVelocity,
            S04PacketEntityEquipment,
            S1FPacketSetExperience,
            S06PacketUpdateHealth,
            S3BPacketScoreboardObjective,
            S3EPacketTeams,
            S3CPacketUpdateScore,
            S05PacketSpawnPosition,
            S03PacketTimeUpdate,
            S45PacketTitle,
            S33PacketUpdateSign,
            S47PacketPlayerListHeaderFooter,
            S0DPacketCollectItem,
            S18PacketEntityTeleport,
            S20PacketEntityProperties,
            S1DPacketEntityEffect,
            C14PacketTabComplete,
            C01PacketChatMessage,
            C16PacketClientStatus,
            C15PacketClientSettings,
            C0FPacketConfirmTransaction,
            C11PacketEnchantItem,
            C0EPacketClickWindow,
            C0DPacketCloseWindow,
            C17PacketCustomPayload,
            C02PacketUseEntity,
            C00PacketKeepAlive,
            C03PacketPlayer,
            C04PacketPlayerPosition,
            C06PacketPlayerPosLook,
            C05PacketPlayerLook,
            C13PacketPlayerAbilities,
            C07PacketPlayerDigging,
            C0BPacketEntityAction,
            C0CPacketInput,
            C19PacketResourcePackStatus,
            C09PacketHeldItemChange,
            C10PacketCreativeInventoryAction,
            C12PacketUpdateSign,
            C0APacketAnimation,
            C18PacketSpectate,
            C08PacketPlayerBlockPlacement,
            C00Handshake,
            S02PacketLoginSuccess,
            S01PacketEncryptionRequest,
            S03PacketEnableCompression,
            S00PacketDisconnect,
            C00PacketLoginStart,
            C01PacketEncryptionResponse,
            S01PacketPong,
            S00PacketServerInfo,
            C01PacketPing,
            C00PacketServerQuery,
    };

    private static int nextPacketIndex;

    private final String shortName;

    private final int index;

    private final Context context;

    private Packets(String seargeName, String obfName, Context context)
    {
        super(seargeName, obfName);

        this.shortName = seargeName.substring(Math.max(seargeName.lastIndexOf('.'), seargeName.lastIndexOf('$')) + 1);
        this.index = Packets.nextPacketIndex++;
        Packets.packetMap.put(this.shortName, this);
        this.context = context;
    }

    public int getIndex()
    {
        return this.index;
    }

    public String getShortName()
    {
        return this.shortName;
    }

    public Context getContext()
    {
        return this.context;
    }

    public static int indexOf(String packetClassName)
    {
        for (Packets packet : Packets.packets)
        {
            if (packet.name.equals(packetClassName) || packet.shortName.equals(packetClassName) || packet.obf.equals(packetClassName))
            {
                return packet.index;
            }
        }

        return -1;
    }

    public static int count()
    {
        return Packets.nextPacketIndex;
    }

    /**
     * @param name
     */
    public static Packets getByName(String name)
    {
        return Packets.packetMap.get(name);
    }
}
