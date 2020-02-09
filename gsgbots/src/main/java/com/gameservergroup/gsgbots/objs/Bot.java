package com.gameservergroup.gsgbots.objs;

import com.gameservergroup.gsgbots.entities.EntityBot;
import com.gameservergroup.gsgbots.menus.MenuBot;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;

public class Bot {

    private static final Map<UUID, Bot> BOT_MAP = new HashMap<>();

    private static Material SEARCH_MATERIAL;
    private static int SEARCH_RADIUS;
    private final UUID botUUID;
    private final BlockPosition entityLocation;
    private final UUID whoPlaced;
    private transient EntityBot entityBot;
    private MenuBot menuBot;
    private double moneyBalance;
    private int sandBalance, sandPlaced;
    private boolean status;
    private ImmutableSet<Block> cachedBlocks;

    public Bot(Location location, Player player, int sandBalance) {
        this.whoPlaced = player.getUniqueId();
        this.sandBalance = sandBalance;
        this.status = false;
        this.entityLocation = BlockPosition.of(location);

        this.entityBot = new EntityBot(((CraftWorld) location.getWorld()).getHandle());
        this.entityBot.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) location.getWorld()).getHandle().addEntity(entityBot, CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.entityBot.update(player, 0);
        ((CraftLivingEntity) entityBot.getBukkitEntity()).setRemoveWhenFarAway(false);

        this.botUUID = entityBot.getUniqueID();
        this.cachedBlocks = findNearbyBlocks();
    }

    public static Map<UUID, Bot> getBotMap() {
        return BOT_MAP;
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

    private ImmutableSet<Block> findNearbyBlocks() {
        World world = entityLocation.getWorld();
        System.out.println(entityLocation.toString());
        Set<Block> set = null;
        for (int x = entityLocation.getX() - SEARCH_RADIUS; x < entityLocation.getX() + SEARCH_RADIUS; x++) {
            for (int z = entityLocation.getZ() - SEARCH_RADIUS; z < entityLocation.getZ() + SEARCH_RADIUS; z++) {
                Block blockAt = world.getBlockAt(x, entityLocation.getY(), z);
                if (blockAt.getType() == SEARCH_MATERIAL) {
                    if (set == null) {
                        set = new HashSet<>();
                    }
                    set.add(blockAt);
                }
            }
        }
        return set == null ? null : ImmutableSet.copyOf(set);
    }

    public static void setSearchRadius(int searchRadius) {
        SEARCH_RADIUS = searchRadius;
    }

    public UUID getBotUUID() {
        return botUUID;
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

    public void setCachedBlocks(ImmutableSet<Block> cachedBlocks) {
        this.cachedBlocks = cachedBlocks;
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
                sandPlaced++;
                sandBalance--;
                moneyBalance -= MenuBot.getSandPrice();
            }
        }
        return allCooldown;
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
        BOT_MAP.remove(this.botUUID);
    }

    public EntityBot getEntityBot() {
        if (entityBot == null) {
            Location location = entityLocation.getLocation();
            Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 1, 1, 1);
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity instanceof EntityBot) {
                    entityBot = (EntityBot) nearbyEntity;
                    break;
                }
            }
        }
        return entityBot;
    }

    public void setEntityBot(EntityBot entityBot) {
        this.entityBot = entityBot;
    }

    public MenuBot getMenuBot() {
        return menuBot == null ? menuBot = new MenuBot(this) : menuBot;
    }

    public void setMenuBot(MenuBot menuBot) {
        this.menuBot = menuBot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bot bot = (Bot) o;

        if (Double.compare(bot.moneyBalance, moneyBalance) != 0) return false;
        if (sandBalance != bot.sandBalance) return false;
        if (sandPlaced != bot.sandPlaced) return false;
        if (status != bot.status) return false;
        if (entityBot != null ? !entityBot.equals(bot.entityBot) : bot.entityBot != null) return false;
        if (entityLocation != null ? !entityLocation.equals(bot.entityLocation) : bot.entityLocation != null)
            return false;
        if (whoPlaced != null ? !whoPlaced.equals(bot.whoPlaced) : bot.whoPlaced != null) return false;
        if (botUUID != null ? !botUUID.equals(bot.botUUID) : bot.botUUID != null) return false;
        if (menuBot != null ? !menuBot.equals(bot.menuBot) : bot.menuBot != null) return false;
        return cachedBlocks != null ? cachedBlocks.equals(bot.cachedBlocks) : bot.cachedBlocks == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = entityBot != null ? entityBot.hashCode() : 0;
        result = 31 * result + (entityLocation != null ? entityLocation.hashCode() : 0);
        result = 31 * result + (whoPlaced != null ? whoPlaced.hashCode() : 0);
        result = 31 * result + (botUUID != null ? botUUID.hashCode() : 0);
        result = 31 * result + (menuBot != null ? menuBot.hashCode() : 0);
        temp = Double.doubleToLongBits(moneyBalance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + sandBalance;
        result = 31 * result + sandPlaced;
        result = 31 * result + (status ? 1 : 0);
        result = 31 * result + (cachedBlocks != null ? cachedBlocks.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Bot{" +
                "entityBot=" + entityBot +
                ", entityLocation=" + entityLocation +
                ", whoPlaced=" + whoPlaced +
                ", botUUID=" + botUUID +
                ", menuBot=" + menuBot +
                ", moneyBalance=" + moneyBalance +
                ", sandBalance=" + sandBalance +
                ", sandPlaced=" + sandPlaced +
                ", status=" + status +
                ", cachedBlocks=" + cachedBlocks +
                '}';
    }


}
