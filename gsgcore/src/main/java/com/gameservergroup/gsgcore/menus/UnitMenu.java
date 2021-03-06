package com.gameservergroup.gsgcore.menus;

import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Optional;

public class UnitMenu extends Unit {

    @Override
    public void setup() {
        EventPost.of(InventoryOpenEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(EventFilters.getIgnoreNonMenus())
                .handle(event -> {
                    Menu menu = (Menu) event.getInventory().getHolder();
                    if (menu.getInventoryOpenEventConsumer() != null) {
                        menu.getInventoryOpenEventConsumer().accept(event);
                    }
                }).post(GSG_CORE);

        EventPost.of(InventoryCloseEvent.class)
                .filter(EventFilters.getIgnoreNonMenus())
                .handle(event -> {
                    Menu menu = (Menu) event.getInventory().getHolder();
                    if (menu.getInventoryCloseEventConsumer() != null) {
                        menu.getInventoryCloseEventConsumer().accept(event);
                    }
                }).post(GSG_CORE);

        EventPost.of(InventoryClickEvent.class)
                .filter(EventFilters.getIgnoreNonMenus())
                .handle(event -> {
                    Menu menu = (Menu) event.getClickedInventory().getHolder();
                    final Optional<MenuItem> menuItem = menu.getMenuItem(event.getRawSlot());
                    if (menuItem.isPresent() && menuItem.get().getInventoryClickEventConsumer() != null) {
                        menuItem.get().getInventoryClickEventConsumer().accept(event);
                    }
                })
                .post(GSG_CORE);
    }
}
