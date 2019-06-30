package com.massivecraft.factions.zcore.factionmissions.menus;

import com.gameservergroup.gsgcore.menus.Menu;
import com.massivecraft.factions.P;
import com.massivecraft.factions.units.UnitFactionMissions;

public class MenuMissionsRoot extends Menu {

    public MenuMissionsRoot() {
        super(P.p.getConfig().getString("faction-missions.menus.root.name"), P.p.getConfig().getInt("faction-missions.menus.root.size"));
    }

    @Override
    public void initialize() {

        if (UnitFactionMissions.getInstance().getFillMenuItem() != null) {
            while (getInventory().firstEmpty() != -1) {
                setItem(getInventory().firstEmpty(), UnitFactionMissions.getInstance().getFillMenuItem());
            }
        }
    }
}
