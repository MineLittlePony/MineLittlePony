package com.minelittlepony.util.chron;

/**
 * DON'T TOUCH ME I'M SCAREED
 *
 * Basic touchable object that expires if not gently caressed for all of 30 seconds.
 */
public abstract class Touchable<T extends Touchable<T>> {

    private long expirationPeriod;

    /**
     * Returns whether this object is dead (expired).
     * Expired Touchables are flushed from the ChronicCache on next access.
     */
    public boolean hasExpired() {
        return expirationPeriod <= System.currentTimeMillis();
    }

    /**
     * Touches this object.
     * Internally just updates the expiration date.
     */
    @SuppressWarnings("unchecked")
    public T touch() {
        expirationPeriod = System.currentTimeMillis() + 30000;
        return (T)this;
    }
}
