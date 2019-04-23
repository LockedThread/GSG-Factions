package com.massivecraft.factions.zcore.factionupgrades;

import com.gameservergroup.gsgcore.menus.Menu;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.units.UnitFactionUpgrade;
import org.bukkit.entity.Player;

public class FactionUpgradeMenu extends Menu {

    public FactionUpgradeMenu() {
        super(P.p.getConfig().getString("faction-upgrades.gui.name"), P.p.getConfig().getInt("faction-upgrades.gui.size"));
        setInventoryOpenEventConsumer(event -> {
            FPlayers.getInstance().getByPlayer((Player) event.getPlayer()).setViewingUpgradeMenu(true);
            for (FactionUpgrade factionUpgrade : FactionUpgrade.values()) {
                if (factionUpgrade.isEnabled()) {
                    setItem(factionUpgrade.getSlot(), factionUpgrade.getMenuItem());
                }
            }
        });
        setInventoryCloseEventConsumer(event -> FPlayers.getInstance().getByPlayer((Player) event.getPlayer()).setViewingUpgradeMenu(false));
    }

    @Override
    public void initialize() {
        if (P.p.getConfig().getBoolean("faction-upgrades.gui.fill.enabled")) {
            while (getInventory().firstEmpty() != -1) {
                setItem(getInventory().firstEmpty(), UnitFactionUpgrade.fillItemStack);
            }
        }
    }
}
