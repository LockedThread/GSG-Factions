package com.gameservergroup.gsgprinter.integration.factions.impl;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.FactionsIntegration;
import com.massivecraft.factions.event.FPlayerFlightDisableEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import org.bukkit.entity.Player;

public class LockedThreadFactionsUUIDImpl implements FactionsIntegration {

    @Override
    public void hookFlightDisable() {
        EventPost.of(FPlayerFlightDisableEvent.class)
                .filter(event -> event.getfPlayer() != null && GSGPrinter.getInstance().getUnitPrinter().getPrintingPlayers().containsKey(event.getfPlayer().getPlayer().getUniqueId()))
                .handle(event -> GSGPrinter.getInstance().getUnitPrinter().disablePrinter(event.getfPlayer().getPlayer(), true, true))
                .post(GSGPrinter.getInstance());
    }

    @Override
    public void setupListeners() {
        EventPost.of(FactionDisbandEvent.class)
                .handle(event -> {
                    for (Player onlinePlayer : event.getFaction().getOnlinePlayers()) {
                        GSGPrinter.getInstance().getUnitPrinter().disablePrinter(onlinePlayer, true);
                    }
                }).post(GSGPrinter.getInstance());

        EventPost.of(FPlayerLeaveEvent.class)
                .handle(event -> GSGPrinter.getInstance().getUnitPrinter().disablePrinter(event.getfPlayer().getPlayer(), true))
                .post(GSGPrinter.getInstance());
    }
}
