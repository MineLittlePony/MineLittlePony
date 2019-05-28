package com.minelittlepony.client.util.render;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@SuppressWarnings("unchecked")
public abstract class AbstractRenderer<T extends AbstractRenderer<T>> extends Cuboid {

    protected final Model baseModel;

    protected int textureOffsetX;
    protected int textureOffsetY;

    protected float modelOffsetX;
    protected float modelOffsetY;
    protected float modelOffsetZ;

    public AbstractRenderer(Model model) {
        super(model);
        baseModel = model;
    }

    public AbstractRenderer(Model model, int texX, int texY) {
        super(model, texX, texY);
        baseModel = model;
    }

    /**
     * Called to create a new instance of this renderer (used for child renderers)
     */
    protected abstract T copySelf();

    @Override
    public T setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        super.setTextureOffset(x, y);
        return (T) this;
    }

    /**
     * Flips the mirror flag. All faces are mirrored until this is called again.
     */
    public T flip() {
        return mirror(!mirror);
    }

    public T mirror(boolean m) {
        mirror = m;
        return (T) this;
    }

    /**
     * Sets the texture offset
     */
    public T tex(int x, int y) {
        return setTextureOffset(x, y);
    }

    /**
     * Sets the texture size for this renderer.
     */
    public T size(int w, int h) {
        return (T) setTextureSize(w, h);
    }

    /**
     * Positions this model in space.
     */
    public T at(float x, float y, float z) {
        return (T)at(this, x, y, z);
    }

    /**
     * Sets an offset to be used on all shapes and children created through this renderer.
     */
    public T offset(float x, float y, float z) {
        modelOffsetX = x;
        modelOffsetY = y;
        modelOffsetZ = z;
        return (T) this;
    }

    /**
     * Adjusts the rotation center of the given renderer by the given amounts in each direction.
     */
    public static void shiftRotationPoint(Cuboid renderer, float x, float y, float z) {
        renderer.rotationPointX += x;
        renderer.rotationPointY += y;
        renderer.rotationPointZ += z;
    }

    /**
     * Sets this renderer's rotation angles.
     */
    public T rotate(float x, float y, float z) {
        pitch = x;
        yaw = y;
        roll = z;
        return (T) this;
    }

    /**
     * Positions a given model in space by setting its offset values divided
     * by 16 to account for scaling applied inside the model.
     */
    public static <T extends Cuboid> T at(T renderer, float x, float y, float z) {
        renderer.x = x / 16;
        renderer.y = y / 16;
        renderer.z = z / 16;
        return renderer;
    }

    /**
     * Rotates this model to align itself with the angles of another.
     */
    public void rotateTo(Cuboid other) {
        rotate(other.pitch, other.yaw, other.roll);
    }

    /**
     * Shifts this model to align its center with the center of another.
     */
    public T rotateAt(Cuboid other) {
        return around(other.rotationPointX, other.rotationPointY, other.rotationPointZ);
    }

    /**
     * Sets the rotation point.
     */
    public T around(float x, float y, float z) {
        setRotationPoint(x, y, z);
        return (T) this;
    }

    /**
     * Gets or creates a new child model based on its unique index.
     * New children will be of the same type and inherit the same textures and offsets of the original.
     */
    public T child(int index) {
        if (children == null || index >= children.size()) {
            return child();
        }
        return (T)children.get(index);
    }

    /**
     * Returns a brand new child under this renderer.
     */
    public T child() {
        T copy = copySelf();
        child(copy.offset(modelOffsetX, modelOffsetY, modelOffsetZ));
        copy.textureHeight = textureHeight;
        copy.textureWidth = textureWidth;
        return copy;
    }

    /**
     * Adds a new child renderer and returns itself for chaining.
     */
    public <K extends Cuboid> T child(K child) {
        addChild(child);
        return (T)this;
    }

    @Override
    public T addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth, float unknown, int texX, int texY) {
        partName = name + "." + partName;

        setTextureOffset(texX, texY);
        addBox(offX, offY, offZ, width, height, depth);
        boxes.get(boxes.size() - 1).setName(partName);

        return (T) this;
    }

    @Override
    public T addBox(float offX, float offY, float offZ, int width, int height, int depth) {
        addBox(offX, offY, offZ, width, height, depth, 0);
        return (T) this;
    }

    @Override
    public T addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
        addBox(offX, offY, offZ, width, height, depth, 0, mirrored);
        return (T)this;
    }

    @Override
    public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
        addBox(offX, offY, offZ, width, height, depth, scaleFactor, mirror);
    }

    @Override
    public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        createBox(modelOffsetX + offX, modelOffsetY + offY, modelOffsetZ + offZ, width, height, depth, scaleFactor, mirrored);
    }

    /**
     * Creates a textured box.
     */
    public T box(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
        addBox(offX, offY, offZ, width, height, depth, scaleFactor, mirror);

        return (T)this;
    }

    protected void createBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        boxes.add(new Box<>(this, textureOffsetX, textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor, mirrored));
    }
}
