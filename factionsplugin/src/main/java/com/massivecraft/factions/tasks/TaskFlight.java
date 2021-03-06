package com.massivecraft.factions.tasks;

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

public class TaskFlight {

    private static TaskFlight instance;

    public EnemiesTask enemiesTask;
    public ParticleTrailsTask trailsTask;

    private TaskFlight() {
        double enemyCheck = P.p.getConfig().getDouble("f-fly.radius-check", 1) * 20;
        if (enemyCheck > 0) {
            enemiesTask = new EnemiesTask(P.p.getConfig().getDouble("f-fly.enemy-radius"));
            enemiesTask.runTaskTimer(P.p, 0, (long) enemyCheck);
        }

        double spawnRate = P.p.getConfig().getDouble("f-fly.trails.spawn-rate", 0) * 20;
        if (spawnRate > 0) {
            trailsTask = new ParticleTrailsTask();
            trailsTask.runTaskTimer(P.p, 0, (long) spawnRate);
        }
    }

    public static void start() {
        instance = new TaskFlight();
    }

    public static void stop() {
        instance.enemiesTask.cancel();
        instance.trailsTask.cancel();
    }

    public static TaskFlight instance() {
        return instance;
    }

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

    public class ParticleTrailsTask extends BukkitRunnable {

        private int amount;
        private float speed;

        private ParticleTrailsTask() {
            this.amount = P.p.getConfig().getInt("f-fly.trails.amount", 20);
            this.speed = (float) P.p.getConfig().getDouble("f-fly.trails.speed", 0.02);
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