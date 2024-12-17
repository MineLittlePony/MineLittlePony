package com.minelittlepony.api.config;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import com.google.gson.GsonBuilder;
import com.minelittlepony.api.pony.meta.*;
import com.minelittlepony.common.client.gui.VisibilityMode;
import com.minelittlepony.common.util.GamePaths;
import com.minelittlepony.common.util.settings.*;

import java.nio.file.Path;
import java.util.HashSet;

/**
 * Storage container for MineLP client settings.
 */
public class PonyConfig extends Config {
    private static PonyConfig instance;

    public static PonyConfig getInstance() {
        if (instance != null) {
            return instance;
        }
        return new PonyConfig(GamePaths.getConfigDirectory().resolve("minelp.json"));
    }

    /**
     * Sets the pony level. Want MOAR PONEHS? Well here you go.
     */
    public final Setting<PonyLevel> ponyLevel = value("ponylevel", PonyLevel.PONIES)
                .addComment("How much pony do you want?")
                .addComment("PONIES - all players are turned into ponies")
                .addComment("HUMANS - all players are humans")
                .addComment("BOTH - players with compatible skins will be ponies whilst the rest are humans");
    private final Setting<Float> scaleFactor = value("globalScaleFactor", 0.9F)
                .addComment("How large do you want your ponies to be?")
                .addComment("Default is show scale (0.9)");

    public final Setting<Boolean> sizes =      value("settings", "sizes", true)
                .addComment("Allows ponies of different sizes/ages");
    public final Setting<Boolean> snuzzles =   value("settings", "snuzzles", true)
                .addComment("Controls whether ponies have snouts");
    public final Setting<Boolean> fillycam =   value("settings", "fillycam", true)
                .addComment("Turn on to adjust the player's camera position to their model");
    private final Setting<Boolean> showscale = value("settings", "showscale", true)
                .addComment("Adjusts pony scales to match the show (approximate)");
    public final Setting<Boolean> fpsmagic =   value("settings", "fpsmagic", true)
                .addComment("Uses magic effects in first person")
                .addComment("Turn this off if you encounter any compatibility issues with other mods");
    public final Setting<Boolean> tpsmagic =   value("settings", "tpsmagic", true)
                .addComment("Uses magic effects in third person");
    public final Setting<Boolean> ponyskulls = value("settings", "ponyskulls", true)
                .addComment("Not enough ponies? Turn this on to turn player heads and skulls into ponies too!");
    public final Setting<Boolean> frustrum =   value("settings", "frustrum", true)
                .addComment("Adjust camera intersection checks to properly cull entities when they're not in view.")
                .addComment("Helps to prevent entities from vanishing when they're in long stacks");
    public final Setting<Boolean> horsieMode = value("settings", "horsieMode", false)
                .addComment("Enables the alternative horsey models from the April Fools 2023 update");

    public final Setting<SizePreset> sizeOverride = value("debug", "sizeOverride", SizePreset.UNSET)
                .addComment("Overrides pony sizes")
                .addComment("Possible values: TALL, BULKY, LANKY, NORMAL, YEARLING, FOAL, UNSET (default)");

    public final Setting<Race> raceOverride = value("debug", "raceOverride", Race.HUMAN)
                .addComment("Overrides pony races")
                .addComment("Possible values: HUMAN (default), EARTH, PEGASUS, UNICORN, ALICORN, CHANGELING, ZEBRA, CHANGEDLING, GRYPHON, HIPPOGRIFF, KIRIN, BAYPONT, SEAPONY");

    public final Setting<Boolean> disablePonifiedArmour = value("debug", "usePonifiedArmour", false)
                .addComment("Disables pony armour textures.")
                .addComment("If enabled, only the vanilla textures will be considered");

    public final Setting<Boolean> flappyElytras = value("customisation", "flappyElytras", false)
                .addComment("Pegasi will use their wings to fly even when they're wearing an elytra");

    public final Setting<Boolean> noFun = value("customisation", "noFun", false)
                .addComment("Disables certain easter eggs and secrets (party pooper)")
                .addComment("Turning this off may help with compatibility in some cases");

    public final Setting<Boolean> enableFabricModelsApiSupport = value("settings", "enableFabricModelsApiSupport", false)
            .addComment("Enables rendering of modded armour registered via the fabric api")
            .addComment("Note that since any armour registered in this way is designed to work for the human model, pieces may not fit exactly.")
            .addComment("i.e. Anything that goes on the plyer's backs needs to be rotated by the mod developer when rendered on a pony")
            .addComment("Developers: To know if you're being rendered on a pony model, check the renderstate or model class with")
            .addComment(" state instanceof com.minelittlepony.api.model.PonyModel$AttributedHolder or model instanceof com.minelittlepony.api.model.PonyModel")
            .addComment(" Note that the matrix stack your receieve is pre-transformed for the body part your model is attached to, so if you intend to call model.transform(state, part, matrices)")
            .addComment(" with a different part (ie BACK) you must pop the stack to revert to the previous first.");

    public final Setting<VisibilityMode> horseButton = value("horseButton", VisibilityMode.AUTO)
                .addComment("Whether to show the mine little pony settings button on the main menu")
                .addComment("AUTO (default) - only show when HDSkins is not installed")
                .addComment("ON - always show")
                .addComment("OFF - never show");

    public final Setting<HashSet<Identifier>> forwardHoldingItems = value("customisation", "forwardHoldingItems", HashSet::new, Identifier.class)
                .addComment("Contains a list of item ids that should preserve orientation")
                .addComment("when held in a unicorn's magical aura in first person");

    public PonyConfig(Path path) {
        super(new HeirarchicalJsonConfigAdapter(new GsonBuilder()
                .registerTypeAdapter(Identifier.class, new ToStringAdapter<>(Identifier::toString, Identifier::of))), path);
        instance = this;
    }

    public float setGlobalScaleFactor(float f) {

        if (f < 0.15F) {
            f = 0.1F;
        }
        if (f > 2.95) {
            f = 3;
        }
        if (f > 1.97 && f < 2.03) {
            f = 2;
        }
        if (f > 0.97 && f < 1.03) {
            f = 1;
        }
        if (f > 0.87 && f < 0.93) {
            f = 0.9F;
        }

        f = Math.round(MathHelper.clamp(f, 0.1F, 3) * 100F) / 100F;

        scaleFactor.set(f);
        showscale.set(f != 1);

        return getGlobalScaleFactor();
    }

    /**
     * Gets the universal scale factor used to determine how tall ponies are.
     */
    public float getGlobalScaleFactor() {
        return showscale.get() ? scaleFactor.get() : 1;
    }


    /**
     * Gets the actual race determined by the given pony level.
     * PonyLevel.HUMANS would force all races to be humans.
     * PonyLevel.BOTH is no change.
     * PonyLevel.PONIES no change.
     */
    public static Race getEffectiveRace(Race race) {

        Race override = getInstance().raceOverride.get();
        if (override != Race.HUMAN) {
            return override;
        }

        if (getInstance().ponyLevel.get() == PonyLevel.HUMANS) {
            return Race.HUMAN;
        }

        return race;
    }

    public static Size getEffectiveSize(Size size) {
        SizePreset sz = getInstance().sizeOverride.get();

        if (sz != SizePreset.UNSET) {
            return sz;
        }

        if (size == SizePreset.UNSET || !instance.sizes.get()) {
            return SizePreset.NORMAL;
        }

        return size;
    }
}
