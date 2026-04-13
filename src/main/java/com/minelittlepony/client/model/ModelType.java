package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.LivingEntity;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.model.gear.*;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.armour.PonyArmourModel;
import com.minelittlepony.client.model.armour.PonyArmourStandModel;
import com.minelittlepony.client.model.entity.*;
import com.minelittlepony.client.model.entity.race.*;
import com.minelittlepony.client.model.gear.*;
import com.minelittlepony.common.util.Untyped;
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

    public static final ModelKey<ClientPonyModel<?>> WITCH = register("witch", WitchPonyModel::new);
    public static final ModelKey<ClientPonyModel<?>> ZOMBIE = register("zombie", ZomponyModel::new);
    public static final ModelKey<ClientPonyModel<?>> PIGLIN = register("piglin", PiglinPonyModel::new);
    public static final ModelKey<ClientPonyModel<?>> SKELETON = register("skeleton", tree -> new AlicornModel<>(tree, false));
    public static final ModelKey<Model.Simple> BOGGED_MUSHROOMS = register("bogged_mushrooms", tree -> new Model.Simple(tree, RenderTypes::entityTranslucent));
    public static final ModelKey<ClientPonyModel<?>> SKELETON_CLOTHES = register("skeleton_clothes", tree -> new AlicornModel<>(tree, false));
    public static final ModelKey<ClientPonyModel<?>> PILLAGER = register("pillager", tree -> new ChangelingModel<>(tree, false));
    public static final ModelKey<ClientPonyModel<?>> ILLAGER = register("illager", IllagerPonyModel::new);
    public static final ModelKey<ClientPonyModel<?>> GUARDIAN = register("guardian", SeaponyModel::new);
    public static final ModelKey<ClientPonyModel<?>> ENDERMAN = register("enderman", EnderStallionModel::new);
    public static final ModelKey<ParaspriteModel> VEX = register("vex", ParaspriteModel::new);
    public static final ModelKey<StriderDragonModel> STRIDER = register("strider", StriderDragonModel::new);
    public static final ModelKey<SaddleModel> STRIDER_SADDLE = register("strider_saddle", SaddleModel::new);
    public static final ModelKey<BreezieModel> ALLAY = register("allay", BreezieModel::new);
    public static final ModelKey<ClientPonyModel<?>> COPPER_GOLEM = register("copper_golem", CopperPonyModel::new);
    public static final ModelKey<ClientPonyModel<?>> SPIKE = register("spike", SpikeModel::new);

    public static final ModelKey<PonyElytra> ELYTRA = register("elytra", PonyElytra::new);

    public static final ModelKey<PonyArmourStandModel> ARMOUR_STAND = register("armour_stand", PonyArmourStandModel::new);
    public static final ModelKey<ClientPonyModel<?>> INNER_VANILLA_ARMOR = register("armor/inner_vanilla_armor", PonyArmourModel::new);
    public static final ModelKey<ClientPonyModel<?>> OUTER_VANILLA_ARMOR = register("armor/outer_vanilla_armor", PonyArmourModel::new);
    public static final ModelKey<ClientPonyModel<?>> INNER_PONY_ARMOR = register("armor/inner_pony_armor", PonyArmourModel::new);
    public static final ModelKey<ClientPonyModel<?>> OUTER_PONY_ARMOR = register("armor/outer_pony_armor", PonyArmourModel::new);

    public static final GearModelKey<AbstractGearModel<?>> STETSON = registerGear("stetson", Wearable.STETSON, t -> new WearableGear<>(t, Wearable.STETSON, BodyPart.HEAD, 0.15F));
    public static final GearModelKey<SaddleBags<?>> SADDLEBAGS_BOTH = registerGear("saddlebags", Wearable.SADDLE_BAGS_BOTH, t -> new SaddleBags<>(t, Wearable.SADDLE_BAGS_BOTH));
    public static final GearModelKey<SaddleBags<?>> SADDLEBAGS_LEFT = registerGear(SADDLEBAGS_BOTH, Wearable.SADDLE_BAGS_LEFT, t -> new SaddleBags<>(t, Wearable.SADDLE_BAGS_LEFT));
    public static final GearModelKey<SaddleBags<?>> SADDLEBAGS_RIGHT = registerGear(SADDLEBAGS_BOTH, Wearable.SADDLE_BAGS_RIGHT, t -> new SaddleBags<>(t, Wearable.SADDLE_BAGS_RIGHT));
    public static final GearModelKey<AbstractGearModel<?>> CROWN = registerGear("crown", Wearable.CROWN, t -> new WearableGear<>(t.getChild("crown"), Wearable.CROWN, BodyPart.HEAD, 0.1F));
    public static final GearModelKey<AbstractGearModel<?>> MUFFIN = registerGear("muffin", Wearable.MUFFIN, t -> new WearableGear<>(t.getChild("crown"), Wearable.MUFFIN, BodyPart.HEAD, 0.45F));
    public static final GearModelKey<AbstractGearModel<?>> WITCH_HAT = registerGear("witch_hat", Wearable.HAT, t -> new WearableGear<>(t.getChild("hat"), Wearable.HAT, BodyPart.HEAD, 0.7F));
    public static final GearModelKey<DeerAntlers<?>> ANTLERS = registerGear("antlers", Wearable.ANTLERS, DeerAntlers::new);

    public static final PlayerModelKey<ClientPonyModel<?>> ALICORN = registerPlayer("alicorn", Race.ALICORN, AlicornModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> UNICORN = registerPlayer("unicorn", Race.UNICORN, UnicornModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> KIRIN = registerPlayer("kirin", Race.KIRIN, KirinModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> PEGASUS = registerPlayer("pegasus", Race.PEGASUS, PegasusModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> GRYPHON = registerPlayer("gryphon", Race.GRYPHON, PegasusModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> HIPPOGRIFF = registerPlayer("hippogriff", Race.HIPPOGRIFF, PegasusModel::new, PonyArmourModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> EARTH_PONY = registerPlayer("earth_pony", Race.EARTH, EarthPonyModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> SEA_PONY = registerPlayer("sea_pony", Race.SEAPONY, SeaponyModel::new, SeaponyModel.Armour::new);
    public static final PlayerModelKey<ClientPonyModel<?>> BAT_PONY = registerPlayer("bat_pony", Race.BATPONY, PegasusModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> CHANGELING = registerPlayer("changeling", Race.CHANGELING, ChangelingModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> CHANGEDLING = registerPlayer("reformed_changeling", Race.CHANGEDLING, ChangelingModel::new);
    public static final PlayerModelKey<ClientPonyModel<?>> ZEBRA = registerPlayer("zebra", Race.ZEBRA, EarthPonyModel::new);

    static <E extends LivingEntity, T extends Model<?> & MsonModel & PonyModel<?>> PlayerModelKey<T> registerPlayer(String name, Race race,
            BiFunction<ModelPart, Boolean, T> constructor) {
        return registerPlayer(name, race, constructor, PonyArmourModel::new);
    }

    static <T extends Model<?> & PonyModel<?>> PlayerModelKey<T> registerPlayer(String name, Race race,
            BiFunction<ModelPart, Boolean, T> constructor,
            MsonModel.Factory<PonyModel<?>> armorFactory) {
        return Untyped.cast(PLAYER_MODELS.computeIfAbsent(race, _ -> new PlayerModelKey<T>(name, constructor, armorFactory)));
    }

    static <T extends AbstractGearModel<?>> GearModelKey<T> registerGear(String name, Wearable wearable, MsonModel.Factory<T> constructor) {
        return Untyped.cast(GEAR_MODELS.computeIfAbsent(wearable, _ -> {
            return new GearModelKey<T>(Mson.getInstance().registerModel(MineLittlePony.id("gear/" + name), constructor), constructor);
        }));
    }

    static <T extends AbstractGearModel<?>> GearModelKey<T> registerGear(GearModelKey<T> key, Wearable wearable, MsonModel.Factory<T> constructor) {
        return Untyped.cast(GEAR_MODELS.computeIfAbsent(wearable, _ -> new GearModelKey<T>(key.key, constructor)));
    }

    static <T extends Model<?>> ModelKey<T> register(String name, MsonModel.Factory<T> constructor) {
        return new ModelKeyImpl<T>(MineLittlePony.id(name), constructor);
    }

    @Nullable
    public static <T extends Model<?> & PonyModel<?>> PlayerModelKey<T> getPlayerModel(Race race) {
        return Untyped.cast(PLAYER_MODELS.get(race));
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
