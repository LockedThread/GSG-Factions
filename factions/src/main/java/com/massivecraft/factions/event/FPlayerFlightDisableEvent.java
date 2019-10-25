package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FPlayerFlightDisableEvent extends FactionPlayerEvent {

    public FPlayerFlightDisableEvent(Faction faction, FPlayer fPlayer) {
        super(faction, fPlayer);
    }
}
