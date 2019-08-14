package com.gameservergroup.gsgcollectors.integration.impl.landowner;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.LandOwnerIntegration;
import com.gameservergroup.gsgcollectors.obj.Collector;
import com.gameservergroup.gsgcollectors.units.UnitCollectors;
import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LandOwnerASkyBlockImpl implements LandOwnerIntegration {

    @Override
    public void setupListeners(UnitCollectors unitCollectors) {
        EventPost.of(PlayerInteractEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getClickedBlock() != null)
                .filter(event -> event.getAction() == Action.RIGHT_CLICK_BLOCK)
                .handle(event -> {
                    Collector collector = unitCollectors.getCollector(event.getClickedBlock().getLocation());
                    if (collector != null && collector.getBlockPosition().equals(BlockPosition.of(event.getClickedBlock()))) {
                        Island islandAt = ASkyBlockAPI.getInstance().getIslandAt(event.getClickedBlock().getLocation());
                        if (islandAt.getMembers().contains(event.getPlayer().getUniqueId())) {
                            if (GSGCollectors.getInstance().getConfig().getBoolean("options.landowner.askyblock.only-leader")) {
                                if (!islandAt.getOwner().equals(event.getPlayer().getUniqueId())) {
                                    event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_NOT_YOURS.toString());
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        } else {
                            event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_NOT_YOURS.toString());
                            event.setCancelled(true);
                            return;
                        }
                        if (event.getPlayer().isSneaking() && event.getPlayer().hasPermission("gsgcollector.clicktosell")) {
                            collector.sellAll(event.getPlayer());
                        } else {
                            if (collector.getMenuCollector().getInventory().getViewers().isEmpty()) {
                                collector.getMenuCollector().refresh();
                            }
                            event.getPlayer().openInventory(collector.getMenuCollector().getInventory());
                        }
                        event.setCancelled(true);
                    }
                }).post(GSGCollectors.getInstance());
    }
}
