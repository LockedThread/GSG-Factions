package com.gameservergroup.gsggen.objs;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.enums.GenMessages;
import com.gameservergroup.gsggen.generation.Generation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.List;
import java.util.Objects;

public class Gen {

    private final ConfigurationSection configurationSection;
    private final String name;
    private final Material material;
    private final Direction direction;
    private final double price;
    private final boolean patch;
    private final int length;

    public Gen(ConfigurationSection configurationSection, Direction direction, double price, boolean patch, Material material) {
        this(configurationSection, direction, price, patch, 256, material);
    }

    public Gen(ConfigurationSection configurationSection, Direction direction, double price, boolean patch, int length, Material material) {
        this.direction = direction;
        this.price = price;
        this.patch = patch;
        this.name = configurationSection.getName();
        this.configurationSection = configurationSection;
        this.length = length;
        this.material = material;
        CustomItem customItem = CustomItem.of(GSGGen.getInstance(), configurationSection.getConfigurationSection("item"), name);
        if (isBucket()) {
            customItem.setInteractEventConsumer(event -> {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (GSGGen.getInstance().getCombatIntegration() != null && GSGGen.getInstance().getCombatIntegration().isTagged(event.getPlayer())) {
                        event.getPlayer().sendMessage(GenMessages.CANT_PLACE_IN_COMBAT.toString());
                        event.setCancelled(true);
                    } else {
                        EconomyResponse economyResponse = Module.getEconomy().withdrawPlayer(event.getPlayer(), price);
                        if (economyResponse.transactionSuccess()) {
                            Block relative = event.getClickedBlock().getRelative(event.getBlockFace());
                            event.setCancelled(true);
                            relative.setType(getMaterial());
                            event.getPlayer().updateInventory();
                            getGeneration(relative, direction == Direction.HORIZONTAL ? event.getBlockFace() : direction.getBlockFaces()[0]).enable();
                        } else {
                            event.getPlayer().sendMessage(GenMessages.CANT_AFFORD.toString());
                            event.setCancelled(true);
                        }
                    }
                }
            });
        } else {
            customItem.setPlaceEventConsumer(event -> {
                if (GSGGen.getInstance().getCombatIntegration() != null && GSGGen.getInstance().getCombatIntegration().isTagged(event.getPlayer())) {
                    event.getPlayer().sendMessage(GenMessages.CANT_PLACE_IN_COMBAT.toString());
                    event.setCancelled(true);
                } else {
                    event.getPlayer().setItemInHand(event.getItemInHand());
                    if (GSGGen.getInstance().getConfig().getBoolean("enemy-check.enabled")) {
                        double distance = GSGGen.getInstance().getConfig().getDouble("enemy-check.distance");
                        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
                        List<Entity> nearbyEntities = event.getPlayer().getNearbyEntities(distance, distance, distance);
                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof Player) {
                                FPlayer other = FPlayers.getInstance().getByPlayer((Player) entity);
                                if (fPlayer.getRelationTo(other) == Relation.ENEMY) {
                                    event.getPlayer().sendMessage(GenMessages.ENEMIES_NEARBY.toString());
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                    }
                    EconomyResponse economyResponse = Module.getEconomy().withdrawPlayer(event.getPlayer(), price);
                    if (economyResponse.transactionSuccess()) {
                        getGeneration(event.getBlockPlaced(), direction == Direction.HORIZONTAL ? event.getBlockAgainst().getFace(event.getBlockPlaced()) : direction.getBlockFaces()[0]).enable();
                    } else {
                        event.getPlayer().sendMessage(GenMessages.CANT_AFFORD.toString());
                        event.setCancelled(true);
                    }
                }
            });
        }
    }

    public Generation getGeneration(Block startingBlock) {
        return getGeneration(startingBlock, null);
    }

    public Generation getGeneration(Block startingBlock, BlockFace blockFace) {
        return direction == Direction.VERTICAL_UP ? new Generation(BlockPosition.of(startingBlock), this, BlockFace.UP) : direction == Direction.VERTICAL_DOWN ? new Generation(BlockPosition.of(startingBlock), this, BlockFace.DOWN) : direction == Direction.HORIZONTAL ? new Generation(BlockPosition.of(startingBlock), this, blockFace) : null;
    }

    public CustomItem getCustomItem() {
        return CustomItem.getCustomItem(name);
    }

    public MenuItem getMenuItem() {
        return MenuItem.of(ItemStackBuilder.of(configurationSection.getConfigurationSection("menu")).build())
                .setInventoryClickEventConsumer(event -> {
                    event.setCancelled(true);
                    if (event.getWhoClicked().getInventory().firstEmpty() == -1) {
                        event.getWhoClicked().sendMessage(GenMessages.INVENTORY_FULL.toString());
                        if (GSGGen.getInstance().getUnitGen().isCloseInventoryOnNoMoney()) {
                            event.getWhoClicked().closeInventory();
                        }
                    } else {
                        event.getWhoClicked().getInventory().addItem(getCustomItem().getItemStack());
                        if (GSGGen.getInstance().getUnitGen().isCloseInventoryOnPurchase()) {
                            event.getWhoClicked().closeInventory();
                        }
                    }
                });
    }

    public boolean isFree() {
        return getPrice() == 0.0;
    }

    public boolean isBucket() {
        return getCustomItem().getItemStack().getType() == Material.LAVA_BUCKET || getCustomItem().getItemStack().getType() == Material.WATER_BUCKET;
    }

    public Material getMaterial() {
        return material;
    }

    public Direction getDirection() {
        return direction;
    }

    public double getPrice() {
        return price;
    }

    public boolean isPatch() {
        return patch;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gen gen = (Gen) o;

        if (Double.compare(gen.price, price) != 0) return false;
        if (patch != gen.patch) return false;
        if (length != gen.length) return false;
        if (!Objects.equals(configurationSection, gen.configurationSection))
            return false;
        if (!Objects.equals(name, gen.name)) return false;
        if (material != gen.material) return false;
        return direction == gen.direction;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = configurationSection != null ? configurationSection.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (material != null ? material.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (patch ? 1 : 0);
        result = 31 * result + length;
        return result;
    }

    @Override
    public String toString() {
        return "Gen{" +
                "configurationSection=" + configurationSection +
                ", name='" + name + '\'' +
                ", material=" + material +
                ", direction=" + direction +
                ", price=" + price +
                ", patch=" + patch +
                ", length=" + length +
                '}';
    }
}
