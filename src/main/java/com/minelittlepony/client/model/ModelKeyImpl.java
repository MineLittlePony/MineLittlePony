package com.minelittlepony.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import com.google.common.base.Preconditions;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.mson.api.*;
import com.minelittlepony.mson.api.MsonModel.Factory;
import com.minelittlepony.mson.api.model.traversal.PartSkeleton;
import com.minelittlepony.mson.api.model.traversal.SkeletonisedModel;
import com.minelittlepony.mson.api.parser.FileContent;
import com.minelittlepony.mson.api.parser.locals.LocalBlock;
import com.minelittlepony.mson.impl.model.RootContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;

final class ModelKeyImpl<M extends Model> implements ModelKey<M>, LocalBlock {

    private final Map<String, Incomplete<Float>> horseModeValues = Util.make(new HashMap<>(), map -> {
        map.put("head_elongation", Incomplete.completed(-1F));
        map.put("neck_dilate_z", Incomplete.completed(1.5F));
        map.put("neck_dilate_y", Incomplete.completed(3F));
        map.put("global_ear_shortening", Incomplete.completed(-0.5F));
    });

    private final ModelKey<M> key;
    private final MsonModel.Factory<M> constr;

    ModelKeyImpl(Identifier id, MsonModel.Factory<M> constr) {
        this.key = Mson.getInstance().registerModel(id, constr);
        this.constr = constr;
    }

    @Override
    public Identifier getId() {
        return key.getId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends M> V createModel() {
        return (V)createModel(constr);
    }

    @Override
    public <V extends M> V createModel(Factory<V> factory) {
        Preconditions.checkNotNull(factory, "Factory should not be null");

        return getModelData().map(content -> {

            ModelContext ctx = getModelContext(content);

            ModelPart root = ctx.toTree();
            V t = factory.create(root);

            if (t instanceof SkeletonisedModel) {
                ((SkeletonisedModel)t).setSkeleton(content.getSkeleton()
                        .map(s -> PartSkeleton.of(root, s))
                        .orElseGet(() -> PartSkeleton.of(root)));
            }
            if (t instanceof MsonModel) {
                if (ctx instanceof RootContext) {
                    ((RootContext)ctx).setModel(t);
                }
                ((MsonModel)t).init((ModelView)ctx);
            }
            return t;
        })
        .orElseThrow(() -> new IllegalStateException("Model file for " + getId() + " was not loaded!"));
    }

    @Override
    public Optional<ModelPart> createTree() {
        return getModelData().map(this::getModelContext).map(ModelContext::toTree);
    }

    private ModelContext getModelContext(FileContent<?> content) {
        if (MineLittlePony.getInstance().getConfig().horsieMode.get()) {
            return content.createContext(null, null, content.getLocals().extendWith(getId(), Optional.of(this), Optional.empty()).bake());
        }
        return content.createContext(null, null, content.getLocals().bake());
    }

    @Override
    public Optional<FileContent<?>> getModelData() {
        return key.getModelData();
    }

    @Override
    public Set<String> appendKeys(Set<String> output) {
        output.addAll(horseModeValues.keySet());
        return output;
    }

    @Override
    public Optional<CompletableFuture<Incomplete<Float>>> get(String name) {
        if (horseModeValues.containsKey(name)) {
            return Optional.of(CompletableFuture.completedFuture(horseModeValues.get(name)));
        }
        return Optional.empty();
    }
}