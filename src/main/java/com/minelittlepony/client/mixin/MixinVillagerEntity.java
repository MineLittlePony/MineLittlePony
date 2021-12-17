package com.minelittlepony.client.mixin;

import com.kenza.VillagerEntityExtension;
import com.kenza.VillagerEntityExtensionImpl;
import com.minelittlepony.api.pony.meta.Race;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VillagerEntity.class)
public abstract class MixinVillagerEntity extends MerchantEntity implements InteractionObserver, VillagerDataContainer, VillagerEntityExtension {

    @Shadow
    public abstract VillagerData getVillagerData();

    protected VillagerEntityExtensionImpl villagerEntityExtension;

    public MixinVillagerEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }


    @Inject(method = "initialize", at = @At("TAIL"))
    public void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        villagerEntityExtension.onSpawn();
    }


    @Inject(method = "setVillagerData", at = @At("HEAD"))
    private void setVillagerDataBefore(VillagerData villagerData,CallbackInfo ci) {
        villagerEntityExtension.setVillagerDataBefore(villagerData, getVillagerData());
    }

    @Inject(method = "setVillagerData", at = @At("TAIL"))
    private void setVillagerDataAfter(VillagerData villagerData,CallbackInfo ci) {
        villagerEntityExtension.setVillagerDataAfter();
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


    @Inject(method = "onGrowUp", at = @At("TAIL"))
    public void onGrowUp(CallbackInfo ci) {
        villagerEntityExtension.onGrowUp();
    }


    @Override
    public void setCustomName(boolean force) {
        villagerEntityExtension.setCustomName(force);
    }

    @Override
    public int getPonySkinID() {
        return villagerEntityExtension.getPonySkinID();
    }

    @Override
    public void setPonySkinID(int ponySkinID) {
        villagerEntityExtension.setPonySkinID(ponySkinID);
    }

    @NotNull
    @Override
    public Race getPonyRace() {
        return villagerEntityExtension.getPonyRace();
    }

    @Override
    public void setPonyRace(@NotNull Race ponyRace) {
        villagerEntityExtension.setPonyRace(ponyRace);
    }
}


