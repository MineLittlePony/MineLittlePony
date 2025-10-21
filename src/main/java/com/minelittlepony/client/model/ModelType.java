package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.Model.SinglePartModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.*;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.model.entity.*;
import com.minelittlepony.client.model.entity.race.*;
import com.minelittlepony.client.model.gear.*;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.mson.api.Mson;
import com.minelittlepony.mson.api.MsonModel;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public final class ModelType {
    private static final Map<Race, PlayerModelKey<?>> PLAYER_MODELS = new HashMap<>();
    private static final Map<Wearable, GearModelKey<? extends Gear<?>>> GEAR_MODELS = new HashMap<>();

    public static final ModelKey<DJPon3EarsModel> DJ_PON_3 = register("dj_pon_three", DJPon3EarsModel::new);

    public static final ModelKey<WitchPonyModel> WITCH = register("witch", WitchPonyModel::new);
    public static final ModelKey<ZomponyModel<?>> ZOMBIE = register("zombie", ZomponyModel::new);
    public static final ModelKey<PiglinPonyModel> PIGLIN = register("piglin", PiglinPonyModel::new);
    public static final ModelKey<AlicornModel<?>> SKELETON = register("skeleton", tree -> new AlicornModel<>(tree, false));
    public static final ModelKey<SinglePartModel> BOGGED_MUSHROOMS = register("bogged_mushrooms", tree -> new SinglePartModel(tree, RenderLayer::getEntityTranslucent));
    public static final ModelKey<AlicornModel<?>> SKELETON_CLOTHES = register("skeleton_clothes", tree -> new AlicornModel<>(tree, false));
    public static final ModelKey<ChangelingModel<?>> PILLAGER = register("pillager", tree -> new ChangelingModel<>(tree, false));
    public static final ModelKey<IllagerPonyModel<?>> ILLAGER = register("illager", IllagerPonyModel::new);
    public static final ModelKey<SeaponyModel<?>> GUARDIAN = register("guardian", SeaponyModel::new);
    public static final ModelKey<EnderStallionModel> ENDERMAN = register("enderman", EnderStallionModel::new);
    public static final ModelKey<ParaspriteModel> VEX = register("vex", ParaspriteModel::new);
    public static final ModelKey<SpikeModel> STRIDER = register("strider", SpikeModel::new);
    public static final ModelKey<SaddleModel> STRIDER_SADDLE = register("strider_saddle", SaddleModel::new);
    public static final ModelKey<BreezieModel> ALLAY = register("allay", BreezieModel::new);
    public static final ModelKey<CopperPonyModel> COPPER_GOLEM = register("copper_golem", CopperPonyModel::new);

    public static final ModelKey<PonyElytra<?>> ELYTRA = register("elytra", PonyElytra::new);

    public static final ModelKey<ArmorStandEntityModel> ARMOUR_STAND = register("armour_stand", ArmorStandEntityModel::new);
    public static final ModelKey<AbstractPonyModel<?>> INNER_VANILLA_ARMOR = register("armor/inner_vanilla_armor", PonyArmourModel::new);
    public static final ModelKey<AbstractPonyModel<?>> OUTER_VANILLA_ARMOR = register("armor/outer_vanilla_armor", PonyArmourModel::new);
    public static final ModelKey<AbstractPonyModel<?>> INNER_PONY_ARMOR = register("armor/inner_pony_armor", PonyArmourModel::new);
    public static final ModelKey<AbstractPonyModel<?>> OUTER_PONY_ARMOR = register("armor/outer_pony_armor", PonyArmourModel::new);

    public static final GearModelKey<AbstractGearModel<?>> STETSON = registerGear("stetson", Wearable.STETSON, t -> new WearableGear<>(t, Wearable.STETSON, BodyPart.HEAD, 0.15F));
    public static final GearModelKey<SaddleBags<?>> SADDLEBAGS_BOTH = registerGear("saddlebags", Wearable.SADDLE_BAGS_BOTH, t -> new SaddleBags<>(t, Wearable.SADDLE_BAGS_BOTH));
    public static final GearModelKey<SaddleBags<?>> SADDLEBAGS_LEFT = registerGear(SADDLEBAGS_BOTH, Wearable.SADDLE_BAGS_LEFT, t -> new SaddleBags<>(t, Wearable.SADDLE_BAGS_LEFT));
    public static final GearModelKey<SaddleBags<?>> SADDLEBAGS_RIGHT = registerGear(SADDLEBAGS_BOTH, Wearable.SADDLE_BAGS_RIGHT, t -> new SaddleBags<>(t, Wearable.SADDLE_BAGS_RIGHT));
    public static final GearModelKey<AbstractGearModel<?>> CROWN = registerGear("crown", Wearable.CROWN, t -> new WearableGear<>(t.getChild("crown"), Wearable.CROWN, BodyPart.HEAD, 0.1F));
    public static final GearModelKey<AbstractGearModel<?>> MUFFIN = registerGear("muffin", Wearable.MUFFIN, t -> new WearableGear<>(t.getChild("crown"), Wearable.MUFFIN, BodyPart.HEAD, 0.45F));
    public static final GearModelKey<AbstractGearModel<?>> WITCH_HAT = registerGear("witch_hat", Wearable.HAT, t -> new WearableGear<>(t.getChild("hat"), Wearable.HAT, BodyPart.HEAD, 0.7F));
    public static final GearModelKey<DeerAntlers<?>> ANTLERS = registerGear("antlers", Wearable.ANTLERS, DeerAntlers::new);

    public static final PlayerModelKey<AlicornModel<?>> ALICORN = registerPlayer("alicorn", Race.ALICORN, AlicornModel::new);
    public static final PlayerModelKey<UnicornModel<?>> UNICORN = registerPlayer("unicorn", Race.UNICORN, UnicornModel::new);
    public static final PlayerModelKey<KirinModel<?>> KIRIN = registerPlayer("kirin", Race.KIRIN, KirinModel::new);
    public static final PlayerModelKey<PegasusModel<?>> PEGASUS = registerPlayer("pegasus", Race.PEGASUS, PegasusModel::new);
    public static final PlayerModelKey<PegasusModel<?>> GRYPHON = registerPlayer("gryphon", Race.GRYPHON, PegasusModel::new);
    public static final PlayerModelKey<PegasusModel<?>> HIPPOGRIFF = registerPlayer("hippogriff", Race.HIPPOGRIFF, PegasusModel::new, PonyArmourModel::new);
    public static final PlayerModelKey<EarthPonyModel<?>> EARTH_PONY = registerPlayer("earth_pony", Race.EARTH, EarthPonyModel::new);
    public static final PlayerModelKey<SeaponyModel<?>> SEA_PONY = registerPlayer("sea_pony", Race.SEAPONY, SeaponyModel::new, SeaponyModel.Armour::new);
    public static final PlayerModelKey<PegasusModel<?>> BAT_PONY = registerPlayer("bat_pony", Race.BATPONY, PegasusModel::new);
    public static final PlayerModelKey<ChangelingModel<?>> CHANGELING = registerPlayer("changeling", Race.CHANGELING, ChangelingModel::new);
    public static final PlayerModelKey<ChangelingModel<?>> CHANGEDLING = registerPlayer("reformed_changeling", Race.CHANGEDLING, ChangelingModel::new);
    public static final PlayerModelKey<EarthPonyModel<?>> ZEBRA = registerPlayer("zebra", Race.ZEBRA, EarthPonyModel::new);

    static <E extends LivingEntity, T extends Model<?> & MsonModel & PonyModel<?>> PlayerModelKey<T> registerPlayer(String name, Race race,
            BiFunction<ModelPart, Boolean, T> constructor) {
        return registerPlayer(name, race, constructor, PonyArmourModel::new);
    }

    @SuppressWarnings("unchecked")
    static <T extends Model<?> & PonyModel<?>> PlayerModelKey<T> registerPlayer(String name, Race race,
            BiFunction<ModelPart, Boolean, T> constructor,
            MsonModel.Factory<AbstractPonyModel<?>> armorFactory) {
        return (PlayerModelKey<T>)PLAYER_MODELS.computeIfAbsent(race, r -> new PlayerModelKey<T>(name, constructor, armorFactory));
    }

    @SuppressWarnings("unchecked")
    static <T extends AbstractGearModel<?>> GearModelKey<T> registerGear(String name, Wearable wearable, MsonModel.Factory<T> constructor) {
        return (GearModelKey<T>)GEAR_MODELS.computeIfAbsent(wearable, w -> {
            return new GearModelKey<T>(Mson.getInstance().registerModel(MineLittlePony.id("gear/" + name), constructor), constructor);
        });
    }

    @SuppressWarnings("unchecked")
    static <T extends AbstractGearModel<?>> GearModelKey<T> registerGear(GearModelKey<T> key, Wearable wearable, MsonModel.Factory<T> constructor) {
        return (GearModelKey<T>)GEAR_MODELS.computeIfAbsent(wearable, w -> new GearModelKey<T>(key.key, constructor));
    }

    static <T extends Model<?>> ModelKey<T> register(String name, MsonModel.Factory<T> constructor) {
        return new ModelKeyImpl<T>(MineLittlePony.id(name), constructor);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends Model<?> & PonyModel<?>> PlayerModelKey<T> getPlayerModel(Race race) {
        return (PlayerModelKey<T>)PLAYER_MODELS.get(race);
    }

    public static Stream<Map.Entry<Wearable, GearModelKey<? extends Gear<?>>>> getWearables() {
        return GEAR_MODELS.entrySet().stream();
    }

    public static void bootstrap() { }

    public record GearModelKey<T extends Gear<?>>(ModelKey<T> key, MsonModel.Factory<T> constructor) {
        public T createModel() {
            return key.createModel(constructor);
        }
    }
}
