package com.gameservergroup.gsgcore.events;

import com.gameservergroup.gsgcore.menus.Menu;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class EventFilters {

    private static final Predicate<PlayerMoveEvent> IGNORE_SAME_CHUNK = event -> event.getFrom().getChunk().getX() == event.getTo().getChunk().getX() && event.getFrom().getChunk().getZ() == event.getTo().getChunk().getZ();
    private static final Predicate<? extends Cancellable> IGNORE_CANCELLED = cancellable -> !cancellable.isCancelled();
    private static final Predicate<? extends InventoryEvent> IGNORE_NON_MENUS = (Predicate<InventoryEvent>) event -> event instanceof InventoryClickEvent ? ((InventoryClickEvent) event).getClickedInventory().getHolder() instanceof Menu : event.getInventory().getHolder() instanceof Menu;
    private static final Predicate<? extends Event> IGNORE_HAND_NULL = event -> {
        if (event instanceof PlayerEvent) {
            return ((PlayerEvent) event).getPlayer().getItemInHand() != null;
        } else if (event instanceof BlockBreakEvent) {
            return ((BlockBreakEvent) event).getPlayer().getItemInHand() != null;
        } else if (event instanceof BlockPlaceEvent) {
            return ((BlockPlaceEvent) event).getItemInHand() != null;
        }
        return false;
    };
    private static final Predicate<? extends Event> IGNORE_HAND_META_NULL = event -> {
        if (event instanceof PlayerEvent) {
            return ((PlayerEvent) event).getPlayer().getItemInHand().hasItemMeta();
        } else if (event instanceof BlockBreakEvent) {
            return ((BlockBreakEvent) event).getPlayer().getItemInHand().hasItemMeta();
        } else if (event instanceof BlockPlaceEvent) {
            return ((BlockPlaceEvent) event).getItemInHand().hasItemMeta();
        }
        return false;
    };
    private static final Predicate<? extends Event> IGNORE_HAND_DISPLAYNAME_NULL = event -> {
        if (event instanceof PlayerEvent) {
            return ((PlayerEvent) event).getPlayer().getItemInHand().getItemMeta().hasDisplayName();
        } else if (event instanceof BlockBreakEvent) {
            return ((BlockBreakEvent) event).getPlayer().getItemInHand().getItemMeta().hasDisplayName();
        } else if (event instanceof BlockPlaceEvent) {
            return ((BlockPlaceEvent) event).getItemInHand().getItemMeta().hasDisplayName();
        }
        return false;
    };

    public static <T extends Event> Predicate<T> getIgnoreHandDisplaynameNull() {
        return (Predicate<T>) IGNORE_HAND_DISPLAYNAME_NULL;
    }

    public static <T extends Event> Predicate<T> getIgnoreHandMetaNull() {
        return (Predicate<T>) IGNORE_HAND_META_NULL;
    }

    public static <T extends InventoryEvent> Predicate<T> getIgnoreNonMenus() {
        return (Predicate<T>) IGNORE_NON_MENUS;
    }

    public static <T extends Cancellable> Predicate<T> getIgnoreCancelled() {
        return (Predicate<T>) IGNORE_CANCELLED;
    }

    public static Predicate<PlayerMoveEvent> getIgnoreSameChunk() {
        return IGNORE_SAME_CHUNK;
    }

    public static <T extends Event> Predicate<T> getIgnoreHandNull() {
        return (Predicate<T>) IGNORE_HAND_NULL;
    }
}
