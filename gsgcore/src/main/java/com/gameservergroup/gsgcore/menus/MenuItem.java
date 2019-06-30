package com.gameservergroup.gsgcore.menus;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class MenuItem {

    private transient int hash;
    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> inventoryClickEventConsumer;

    private MenuItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        recalculateHash();
    }

    public static MenuItem of(ItemStack itemStack) {
        return new MenuItem(itemStack);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public MenuItem setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        recalculateHash();
        return this;
    }

    public Consumer<InventoryClickEvent> getInventoryClickEventConsumer() {
        return inventoryClickEventConsumer;
    }

    public MenuItem setInventoryClickEventConsumer(Consumer<InventoryClickEvent> inventoryClickEventConsumer) {
        this.inventoryClickEventConsumer = inventoryClickEventConsumer;
        recalculateHash();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuItem menuItem = (MenuItem) o;

        return hash == menuItem.hash;
    }

    public void recalculateHash() {
        int result = hash;
        result = 31 * result + (itemStack != null ? itemStack.hashCode() : 0);
        result = 31 * result + (inventoryClickEventConsumer != null ? inventoryClickEventConsumer.hashCode() : 0);
        this.hash = result;
    }
}
