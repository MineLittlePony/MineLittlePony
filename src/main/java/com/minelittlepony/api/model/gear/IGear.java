package com.minelittlepony.api.model.gear;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import com.minelittlepony.api.model.BodyPart;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.pony.meta.Wearable;
import com.minelittlepony.client.render.entity.feature.GearFeature;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Interface for an accessory on a pony's body.
 */
public interface IGear {
    /**
     * Registers a custom gear to be used with the mod.
     * <p>
     * This would be awesome for creating socks.
     */
    static Supplier<IGear> register(Supplier<IGear> gear) {
        GearFeature.addModGear(gear);
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
    boolean canRender(IModel model, Entity entity);

    /**
     * Gets the body location that this wearable appears on.
     */
    BodyPart getGearLocation();

    /**
     * Gets the texture to use for this wearable.
     *
     * If you need to use the player's own skin, use {@link IRenderContext#getDefaultTexture(entity, wearable)}
     */
    <T extends Entity> Identifier getTexture(T entity, Context<T, ?> context);

    default <T extends Entity> RenderLayer getLayer(T entity, Context<T, ?> context) {
        return RenderLayer.getEntityTranslucent(getTexture(entity, context));
    }

    /**
     * Orients this wearable.
     */
    default void setModelAttributes(IModel model, Entity entity) {

    }
    /**
     * Sets the model's various rotation angles.
     *
     * See {@link AbstractPonyMode.setRotationAndAngle} for an explanation of the various parameters.
     */
    default void pose(boolean rainboom, UUID interpolatorId, float move, float swing, float bodySwing, float ticks) {

    }

    /**
     * Renders this model component.
     */
    void render(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, UUID interpolatorId);

    /**
     * Sets whether this part should be rendered.
     */
    default void setVisible(boolean visible) {

    }

    /**
     * A render context for instance of IGear.
     *
     * @param <T> The type of entity being rendered.
     * @param <M> The type of the entity's primary model.
     */
    public interface Context<T extends Entity, M extends IModel> {
        /**
         * The empty context.
         */
        Context<?, ?> NULL = (e, g) -> null;

        /**
         * Checks whether the given wearable and gear are able to render for this specific entity and its renderer.
         */
        default boolean shouldRender(M model, T entity, Wearable wearable, IGear gear) {
            return gear.canRender(model, entity);
        }

        @Nullable
        default M getEntityModel() {
            return null;
        }

        /**
         * Gets the default texture to use for this entity and wearable.
         *
         * May be the entity's own texture or a specific texture allocated for that wearable.
         */
        Identifier getDefaultTexture(T entity, Wearable wearable);
    }
}
