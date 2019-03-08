package com.gameservergroup.gsgcore.menus;

import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class UnitMenu extends Unit {

    @Override
    public void setup() {
        EventPost.of(InventoryOpenEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(EventFilters.getIgnoreNonMenus())
                .handle(event -> {
                    Menu menu = (Menu) event.getInventory().getHolder();
                    System.out.println("called InventoryOpenEvent - " + menu.getInventory().getName());
                    if (menu.getInventoryOpenEventConsumer() != null) {
                        menu.getInventoryOpenEventConsumer().accept(event);
                        System.out.println("Called open event consumer for " + menu.getInventory().getName());
                    }
                }).post(GSG_CORE);

        EventPost.of(InventoryCloseEvent.class)
                .filter(EventFilters.getIgnoreNonMenus())
                .handle(event -> {
                    Menu menu = (Menu) event.getInventory().getHolder();
                    System.out.println("called InventoryCloseEvent - " + menu.getInventory().getName());
                    if (menu.getInventoryCloseEventConsumer() != null) {
                        menu.getInventoryCloseEventConsumer().accept(event);
                        System.out.println("Called close event consumer for " + menu.getInventory().getName());
                    }
                }).post(GSG_CORE);

        EventPost.of(InventoryClickEvent.class)
                .filter(EventFilters.getIgnoreNonMenus())
                .handle(event -> {
                    Menu menu = (Menu) event.getInventory().getHolder();
                    System.out.println("called InventoryOpenEvent - " + menu.getInventory().getName());
                    menu.getMenuItem(event.getRawSlot()).ifPresent(menuItem1 -> menuItem1.getInventoryClickEventConsumer().accept(event));
                }).post(GSG_CORE);
    }
}
