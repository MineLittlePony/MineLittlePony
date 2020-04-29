package com.minelittlepony.client.model.entity.race;

import com.google.common.collect.Maps;
import com.minelittlepony.api.pony.meta.Race;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.PlayerModelKey;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum PlayerModels {
    /**
     * The default non-pony model. This is typically handled my the vanilla renderer.
     */
    DEFAULT("default", "slim", Race.HUMAN),
    EARTHPONY(Race.EARTH),
    PEGASUS(Race.PEGASUS),
    BATPONY(Race.BATPONY),
    UNICORN(Race.UNICORN),
    ALICORN(Race.ALICORN),
    CHANGELING(Race.CHANGELING),
    ZEBRA(Race.ZEBRA),
    SEAPONY(Race.SEAPONY);

    public static final List<PlayerModels> registry = Arrays.asList(values());
    private static final Map<Race, PlayerModels> raceModelsMap = Maps.newEnumMap(Race.class);

    static {
        for (PlayerModels i : values()) {
            raceModelsMap.put(i.race, i);
        }
    }

    private final String normal;
    private final String slim;

    private final Race race;

    PlayerModels(Race race) {
        normal = name().toLowerCase();
        slim = "slim" + normal;

        this.race = race;
    }

    PlayerModels(String normalKey, String slimKey, Race race) {
        normal = normalKey;
        slim = slimKey;

        this.race = race;
    }

    public PlayerModelKey<?, ?> getModelKey() {
        return ModelType.getPlayerModel(race);
    }

    public String getId(boolean isSlim) {
        return isSlim ? slim : normal;
    }

    public static PlayerModels forRace(Race race) {
        return raceModelsMap.getOrDefault(race.getAlias(), DEFAULT);
    }
}
