package com.gameservergroup.gsgcollectors.integration.impl.landowner;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.LandOwnerIntegration;
import com.gameservergroup.gsgcollectors.obj.Collector;
import com.gameservergroup.gsgcollectors.units.UnitCollectors;
import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.github.paperspigot.Title;

public class LandOwnerFactionsUUIDImpl implements LandOwnerIntegration {

    private Role atLeastRole;

    @Override
    public void setupListeners(UnitCollectors unitCollectors) {
        GSGCollectors.getInstance().getServer().getScheduler().runTaskLater(GSGCollectors.getInstance(), () -> atLeastRole = Role.fromString(GSGCollectors.getInstance().getConfig().getString("options.landowner.factions.at-least-role")), 1L);
        EventPost.of(PlayerInteractEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getClickedBlock() != null)
                .handle(event -> {
                    Collector collector = unitCollectors.getCollector(event.getClickedBlock().getLocation());
                    if (collector != null && collector.getBlockPosition().equals(BlockPosition.of(event.getClickedBlock()))) {
                        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
                        Faction myFaction = fPlayer.getFaction();
                        if (!unitCollectors.isEditWhilstFactionless() && myFaction.isWilderness() && !fPlayer.isAdminBypassing()) {
                            event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_FACTIONLESS.toString());
                        } else {
                            FLocation fLocation = new FLocation(event.getClickedBlock());
                            Faction factionThere = Board.getInstance().getFactionAt(fLocation);
                            if (!unitCollectors.isAccessNotYours() && factionThere.getRelationTo(myFaction) != Relation.MEMBER && !factionThere.isWilderness() && !fPlayer.isAdminBypassing()) {
                                event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_NOT_YOURS.toString());
                                event.setCancelled(true);
                            } else if (unitCollectors.isRoleRestricted() && !fPlayer.getRole().isAtLeast(atLeastRole) && !fPlayer.isAdminBypassing()) {
                                event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_NO_PERMISSIONS.toString().replace("{role}", atLeastRole.toString()));
                                event.setCancelled(true);
                            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                if (event.getPlayer().isSneaking() && event.getPlayer().hasPermission("gsgcollector.clicktosell")) {
                                    collector.sellAll(event.getPlayer());
                                } else {
                                    if (factionThere.isWilderness()) {
                                        collector.setLandOwner("Wilderness");
                                        collector.resetCollectorMenu();
                                    } else if (!collector.getLandOwner().equals(myFaction.getTag()) && factionThere.getRelationTo(myFaction) == Relation.MEMBER && !fPlayer.isAdminBypassing()) {
                                        collector.setLandOwner(myFaction.getTag());
                                        collector.resetCollectorMenu();
                                    }
                                    if (collector.getMenuCollector().getInventory().getViewers().isEmpty()) {
                                        collector.getMenuCollector().refresh();
                                    }
                                    event.getPlayer().openInventory(collector.getMenuCollector().getInventory());
                                }
                                event.setCancelled(true);
                            }
                        }
                    }
                }).post(GSGCollectors.getInstance());

        unitCollectors.setCollectorItem(CustomItem.of(GSGCollectors.getInstance(), GSGCollectors.getInstance().getConfig().getConfigurationSection("collector-item"))
                .setPlaceEventConsumer(event -> {
                    Collector collector = unitCollectors.getCollector(event.getBlockPlaced().getLocation());
                    if (collector == null) {

                        Faction factionAt = Board.getInstance().getFactionAt(new FLocation(event.getBlockPlaced()));

                        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());

                        if (factionAt.isWilderness() && GSGCollectors.getInstance().getConfig().getBoolean("options.landowner.factions.allowed-in-wilderness", false)) {
                            event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_WILDERNESS.toString());
                        } else if (factionAt.getTag().equals(fPlayer.getFaction().getTag())) {
                            if (fPlayer.getRole().isAtLeast(atLeastRole)) {
                                unitCollectors.createCollector(event.getBlockPlaced().getLocation(), unitCollectors.getFactionsBankIntegration() != null ? unitCollectors.getFactionsBankIntegration().getFaction(event.getPlayer()).getTag() : event.getPlayer().getName());
                                if (!CollectorMessages.TITLE_COLLECTOR_PLACE.toString().isEmpty()) {
                                    if (unitCollectors.isUseTitles()) {
                                        event.getPlayer().sendTitle(Title.builder().title(CollectorMessages.TITLE_COLLECTOR_PLACE.toString()).fadeIn(5).fadeOut(5).stay(25).build());
                                    } else {
                                        event.getPlayer().sendMessage(CollectorMessages.TITLE_COLLECTOR_PLACE.toString());
                                    }
                                }
                            } else {
                                event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_NOT_ROLE.toString());
                            }
                        } else {
                            event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_NOT_YOURS.toString());
                        }
                    } else if (canAccessCollector(event.getPlayer(), collector, event.getBlockPlaced().getLocation(), true)) {
                        collector.getBlockPosition().getBlock().setType(Material.AIR);
                        collector.setBlockPosition(BlockPosition.of(event.getBlockPlaced()));
                        event.getPlayer().sendMessage(CollectorMessages.UPDATED_COLLECTOR_BLOCKPOSITION.toString());
                        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                            event.getPlayer().setItemInHand(event.getItemInHand());
                        }
                    }
                }));
    }

    @Override
    public boolean canAccessCollector(Player player, Collector collector, Location location, boolean sendMessage) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction myFaction = fPlayer.getFaction();
        if (!GSGCollectors.getInstance().getUnitCollectors().isEditWhilstFactionless() && myFaction.isWilderness() && !fPlayer.isAdminBypassing()) {
            if (sendMessage) {
                player.sendMessage(CollectorMessages.NO_ACCESS_FACTIONLESS.toString());
            }
            return false;
        }
        FLocation fLocation = new FLocation(location);
        Faction factionThere = Board.getInstance().getFactionAt(fLocation);
        if (!GSGCollectors.getInstance().getUnitCollectors().isAccessNotYours() && factionThere.getRelationTo(myFaction) != Relation.MEMBER && !factionThere.isWilderness() && !fPlayer.isAdminBypassing()) {
            if (sendMessage) {
                player.sendMessage(CollectorMessages.NO_ACCESS_NOT_YOURS.toString());
            }
            return false;
        }
        if (GSGCollectors.getInstance().getUnitCollectors().isRoleRestricted() && !fPlayer.getRole().isAtLeast(atLeastRole) && !fPlayer.isAdminBypassing()) {
            if (sendMessage) {
                player.sendMessage(CollectorMessages.NO_ACCESS_NO_PERMISSIONS.toString().replace("{role}", atLeastRole.toString()));
            }
            return false;
        }
        return true;
    }
}
