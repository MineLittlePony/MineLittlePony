package com.minelittlepony.model.player;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.render.player.RenderPonyPlayer;
import com.minelittlepony.render.player.RenderSeaponyPlayer;

import net.minecraft.client.renderer.entity.RenderManager;

import com.minelittlepony.model.ModelWrapper;

public enum PlayerModels {
    /**
     * The default non-pony model. This is typically handled my the vanilla renderer.
     */
    DEFAULT("default", "slim", () -> PMAPI.earthpony, () -> PMAPI.earthponySmall),
    EARTH("earthpony", () -> PMAPI.earthpony, () -> PMAPI.earthponySmall),
    PEGASUS("pegasus", () -> PMAPI.pegasus, () -> PMAPI.pegasusSmall),
    BATPONY("batpony", () -> PMAPI.bat, () -> PMAPI.batSmall),
    UNICORN("unicorn", () -> PMAPI.unicorn, () -> PMAPI.unicornSmall),
    ALICORN("alicorn", () -> PMAPI.alicorn, () -> PMAPI.alicornSmall),
    CHANGELING("changeling", () -> PMAPI.bug, () -> PMAPI.bugSmall),
    ZEBRA("zebra", () -> PMAPI.zebra, () -> PMAPI.zebraSmall),
    SEAPONY("seapony", () -> PMAPI.seapony, () -> PMAPI.seapony) {
        @Override
        public RenderPonyPlayer createRenderer(RenderManager manager, boolean slimArms) {
            return new RenderSeaponyPlayer(manager, slimArms, PlayerModels.UNICORN.getModel(slimArms), getModel(slimArms));
        }
    };

    private final ModelResolver normal, slim;

    private final String normalKey, slimKey;

    PlayerModels(String key, ModelResolver normal, ModelResolver slim) {
        this(key, "slim" + key, normal, slim);
    }

    PlayerModels(String normalKey, String slimKey, ModelResolver normal, ModelResolver slim) {
        this.normalKey = normalKey;
        this.slimKey = normalKey;

        this.normal = normal;
        this.slim = slim;
    }

    public ModelWrapper getModel(boolean slim) {
        return slim ? this.slim.resolve() : normal.resolve();
    }

    public String getId(boolean useSlimArms) {
        return useSlimArms ? slimKey : normalKey;
    }

    public RenderPonyPlayer createRenderer(RenderManager manager, boolean slimArms) {
        return new RenderPonyPlayer(manager, slimArms, getModel(slimArms));
    }

    /**
     * FIXME: PMAPI fields are null when the game starts.
     */
    static interface ModelResolver {
        ModelWrapper resolve();
    }
}
