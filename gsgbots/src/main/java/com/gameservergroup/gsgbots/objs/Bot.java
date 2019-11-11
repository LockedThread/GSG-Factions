package com.gameservergroup.gsgbots.objs;

import com.gameservergroup.gsgbots.entities.EntityBot;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Bot {

    private static Material SEARCH_MATERIAL;
    private static int SEARCH_RADIUS;

    private transient final EntityBot entityBot;
    private final BlockPosition entityLocation;
    private final UUID whoPlaced;
    private double moneyBalance;
    private int sandBalance, sandPlaced;
    private boolean status;
    private ImmutableSet<Block> cachedBlocks;

    private Bot(Location location, Player player, int sandBalance) {
        this.whoPlaced = player.getUniqueId();
        this.sandBalance = sandBalance;
        this.status = false;

        this.entityBot = new EntityBot(((CraftWorld) location.getWorld()).getHandle(), player, sandBalance);
        this.entityBot.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        this.entityLocation = BlockPosition.of(location);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(entityBot);

        this.cachedBlocks = findNearbyBlocks();
    }

    public static Material getSearchMaterial() {
        return SEARCH_MATERIAL;
    }

    public static void setSearchMaterial(Material searchMaterial) {
        SEARCH_MATERIAL = searchMaterial;
    }

    public static int getSearchRadius() {
        return SEARCH_RADIUS;
    }

    public static void setSearchRadius(int searchRadius) {
        SEARCH_RADIUS = searchRadius;
    }

    /**
     * @return whether or not it is on cooldown
     */
    public boolean placeSand() {
        if (cachedBlocks.isEmpty()) {
            return true;
        }

        boolean allCooldown = true;
        for (Block cachedBlock : cachedBlocks) {
            if (cachedBlock.getType() == Material.AIR) {
                cachedBlock.setType(Material.SAND);
                allCooldown = false;
            }
        }
        return allCooldown;
    }

    private ImmutableSet<Block> findNearbyBlocks() {
        World world = entityLocation.getWorld();
        System.out.println(entityLocation.toString());
        Set<Block> set = null;
        for (int x = entityLocation.getX() - SEARCH_RADIUS; x < entityLocation.getX() + SEARCH_RADIUS; x++) {
            for (int z = entityLocation.getZ() - SEARCH_RADIUS; z < entityLocation.getZ() + SEARCH_RADIUS; z++) {
                Block blockAt = world.getBlockAt(x, entityLocation.getY(), z);
                if (set == null) {
                    set = new HashSet<>();
                }
                set.add(blockAt);
            }
        }
        return set == null ? null : ImmutableSet.copyOf(set);
    }

    public void killEntity() {
        if (entityBot != null) {
            entityBot.die();
        } else {
            Location location = entityLocation.getLocation();
            Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 1, 1, 1);
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity instanceof EntityBot) {
                    ((EntityBot) nearbyEntity).die();
                    return;
                }
            }
        }
    }

    public EntityBot getEntityBot() {
        return entityBot;
    }

    public BlockPosition getEntityLocation() {
        return entityLocation;
    }

    public double getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(double moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public int getSandBalance() {
        return sandBalance;
    }

    public void setSandBalance(int sandBalance) {
        this.sandBalance = sandBalance;
    }

    public int getSandPlaced() {
        return sandPlaced;
    }

    public void setSandPlaced(int sandPlaced) {
        this.sandPlaced = sandPlaced;
    }

    public UUID getWhoPlaced() {
        return whoPlaced;
    }

    public ImmutableSet<Block> getCachedBlocks() {
        return cachedBlocks;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
