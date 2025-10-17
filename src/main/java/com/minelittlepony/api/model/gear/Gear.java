package com.minelittlepony.api.model.gear;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.*;
import com.minelittlepony.api.pony.meta.Wearable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Interface for an accessory on a pony's body.
 */
public interface Gear<S extends BipedEntityRenderState & PonyModel.AttributedHolder> {
    List<Supplier<Gear<?>>> MOD_GEARS = new ArrayList<>();

    /**
     * Registers a custom gear to be used with the mod.
     * <p>
     * This would be awesome for creating socks.
     */
    static Supplier<Gear<?>> register(Supplier<Gear<?>> gear) {
        MOD_GEARS.add(gear);
        return gear;
    }

    /**
     * Determines if this wearable can and is worn by the selected entity.
     *
     * @param model     The primary model
     * @param entity    The entity being rendered
     *
     * @return True to render this wearable
     */
    boolean canRender(PonyModel<?> model, S entity);

    /**
     * Gets the body location that this wearable appears on.
     */
    BodyPart getGearLocation();

    default boolean isStackable() {
        return getStackingHeight() > 0;
    }

    /**
     * The vertical height of this gear when present in a stack.
     *
     * Any gear rendered after this one will be shifted to sit on top of it.
     */
    default float getStackingHeight() {
        return 0;
    }

    /**
     * Gets the texture to use for this wearable.
     *
     * If you need to use the player's own skin, use {@link IRenderContext#getDefaultTexture(entity, wearable)}
     */
    Identifier getTexture(S entity, Context<S, ?> context);

    /**
     * Gets the layer used to render this piece of gear.
     */
    default RenderLayer getLayer(S entity, Context<S, ?> context) {
        return RenderLayer.getEntityTranslucent(getTexture(entity, context));
    }

    /**
     * Applies body transformations for this wearable
     */
    default void transform(S state, PonyModel<S> model, MatrixStack matrices) {
        BodyPart part = getGearLocation();
        model.transform(state, part,  matrices);
        model.getBodyPart(part).applyTransform(matrices);
    }

    /**
     * Renders this model component.
     */
    void render(MatrixStack stack, GearRenderState<S> state, OrderedRenderCommandQueue queue, RenderLayer layer, int overlay, int light, int color);

    /**
     * A render context for instance of IGear.
     *
     * @param <T> The type of entity being rendered.
     * @param <M> The type of the entity's primary model.
     */
    public interface Context<S extends BipedEntityRenderState & PonyModel.AttributedHolder, M extends PonyModel<?>> {
        /**
         * The empty context.
         */
        @SuppressWarnings("rawtypes")
        Context<?, ?> NULL = new Context() {
            @Override
            public Identifier getDefaultTexture(BipedEntityRenderState entity, Wearable wearable) {
                return wearable.getDefaultTexture();
            }
        };

        /**
         * Checks whether the given wearable and gear are able to render for this specific entity and its renderer.
         */
        default boolean shouldRender(M model, S entity, Wearable wearable, Gear<S> gear) {
            return gear.canRender(model, entity);
        }

        /**
         * Gets the default texture to use for this entity and wearable.
         *
         * May be the entity's own texture or a specific texture allocated for that wearable.
         */
        Identifier getDefaultTexture(S entity, Wearable wearable);
    }

    public class GearRenderState<S extends BipedEntityRenderState & PonyModel.AttributedHolder> {
        public S entityState;
        public PonyModel<S> model;
        public float limbDistance;
        public float limbAngle;
        public float bodySwing;
    }
}
