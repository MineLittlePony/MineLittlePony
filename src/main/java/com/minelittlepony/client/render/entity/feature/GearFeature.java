package com.minelittlepony.client.render.entity.feature;

import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.random.Random;

import com.google.common.cache.*;
import com.google.common.collect.Streams;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.gear.IGear;
import com.minelittlepony.api.model.gear.IStackable;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.IPonyModel;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.render.IPonyRenderContext;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GearFeature<T extends LivingEntity, M extends EntityModel<T> & IPonyModel<T>> extends AbstractPonyFeature<T, M> {

    private static final List<Supplier<IGear>> MOD_GEARS = new ArrayList<>();

    public static void addModGear(Supplier<IGear> gear) {
        MOD_GEARS.add(gear);
    }

    private final List<Entry> gears = Streams.concat(
            ModelType.getWearables().map(e -> new Entry(e.getValue().createModel(), e.getKey())),
            MOD_GEARS.stream().map(e -> new Entry(e.get(), Wearable.NONE))
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

    public GearFeature(IPonyRenderContext<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }

        final M model = getModelWrapper().body();
        final Object2FloatMap<BodyPart> renderStackingOffsets = new Object2FloatLinkedOpenHashMap<>();

        for (var entry : randomisedGearCache.getUnchecked(entity.getUuid().getLeastSignificantBits())) {
            if (getContext().shouldRender(model, entity, entry.wearable, entry.gear)) {
                stack.push();
                BodyPart part = entry.gear.getGearLocation();
                entry.gear.transform(model, stack);

                if (entry.gear instanceof IStackable s) {
                    float v = renderStackingOffsets.getFloat(part);
                    if (v != 0) {
                        stack.translate(0, -v, 0);
                    }
                    renderStackingOffsets.put(part, v + s.getStackingHeight());
                }

                renderGear(model, entity, entry.gear, stack, renderContext, lightUv, limbDistance, limbAngle, tickDelta);
                stack.pop();
            }
        }
    }

    private void renderGear(M model, T entity, IGear gear, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, float limbDistance, float limbAngle, float tickDelta) {
        gear.pose(model, entity, model.getAttributes().isGoingFast, entity.getUuid(), limbDistance, limbAngle, model.getWobbleAmount(), tickDelta);
        gear.render(stack, renderContext.getBuffer(gear.getLayer(entity, getContext())), lightUv, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1, entity.getUuid());
    }

    static record Entry(IGear gear, Wearable wearable) {}
}
