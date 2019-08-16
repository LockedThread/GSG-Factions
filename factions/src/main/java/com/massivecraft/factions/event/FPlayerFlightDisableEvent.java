package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FPlayerFlightDisableEvent extends FactionPlayerEvent {

    public FPlayerFlightDisableEvent(Faction faction, FPlayer fPlayer) {
        super(faction, fPlayer);
        System.out.println("FPlayerFlightDisableEvent Fired");
        System.out.println("faction = [" + faction.toString() + "], fPlayer = [" + fPlayer.toString() + "]");
    }
}
