package com.massivecraft.factions.util;

import com.darkblade12.particleeffect.ParticleEffect;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class SeeChunkUtil extends BukkitRunnable {

    private static ParticleEffect effect;
    private ConcurrentHashMap<UUID, Boolean> playersSeeingChunks = new ConcurrentHashMap<>();

    public SeeChunkUtil() {
        String effectName = P.p.getConfig().getString("see-chunk.particle", "REDSTONE");
        effect = ParticleEffect.fromName(effectName);
        if (effect == null) {
            effect = ParticleEffect.REDSTONE;
        }
        P.p.log(Level.INFO, "Using %s as the ParticleEffect for /f sc", P.p.particleProvider.effectName(effect));
    }

    public static void showPillars(Player me) {
        World world = me.getWorld();
        FLocation flocation = new FLocation(me);
        int chunkX = (int) flocation.getX();
        int chunkZ = (int) flocation.getZ();

        int blockX;
        int blockZ;

        blockX = chunkX * 16;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ);


        blockX = chunkX * 16 + 15;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ);

        blockX = chunkX * 16;
        blockZ = chunkZ * 16 + 15;
        showPillar(me, world, blockX, blockZ);

        blockX = chunkX * 16 + 15;
        blockZ = chunkZ * 16 + 15;
        showPillar(me, world, blockX, blockZ);
    }

    private static void showPillar(Player player, World world, int blockX, int blockZ) {
        for (int blockY = 0; blockY < player.getLocation().getBlockY() + 30; blockY++) {
            Location loc = new Location(world, blockX, blockY, blockZ).add(0.5, 0, 0.5);
            if (loc.getBlock().getType() != Material.AIR) {
                continue;
            }
            effect.display(0, 0, 0, 0, 2, loc, player);
        }
    }

    @Override
    public void run() {
        for (UUID playerId : playersSeeingChunks.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) {
                playersSeeingChunks.remove(playerId);
                continue;
            }
            showPillars(player);
        }
    }

    public void updatePlayerInfo(UUID uuid, boolean toggle) {
        if (toggle) {
            playersSeeingChunks.put(uuid, true);
        } else {
            playersSeeingChunks.remove(uuid);
        }
    }
}