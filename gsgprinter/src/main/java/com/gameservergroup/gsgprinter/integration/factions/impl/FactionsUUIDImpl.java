package com.gameservergroup.gsgprinter.integration.factions.impl;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.utils.Utils;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.enums.PrinterMessages;
import com.gameservergroup.gsgprinter.integration.FactionsIntegration;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.struct.Relation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FactionsUUIDImpl implements FactionsIntegration {

    @Override
    public void hookFlightDisable() {
        GSGPrinter.getInstance().getServer().getScheduler().runTaskTimer(GSGPrinter.getInstance(), () -> {
            for (UUID uuid : GSGPrinter.getInstance().getUnitPrinter().getPrintingPlayers().keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                if (fPlayer.getRelationToLocation() != Relation.MEMBER || ((GSGPrinter.getInstance().getCombatIntegration() != null && GSGPrinter.getInstance().getCombatIntegration().isTagged(player)) || enemiesNearby(fPlayer, GSGPrinter.getInstance().getConfig().getInt("flight-check.radius")))) {
                    GSGPrinter.getInstance().getUnitPrinter().disablePrinter(player, true, true);
                }
            }
        }, 0, GSGPrinter.getInstance().getConfig().getInt("flight-check.interval"));
    }

    @Override
    public void setupListeners() {
        EventPost.of(FactionDisbandEvent.class)
                .handle(event -> {
                    for (Player onlinePlayer : event.getFaction().getOnlinePlayers()) {
                        GSGPrinter.getInstance().getUnitPrinter().disablePrinter(onlinePlayer, true);
                    }
                }).post(GSGPrinter.getInstance());

        EventPost.of(FPlayerLeaveEvent.class)
                .handle(event -> GSGPrinter.getInstance().getUnitPrinter().disablePrinter(event.getfPlayer().getPlayer(), true))
                .post(GSGPrinter.getInstance());

        CommandPost.of()
                .builder()
                .assertPlayer()
                .assertPermission("gsgprinter.toggle")
                .handler(commandContext -> {
                    Player player = commandContext.getSender();
                    if (GSGPrinter.getInstance().getUnitPrinter().getPrintingPlayers().containsKey(player.getUniqueId())) {
                        GSGPrinter.getInstance().getUnitPrinter().disablePrinter(player, true);
                    } else if (GSGPrinter.getInstance().isEnableCombatTagPlusIntegration() && GSGPrinter.getInstance().getCombatIntegration().isTagged(player)) {
                        commandContext.reply(PrinterMessages.YOU_ARE_IN_COMBAT);
                    } else if (Board.getInstance().getFactionAt(new FLocation(player.getLocation())) != FPlayers.getInstance().getByPlayer(player).getFaction()) {
                        commandContext.reply(PrinterMessages.MUST_BE_IN_FRIENDLY_TERRITORY);
                    } else if (FPlayers.getInstance().getByPlayer(player).getFaction().isWilderness()) {
                        commandContext.reply(PrinterMessages.YOU_ARE_FACTIONLESS);
                    } else if (!Utils.playerInventoryIsEmpty(player)) {
                        commandContext.reply(PrinterMessages.INVENTORY_MUST_BE_EMPTY);
                    } else {
                        GSGPrinter.getInstance().getUnitPrinter().enablePrinter(player, true);
                    }
                }).post(GSGPrinter.getInstance(), "print", "printer", "printermode");
    }

    private boolean enemiesNearby(FPlayer target, int radius) {
        for (Entity entity : target.getPlayer().getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player) {
                if (((Player) entity).canSee(target.getPlayer())) {
                    FPlayer playerNearby = FPlayers.getInstance().getByPlayer((Player) entity);
                    if (!playerNearby.isAdminBypassing() &&
                            (!playerNearby.isOnline() || ((Player) entity).getGameMode() != GameMode.SPECTATOR) &&
                            (!playerNearby.hasFaction()) &&
                            playerNearby.getRelationTo(target) == Relation.ENEMY) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
