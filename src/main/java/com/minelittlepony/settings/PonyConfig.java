package com.minelittlepony.settings;

import net.minecraft.util.math.MathHelper;

import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.api.pony.meta.Sizes;
import com.minelittlepony.common.util.GamePaths;
import com.minelittlepony.common.util.settings.*;

import java.nio.file.Path;

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

    public final Setting<Sizes> sizeOverride = value("debug", "sizeOverride", Sizes.UNSET)
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

    public PonyConfig(Path path) {
        super(HEIRARCHICAL_JSON_ADAPTER, path);
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

        Race override = instance.raceOverride.get();
        if (override != Race.HUMAN) {
            return override;
        }

        if (instance.ponyLevel.get() == PonyLevel.HUMANS) {
            return Race.HUMAN;
        }

        return race;
    }
}
