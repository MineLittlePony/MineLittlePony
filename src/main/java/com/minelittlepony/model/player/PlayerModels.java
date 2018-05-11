package com.minelittlepony.model.player;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.ModelWrapper;

public enum PlayerModels {
    /**
     * @deprecated Will be removed in a later revision
     */
    @Deprecated HUMAN("default", "slim", () -> PMAPI.pony, () -> PMAPI.ponySmall),
    EARTH("earthpony", "slimearthpony", () -> PMAPI.earthpony, () -> PMAPI.earthponySmall),
    PEGASUS("pegasus", "slimpegasus", () -> PMAPI.pegasus, () -> PMAPI.pegasusSmall),
    UNICORN("unicorn", "slimunicorn", () -> PMAPI.unicorn, () -> PMAPI.unicornSmall),
    ALICORN("alicorn", "slimalicorn", () -> PMAPI.alicorn, () -> PMAPI.alicornSmall),
    ZEBRA("zebra", "slimzebra", () -> PMAPI.zebra, () -> PMAPI.zebraSmall);

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

    /**
     * FIXME: PMAPI fields are null when the game starts.
     */
    static interface ModelResolver {
        ModelWrapper resolve();
    }
}
