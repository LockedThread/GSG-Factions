package com.gameservergroup.gsgcore.menus;

import com.gameservergroup.gsgcore.utils.Text;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class Menu implements InventoryHolder {

    private Consumer<InventoryOpenEvent> inventoryOpenEventConsumer;
    private Consumer<InventoryCloseEvent> inventoryCloseEventConsumer;
    private Inventory inventory;
    private Int2ObjectOpenHashMap<MenuItem> menuItems;

    public Menu(String name, int size) {
        this.inventory = Bukkit.createInventory(this, size, Text.toColor(name));
        this.menuItems = new Int2ObjectOpenHashMap<>();
    }

    public abstract void initialize();

    public Optional<MenuItem> getMenuItem(int slot) {
        return Optional.ofNullable(menuItems.get(slot));
    }

    public void setItem(int slot, MenuItem menuItem) {
        if (slot > inventory.getSize()) {
            throw new RuntimeException("Unable to add a MenuItem to a Menu due to the menu's size. Increase your menu size or contact LockedThread.");
        }
        inventory.setItem(slot, menuItem.getItemStack());
        menuItems.put(slot, menuItem);
    }

    public void setItem(int slot, ItemStack itemStack) {
        setItem(slot, MenuItem.of(itemStack).setInventoryClickEventConsumer(event -> event.setCancelled(true)));
    }

    public Int2ObjectOpenHashMap<MenuItem> getMenuItems() {
        return menuItems;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Consumer<InventoryOpenEvent> getInventoryOpenEventConsumer() {
        return inventoryOpenEventConsumer;
    }

    public void setInventoryOpenEventConsumer(Consumer<InventoryOpenEvent> inventoryOpenEventConsumer) {
        this.inventoryOpenEventConsumer = inventoryOpenEventConsumer;
    }

    public Consumer<InventoryCloseEvent> getInventoryCloseEventConsumer() {
        return inventoryCloseEventConsumer;
    }

    public void setInventoryCloseEventConsumer(Consumer<InventoryCloseEvent> inventoryCloseEventConsumer) {
        this.inventoryCloseEventConsumer = inventoryCloseEventConsumer;
    }

    public void clear() {
        inventory.clear();
        menuItems.clear();
    }
}
