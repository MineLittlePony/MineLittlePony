package com.voxelmodpack.hdskins;

public final class Later extends Thread {

    private final int delay;

    protected Later(int delay, Runnable runnable) {
        super(runnable);
        this.delay = delay;
    }

    public static void performLater(int delay, Runnable callable) {
        new Later(delay, callable).start();
    }

    public static void performNow(Runnable callable) {
        new Later(0, callable).start();
    }

    @Override
    public void run() {
        try {
            if (delay > 0) sleep(delay);
        } catch (InterruptedException e) {}
        super.run();
    }
}
