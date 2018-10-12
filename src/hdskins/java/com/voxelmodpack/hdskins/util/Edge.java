package com.voxelmodpack.hdskins.util;

public abstract class Edge {

    private boolean previousState;

    private Callback callback;

    public Edge(Callback callback) {
        this.callback = callback;
    }

    public void update() {
        boolean state = nextState();

        if (state != previousState) {
            previousState = state;
            callback.call(state);
        }
    }

    public boolean getState() {
        return previousState;
    }

    protected abstract boolean nextState();

    @FunctionalInterface
    public interface Callback {
        void call(boolean state);
    }
}