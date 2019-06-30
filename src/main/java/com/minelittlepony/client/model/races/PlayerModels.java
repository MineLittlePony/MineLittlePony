package com.minelittlepony.client.model.races;

import com.google.common.collect.Maps;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.entities.ModelSeapony;
import com.minelittlepony.client.render.entities.player.RenderPonyPlayer;
import com.minelittlepony.client.render.entities.player.RenderSeaponyPlayer;
import com.minelittlepony.model.IModel;
import com.minelittlepony.pony.meta.Race;

import javax.annotation.Nullable;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;

import java.util.Map;
import java.util.function.Function;

public enum PlayerModels {
    /**
     * The default non-pony model. This is typically handled my the vanilla renderer.
     */
    DEFAULT("default", "slim", Race.HUMAN, ModelEarthPony::new),
    EARTHPONY(Race.EARTH, ModelEarthPony::new),
    PEGASUS(Race.PEGASUS, ModelPegasus::new),
    BATPONY(Race.BATPONY, ModelBatpony::new),
    UNICORN(Race.UNICORN, ModelUnicorn::new),
    ALICORN(Race.ALICORN, ModelAlicorn::new),
    CHANGELING(Race.CHANGELING, ModelChangeling::new),
    ZEBRA(Race.ZEBRA, ModelZebra::new),
    SEAPONY(Race.SEAPONY, ModelSeapony::new) {
        @Override
        public RenderPonyPlayer createRenderer(EntityRenderDispatcher manager, boolean slimArms) {
            return new RenderSeaponyPlayer(manager, slimArms, PlayerModels.UNICORN.getWrappedModel(slimArms), getWrappedModel(slimArms));
        }
    };

    private static final Map<Race, PlayerModels> raceModelsMap = Maps.newEnumMap(Race.class);

    static {
        for (PlayerModels i : values()) {
            raceModelsMap.put(i.race, i);
        }
    }

    private final Function<Boolean, IModel> resolver;

    private final PendingModel normal;
    private final PendingModel slim;

    private final Race race;

    PlayerModels(Race race, Function<Boolean, IModel> resolver) {
        normal = new PendingModel(name().toLowerCase());
        slim = new PendingModel("slim" + normal.key);

        this.resolver = resolver;

        this.race = race;
    }

    PlayerModels(String normalKey, String slimKey, Race race, Function<Boolean, IModel> resolver) {
        normal = new PendingModel(normalKey);
        slim = new PendingModel(slimKey);

        this.resolver = resolver;

        this.race = race;
    }

    public PendingModel getPendingModel(boolean isSlim) {
        return isSlim ? slim : normal;
    }

    public <T extends LivingEntity, M extends IModel> ModelWrapper<T, M> getWrappedModel(boolean isSlim) {
        return getPendingModel(isSlim).getWrappedModel(isSlim);
    }

    public String getId(boolean isSlim) {
        return getPendingModel(isSlim).key;
    }

    public RenderPonyPlayer createRenderer(EntityRenderDispatcher manager, boolean slimArms) {
        return new RenderPonyPlayer(manager, slimArms, getWrappedModel(slimArms));
    }

    public static PlayerModels forRace(Race race) {
        return raceModelsMap.getOrDefault(race.getAlias(), DEFAULT);
    }

    private final class PendingModel {
        @Nullable
        private ModelWrapper<?, IModel> model;

        private final String key;

        PendingModel(String key) {
            this.key = key;
        }

        @SuppressWarnings("unchecked")
        public <T extends LivingEntity, M extends IModel> ModelWrapper<T, M> getWrappedModel(boolean isSlim) {
            if (model == null) {
                model = new ModelWrapper<>(resolver.apply(isSlim));
            }

            return (ModelWrapper<T, M>)model;
        }
    }
}
