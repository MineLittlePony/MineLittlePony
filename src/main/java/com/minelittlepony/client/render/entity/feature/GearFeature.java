package com.minelittlepony.client.render.entity.feature;

import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EmptyBlockView;

import com.google.common.cache.*;
import com.google.common.collect.Streams;
import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.model.gear.Gear;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.armour.ArmourLayer;
import com.minelittlepony.client.model.armour.ArmourRendererPlugin;
import com.minelittlepony.client.render.PonyRenderContext;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GearFeature<T extends LivingEntity, M extends EntityModel<T> & PonyModel<T>> extends AbstractPonyFeature<T, M> {

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

    public GearFeature(PonyRenderContext<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, T entity, float limbDistance, float limbAngle, float tickDelta, float age, float headYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }

        boolean hasSkull = false;
        for (ItemStack skull : ArmourRendererPlugin.INSTANCE.get().getArmorStacks(entity, EquipmentSlot.HEAD, ArmourLayer.OUTER, ArmourRendererPlugin.ArmourType.SKULL)) {
            if (skull.getItem() instanceof BlockItem b && (b.getBlock() instanceof SkullBlock || b.getBlock().getDefaultState().isSolidBlock(EmptyBlockView.INSTANCE, BlockPos.ORIGIN))) {
                hasSkull = true;
                break;
            }
        }

        final M model = getModelWrapper().body();
        final Object2FloatMap<BodyPart> renderStackingOffsets = new Object2FloatLinkedOpenHashMap<>();

        for (var entry : randomisedGearCache.getUnchecked(entity.getUuid().getLeastSignificantBits())) {
            if (getContext().shouldRender(model, entity, entry.wearable(), entry.gear())) {
                stack.push();
                Gear gear = entry.gear();
                gear.transform(model, stack);
                BodyPart part = gear.getGearLocation();
                if (part != BodyPart.HEAD || model.getAttributes().headVisible) {
                    if (hasSkull && part == BodyPart.HEAD && renderStackingOffsets.getFloat(part) == 0) {
                        renderStackingOffsets.put(part, 0.25F);
                    }

                    if (gear.isStackable()) {
                        float v = renderStackingOffsets.getFloat(part);
                        if (v != 0) {
                            stack.translate(0, -v, 0);
                        }
                        renderStackingOffsets.put(part, v + gear.getStackingHeight());
                    }

                    renderGear(model, entity, gear, stack, renderContext, lightUv, limbDistance, limbAngle, tickDelta);
                }
                stack.pop();
            }
        }
    }

    private void renderGear(M model, T entity, Gear gear, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, float limbDistance, float limbAngle, float tickDelta) {
        gear.pose(model, entity, model.getAttributes().isGoingFast, entity.getUuid(), limbDistance, limbAngle, model.getWobbleAmount(), tickDelta);
        gear.render(stack, renderContext.getBuffer(gear.getLayer(entity, getContext())), lightUv, OverlayTexture.DEFAULT_UV, Colors.WHITE, entity.getUuid());
    }

    static record Entry(Gear gear, Wearable wearable) { }
}
