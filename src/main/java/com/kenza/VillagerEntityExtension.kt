package com.kenza

import com.minelittlepony.api.pony.meta.Race
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
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.LiteralText
import net.minecraft.village.VillagerData
import net.minecraft.village.VillagerProfession

@Serializable
data class VillagerEntityExtraData(
    var ponySkinID: Int = -1,
    val ponyRace: Race = Race.HUMAN,
    val firstName: String = "",
    val secondName: String = ""
)

interface VillagerEntityExtension {
    fun setCustomName(force: Boolean)

    var ponySkinID: Int
    var ponyRace: Race
    val ponyName: String
    val professionName: String
}

class VillagerEntityExtensionImpl(val entity: Entity) : VillagerEntityExtension {

    private var flagProfessionWasChanged = false

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
    override var ponyRace: Race
        get() = villagerProperty.ponyRace
        set(value) {
            villagerProperty = villagerProperty.copy(ponyRace = value)
        }

    override val ponyName: String
        get() = getPonyCustomName()

    override val professionName: String
        get() = getProfessionNameValue()



    var firstName: String
        get() = villagerProperty.firstName
        set(value) {
            villagerProperty = villagerProperty.copy(firstName = value)
        }

    var secondName: String
        get() = villagerProperty.secondName
        set(value) {
            villagerProperty = villagerProperty.copy(secondName = value)
        }


    override fun setCustomName(force: Boolean) {
        //&& (entity as? VillagerEntity)?.isBaby?.not() ?: false
        if ((!entity.hasCustomName() || (force)) ) {
            entity.customName = LiteralText(getPonyCustomName())
            entity.isCustomNameVisible = true
        }

        entity.customName = LiteralText(getPonyCustomName())
        entity.isCustomNameVisible = true
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

    fun setVillagerDataBefore(
        newVillagerData: VillagerData,
        oldVillagerData: VillagerData,
    ) {
        if (newVillagerData.profession !== oldVillagerData.profession) {
            flagProfessionWasChanged = true
        }
    }

    fun setVillagerDataAfter() {
//        if (flagProfessionWasChanged) {
//            setCustomName(true)
//            flagProfessionWasChanged = false
//        }
    }

    fun onGrowUp() {

        val villager = (entity as? VillagerEntity) ?: return

//        if (villager.world is ServerWorld) {
//            setCustomName(true)
//        }


    }

    private fun getProfessionNameValue(): String {
        val profession = (entity as? VillagerEntity)?.villagerData?.profession

        val professionName = if (profession != VillagerProfession.NONE) {
            profession.toString().upperFirstLetter()
        } else {
            ""
        }
        return professionName
    }


    private fun getPonyCustomName(): String {
//        val profession = (entity as? VillagerEntity)?.villagerData?.profession

//        val professionName = if (profession != VillagerProfession.NONE) {
//            "(${profession.toString().upperFirstLetter()})"
//        } else {
//            ""
//        }
//        return "$firstName $secondName $professionName".trim()
        return "$firstName $secondName".trim()
    }

    private fun ckeckAndSetSkinId() {
        if (ponySkinID < 0) {
            ponySkinID = random(1, PONIES_SKINS_COUNT)
        }

        if (ponyRace == Race.HUMAN) {
            val ponyRaceChance = random(1, 100)

            ponyRace = when (ponyRaceChance) {
                in 0..20 -> Race.EARTH
                in 31..60 -> Race.PEGASUS
                in 61..100 -> Race.UNICORN
                else -> Race.UNICORN
            }
        }

        if (firstName.isEmpty()) {
            firstName = PonyNames.generateFirstName()
        }

        if (secondName.isEmpty()) {
            secondName = PonyNames.generateSecondName()
        }

    }


    companion object {

        val NBT_VILLAGER_EXTRA_DATA_KEY = "NBT_VILLAGER_EXTRA_DATA_KEY"

        val PONIES_SKINS_COUNT: Int by lazy {
//            95
            397
//            File(this.javaClass.classLoader.getResource(PATH_ASSET_FOLDER_ALL_PONIES_SKINS).toURI()).list().size
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


fun Entity.toVillagerEntityExtension(): VillagerEntityExtension? {
    return this as? VillagerEntityExtension
}

