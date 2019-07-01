package com.gameservergroup.gsgcore.menus;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.Consumer;

public class MenuItem {

    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> inventoryClickEventConsumer;

    private MenuItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static MenuItem of(ItemStack itemStack) {
        return new MenuItem(itemStack);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public MenuItem setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public Consumer<InventoryClickEvent> getInventoryClickEventConsumer() {
        return inventoryClickEventConsumer;
    }

    public MenuItem setInventoryClickEventConsumer(Consumer<InventoryClickEvent> inventoryClickEventConsumer) {
        this.inventoryClickEventConsumer = inventoryClickEventConsumer;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuItem menuItem = (MenuItem) o;

        return Objects.equals(itemStack, menuItem.itemStack) && Objects.equals(inventoryClickEventConsumer, menuItem.inventoryClickEventConsumer);
    }

    @Override
    public int hashCode() {
        return 31 * (itemStack != null ? itemStack.hashCode() : 0) + (inventoryClickEventConsumer != null ? inventoryClickEventConsumer.hashCode() : 0);
    }
}
