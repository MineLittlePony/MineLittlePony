package com.minelittlepony.client.model.gear;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.model.gear.IGear;

public interface IGearRenderContext<T extends Entity> {

    IGearRenderContext<?> NULL = (e, g) -> null;

    Identifier getDefaultTexture(T entity, IGear gear);
}
