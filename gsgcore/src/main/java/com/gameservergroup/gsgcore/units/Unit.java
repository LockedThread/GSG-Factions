package com.gameservergroup.gsgcore.units;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.utils.CallBack;

public abstract class Unit {

    public static final GSGCore GSG_CORE = GSGCore.getInstance();
    private CallBack callBack;

    public void call() {
        setup();
    }

    public abstract void setup();

    public void hookDisable(CallBack callBack) {
        this.callBack = callBack;
    }

    public CallBack getCallBack() {
        return callBack;
    }
}
