package com.massivecraft.factions.tasks.flight;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleTrailsTask extends BukkitRunnable {

    private int amount;
    private float speed;

    public ParticleTrailsTask() {
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