package com.gameservergroup.gsgoutpost.tasks;

import com.gameservergroup.gsgoutpost.GSGOutpost;
import com.gameservergroup.gsgoutpost.enums.OutpostMessages;
import com.gameservergroup.gsgoutpost.enums.OutpostState;
import com.gameservergroup.gsgoutpost.objs.Outpost;
import com.gameservergroup.gsgoutpost.utils.PercentageUtil;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskOutpost extends BukkitRunnable {

    private static final GSGOutpost GSG_OUTPOST = GSGOutpost.getInstance();

    private Outpost outpost;

    public TaskOutpost(Outpost outpost) {
        this.outpost = outpost;
    }

    @Override
    public void run() {
        if (outpost == null) {
            cancel();
            return;
        }

        if (outpost.getOutpostState() == OutpostState.DISABLED) {
            GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_DISABLED.toString());
        }

        if (outpost.getOutpostState() == OutpostState.NEUTRALIZE_WAITING || outpost.getOutpostState() == OutpostState.CAPTURE_WAITING) {
            GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_WAITING.toString());
        }

        if (outpost.getOutpostState() != OutpostState.DISABLED) {
            for (Player s : outpost.getPlayers()) {
                if (s.isDead())
                    outpost.getPlayers().add(s);
                if (!GSG_OUTPOST.isInRegion(s.getLocation(), outpost.getUniqueIdentifier()))
                    outpost.getPlayers().add(s);
            }

            if (outpost.isOneFaction()) {
                if (outpost.getOutpostState() == OutpostState.NEUTRALIZING_PAUSED)
                    outpost.setOutpostState(OutpostState.NEUTRALIZE_WAITING);
                if (outpost.getOutpostState() == OutpostState.CAPTURING_PAUSED)
                    outpost.setOutpostState(OutpostState.CAPTURING);
            }

            if (outpost.getPlayers().size() == 0 && outpost.getOutpostState() != OutpostState.CAPTURED) {
                if (outpost.getOutpostState() == OutpostState.CAPTURING)
                    outpost.setOutpostState(OutpostState.CAPTURE_WAITING);
                if (!outpost.getOutpostState().equals(OutpostState.CAPTURE_WAITING)) {
                    outpost.setOutpostState(OutpostState.NEUTRALIZE_WAITING);
                }
            } else {
                for (Player player : outpost.getPlayers()) {
                    FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                    if (fPlayer.hasFaction()) {
                        Faction faction = fPlayer.getFaction();
                        if (!outpost.isOneFaction()) {
                            if (outpost.getOutpostState() == OutpostState.NEUTRALIZING)
                                outpost.setOutpostState(OutpostState.NEUTRALIZING_PAUSED);
                            if (outpost.getOutpostState() == OutpostState.CAPTURING)
                                outpost.setOutpostState(OutpostState.CAPTURING_PAUSED);
                        } else {
                            if (outpost.getCapturedFaction() != null && outpost.getCapturedFaction() == outpost.findFaction()) {
                                outpost.setOutpostState(OutpostState.CAPTURED);
                                outpost.setFaction(faction);
                                return;
                            }
                            if (outpost.getOutpostState() == OutpostState.NEUTRALIZING_PAUSED)
                                outpost.setOutpostState(OutpostState.NEUTRALIZING);
                            if (outpost.getOutpostState() == OutpostState.CAPTURING_PAUSED)
                                outpost.setOutpostState(OutpostState.CAPTURING);
                            if (faction != outpost.getFaction())
                                outpost.setOutpostState(OutpostState.NEUTRALIZING);
                            outpost.setFaction(faction);
                        }
                    }
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

        if (outpost.getOutpostState() == OutpostState.CAPTURING) {
            outpost.setPercentage(outpost.getPercentage() + 1);

            GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_CAPTURING_BROADCAST.toString().replace("{faction}", outpost.getFaction().getTag()).replace("{formatted-percentage}", PercentageUtil.getPercentage(outpost.getPercentage())).replace("{percentage}", String.valueOf(outpost.getPercentage())));

            if (outpost.getPercentage() == 100) {
                outpost.setOutpostState(OutpostState.CAPTURED);
                outpost.setPercentage(100);
                Bukkit.broadcastMessage(OutpostMessages.OUTPOST_STATUS_CAPTURED_BROADCAST.toString().replace("{outpost}", outpost.getUniqueIdentifier()).replace("{faction}", outpost.getFaction().getTag()));
                outpost.setCapturedFaction(outpost.getFaction());
            }
        }

        if (outpost.getOutpostState() == OutpostState.CAPTURING_PAUSED) {
            GSG_OUTPOST.sendUpdate(Bukkit.getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_NEUTRALIZING_PAUSED.toString().replace("{percentage}", String.valueOf(outpost.getPercentage())));
        }

        if (outpost.getOutpostState() == OutpostState.CAPTURED) {
            if (outpost.getCaptureMessage() != null) {
                GSG_OUTPOST.sendUpdate(outpost.getCapturedFaction().getOnlinePlayers(), outpost.getCaptureMessage());
            } else {
                GSG_OUTPOST.sendUpdate(outpost.getCapturedFaction().getOnlinePlayers(), OutpostMessages.OUTPOST_STATUS_CAPTURED.toString().replace("{outpost}", outpost.getUniqueIdentifier()));
            }
        }
    }
}
