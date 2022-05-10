package com.kenza

import com.minelittlepony.api.pony.meta.Race
import com.minelittlepony.common.event.ScreenInitCallback
import dev.emi.trinkets.api.TrinketsApi
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.OpenToLanScreen
import net.minecraft.client.gui.screen.SaveLevelScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.world.CreateWorldScreen
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.function.Predicate


//summon minecraft:villager ~ ~ ~ {VillagerData:{level:1,profession:"minecraft:nitwit",type:"minecraft:desert"}}
object KenzaInjector {

    val LOGGER = LogManager.getLogger()

    val resData = HashMap<String, Identifier>()
    val pathHashMap = HashMap<Int, String>()


    private var initTitleCounter = 0

    fun init() {

        ScreenInitCallback.EVENT.register(ScreenInitCallback { screen: Screen?, buttons: ScreenInitCallback.ButtonList? ->
//            this.onScreenInit(
//                screen,
//                buttons
//            )
        })

        onEntityLoaded()
    }


    private fun onScreenInit(screen: Screen?, buttons: ScreenInitCallback.ButtonList?) {
        if (screen is CreateWorldScreen) {

        }
        if (screen is OpenToLanScreen) {
//            buttons.addButton(Button(screen.width / 2 - 155, 130, 150, 20))
//                .onClick { b: Button? -> MinecraftClient.getInstance().setScreen(LanSettingsScreen(screen)) }
//                .style.text = TranslatableText("unicopia.options.title")
        }
        if (screen is TitleScreen) {
            //open world after start minecraft
            initTitleCounter++
            if (initTitleCounter == 2) {
                val client = MinecraftClient.getInstance()
                client.levelStorage.levelList.firstOrNull()?.let { level ->
                    client.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
                    if (client.levelStorage.levelExists(level.name)) {
                        client.setScreenAndRender(SaveLevelScreen(TranslatableText("selectWorld.data_read")))
                        client.startIntegratedServer(level.name)
                    }
                }
            }
        }
    }

    private fun onEntityLoaded() {

        ServerEntityEvents.ENTITY_LOAD.register(ServerEntityEvents.Load { entity: Entity?, serverWorld: ServerWorld? ->
            entity?.toVillagerEntityExtension()?.setCustomName(false)
//            entity?.toVillagerEntityExtension()?.setCustomName(true)
        })
//
//        ServerEntityEvents.ENTITY_UNLOAD.register(ServerEntityEvents.Unload { entity: Entity?, serverWorld: ServerWorld? ->
//            val x1 = 2
//            entity?.toVillagerSkinContainer()?.initSkinID()
//        })
//
        ClientEntityEvents.ENTITY_LOAD.register(ClientEntityEvents.Load(object : (Entity, ClientWorld) -> Unit {
            override fun invoke(entity: Entity, p2: ClientWorld) {
//                entity.toVillagerSkinContainer()?.test2()
            }
        }))
//
//        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler: ServerPlayNetworkHandler, sender: PacketSender, server: MinecraftServer? ->
//            val x1 = handler.player
//            x1.cameraEntity
//        })
    }

    fun findTexturePath(entity: Entity): String {
        val skidID = entity.toVillagerEntityExtension()?.ponySkinID ?: ""
        return "textures/entity/villager/all/$skidID.png"
    }

    fun getOrCreateIdentifier(path: String): Identifier {
        val identifier = resData.get(path)
        identifier?.let {
            return identifier
        }
        Identifier("minelittlepony", path).let {
            resData.put(path, it)
            return it
        }
    }


    fun findTexture(entity: Entity): Identifier {
        findTexturePath(entity).let { path ->
            return getOrCreateIdentifier(path)
        }
    }

    fun findTexture(category: String, identifier: Identifier, entityType: String): Identifier {
        val path = "textures/entity/" + entityType + "/" + category + "/" + identifier.path + ".png"
        return getOrCreateIdentifier(path)
    }


    fun getOverridePonyRaceOfEntity(entity: Entity): Race? {

        return if (entity.canLoadDynamicPonySkin()) {
            entity.toVillagerEntityExtension()?.ponyRace
        } else {
            null
        }
    }


    fun LivingEntity.entityHasElytraFromTrinkets(): Boolean {
        return try {
            TrinketsApi.getTrinketComponent(this).value?.getEquipped { stack: ItemStack ->
                stack.isOf(
                    Items.ELYTRA
                )
            }?.isNotEmpty() ?: false
        } catch (e: NoClassDefFoundError) {
            false
        }
    }

}

fun Entity.canLoadDynamicPonySkin(): Boolean {
    return (this.toVillagerEntityExtension()?.ponySkinID ?: -1) >= 0
}

val <T> Optional<T>.value: T?
    get() = orElse(null)
