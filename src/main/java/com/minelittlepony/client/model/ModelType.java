package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.entity.LivingEntity;
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
import com.minelittlepony.client.model.entity.race.ModelEarthPony;
import com.minelittlepony.client.model.entity.race.ModelPegasus;
import com.minelittlepony.client.model.entity.race.ModelUnicorn;
import com.minelittlepony.client.model.entity.race.ModelZebra;
import com.minelittlepony.client.render.entity.RenderPonyPlayer;
import com.minelittlepony.client.render.entity.RenderSeaponyPlayer;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.mson.api.MsonModel;
import com.minelittlepony.pony.meta.Race;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModelType {

    private static final Map<Race, PlayerModelKey<?, ?>> PLAYER_MODELS = new HashMap<>();

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

    public static final PlayerModelKey<?, ModelAlicorn<?>> ALICORN = registerPlayer("alicorn", Race.ALICORN, ModelAlicorn::new);
    public static final PlayerModelKey<?, ModelUnicorn<?>> UNICORN = registerPlayer("unicorn", Race.UNICORN, ModelUnicorn::new);
    public static final PlayerModelKey<?, ModelUnicorn<?>> KIRIN = registerPlayer("kirin", Race.KIRIN, ModelUnicorn::new);
    public static final PlayerModelKey<?, ModelPegasus<?>> PEGASUS = registerPlayer("pegasus", Race.PEGASUS, ModelPegasus::new);
    public static final PlayerModelKey<?, ModelPegasus<?>> GRYPHON = registerPlayer("gryphon", Race.GRYPHON, ModelPegasus::new);
    public static final PlayerModelKey<?, ModelPegasus<?>> HIPPOGRIFF = registerPlayer("hippogriff", Race.HIPPOGRIFF, ModelPegasus::new);
    public static final PlayerModelKey<?, ModelEarthPony<?>> EARTH_PONY = registerPlayer("earth_pony", Race.EARTH, ModelEarthPony::new);
    public static final PlayerModelKey<?, ModelEarthPony<?>> SEA_PONY = registerPlayer("sea_pony", Race.SEAPONY, ModelEarthPony::new, RenderSeaponyPlayer::new);
    public static final PlayerModelKey<?, ModelPegasus<?>> BAT_PONY = registerPlayer("bat_pony", Race.BATPONY, ModelPegasus::new);
    public static final PlayerModelKey<?, ModelChangeling<?>> CHANGELING = registerPlayer("changeling", Race.CHANGELING, ModelChangeling::new);
    public static final PlayerModelKey<?, ModelChangeling<?>> CHANGEDLING = registerPlayer("reformed_changeling", Race.CHANGEDLING, ModelChangeling::new);
    public static final PlayerModelKey<?, ModelZebra<?>> ZEBRA = registerPlayer("zebra", Race.ZEBRA, ModelZebra::new);

    static <E extends LivingEntity, T extends Model & MsonModel> PlayerModelKey<E, T> registerPlayer(String name, Race race, Function<Boolean, T> constructor) {
        return registerPlayer(name, race, constructor, RenderPonyPlayer::new);
    }

    @SuppressWarnings("unchecked")
    static <E extends LivingEntity, T extends Model & MsonModel> PlayerModelKey<E, T> registerPlayer(String name, Race race, Function<Boolean, T> constructor, PlayerModelKey.RendererFactory rendererFactory) {
        return (PlayerModelKey<E, T>)PLAYER_MODELS.computeIfAbsent(race, r -> {
            return new PlayerModelKey<>(new Identifier("minelittlepony", "races/" + name), constructor, rendererFactory);
        });
    }

    static <T extends Model & MsonModel> ModelKey<T> register(String name, Supplier<T> constructor) {
        return Mson.getInstance().registerModel(new Identifier("minelittlepony", name), constructor);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends LivingEntity, T extends Model & MsonModel> PlayerModelKey<E, T> getPlayerModel(Race race) {
        return (PlayerModelKey<E, T>)PLAYER_MODELS.get(race);
    }

    public static void bootstrap() {};
}
