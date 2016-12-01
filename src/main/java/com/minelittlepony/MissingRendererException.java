package com.minelittlepony;

public class MissingRendererException extends RuntimeException {

    public MissingRendererException(Class<?> cl) {
        super("Could not find a renderer for " + cl.getName() + ". This is a bug.");
    }
}
