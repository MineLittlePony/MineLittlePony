package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.model.entity.BreezieModel;
import com.minelittlepony.client.model.entity.EnderStallionModel;
import com.minelittlepony.client.model.entity.GuardianPonyModel;
import com.minelittlepony.client.model.entity.IllagerPonyModel;
import com.minelittlepony.client.model.entity.ParaspriteModel;
import com.minelittlepony.client.model.entity.PiglinPonyModel;
import com.minelittlepony.client.model.entity.PillagerPonyModel;
import com.minelittlepony.client.model.entity.SkeleponyModel;
import com.minelittlepony.client.model.entity.VillagerPonyModel;
import com.minelittlepony.client.model.entity.WitchPonyModel;
import com.minelittlepony.client.model.entity.ZomponyModel;
import com.minelittlepony.client.model.entity.ZomponyVillagerModel;
import com.minelittlepony.client.model.entity.race.AlicornModel;
import com.minelittlepony.client.model.entity.race.ChangelingModel;
import com.minelittlepony.client.model.entity.race.EarthPonyModel;
import com.minelittlepony.client.model.entity.race.PegasusModel;
import com.minelittlepony.client.model.entity.race.SeaponyModel;
import com.minelittlepony.client.model.entity.race.UnicornModel;
import com.minelittlepony.client.model.entity.race.ZebraModel;
import com.minelittlepony.client.model.gear.AbstractGear;
import com.minelittlepony.client.model.gear.ChristmasHat;
import com.minelittlepony.client.model.gear.Muffin;
import com.minelittlepony.client.model.gear.SaddleBags;
import com.minelittlepony.client.model.gear.Stetson;
import com.minelittlepony.client.model.gear.WitchHat;
import com.minelittlepony.client.render.entity.PlayerPonyRenderer;
import com.minelittlepony.client.render.entity.PlayerSeaponyRenderer;
import com.minelittlepony.model.gear.IGear;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.mson.api.MsonModel;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public final class ModelType {

    private static final Map<Race, PlayerModelKey<?, ?>> PLAYER_MODELS = new HashMap<>();
    private static final Map<Wearable, ModelKey<? extends IGear>> GEAR_MODELS = new HashMap<>();

    public static final ModelKey<DJPon3EarsModel> DJ_PON_3 = register("dj_pon_three", DJPon3EarsModel::new);

    public static final ModelKey<VillagerPonyModel<?>> VILLAGER = register("villager", VillagerPonyModel::new);
    public static final ModelKey<WitchPonyModel> WITCH = register("witch", WitchPonyModel::new);
    public static final ModelKey<ZomponyModel<?>> ZOMBIE = register("zombie", ZomponyModel::new);
    public static final ModelKey<PiglinPonyModel> PIGLIN = register("piglin", PiglinPonyModel::new);
    public static final ModelKey<ZomponyVillagerModel> ZOMBIE_VILLAGER = register("zombie_villager", ZomponyVillagerModel::new);
    public static final ModelKey<SkeleponyModel<?>> SKELETON = register("skeleton", SkeleponyModel::new);
    public static final ModelKey<SkeleponyModel<?>> SKELETON_CLOTHES = register("skeleton_clothes", SkeleponyModel::new);
    public static final ModelKey<PillagerPonyModel<?>> PILLAGER = register("pillager", PillagerPonyModel::new);
    public static final ModelKey<IllagerPonyModel<?>> ILLAGER = register("illager", IllagerPonyModel::new);
    public static final ModelKey<GuardianPonyModel> GUARDIAN = register("guardian", GuardianPonyModel::new);
    public static final ModelKey<EnderStallionModel> ENDERMAN = register("enderman", EnderStallionModel::new);
    public static final ModelKey<BreezieModel<VexEntity>> BREEZIE = register("breezie", BreezieModel::new);
    public static final ModelKey<ParaspriteModel> PARASPRITE = register("parasprite", ParaspriteModel::new);

    public static final ModelKey<PonyElytra<?>> ELYTRA = register("elytra", PonyElytra::new);
    public static final ModelKey<PonySkullModel> SKULL = register("skull", PonySkullModel::new);

    public static final ModelKey<PonyArmourModel<?>> ARMOUR_INNER = register("armour_inner", PonyArmourModel::new);
    public static final ModelKey<PonyArmourModel<?>> ARMOUR_OUTER = register("armour_outer", PonyArmourModel::new);

    public static final ModelKey<Stetson> STETSON = registerGear("stetson", Wearable.STETSON, Stetson::new);
    public static final ModelKey<SaddleBags> SADDLEBAGS = registerGear("saddlebags", Wearable.SADDLE_BAGS, SaddleBags::new);
    public static final ModelKey<Muffin> MUFFIN = registerGear("muffin", Wearable.MUFFIN, Muffin::new);
    public static final ModelKey<WitchHat> WITCH_HAT = registerGear("witch_hat", Wearable.HAT, WitchHat::new);
    public static final ModelKey<ChristmasHat> ANTLERS = registerGear("antlers", Wearable.ANTLERS, ChristmasHat::new);

    public static final PlayerModelKey<?, AlicornModel<?>> ALICORN = registerPlayer("alicorn", Race.ALICORN, AlicornModel::new);
    public static final PlayerModelKey<?, UnicornModel<?>> UNICORN = registerPlayer("unicorn", Race.UNICORN, UnicornModel::new);
    public static final PlayerModelKey<?, UnicornModel<?>> KIRIN = registerPlayer("kirin", Race.KIRIN, UnicornModel::new);
    public static final PlayerModelKey<?, PegasusModel<?>> PEGASUS = registerPlayer("pegasus", Race.PEGASUS, PegasusModel::new);
    public static final PlayerModelKey<?, PegasusModel<?>> GRYPHON = registerPlayer("gryphon", Race.GRYPHON, PegasusModel::new);
    public static final PlayerModelKey<?, PegasusModel<?>> HIPPOGRIFF = registerPlayer("hippogriff", Race.HIPPOGRIFF, PegasusModel::new);
    public static final PlayerModelKey<?, EarthPonyModel<?>> EARTH_PONY = registerPlayer("earth_pony", Race.EARTH, EarthPonyModel::new);
    public static final PlayerModelKey<?, SeaponyModel<?>> SEA_PONY = registerPlayer("sea_pony", Race.SEAPONY, SeaponyModel::new, PlayerSeaponyRenderer::new);
    public static final PlayerModelKey<?, PegasusModel<?>> BAT_PONY = registerPlayer("bat_pony", Race.BATPONY, PegasusModel::new);
    public static final PlayerModelKey<?, ChangelingModel<?>> CHANGELING = registerPlayer("changeling", Race.CHANGELING, ChangelingModel::new);
    public static final PlayerModelKey<?, ChangelingModel<?>> CHANGEDLING = registerPlayer("reformed_changeling", Race.CHANGEDLING, ChangelingModel::new);
    public static final PlayerModelKey<?, ZebraModel<?>> ZEBRA = registerPlayer("zebra", Race.ZEBRA, ZebraModel::new);

    static <E extends LivingEntity, T extends Model & MsonModel> PlayerModelKey<E, T> registerPlayer(String name, Race race, BiFunction<ModelPart, Boolean, T> constructor) {
        return registerPlayer(name, race, constructor, PlayerPonyRenderer::new);
    }

    @SuppressWarnings("unchecked")
    static <E extends LivingEntity, T extends Model & MsonModel> PlayerModelKey<E, T> registerPlayer(String name, Race race, BiFunction<ModelPart, Boolean, T> constructor, PlayerModelKey.RendererFactory rendererFactory) {
        return (PlayerModelKey<E, T>)PLAYER_MODELS.computeIfAbsent(race, r -> {
            return new PlayerModelKey<>(name, constructor, rendererFactory);
        });
    }

    @SuppressWarnings("unchecked")
    static <T extends AbstractGear> ModelKey<T> registerGear(String name, Wearable wearable, MsonModel.Factory<T> constructor) {
        return (ModelKey<T>)GEAR_MODELS.computeIfAbsent(wearable, w -> {
            return Mson.getInstance().registerModel(new Identifier("minelittlepony", "gear/" + name), constructor);
        });
    }

    static <T extends Model> ModelKey<T> register(String name, MsonModel.Factory<T> constructor) {
        return Mson.getInstance().registerModel(new Identifier("minelittlepony", name), constructor);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends LivingEntity, T extends Model & MsonModel> PlayerModelKey<E, T> getPlayerModel(Race race) {
        return (PlayerModelKey<E, T>)PLAYER_MODELS.get(race);
    }

    public static Stream<Map.Entry<Wearable, ModelKey<? extends IGear>>> getWearables() {
        return GEAR_MODELS.entrySet().stream();
    }

    public static void bootstrap() {};
}
