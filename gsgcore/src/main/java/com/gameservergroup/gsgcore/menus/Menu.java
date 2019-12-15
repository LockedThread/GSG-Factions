package com.gameservergroup.gsgcore.menus;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.fill.FillOptions;
import com.gameservergroup.gsgcore.utils.Text;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Menu implements InventoryHolder {

    private static final Consumer<InventoryClickEvent> SET_CANCELLED = event -> event.setCancelled(true);

    private Consumer<InventoryOpenEvent> inventoryOpenEventConsumer;
    private Consumer<InventoryCloseEvent> inventoryCloseEventConsumer;
    private Inventory inventory;
    private final Int2ObjectOpenHashMap<MenuItem> menuItems;
    private final FillOptions fillOptions;

    public Menu(String name, int size, FillOptions fillOptions) {
        this.inventory = Bukkit.createInventory(this, size, Text.toColor(name));
        this.menuItems = new Int2ObjectOpenHashMap<>();
        this.fillOptions = fillOptions;
    }

    public Menu(String name, int size) {
        this(name, size, null);
    }

    public abstract void initialize();

    public final void fill() {
        if (fillOptions != null) {
            switch (fillOptions.getFillMode()) {
                case CHECKERED:
                    for (int i = 0; i < fillOptions.getDyeColorsList().size(); i++) {
                        DyeColor dyeColor = fillOptions.getDyeColorsList().get(i);
                        setItem(getInventory().firstEmpty(), ItemStackBuilder.of(Material.STAINED_GLASS_PANE).setDyeColor(dyeColor).setDisplayName("").build());
                        if (i == fillOptions.getDyeColorsList().size() - 1 && getInventory().firstEmpty() != -1) {
                            i = 0;
                        }
                    }
                    break;
                case RANDOM:
                    if (fillOptions.getDyeColorsList().size() == 0) {
                        return;
                    } else if (fillOptions.getDyeColorsList().size() > 1) {
                        while (getInventory().firstEmpty() != -1) {
                            setItem(getInventory().firstEmpty(), ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                                    .setDyeColor(fillOptions.getDyeColorsList().get(GSGCore.getInstance().getRandom().nextInt(fillOptions.getDyeColorsList().size())))
                                    .setDisplayName("")
                                    .build());
                        }
                        break;
                    }
                case SOLID:
                    final ItemStack itemStack = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).setDyeColor(fillOptions.getDyeColor()).setDisplayName("").build();
                    while (getInventory().firstEmpty() != -1) {
                        setItem(getInventory().firstEmpty(), itemStack);
                    }
                    break;
            }
        }
    }


    public final Optional<MenuItem> getMenuItem(int slot) {
        return Optional.ofNullable(menuItems.get(slot));
    }

    public final void setItem(int slot, MenuItem menuItem) {
        if (slot > inventory.getSize()) {
            throw new RuntimeException("Unable to add a MenuItem to a Menu due to the menu's size. Increase your menu size or contact LockedThread.");
        }
        if (menuItem.getInventoryClickEventConsumer() == null) {
            menuItem.setInventoryClickEventConsumer(SET_CANCELLED);
        }
        inventory.setItem(slot, menuItem.getItemStack());
        menuItems.put(slot, menuItem);
    }

    public final void setItem(int slot, ItemStack itemStack) {
        setItem(slot, MenuItem.of(itemStack).setInventoryClickEventConsumer(SET_CANCELLED));
    }

    public final Int2ObjectOpenHashMap<MenuItem> getMenuItems() {
        return menuItems;
    }

    public final void setInventory(String name, int size) {
        this.inventory = Bukkit.createInventory(this, size, Text.toColor(name));
        this.menuItems.clear();
    }

    @Override
    public final Inventory getInventory() {
        return inventory;
    }

    public final void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public final Consumer<InventoryOpenEvent> getInventoryOpenEventConsumer() {
        return inventoryOpenEventConsumer;
    }

    public final void setInventoryOpenEventConsumer(Consumer<InventoryOpenEvent> inventoryOpenEventConsumer) {
        this.inventoryOpenEventConsumer = inventoryOpenEventConsumer;
    }

    public final Consumer<InventoryCloseEvent> getInventoryCloseEventConsumer() {
        return inventoryCloseEventConsumer;
    }

    public final void setInventoryCloseEventConsumer(Consumer<InventoryCloseEvent> inventoryCloseEventConsumer) {
        this.inventoryCloseEventConsumer = inventoryCloseEventConsumer;
    }

    public void clear() {
        inventory.clear();
        menuItems.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menu menu = (Menu) o;

        if (!Objects.equals(inventoryOpenEventConsumer, menu.inventoryOpenEventConsumer))
            return false;
        if (!Objects.equals(inventoryCloseEventConsumer, menu.inventoryCloseEventConsumer))
            return false;
        if (!Objects.equals(inventory, menu.inventory)) return false;
        return Objects.equals(menuItems, menu.menuItems);
    }

    @Override
    public int hashCode() {
        int result = inventoryOpenEventConsumer != null ? inventoryOpenEventConsumer.hashCode() : 0;
        result = 31 * result + (inventoryCloseEventConsumer != null ? inventoryCloseEventConsumer.hashCode() : 0);
        result = 31 * result + (inventory != null ? inventory.hashCode() : 0);
        result = 31 * result + (menuItems != null ? menuItems.hashCode() : 0);
        return result;
    }
}
