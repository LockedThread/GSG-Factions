package com.massivecraft.factions.zcore.factionupgrades;

import com.gameservergroup.gsgcore.menus.Menu;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.units.UnitFactionUpgrade;

public class FactionUpgradeMenu extends Menu {

    private Faction faction;

    public FactionUpgradeMenu(Faction faction) {
        super(P.p.getConfig().getString("faction-upgrades.gui.name"), P.p.getConfig().getInt("faction-upgrades.gui.size"));
        this.faction = faction;
        initialize();
    }

    @Override
    public void initialize() {
        for (FactionUpgrade factionUpgrade : FactionUpgrade.values()) {
            if (factionUpgrade.isEnabled()) {
                setItem(factionUpgrade.getSlot(), factionUpgrade.getMenuItem(faction));
            }
        }

        if (P.p.getConfig().getBoolean("faction-upgrades.gui.fill.enabled")) {
            while (getInventory().firstEmpty() != -1) {
                setItem(getInventory().firstEmpty(), UnitFactionUpgrade.fillItemStack);
            }
        }
    }
}
