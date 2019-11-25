package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.entity.ModelBreezie;
import com.minelittlepony.client.model.entity.ModelEnderStallion;
import com.minelittlepony.client.model.entity.ModelGuardianPony;
import com.minelittlepony.client.model.entity.ModelIllagerPony;
import com.minelittlepony.client.model.entity.ModelPillagerPony;
import com.minelittlepony.client.model.entity.ModelSkeletonPony;
import com.minelittlepony.client.model.entity.ModelVillagerPony;
import com.minelittlepony.client.model.entity.ModelWitchPony;
import com.minelittlepony.client.model.entity.ModelZombiePony;
import com.minelittlepony.client.model.entity.ModelZombieVillagerPony;
import com.minelittlepony.client.model.entity.race.ModelAlicorn;
import com.minelittlepony.client.model.entity.race.ModelChangeling;
import com.minelittlepony.client.model.entity.race.ModelPegasus;
import com.minelittlepony.client.model.entity.race.ModelUnicorn;
import com.minelittlepony.client.model.entity.race.ModelZebra;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.mson.api.MsonModel;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModelType {
    public static final ModelKey<ModelVillagerPony<?>> VILLAGER = register("villager", ModelVillagerPony::new);
    public static final ModelKey<ModelWitchPony> WITCH = register("witch", ModelWitchPony::new);
    public static final ModelKey<ModelZombiePony<?>> ZOMBIE = register("zombie", ModelZombiePony::new);
    public static final ModelKey<ModelZombieVillagerPony> ZOMBIE_VILLAGER = register("zombie_villager", ModelZombieVillagerPony::new);
    public static final ModelKey<ModelSkeletonPony<?>> SKELETON = register("skeleton", ModelSkeletonPony::new);
    public static final ModelKey<ModelPillagerPony<?>> PILLAGER = register("pillager", ModelPillagerPony::new);
    public static final ModelKey<ModelIllagerPony<?>> ILLAGER = register("illager", ModelIllagerPony::new);
    public static final ModelKey<ModelGuardianPony> GUARDIAN = register("guardian", ModelGuardianPony::new);
    public static final ModelKey<ModelEnderStallion> ENDERMAN = register("enderman", ModelEnderStallion::new);
    public static final ModelKey<ModelBreezie<VexEntity>> BREEZIE = register("breezie", ModelBreezie::new);

    public static final PlayerModelKey<ModelAlicorn<?>> ALICORN = registerPlayer("alicorn", ModelAlicorn::new);
    public static final PlayerModelKey<ModelUnicorn<?>> UNICORN = registerPlayer("unicorn", ModelUnicorn::new);
    public static final PlayerModelKey<ModelPegasus<?>> PEGASUS = registerPlayer("pegasus", ModelPegasus::new);
    public static final PlayerModelKey<ModelPegasus<?>> BAT_PONY = registerPlayer("batpony", ModelPegasus::new);
    public static final PlayerModelKey<ModelChangeling<?>> CHANGELING = registerPlayer("changeling", ModelChangeling::new);
    public static final PlayerModelKey<ModelZebra<?>> ZEBRA = registerPlayer("zebra", ModelZebra::new);

    static <T extends Model & MsonModel> PlayerModelKey<T> registerPlayer(String name, Function<Boolean, T> constructor) {
        return new PlayerModelKey<>(new Identifier("minelittlepony", "races/" + name), constructor);
    }

    static <T extends Model & MsonModel> ModelKey<T> register(String name, Supplier<T> constructor) {
        return Mson.getInstance().registerModel(new Identifier("minelittlepony", name), constructor);
    }

    public static void bootstrap() {};
}
