package com.minelittlepony.client.util.render;

import net.minecraft.client.model.Box;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.Quad;
import net.minecraft.client.model.Vertex;
import net.minecraft.util.math.Direction;

public class Part extends ModelPart {

    protected final Model baseModel;

    protected int textureOffsetX;
    protected int textureOffsetY;

    protected float modelOffsetX;
    protected float modelOffsetY;
    protected float modelOffsetZ;

    public boolean mirrory;
    public boolean mirrorz;

    public Part(Model model) {
        super(model);
        baseModel = model;
    }

    public Part(Model model, int texX, int texY) {
        super(model, texX, texY);
        baseModel = model;
    }

    /**
     * Called to create a new instance of this renderer (used for child renderers)
     */
    protected Part copySelf() {
        return new Part(baseModel, textureOffsetX, textureOffsetY);
    }

    @Override
    public Part setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        super.setTextureOffset(x, y);
        return  this;
    }

    /**
     * Flips the mirror flag. All faces are mirrored until this is called again.
     */
    public Part flip() {
        return mirror(!mirror);
    }

    public Part mirror(boolean m) {
        mirror = m;
        return  this;
    }

    /**
     * Sets the texture offset
     */
    public Part tex(int x, int y) {
        return setTextureOffset(x, y);
    }

    /**
     * Sets the texture size for this renderer.
     */
    public Part size(int w, int h) {
        return (Part)setTextureSize(w, h);
    }

    /**
     * Positions this model in space.
     */
    public Part at(float x, float y, float z) {
        return at(this, x, y, z);
    }

    /**
     * Sets an offset to be used on all shapes and children created through this renderer.
     */
    public Part offset(float x, float y, float z) {
        modelOffsetX = x;
        modelOffsetY = y;
        modelOffsetZ = z;
        return  this;
    }

    /**
     * Adjusts the rotation center of the given renderer by the given amounts in each direction.
     */
    public static void shiftRotationPoint(ModelPart renderer, float x, float y, float z) {
        renderer.pivotX += x;
        renderer.pivotY += y;
        renderer.pivotZ += z;
    }

    /**
     * Sets this renderer's rotation angles.
     */
    public Part rotate(float x, float y, float z) {
        pitch = x;
        yaw = y;
        roll = z;
        return  this;
    }

    /**
     * Positions a given model in space by setting its offset values divided
     * by 16 to account for scaling applied inside the model.
     */
    public static <T extends ModelPart> T at(T renderer, float x, float y, float z) {
        renderer.x = x / 16;
        renderer.y = y / 16;
        renderer.z = z / 16;
        return renderer;
    }

    /**
     * Rotates this model to align itself with the angles of another.
     */
    public void rotateTo(ModelPart other) {
        rotate(other.pitch, other.yaw, other.roll);
    }

    /**
     * Shifts this model to align its center with the center of another.
     */
    public Part rotateAt(ModelPart other) {
        return around(other.pivotX, other.pivotY, other.pivotZ);
    }

    /**
     * Sets the rotation point.
     */
    public Part around(float x, float y, float z) {
        setPivot(x, y, z);
        return  this;
    }

    /**
     * Gets or creates a new child model based on its unique index.
     * New children will be of the same type and inherit the same textures and offsets of the original.
     */
    public Part child(int index) {
        if (children == null || index >= children.size()) {
            return child();
        }
        return (Part)children.get(index);
    }

    /**
     * Returns a brand new child under this renderer.
     */
    public Part child() {
        Part copy = copySelf();
        child(copy.offset(modelOffsetX, modelOffsetY, modelOffsetZ));
        copy.textureHeight = textureHeight;
        copy.textureWidth = textureWidth;
        return copy;
    }

    /**
     * Adds a new child renderer and returns itself for chaining.
     */
    public <K extends ModelPart> Part child(K child) {
        addChild(child);
        return  this;
    }


    /**
     * Flips the Z bit. Any calls to add a plane will be mirrored until this is called again.
     */
    public Part flipZ() {
        mirrorz = !mirrorz;
        return  this;
    }

    /**
     * Flips the Y bit. Any calls to add a plane will be mirrored until this is called again.
     */
    public Part flipY() {
        mirrory = !mirrory;
        return  this;
    }

    private Part addPlane(float offX, float offY, float offZ, int width, int height, int depth, float scale, Direction face) {
        cuboids.add(new Plane(this, textureOffsetX, textureOffsetY, modelOffsetX + offX, modelOffsetY + offY, modelOffsetZ + offZ, width, height, depth, scale, face));
        return  this;
    }

    public Part top(float offX, float offY, float offZ, int width, int depth, float scale) {
        return addPlane(offX, offY, offZ, width, 0, depth, scale, Direction.UP);
    }

    public Part bottom(float offX, float offY, float offZ, int width, int depth, float scale) {
        return addPlane(offX, offY, offZ, width, 0, depth, scale, Direction.DOWN);
    }

    public Part west(float offX, float offY, float offZ, int height, int depth, float scale) {
        return addPlane(offX, offY, offZ, 0, height, depth, scale, Direction.WEST);
    }

    public Part east(float offX, float offY, float offZ, int height, int depth, float scale) {
        return addPlane(offX, offY, offZ, 0, height, depth, scale, Direction.EAST);
    }

    public Part north(float offX, float offY, float offZ, int width, int height, float scale) {
        return addPlane(offX, offY, offZ - scale * 2, width, height, 0, scale, Direction.NORTH);
    }

    public Part south(float offX, float offY, float offZ, int width, int height, float scale) {
        return addPlane(offX, offY, offZ + scale * 2, width, height, 0, scale, Direction.SOUTH);
    }

    @Override
    public Part addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth, float unknown, int texX, int texY) {
        partName = name + "." + partName;

        setTextureOffset(texX, texY);
        addBox(offX, offY, offZ, width, height, depth);
        cuboids.get(cuboids.size() - 1).setName(partName);

        return  this;
    }

    @Override
    public Part addBox(float offX, float offY, float offZ, int width, int height, int depth) {
        addCuboid(offX, offY, offZ, width, height, depth, 0);
        return  this;
    }

    @Override
    public Part addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
        addCuboid(offX, offY, offZ, width, height, depth, 0, mirrored);
        return this;
    }

    @Override
    public void addCuboid(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
        addCuboid(offX, offY, offZ, width, height, depth, scaleFactor, mirror);
    }

    @Override
    public void addCuboid(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        createBox(offX, offY, offZ, width, height, depth, scaleFactor, mirrored);
    }

    /**
     * Creates a textured box.
     */
    public Part box(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
        addCuboid(offX, offY, offZ, width, height, depth, scaleFactor, mirror);

        return this;
    }

    public Part cone(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
        cuboids.add(new Cone(this, textureOffsetX, textureOffsetY, modelOffsetX + offX, modelOffsetY + offY, modelOffsetZ + offZ, width, height, depth, scaleFactor));

        return this;
    }

    protected void createBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrored) {
        cuboids.add(new Box(this, textureOffsetX, textureOffsetY, modelOffsetX + offX, modelOffsetY + offY, modelOffsetZ + offZ, width, height, depth, scaleFactor, mirrored));
    }

    /**
     * Creates a new vertex mapping the given (x, y, z) coordinates to a texture offset.
     */
    Vertex vert(float x, float y, float z, int texX, int texY) {
        return new Vertex(x, y, z, texX, texY);
    }

    /**
     * Creates a new quad with the given spacial vertices.
     */
    Quad quad(int startX, int width, int startY, int height, Vertex ...verts) {
        return new Quad(verts,
                startX,         startY,
                startX + width, startY + height,
                textureWidth, textureHeight);
    }
}
