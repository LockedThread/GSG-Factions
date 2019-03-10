package com.gameservergroup.gsggen.objs;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.utils.Text;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.generation.Generation;
import com.gameservergroup.gsggen.generation.GenerationHorizontal;
import com.gameservergroup.gsggen.generation.GenerationVertical;
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
                    Block relative = event.getClickedBlock().getRelative(event.getBlockFace());
                    event.setCancelled(true);
                    relative.setType(getMaterial());
                    event.getPlayer().updateInventory();
                    getGeneration(relative, direction == Direction.HORIZONTAL ? event.getBlockFace() : direction.getBlockFaces()[0]).enable();
                }
            });
        } else {
            customItem.setPlaceEventConsumer(event -> {
                System.out.println(getMaterial());
                event.getPlayer().setItemInHand(event.getItemInHand());
                getGeneration(event.getBlockPlaced(), direction == Direction.HORIZONTAL ? event.getBlockAgainst().getFace(event.getBlockPlaced()) : direction.getBlockFaces()[0]).enable();
            });
        }
    }

    public Generation getGeneration(Block startingBlock) {
        return getGeneration(startingBlock, null);
    }

    public Generation getGeneration(Block startingBlock, BlockFace blockFace) {
        System.out.println("startingBlock = [" + startingBlock + "], blockFace = [" + blockFace + "]");
        switch (direction) {
            case VERTICAL_UP:
                return new GenerationVertical(BlockPosition.of(startingBlock), this, Direction.VERTICAL_UP);
            case VERTICAL_DOWN:
                return new GenerationVertical(BlockPosition.of(startingBlock), this, Direction.VERTICAL_DOWN);
            case HORIZONTAL:
                return new GenerationHorizontal(BlockPosition.of(startingBlock), this, blockFace);
            default:
                return null;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gen gen = (Gen) o;

        return Double.compare(gen.price, price) == 0 && patch == gen.patch && material == gen.material && direction == gen.direction;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = material != null ? material.hashCode() : 0;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (patch ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Gen{" +
                "material=" + material +
                ", direction=" + direction +
                ", price=" + price +
                ", patch=" + patch +
                '}';
    }

    public int getLength() {
        return length;
    }
}
