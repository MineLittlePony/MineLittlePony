package com.minelittlepony.client.util.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.model.BoxBuilder;
import com.minelittlepony.mson.api.model.BoxBuilder.ContentAccessor;
import com.minelittlepony.mson.api.model.Face;
import com.minelittlepony.mson.api.model.MsonPart;
import com.minelittlepony.mson.api.model.QuadsBuilder;

@Deprecated
public class Part extends ModelPart implements MsonPart, ModelContext {

    public Part(Model model) {
        super(model);
    }

    public Part(Model model, int texX, int texY) {
        super(model, texX, texY);
    }

    @Deprecated
    public Part child() {
        return this;
    }

    @Deprecated
    public Part flipX() {
        return this;
    }

    @Deprecated
    public Part flipZ() {
        return this;
    }

    @Deprecated
    public Part child(int i) {
        return this;
    }

    @Deprecated
    public Part child(Part prt) {
        return prt;
    }

    @Deprecated
    public Part mirror(boolean m) {
        this.mirror = m;
        return this;
    }

    @Deprecated
    public Part size(int u, int v) {
        this.setTextureSize(u, v);
        return this;
    }

    @Deprecated
    public Part tex(int u, int v) {
        this.setTextureOffset(u, v);
        return this;
    }

    @Override
    @Deprecated
    public Part offset(float x, float y, float z) {
        return this;
    }

    @Override
    @Deprecated
    public Part around(float x, float y, float z) {
        this.setPivot(x, y, z);
        return this;
    }
    @Override
    @Deprecated
    public Part rotate(float x, float y, float z) {
        MsonPart.super.rotate(x, y, z);
        return this;
    }

    @Deprecated
    public Part face(float x, float y, float z, int w, int h, float stretch, Face face) {
        ((ContentAccessor)this).cubes().add(new BoxBuilder(this)
            .pos(face.transformPosition(new float[] {x,  y, z}, this))
            .size(face.getAxis(), new int[] {w, h})
            .stretch(stretch, stretch, stretch)
            .build(QuadsBuilder.plane(face)));
        return this;
    }

    @Deprecated
    public Part box(float x, float y, float z, int w, int h, int d, float stretch) {
        ((ContentAccessor)this).cubes().add(new BoxBuilder(this)
                .pos(x,  y, z)
                .size(w, h, d)
                .stretch(stretch, stretch, stretch)
                .build());
        return this;
    }

    @Deprecated
    public Part east(float x, float y, float z, int w, int h, float stretch) {
        return face(x, y, z, w, h, stretch, Face.EAST);
    }

    @Deprecated
    public Part west(float x, float y, float z, int w, int h, float stretch) {
        return face(x, y, z, w, h, stretch, Face.WEST);
    }

    @Deprecated
    public Part north(float x, float y, float z, int w, int h, float stretch) {
        return face(x, y, z, w, h, stretch, Face.NORTH);
    }

    @Deprecated
    public Part south(float x, float y, float z, int w, int h, float stretch) {
        return face(x, y, z, w, h, stretch, Face.SOUTH);
    }

    @Deprecated
    public Part top(float x, float y, float z, int w, int h, float stretch) {
        return face(x, y, z, w, h, stretch, Face.UP);
    }

    @Deprecated
    public Part bottom(float x, float y, float z, int w, int h, float stretch) {
        return face(x, y, z, w, h, stretch, Face.DOWN);
    }

    @Override
    public Model getModel() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getContext() throws ClassCastException {
        return (T)this;
    }

    @Override
    public Locals getLocals() {
        return null;
    }

    @Override
    public float getScale() {
        return 0;
    }

    @Override
    public <T> T computeIfAbsent(String name, ContentSupplier<T> supplier) {
        return null;
    }

    @Override
    public <T> T findByName(String name) {
        return null;
    }

    @Override
    public void findByName(String name, ModelPart output) {
    }

    @Override
    public ModelContext getRoot() {
        return this;
    }

    @Override
    public ModelContext resolve(Object child) {
        return this;
    }
}
