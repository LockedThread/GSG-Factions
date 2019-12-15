package com.massivecraft.factions.tasks.flight;

import com.massivecraft.factions.P;

public class TaskFlight {

    private static TaskFlight instance;

    public EnemiesTask enemiesTask;
    public ParticleTrailsTask trailsTask;

    private TaskFlight() {
        double enemyCheck = P.p.getConfig().getDouble("f-fly.radius-check", 1) * 20;
        if (enemyCheck > 0) {
            (enemiesTask = new EnemiesTask(P.p.getConfig().getDouble("f-fly.enemy-radius"))).runTaskTimer(P.p, 0, (long) enemyCheck);
        }

        double spawnRate = P.p.getConfig().getDouble("f-fly.trails.spawn-rate", 0) * 20;
        if (spawnRate > 0) {
            (trailsTask = new ParticleTrailsTask()).runTaskTimer(P.p, 0, (long) spawnRate);
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
}