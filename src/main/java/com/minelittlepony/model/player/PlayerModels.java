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
    EARTH("earthpony", "slimearthpony", () -> PMAPI.earthpony, () -> PMAPI.earthponySmall),
    PEGASUS("pegasus", "slimpegasus", () -> PMAPI.pegasus, () -> PMAPI.pegasusSmall),
    BATPONY("batpony", "slimbatpony", () -> PMAPI.bat, () -> PMAPI.batSmall),
    UNICORN("unicorn", "slimunicorn", () -> PMAPI.unicorn, () -> PMAPI.unicornSmall),
    ALICORN("alicorn", "slimalicorn", () -> PMAPI.alicorn, () -> PMAPI.alicornSmall),
    ZEBRA("zebra", "slimzebra", () -> PMAPI.zebra, () -> PMAPI.zebraSmall),
    SEAPONY("seapony", "slimseapony", () -> PMAPI.seapony, () -> PMAPI.seapony) {
        public RenderPonyPlayer createRenderer(RenderManager manager, boolean slimArms) {
            return new RenderSeaponyPlayer(manager, slimArms, PlayerModels.UNICORN.getModel(slimArms), getModel(slimArms));
        }
    };

    private final ModelResolver normal, slim;

    private final String normalKey, slimKey;

    PlayerModels(String normalKey, String slimKey, ModelResolver normal, ModelResolver slim) {
        this.normalKey = normalKey;
        this.slimKey = slimKey;

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
