package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.factionstatistics.FactionStatistic;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;


public class FactionsBlockListener implements Listener {

    public P p;

    public FactionsBlockListener(P p) {
        this.p = p;
    }

    public static boolean playerCanBuildDestroyBlock(Player player, Location location, String action, boolean justCheck) {
        String name = player.getName();
        if (Conf.playersWhoBypassAllProtection.contains(name)) {
            return true;
        }

        FPlayer me = FPlayers.getInstance().getById(player.getUniqueId().toString());
        if (me.isAdminBypassing()) {
            return true;
        }

        FLocation loc = new FLocation(location);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);

        if (otherFaction.isWilderness()) {
            if (Conf.worldGuardBuildPriority && Worldguard.playerCanBuild(player, location)) {
                return true;
            }

            if (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
                return true; // This is not faction territory. Use whatever you like here.
            }

            if (!justCheck) {
                me.msg("<b>You can't " + action + " in the wilderness.");
            }

            return false;
        } else if (otherFaction.isSafeZone()) {
            if (Conf.worldGuardBuildPriority && Worldguard.playerCanBuild(player, location)) {
                return true;
            }

            if (!Conf.safeZoneDenyBuild || Permission.MANAGE_SAFE_ZONE.has(player)) {
                return true;
            }

            if (!justCheck) {
                me.msg("<b>You can't " + action + " in a safe zone.");
            }

            return false;
        } else if (otherFaction.isWarZone()) {
            if (Conf.worldGuardBuildPriority && Worldguard.playerCanBuild(player, location)) {
                return true;
            }

            if (!Conf.warZoneDenyBuild || Permission.MANAGE_WAR_ZONE.has(player)) {
                return true;
            }

            if (!justCheck) {
                me.msg("<b>You can't " + action + " in a war zone.");
            }

            return false;
        }

        // Also cancel and/or cause pain if player doesn't have ownership rights for this claim
        if (Conf.ownedAreasEnabled && (Conf.ownedAreaDenyBuild || Conf.ownedAreaPainBuild) && !otherFaction.playerHasOwnershipRights(me, loc)) {
//            if (!pain && Conf.ownedAreaPainBuild && !justCheck) {
//                player.damage(Conf.actionDeniedPainAmount);
//
//                if (!Conf.ownedAreaDenyBuild) {
//                    me.msg("<b>It is painful to try to " + action + " in this territory, it is owned by: " + otherFaction.getOwnerListString(loc));
//                }
//            }
            if (Conf.ownedAreaDenyBuild) {
                if (!justCheck) {
                    me.msg("<b>You can't " + action + " in this territory, it is owned by: " + otherFaction.getOwnerListString(loc));
                }

                return false;
            }
        }

//        if (P.p.getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
//            return true;
//        }

        Faction myFaction = me.getFaction();
        Relation rel = myFaction.getRelationTo(otherFaction);
//        boolean online = otherFaction.hasPlayersOnline();
//        boolean pain = !justCheck && rel.confPainBuild(online);
        boolean deny = rel.confDenyBuild(true); // online status doesn't matter for us

        Access access = otherFaction.getAccess(me, PermissableAction.fromString(action));
        if (access == Access.ALLOW) {
            return true;
        }

        // cancel building/destroying in other territory?
        if (deny) {
            if (!justCheck) {
                me.msg("<b>You can't " + action + " in the territory of " + otherFaction.getTag(myFaction));
            }

            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.canBuild()) {
            return;
        }

        // special case for flint&steel, which should only be prevented by DenyUsage list
        if (event.getBlockPlaced().getType() == Material.FIRE) {
            return;
        }

