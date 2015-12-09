package com.brohoof.minelittlepony;

public enum TailLengths {

    STUB(4),
    QUARTER(3),
    HALF(2),
    THREE_QUARTERS(1),
    FULL(0);

    private int size;

    private TailLengths(int size) {
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }
}
