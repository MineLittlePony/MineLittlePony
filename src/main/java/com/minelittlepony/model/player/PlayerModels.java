package com.minelittlepony.model.player;

import com.minelittlepony.model.PMAPI;
import com.minelittlepony.model.ModelWrapper;

public enum PlayerModels {
    HUMAN("default", "slim", () -> PMAPI.human, () -> PMAPI.humanSmall),
    PONY("pony", "slimpony", () -> PMAPI.pony, () -> PMAPI.ponySmall);

    private final ModelResolver normal, slim;

    private final String normalKey, slimKey;

    PlayerModels(String normalKey, String slimKey, ModelResolver normal, ModelResolver slim) {
        this.normalKey = normalKey;
        this.slimKey = slimKey;

        this.normal = normal;
        this.slim = slim;
    }

    public ModelWrapper getModel(boolean slim) {
          return slim ? this.slim.resolve() : this.normal.resolve(); 
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
