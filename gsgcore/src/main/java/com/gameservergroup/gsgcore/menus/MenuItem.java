package com.gameservergroup.gsgcore.menus;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
}
