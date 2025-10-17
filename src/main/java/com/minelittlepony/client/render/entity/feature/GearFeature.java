package com.minelittlepony.client.render.entity.feature;

import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.client.render.*;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.math.random.Random;

import com.google.common.cache.*;
import com.google.common.collect.Streams;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.render.PonyRenderContext;
import com.minelittlepony.client.render.entity.state.PonyRenderState;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GearFeature<
        T extends LivingEntity,
        S extends PonyRenderState,
        M extends ClientPonyModel<S>
    > extends AbstractPonyFeature<S, M> {

    private final List<Entry> gears = Streams.concat(
            ModelType.getWearables().map(e -> new Entry(e.getValue().createModel(), e.getKey())),
            Gear.MOD_GEARS.stream().map(e -> new Entry(e.get(), Wearable.NONE))
    ).collect(Collectors.toList());

    private final LoadingCache<Long, List<Entry>> randomisedGearCache = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build(CacheLoader.from(id -> {
                List<Entry> randomizedOrder = new ArrayList<>();
                List<Entry> pool = new ArrayList<>(gears);

                Random rng = Random.create(id);

                while (!pool.isEmpty()) {
                    randomizedOrder.add(pool.remove(rng.nextInt(pool.size() + 1) % pool.size()));
                }
                return randomizedOrder;
            }));

    public GearFeature(PonyRenderContext<T, S, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        if (state.invisible) {
            return;
        }

        final M model = lookupModel(state).body();
        final Object2FloatMap<BodyPart> renderStackingOffsets = new Object2FloatLinkedOpenHashMap<>();

        for (var entry : randomisedGearCache.getUnchecked(state.attributes.getEntityId().getLeastSignificantBits())) {
            @SuppressWarnings("unchecked")
            Gear<S> gear = (Gear<S>)entry.gear();
            if (getContext().shouldRender(model, state, entry.wearable(), gear)) {
                stack.push();
                gear.transform(state, model, stack);
                BodyPart part = gear.getGearLocation();
                if (part != BodyPart.HEAD || state.headVisible) {
                    if (state.hasHeadBlock && part == BodyPart.HEAD && renderStackingOffsets.getFloat(part) == 0) {
                        renderStackingOffsets.put(part, 0.25F);
                    }

                    if (gear.isStackable()) {
                        float v = renderStackingOffsets.getFloat(part);
                        if (v != 0) {
                            stack.translate(0, -v, 0);
                        }
                        renderStackingOffsets.put(part, v + gear.getStackingHeight());
                    }

                    Gear.GearRenderState<S> gearState = new Gear.GearRenderState<S>();
                    gearState.entityState = state;
                    gearState.model = model;
                    gearState.bodySwing = state.wobbleAmount;
                    gearState.limbDistance = limbDistance;
                    gearState.limbAngle = limbAngle;

                    gear.render(stack, gearState, queue, gear.getLayer(state, getContext()), OverlayTexture.DEFAULT_UV, light, Colors.WHITE);
                }
                stack.pop();
            }
        }
    }

    static record Entry(Gear<?> gear, Wearable wearable) { }
}
