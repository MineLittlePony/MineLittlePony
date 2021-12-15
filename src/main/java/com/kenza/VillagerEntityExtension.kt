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

@Serializable
data class VillagerEntityProperty(var ponySkinID: Int = -1)

interface VillagerEntityExtension {
    var ponySkinID: Int
}

class VillagerEntityExtensionImpl(val entity: Entity) : VillagerEntityExtension {

    private var villagerProperty: VillagerEntityProperty
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
        if(ponySkinID < 0){
            ponySkinID = random(1,20)
        }
    }


    fun writeNbt(tag: NbtCompound): NbtCompound {

        val data = entity.dataTracker.get(VILLAGER_EXTRA_PROPERTY)
        VillagerEntityProperty.serializer().put(data, inTag = tag)
        return tag
    }

    fun readNbt(tag: NbtCompound) {
//        super.fromNbt(tag)
        // Deserialize
        VillagerEntityProperty.serializer().getFrom(tag).let { data ->
            (entity as Entity).dataTracker.set(VILLAGER_EXTRA_PROPERTY, data)
        }
    }

    fun initDataTracker() {
        TrackedDataHandlerRegistry.register(VILLAGER_EXTRA_DATA)
        entity.dataTracker.startTracking(VILLAGER_EXTRA_PROPERTY, VillagerEntityProperty())
    }




    private fun random(from: Int, to: Int) = (Math.random() * (to - from) + from).toInt()


    companion object {

        val VILLAGER_EXTRA_DATA: TrackedDataHandler<VillagerEntityProperty> =
            object : TrackedDataHandler<VillagerEntityProperty> {

                override fun write(buf: PacketByteBuf, value: VillagerEntityProperty) {
                    VillagerEntityProperty.serializer().write(value, toBuf = buf)
                }

                override fun read(buf: PacketByteBuf): VillagerEntityProperty {
                    return VillagerEntityProperty.serializer().readFrom(buf = buf)
                }

                override fun copy(value: VillagerEntityProperty): VillagerEntityProperty {
                    return value
                }

            }

        val VILLAGER_EXTRA_PROPERTY = DataTracker.registerData(VillagerEntity::class.java, VILLAGER_EXTRA_DATA)
    }


}


fun Entity.toVillagerSkinContainer(): VillagerEntityExtension? {
    return this as? VillagerEntityExtension
}

