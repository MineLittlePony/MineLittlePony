package com.minelittlepony.api.model;

import com.minelittlepony.api.pony.PonyData;

public interface IModelWrapper {
    /**
     * Updates metadata values to this wrapper's contained models.
     */
    IModelWrapper applyMetadata(PonyData meta);
}
