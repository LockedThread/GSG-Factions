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
import com.gameservergroup.gsggen.generation.GenerationVerticalDown;
import com.gameservergroup.gsggen.generation.GenerationVerticalUp;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;

public class Gen {

    private final ConfigurationSection configurationSection;
    private final String name;
    private final boolean bucket;
    private final Material material;
    private final Direction direction;
    private final double price;
    private final boolean patch;
    private final int length;

    public Gen(ConfigurationSection configurationSection, Direction direction, double price, boolean patch) {
        this(configurationSection, direction, price, patch, 256);
    }

    public Gen(ConfigurationSection configurationSection, Direction direction, double price, boolean patch, int length) {
        this.direction = direction;
        this.price = price;
        this.patch = patch;
        this.name = configurationSection.getName();
        this.configurationSection = configurationSection;
        this.length = length;
        CustomItem customItem = CustomItem.of(configurationSection.getConfigurationSection("item"), name);
        if (isBucket()) {
            customItem.setBucketEmptyEventConsumer(event -> {
                getGeneration(event.getBlockClicked(), direction == Direction.HORIZONTAL ? event.getBlockFace() : direction.getBlockFaces()[0]).enable();
                event.setCancelled(true);
                event.getBlockClicked().setType(event.getItemStack().getType());
            });
        } else {
            customItem.setPlaceEventConsumer(event ->
                    getGeneration(event.getBlockPlaced(), direction == Direction.HORIZONTAL ? event.getBlockAgainst().getFace(event.getBlockPlaced()) : direction.getBlockFaces()[0]).enable()
            );
        }
        this.material = getCustomItem().getItemStack().getType();
        this.bucket = getCustomItem().getItemStack().getType().name().endsWith("BUCKET");
    }

    public Generation getGeneration(Block startingBlock) {
        return getGeneration(startingBlock, null);
    }

    public Generation getGeneration(Block startingBlock, BlockFace blockFace) {
        switch (direction) {
            case VERTICAL_UP:
                return new GenerationVerticalUp(BlockPosition.of(startingBlock), this);
            case VERTICAL_DOWN:
                return new GenerationVerticalDown(BlockPosition.of(startingBlock), this);
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
                        event.getWhoClicked().closeInventory();
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
        return bucket;
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
