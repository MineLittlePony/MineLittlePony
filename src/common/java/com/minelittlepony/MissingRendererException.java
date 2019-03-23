package com.minelittlepony;

public class MissingRendererException extends RuntimeException {
    private static final long serialVersionUID = -6059469512902628663L;

    public MissingRendererException(Class<?> cl) {
        super("Could not find a renderer for " + cl.getName() + ". This is a bug.");
    }
}
