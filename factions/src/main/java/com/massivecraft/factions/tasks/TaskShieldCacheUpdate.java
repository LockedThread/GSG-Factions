package com.massivecraft.factions.tasks;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.factionshields.FactionShield;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class TaskShieldCacheUpdate extends BukkitRunnable {

    @Override
    public void run() {
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            FactionShield factionShield = faction.getFactionShield();
            if (factionShield != null) {
                boolean cachedValue = factionShield.isInBetween(null);
                faction.setFactionShieldCachedValue(cachedValue);
                P.p.log(Level.INFO, faction.getTag() + " has an " + (cachedValue ? "active" : "inactive") + " faction shield");
                P.p.log(Level.INFO, faction.getTag() + '=' + factionShield.toString());
            }
        }
    }
}
