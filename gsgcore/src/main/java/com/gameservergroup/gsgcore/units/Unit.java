package com.gameservergroup.gsgcore.units;

import com.gameservergroup.gsgcore.GSGCore;

public abstract class Unit {

    public static final GSGCore GSG_CORE = GSGCore.getInstance();
    private Runnable runnable;

    public void call() {
        GSG_CORE.getUnits().add(this);
        setup();
    }

    public abstract void setup();

    public void hookDisable(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

}
