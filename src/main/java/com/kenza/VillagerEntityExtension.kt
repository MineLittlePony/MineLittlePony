package com.kenza

import drawer.getFrom
import drawer.put
import drawer.readFrom
import drawer.write
import kotlinx.serialization.Serializable
import net.minecraft.entity.Entity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import java.io.File

@Serializable
data class VillagerEntityExtraData(var ponySkinID: Int = -1)

interface VillagerEntityExtension {
    var ponySkinID: Int
}

class VillagerEntityExtensionImpl(val entity: Entity) : VillagerEntityExtension {

    private var villagerProperty: VillagerEntityExtraData
        set(value) {
            entity.dataTracker.set(VILLAGER_EXTRA_PROPERTY, value)
        }
        get() = entity.dataTracker.get(VILLAGER_EXTRA_PROPERTY)


    override var ponySkinID: Int
        get() = villagerProperty.ponySkinID
        set(value) {
            villagerProperty = villagerProperty.copy(ponySkinID = value)
        }

    fun onSpawn() {
        ckeckAndSetSkinId()
    }


    fun writeNbt(tag: NbtCompound): NbtCompound {

        val data = entity.dataTracker.get(VILLAGER_EXTRA_PROPERTY)
        VillagerEntityExtraData.serializer().put(data, inTag = tag, key = NBT_VILLAGER_EXTRA_DATA_KEY)
        return tag
    }

    fun readNbt(tag: NbtCompound) {
        VillagerEntityExtraData.serializer().getFrom(tag, key = NBT_VILLAGER_EXTRA_DATA_KEY).let { data ->
            entity.dataTracker.set(VILLAGER_EXTRA_PROPERTY, data)
            ckeckAndSetSkinId()
        }
    }

    fun initDataTracker() {
        TrackedDataHandlerRegistry.register(VILLAGER_EXTRA_DATA)
        entity.dataTracker.startTracking(VILLAGER_EXTRA_PROPERTY, VillagerEntityExtraData())
    }


    private fun ckeckAndSetSkinId(){
        if (ponySkinID < 0) {
            ponySkinID = random(29, PONIES_SKINES_COUNT)
        }
    }

    private fun random(from: Int, to: Int) = (Math.random() * (to - from) + from).toInt()


    companion object {

        val NBT_VILLAGER_EXTRA_DATA_KEY = "NBT_VILLAGER_EXTRA_DATA_KEY"

        val PONIES_SKINES_COUNT: Int by lazy {
            File(this.javaClass.classLoader.getResource(PATH_ASSET_FOLDER_ALL_PONIES_SKINS).toURI()).list().size
        }

        val VILLAGER_EXTRA_DATA: TrackedDataHandler<VillagerEntityExtraData> =
            object : TrackedDataHandler<VillagerEntityExtraData> {

                override fun write(buf: PacketByteBuf, value: VillagerEntityExtraData) {
                    VillagerEntityExtraData.serializer().write(value, toBuf = buf)
                }

                override fun read(buf: PacketByteBuf): VillagerEntityExtraData {
                    return VillagerEntityExtraData.serializer().readFrom(buf = buf)
                }

                override fun copy(value: VillagerEntityExtraData): VillagerEntityExtraData {
                    return value
                }

            }

        val VILLAGER_EXTRA_PROPERTY = DataTracker.registerData(VillagerEntity::class.java, VILLAGER_EXTRA_DATA)
    }


}


fun Entity.toVillagerSkinContainer(): VillagerEntityExtension? {
    return this as? VillagerEntityExtension
}

