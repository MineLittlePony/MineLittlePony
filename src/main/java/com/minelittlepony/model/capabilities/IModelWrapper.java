package com.minelittlepony.model.capabilities;

import com.minelittlepony.api.pony.IPonyData;

public interface IModelWrapper {
    /**
     * Updates metadata values to this wrapper's contained models.
     */
    IModelWrapper apply(IPonyData meta);
}
