package com.gameservergroup.gsggen.objs;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.utils.Text;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.generation.Generation;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;

public class Gen {

    private final ConfigurationSection configurationSection;
    private final String name;
    private final Material material;
    private final Direction direction;
    private final double price;
    private final boolean patch;
    private final int length;
    private int hash;

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
                        event.getPlayer().sendMessage(Text.toColor("&cYou can't place gens in combat"));
                    } else {
                        EconomyResponse economyResponse = Module.getEconomy().withdrawPlayer(event.getPlayer(), price);
                        if (economyResponse.transactionSuccess()) {
                            Block relative = event.getClickedBlock().getRelative(event.getBlockFace());
                            event.setCancelled(true);
                            relative.setType(getMaterial());
                            event.getPlayer().updateInventory();
                            getGeneration(relative, direction == Direction.HORIZONTAL ? event.getBlockFace() : direction.getBlockFaces()[0]).enable();
                        } else {
                            event.getPlayer().sendMessage(Text.toColor("&cYou don't have enough money to place this!"));
                            event.setCancelled(true);
                        }
                    }
                }
            });
        } else {
            customItem.setPlaceEventConsumer(event -> {
                if (GSGGen.getInstance().getCombatIntegration() != null && GSGGen.getInstance().getCombatIntegration().isTagged(event.getPlayer())) {
                    event.getPlayer().sendMessage(Text.toColor("&cYou can't place gens in combat"));
                } else {
                    event.getPlayer().setItemInHand(event.getItemInHand());
                    EconomyResponse economyResponse = Module.getEconomy().withdrawPlayer(event.getPlayer(), price);
                    if (economyResponse.transactionSuccess()) {
                        getGeneration(event.getBlockPlaced(), direction == Direction.HORIZONTAL ? event.getBlockAgainst().getFace(event.getBlockPlaced()) : direction.getBlockFaces()[0]).enable();
                    } else {
                        event.getPlayer().sendMessage(Text.toColor("&cYou don't have enough money to place this!"));
                        event.setCancelled(true);
                    }
                }
            });
        }
        hash = configurationSection.hashCode();
        hash = 31 * hash + (name != null ? name.hashCode() : 0);
        hash = 31 * hash + (material != null ? material.hashCode() : 0);
        hash = 31 * hash + (direction != null ? direction.hashCode() : 0);
        long temp = Double.doubleToLongBits(price);
        hash = 31 * hash + (int) (temp ^ (temp >>> 32));
        hash = 31 * hash + (patch ? 1 : 0);
        hash = 31 * hash + length;
        hash = 31 * hash + hash;
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
                        event.getWhoClicked().sendMessage(Text.toColor("&cYou inventory is full, unable to give you a gen!"));
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

        return gen.hash == this.hash;
    }

    @Override
    public int hashCode() {
        return hash;
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
