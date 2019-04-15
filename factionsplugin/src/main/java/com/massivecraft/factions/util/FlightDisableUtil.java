package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FlightDisableUtil extends BukkitRunnable {

    public static boolean enemiesNearby(FPlayer target, int radius) {
        List<Entity> nearbyEntities = target.getPlayer().getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                FPlayer playerNearby = FPlayers.getInstance().getByPlayer((Player) entity);
                if (!((Player) entity).canSee(target.getPlayer())) {
                    continue;
                }
                if (playerNearby.isAdminBypassing()) {
                    continue;
                }
                if (playerNearby.isStealth()) {
                    continue;
                }
                if (playerNearby.isOnline() && playerNearby.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
                    continue;
                }
                if (target.hasAltFaction() && ((playerNearby.hasAltFaction() && playerNearby.getAltFaction().equals(target.getAltFaction()))
                        || (playerNearby.hasFaction() && playerNearby.getFaction().equals(target.getAltFaction())))) {
                    continue;
                }
                if (playerNearby.getRelationTo(target) == Relation.ENEMY) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            FPlayer pilot = FPlayers.getInstance().getByPlayer(player);
            if (pilot.isFlying() && !pilot.isAdminBypassing() && pilot.getPlayer().getGameMode() != GameMode.SPECTATOR && !pilot.getPlayer().isOp() && ((P.p.getCombatIntegration() != null && P.p.getCombatIntegration().isTagged(player)) || enemiesNearby(pilot, P.p.getConfig().getInt("f-fly.enemy-radius")))) {
                pilot.msg(TL.COMMAND_FLY_ENEMY_DISABLE);
                pilot.setFlying(false);
            }
        }
    }
}
