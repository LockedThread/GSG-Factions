package com.gameservergroup.gsgcore.events;

import com.gameservergroup.gsgcore.menus.Menu;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class EventFilters {

    private static final Predicate<PlayerMoveEvent> IGNORE_SAME_CHUNK = event -> event.getFrom().getChunk().getX() == event.getTo().getChunk().getX() && event.getFrom().getChunk().getZ() == event.getTo().getChunk().getZ();
    private static final Predicate<? extends Cancellable> IGNORE_CANCELLED = cancellable -> !cancellable.isCancelled();
    private static final Predicate<? extends PlayerEvent> IGNORE_HAND_NULL = event -> event.getPlayer().getItemInHand() != null;
    private static final Predicate<? extends InventoryEvent> IGNORE_NON_MENUS = (Predicate<InventoryEvent>) event -> event instanceof InventoryClickEvent ? ((InventoryClickEvent) event).getClickedInventory() instanceof Menu : event.getInventory().getHolder() instanceof Menu;

    public static <T extends InventoryEvent> Predicate<T> getIgnoreNonMenus() {
        return (Predicate<T>) IGNORE_NON_MENUS;
    }

    public static <T extends Cancellable> Predicate<T> getIgnoreCancelled() {
        return (Predicate<T>) IGNORE_CANCELLED;
    }

    public static Predicate<PlayerMoveEvent> getIgnoreSameChunk() {
        return IGNORE_SAME_CHUNK;
    }

    public static <T extends PlayerEvent> Predicate<T> getIgnoreHandNull() {
        return (Predicate<T>) IGNORE_HAND_NULL;
    }
}
