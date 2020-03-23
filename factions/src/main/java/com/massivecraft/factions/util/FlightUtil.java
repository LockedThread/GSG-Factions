package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FlightUtil {

    private static FlightUtil instance;

    public ParticleTrailsTask particleTrailsTask;
    public EnemiesTask enemiesTask;

    private FlightUtil() {
        double enemyCheck = P.p.getConfig().getDouble("f-fly.radius-check") * 20;
        if (enemyCheck > 0) {
            (enemiesTask = new EnemiesTask()).runTaskTimer(P.p, 0, (long) enemyCheck);
        }

        double spawnRate = P.p.getConfig().getDouble("f-fly.trails.spawn-rate") * 20;
        if (spawnRate > 0) {
            (particleTrailsTask = new ParticleTrailsTask()).runTaskTimer(P.p, 0, (long) spawnRate);
        }
    }

    public static void start() {
        instance = new FlightUtil();
    }

    public static void stop() {
        instance.enemiesTask.cancel();
        instance.particleTrailsTask.cancel();
        instance = null;
    }

    public static FlightUtil instance() {
        return instance;
    }

    public boolean enemiesNearby(FPlayer target) {
        if (this.enemiesTask == null) {
            return false;
        } else {
            return this.enemiesTask.enemiesNearby(target);
        }
    }

    public class EnemiesTask extends BukkitRunnable {

        private final double enemyRadius = P.p.getConfig().getDouble("f-fly.enemy-radius");

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                FPlayer pilot = FPlayers.getInstance().getByPlayer(player);
                if (pilot.isFlying() && !pilot.isAdminBypassing()) {
                    if (enemiesNearby(pilot)) {
                        pilot.msg(TL.COMMAND_FLY_ENEMY_DISABLE);
                        pilot.setFlying(false);
                        /*if (pilot.isAutoFlying()) {
                            pilot.setAutoFlying(false);
                        }*/
                    }
                }
            }
        }

        public boolean enemiesNearby(FPlayer target) {
            List<Entity> nearbyEntities = target.getPlayer().getNearbyEntities(enemyRadius, enemyRadius, enemyRadius);
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

    public class ParticleTrailsTask extends BukkitRunnable {

        private int amount;
        private double speed;

        private ParticleTrailsTask() {
            this.amount = P.p.getConfig().getInt("f-fly.trails.amount");
            this.speed = P.p.getConfig().getDouble("f-fly.trails.speed");
        }

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                FPlayer pilot = FPlayers.getInstance().getByPlayer(player);
                if (pilot.isFlying()) {
                    if (pilot.getFlyTrailsEffect() != null && Permission.FLY_TRAILS.has(player) && pilot.getFlyTrailsState()) {
                        Object effect = P.p.particleProvider.effectFromString(pilot.getFlyTrailsEffect());
                        P.p.particleProvider.spawn(effect, player.getLocation(), amount, speed, 0, 0, 0);
                    }
                }
            }
        }

    }

}