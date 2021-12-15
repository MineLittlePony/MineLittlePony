package com.minelittlepony.client.mixin;

import com.kenza.VillagerEntityExtension;
import com.kenza.VillagerEntityExtensionImpl;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class MixinVillagerEntity extends MerchantEntity implements InteractionObserver, VillagerDataContainer, VillagerEntityExtension {

    protected VillagerEntityExtensionImpl villagerEntityExtension;

    public MixinVillagerEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "initialize", at = @At("TAIL"))
    public void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        villagerEntityExtension.onSpawn();
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTracker(CallbackInfo ci) {
        villagerEntityExtension = new VillagerEntityExtensionImpl(this);
        villagerEntityExtension.initDataTracker();
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        villagerEntityExtension.writeNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        villagerEntityExtension.readNbt(nbt);
    }


    @Override
    public int getPonySkinID() {
        return villagerEntityExtension.getPonySkinID();
    }

    @Override
    public void setPonySkinID(int ponySkinID) {
        villagerEntityExtension.setPonySkinID(ponySkinID);
    }
}


