package com.gameservergroup.gsgprinter.integration;

import com.gameservergroup.gsgcore.utils.CallBack;
import org.bukkit.entity.Player;

public interface FactionsIntegration {

    void hookFlightDisable(CallBack<Player> callBack);

}
