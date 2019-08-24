package com.gameservergroup.gsgcollectors.integration;

import com.gameservergroup.gsgcollectors.obj.Collector;
import com.gameservergroup.gsgcollectors.units.UnitCollectors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface LandOwnerIntegration {

    void setupListeners(UnitCollectors unitCollectors);

    boolean canAccessCollector(Player player, Collector collector, Location location, boolean sendMessage);
}
