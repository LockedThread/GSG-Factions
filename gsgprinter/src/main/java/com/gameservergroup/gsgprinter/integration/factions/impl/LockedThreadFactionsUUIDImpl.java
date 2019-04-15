package com.gameservergroup.gsgprinter.integration.factions.impl;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.FactionsIntegration;
import com.massivecraft.factions.event.FPlayerFlightDisableEvent;

public class LockedThreadFactionsUUIDImpl implements FactionsIntegration {

    @Override
    public void hookFlightDisable() {
        EventPost.of(FPlayerFlightDisableEvent.class)
                .filter(event -> event.getfPlayer() != null && GSGPrinter.getInstance().getUnitPrinter().getPrintingPlayers().containsKey(event.getfPlayer().getPlayer().getUniqueId()))
                .handle(event -> GSGPrinter.getInstance().getUnitPrinter().disablePrinter(event.getfPlayer().getPlayer(), true, true))
                .post(GSGPrinter.getInstance());
    }
}
