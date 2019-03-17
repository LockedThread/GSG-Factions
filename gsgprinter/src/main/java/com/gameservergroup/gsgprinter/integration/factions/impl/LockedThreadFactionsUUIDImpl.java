package com.gameservergroup.gsgprinter.integration.factions.impl;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.utils.CallBack;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.FactionsIntegration;
import com.massivecraft.factions.event.FPlayerFlightDisableEvent;
import org.bukkit.entity.Player;

public class LockedThreadFactionsUUIDImpl implements FactionsIntegration {

    @Override
    public void hookFlightDisable(CallBack<Player> callBack) {
        EventPost.of(FPlayerFlightDisableEvent.class)
                .handle(event -> callBack.call(event.getfPlayer().getPlayer()))
                .post(GSGPrinter.getInstance());
    }
}
