package com.massivecraft.factions.tasks.flight;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EnemiesTask extends BukkitRunnable {

    private double radius;

    public EnemiesTask(double radius) {
        this.radius = radius;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            FPlayer pilot = FPlayers.getInstance().getByPlayer(player);
            if (pilot.isFlying() && !pilot.isAdminBypassing()) {
                if (enemiesNearby(pilot, radius)) {
                    pilot.msg(TL.COMMAND_FLY_ENEMY_DISABLE);
                    pilot.setFlying(false);
                }
            }
        }
    }

    public boolean enemiesNearby(FPlayer target, double radius) {
        List<Entity> nearbyEntities = target.getPlayer().getNearbyEntities(radius, radius, radius);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                FPlayer playerNearby = FPlayers.getInstance().getByPlayer((Player) entity);
                if (playerNearby.isAdminBypassing() || playerNearby.isStealth()) {
                    continue;
                }
                if (playerNearby.getRelationTo(target) == Relation.ENEMY) {
                    return true;
                }
            }
        }
        return false;
    }
}
