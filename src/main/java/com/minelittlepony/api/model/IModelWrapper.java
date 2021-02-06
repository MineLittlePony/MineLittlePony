package com.minelittlepony.api.model;

import com.minelittlepony.api.pony.IPonyData;

public interface IModelWrapper {
    /**
     * Updates metadata values to this wrapper's contained models.
     */
    IModelWrapper applyMetadata(IPonyData meta);
}