        // special case for alts
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer.hasAltFaction()) {
            Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

            // only sand and gravel in own land
            switch (event.getBlock().getType()) {
                case SAND:
                case GRAVEL:
                    if (faction != null && faction.equals(fPlayer.getAltFaction()) && faction.isNormal()) {
                        return; // allow it
                    }
                    break;
            }

            fPlayer.msg("<b>You are only allowed to place sand & gravel in your territory as an alt account!");
            event.setCancelled(true);
            return;
        }

        if (playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "build", false)) {
            fPlayer.incrementFactionStatistic(FactionStatistic.BLOCKS_PLACED);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!Conf.handleExploitLiquidFlow) {
            return;
        }
        if (event.getBlock().isLiquid()) {
            if (event.getToBlock().isEmpty()) {
                Faction from = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));
                Faction to = Board.getInstance().getFactionAt(new FLocation(event.getToBlock()));
                if (from == to) {
                    // not concerned with inter-faction events
                    return;
                }
                // from faction != to faction
                if (to.isNormal()) {
                    if (from.isNormal() && from.getRelationTo(to).isAlly()) {
                        return;
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        // special case for alts
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer.hasAltFaction()) {
            Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

            // only sand and gravel in own land
            switch (event.getBlock().getType()) {
                case SAND:
                case GRAVEL:
                    if (faction != null && faction.isNormal() && faction.equals(fPlayer.getAltFaction())) {
                        return; // allow it
                    }
                    break;
            }

            fPlayer.msg("<b>You are only allowed to break sand & gravel in your territory as an alt account!");
            event.setCancelled(true);
            return;
        }

        if (playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false)) {
            fPlayer.incrementFactionStatistic(FactionStatistic.BLOCKS_BROKEN);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getInstaBreak() && !playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!Conf.pistonProtectionThroughDenyBuild) {
            return;
        }

        Faction pistonFaction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

        // target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
        Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

        // if potentially pushing into air/water/lava in another territory, we need to check it out
        if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && !canPistonMoveBlock(pistonFaction, targetBlock.getLocation())) {
            event.setCancelled(true);
        }

        /*
         * note that I originally was testing the territory of each affected block, but since I found that pistons can only push
         * up to 12 blocks and the width of any territory is 16 blocks, it should be safe (and much more lightweight) to test
         * only the final target block as done above
         */
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        // if not a sticky piston, retraction should be fine
        if (!event.isSticky() || !Conf.pistonProtectionThroughDenyBuild) {
            return;
        }

        Location targetLoc = event.getBlock().getRelative(event.getDirection().getOppositeFace(), 2).getLocation();
        Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(targetLoc));

        // Check if the piston is moving in a faction's territory. This disables pistons entirely in faction territory.
        if (otherFaction.isNormal() && P.p.getConfig().getBoolean("disable-pistons-in-territory", false)) {
            event.setCancelled(true);
            return;
        }

        // if potentially retracted block is just air/water/lava, no worries
        if (targetLoc.getBlock().isEmpty() || targetLoc.getBlock().isLiquid()) {
            return;
        }

        Faction pistonFaction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

        if (!canPistonMoveBlock(pistonFaction, targetLoc)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        if (!event.isCancelled()) {
            if (event.getItem() != null && event.getItem().getType() == Material.TNT) {
                Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getBlock().getLocation()));
                if (!faction.isWilderness()) {
                    if (faction.getFactionShield() != null) {
                        if (faction.getFactionShieldCachedValue()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    private boolean canPistonMoveBlock(Faction pistonFaction, Location target) {

        Faction otherFaction = Board.getInstance().getFactionAt(new FLocation(target));

        if (pistonFaction == otherFaction) {
            return true;
        }

        if (otherFaction.isWilderness()) {
            return !Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(target.getWorld().getName());

        } else if (otherFaction.isSafeZone()) {
            return !Conf.safeZoneDenyBuild;

        } else if (otherFaction.isWarZone()) {
            return !Conf.warZoneDenyBuild;

        }

        Relation rel = pistonFaction.getRelationTo(otherFaction);

        return !rel.confDenyBuild(otherFaction.hasPlayersOnline());
    }
}
