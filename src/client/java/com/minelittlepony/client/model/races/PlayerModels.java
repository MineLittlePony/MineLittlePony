package com.minelittlepony.client.model.races;

import com.google.common.collect.Maps;
import com.minelittlepony.client.model.AbstractPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entities.ModelSeapony;
import com.minelittlepony.client.render.entities.player.RenderPonyPlayer;
import com.minelittlepony.client.render.entities.player.RenderSeaponyPlayer;
import com.minelittlepony.common.pony.meta.Race;

import net.minecraft.client.renderer.entity.RenderManager;

import com.voxelmodpack.hdskins.VanillaModels;

import java.util.Map;

public enum PlayerModels {
    /**
     * The default non-pony model. This is typically handled my the vanilla renderer.
     */
    HUMAN(VanillaModels.DEFAULT, Race.HUMAN, ModelEarthPony::new),
    EARTH("earthpony", Race.EARTH, ModelEarthPony::new),
    PEGASUS("pegasus", Race.PEGASUS, ModelPegasus::new),
    BATPONY("batpony", Race.BATPONY, ModelBatpony::new),
    UNICORN("unicorn", Race.UNICORN, ModelUnicorn::new),
    ALICORN("alicorn", Race.ALICORN, ModelAlicorn::new),
    CHANGELING("changeling", Race.CHANGELING, ModelChangeling::new),
    ZEBRA("zebra", Race.ZEBRA, ModelZebra::new),
    SEAPONY("seapony", Race.SEAPONY, a -> new ModelSeapony()) {
        @Override
        public RenderPonyPlayer createRenderer(RenderManager manager, boolean slimArms) {
            return new RenderSeaponyPlayer(manager, slimArms, PlayerModels.UNICORN.getModel(slimArms), getModel(slimArms));
        }
    };

    private static Map<Race, PlayerModels> raceModelsMap = Maps.newEnumMap(Race.class);

    static {
        for (PlayerModels i : values()) {
            raceModelsMap.put(i.race, i);
        }
    }

    private final ModelResolver resolver;

    private ModelWrapper normal;
    private ModelWrapper slim;

    private final String normalKey, slimKey;

    private final Race race;

    PlayerModels(String key, Race race, ModelResolver resolver) {
        this(key, VanillaModels.SLIM + key, race, resolver);
    }

    PlayerModels(String normalKey, String slimKey, Race race, ModelResolver resolver) {
        this.normalKey = normalKey;
        this.slimKey = slimKey;

        this.resolver = resolver;

        this.race = race;
    }

    public ModelWrapper getModel(boolean isSlim) {

        if (isSlim) {
            if (slim == null) {
                slim = new ModelWrapper(resolver.resolve(isSlim));
            }

            return slim;
        }

        if (normal == null) {
            normal = new ModelWrapper(resolver.resolve(isSlim));
        }

        return normal;
    }

    public String getId(boolean useSlimArms) {
        return useSlimArms ? slimKey : normalKey;
    }

    public RenderPonyPlayer createRenderer(RenderManager manager, boolean slimArms) {
        return new RenderPonyPlayer(manager, slimArms, getModel(slimArms));
    }

    public static PlayerModels forRace(Race race) {
        return raceModelsMap.getOrDefault(race.getAlias(), HUMAN);
    }

    /**
     * FIXME: PMAPI fields are null when the game starts.
     */
    @FunctionalInterface
    static interface ModelResolver {
        AbstractPonyModel resolve(boolean slim);
    }
}
