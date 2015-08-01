package com.minelittlepony.minelp.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class CompressiveRendering {
    private boolean isCompressed;
    private int chance = 1;
    private Collection<CompressiveRendering> me;
    private ArrayList<ModelRenderer> expanded;
    private ArrayList<ModelRenderer> compressed;
    private ModelBase model;

    public CompressiveRendering(ModelBase model) {
        this.model = model;
        this.expanded = new ArrayList<ModelRenderer>(1);
        this.compressed = new ArrayList<ModelRenderer>(1);
        model.boxList.remove(this.compressed);
    }

    public void addExpanded(ModelRenderer expanded) {
        this.model.boxList.remove(expanded);
        this.expanded.add(expanded);
    }

    public void addExpanded(ModelRenderer expanded, int weighted) {
        this.model.boxList.remove(expanded);
        this.expanded.addAll(Collections.nCopies(weighted, expanded));
    }

    public void addCompressed(ModelRenderer compressed) {
        this.model.boxList.remove(compressed);
        this.compressed.add(compressed);
    }

    public void addCompressed(ModelRenderer compressed, int weighted) {
        this.model.boxList.remove(compressed);
        this.compressed.addAll(Collections.nCopies(weighted, compressed));
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public int getChance() {
        return this.chance;
    }

    public void init() {
        this.me = Collections.nCopies(this.chance, this);
        this.model.boxList.addAll(this.me);
    }

    public void deInit() {
        this.model.boxList.removeAll(this.me);
        this.me = null;
    }

    public void init_Safe() {
        if (this.me == null) {
            this.init();
        }

    }

    public void deInit_Safe() {
        if (this.me != null) {
            this.deInit();
        }

    }

    public void setIsCompressed(boolean isCompressed) {
        this.isCompressed = isCompressed;
    }

    public boolean getIsCompressed() {
        return this.isCompressed;
    }

    public ModelRenderer getARenderer(Random rand) {
        return this.isCompressed ? (ModelRenderer) this.compressed.get(rand.nextInt(this.compressed.size()))
                : (ModelRenderer) this.expanded.get(rand.nextInt(this.expanded.size()));
    }
}
