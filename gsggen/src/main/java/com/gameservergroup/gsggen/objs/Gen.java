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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;

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
        CustomItem customItem = CustomItem.of(configurationSection.getConfigurationSection("item"), name);
        if (isBucket()) {
            customItem.setInteractEventConsumer(event -> {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (Module.getEconomy().has(event.getPlayer(), price)) {
                        Module.getEconomy().withdrawPlayer(event.getPlayer(), price);
                    } else {
                        event.getPlayer().sendMessage(Text.toColor("&cYou don't have enough money to place this!"));
                        event.setCancelled(true);
                        return;
                    }
                    Block relative = event.getClickedBlock().getRelative(event.getBlockFace());
                    event.setCancelled(true);
                    relative.setType(getMaterial());
                    event.getPlayer().updateInventory();
                    getGeneration(relative, direction == Direction.HORIZONTAL ? event.getBlockFace() : direction.getBlockFaces()[0]).enable();
                }
            });
        } else {
            customItem.setPlaceEventConsumer(event -> {
                event.getPlayer().setItemInHand(event.getItemInHand());
                if (Module.getEconomy().has(event.getPlayer(), price)) {
                    Module.getEconomy().withdrawPlayer(event.getPlayer(), price);
                } else {
                    event.getPlayer().sendMessage(Text.toColor("&cYou don't have enough money to place this!"));
                    event.setCancelled(true);
                    return;
                }
                getGeneration(event.getBlockPlaced(), direction == Direction.HORIZONTAL ? event.getBlockAgainst().getFace(event.getBlockPlaced()) : direction.getBlockFaces()[0]).enable();
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
        return Double.compare(gen.price, price) == 0 &&
                patch == gen.patch &&
                length == gen.length &&
                Objects.equals(configurationSection, gen.configurationSection) &&
                Objects.equals(name, gen.name) &&
                material == gen.material &&
                direction == gen.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(configurationSection, name, material, direction, price, patch, length);
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
