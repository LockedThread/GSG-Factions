package com.gameservergroup.gsgoutpost.tasks;

import com.gameservergroup.gsgoutpost.GSGOutpost;
import com.gameservergroup.gsgoutpost.enums.OutpostMessages;
import com.gameservergroup.gsgoutpost.enums.OutpostState;
import com.gameservergroup.gsgoutpost.objs.Outpost;
import com.gameservergroup.gsgoutpost.utils.PercentageUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class TaskOutpost2 extends BukkitRunnable {

    private static final GSGOutpost GSG_OUTPOST = GSGOutpost.getInstance();

    @Override
    public void run() {
        for (Outpost outpost : GSG_OUTPOST.getOutpostMap().values()) {
            OutpostState outpostState = outpost.getOutpostState();
            System.out.println("outpostState = " + outpostState);
            if (outpostState == OutpostState.DISABLED) {
                break;
            }

            Optional<World> worldOptional = outpost.getWorld();
            if (worldOptional.isPresent()) {
                World world = worldOptional.get();
                RegionManager regionManager = WGBukkit.getRegionManager(world);
                for (Player player : world.getPlayers()) {
                    if (player.isDead()) continue;
                    Optional<ProtectedRegion> protectedRegionOptional = outpost.getProtectedRegion();
                    if (protectedRegionOptional.isPresent()) {
                        ProtectedRegion protectedRegion = protectedRegionOptional.get();
                        if (regionManager.getApplicableRegions(player.getLocation()).getRegions().contains(protectedRegion)) {
                            if (outpost.getPlayers().contains(player)) {
                                Bukkit.broadcastMessage(player.getName() + " is still registered in the region");
                            } else {
                                outpost.getPlayers().add(player);
                                Bukkit.broadcastMessage("Added " + player.getName() + " to the outpost region");
                            }
                        } else {
                            if (outpost.getPlayers().contains(player)) {
                                Bukkit.broadcastMessage("Removed " + player.getName() + " to the outpost region");
                                outpost.getPlayers().remove(player);
                            }
                        }
                    }
                }

                if (outpost.getPlayers().isEmpty() && outpost.getOutpostState() != OutpostState.CAPTURED) {
                    if (outpost.getOutpostState() == OutpostState.CAPTURING) {
                        outpost.setOutpostState(OutpostState.CAPTURE_WAITING);
                    }
                    if (outpost.getOutpostState() != OutpostState.CAPTURE_WAITING) {
                        outpost.setOutpostState(OutpostState.NEUTRALIZE_WAITING);
                    }
                }

                if (outpost.getOutpostState() == OutpostState.NEUTRALIZING) {
                    outpost.setPercentage(outpost.getPercentage() - 1);
                    GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_NEUTRALIZING.toString().replace("{faction}", outpost.getFaction().getTag()).replace("{formatted-percentage}", PercentageUtil.getPercentage(outpost.getPercentage())).replace("{percentage}", String.valueOf(outpost.getPercentage())));

                    if (outpost.getPercentage() == 0) {
                        outpost.setOutpostState(OutpostState.NEUTRALIZED);
                        outpost.setPercentage(101);
                    }
                }

                if (outpost.getOutpostState() == OutpostState.NEUTRALIZED) {
                    outpost.setPercentage(outpost.getPercentage() - 1);

                    Bukkit.broadcastMessage(OutpostMessages.OUTPOST_STATUS_NEUTRALIZED.toString().replace("{outpost}", outpost.getUniqueIdentifier()).replace("{faction}", outpost.getFaction().getTag()));

                    if (outpost.getPercentage() == 100) {
                        outpost.setOutpostState(OutpostState.CAPTURING);
                        outpost.setPercentage(0);
                    }
                }

                boolean oneFaction = outpost.isOneFaction();
                if (oneFaction) {
                    if (outpost.getPercentage() < 100) {
                        if (outpost.getOutpostState() != OutpostState.CAPTURING) {
                            outpost.setOutpostState(OutpostState.CAPTURING);
                            GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_CAPTURING_BROADCAST
                                    .toString()
                                    .replace("{faction}", outpost.getFaction().getTag())
                                    .replace("{formatted-percentage}", PercentageUtil.getPercentage(outpost.getPercentage()))
                                    .replace("{percentage}", String.valueOf(outpost.getPercentage())));
                        }
                        if (outpost.getOutpostState() == OutpostState.NEUTRALIZING_PAUSED) {
                            outpost.setOutpostState(OutpostState.NEUTRALIZE_WAITING);
                            GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_CAPTURING_BROADCAST.toString().replace("{faction}", outpost.getFaction().getTag()).replace("{formatted-percentage}", PercentageUtil.getPercentage(outpost.getPercentage())).replace("{percentage}", String.valueOf(outpost.getPercentage())));
                        }
                        outpost.setPercentage(outpost.getPercentage() + 1);
                        if (outpost.getPercentage() == 100 && outpostState != OutpostState.CAPTURED) {
                            outpost.setOutpostState(OutpostState.CAPTURED);
                            outpost.setCapturedFaction(outpost.getFaction());
                            outpost.setFaction(outpost.findFaction());
                        }
                    } else if (outpost.getPercentage() == 100 && outpostState == OutpostState.CAPTURED) {
                        Bukkit.broadcastMessage(OutpostMessages.OUTPOST_STATUS_CAPTURED_BROADCAST
                                .toString()
                                .replace("{outpost}", outpost.getUniqueIdentifier())
                                .replace("{faction}", outpost.getFaction().getTag()));
                    } else {
                        if (outpostState == OutpostState.NEUTRALIZE_WAITING) {
                            outpost.setPercentage(outpost.getPercentage() - 1);
                        } else if (outpostState == OutpostState.CAPTURE_WAITING) {
                            System.out.println("Found bug 0");
                        } else {
                            System.out.println("Found bug 1");
                        }
                    }
                }
                if (outpost.getOutpostState() == OutpostState.NEUTRALIZING) {
                    outpost.setPercentage(outpost.getPercentage() - 1);
                    GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_NEUTRALIZING.toString().replace("{faction}", outpost.getFaction().getTag()).replace("{formatted-percentage}", PercentageUtil.getPercentage(outpost.getPercentage())).replace("{percentage}", String.valueOf(outpost.getPercentage())));

                    if (outpost.getPercentage() == 0) {
                        outpost.setOutpostState(OutpostState.NEUTRALIZED);
                        outpost.setPercentage(101);
                    }
                }

                if (outpost.getOutpostState() == OutpostState.NEUTRALIZING_PAUSED) {
                    GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_NEUTRALIZING_PAUSED.toString().replace("{percentage}", String.valueOf(outpost.getPercentage())));
                }

                if (outpost.getOutpostState() == OutpostState.NEUTRALIZED) {
                    outpost.setPercentage(outpost.getPercentage() - 1);

                    Bukkit.broadcastMessage(OutpostMessages.OUTPOST_STATUS_NEUTRALIZED.toString().replace("{outpost}", outpost.getUniqueIdentifier()).replace("{faction}", outpost.getFaction().getTag()));

                    if (outpost.getPercentage() == 100) {
                        outpost.setOutpostState(OutpostState.CAPTURING);
                        outpost.setPercentage(0);
                    }
                }

                if (outpost.getOutpostState() == OutpostState.CAPTURING_PAUSED) {
                    GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_NEUTRALIZING_PAUSED.toString().replace("{percentage}", String.valueOf(outpost.getPercentage())));
                }
            }
        }
    }
}
