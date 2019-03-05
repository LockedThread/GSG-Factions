package com.gameservergroup.gsgcore.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Predicate;

public class EventFilters {

    private static final Predicate<PlayerMoveEvent> IGNORE_SAME_CHUNK = event -> event.getFrom().getChunk().getX() == event.getTo().getChunk().getX() && event.getFrom().getChunk().getZ() == event.getTo().getChunk().getZ();
    private static final Predicate<? extends Cancellable> IGNORE_CANCELLED = cancellable -> !cancellable.isCancelled();
    private static final Predicate<? extends PlayerEvent> IGNORE_HAND_NULL = event -> event.getPlayer().getItemInHand() != null;

    public static Predicate<PlayerMoveEvent> getIgnoreSameChunk() {
        return IGNORE_SAME_CHUNK;
    }

    public static Predicate<? extends Cancellable> getIgnoreCancelled() {
        return IGNORE_CANCELLED;
    }

    public static Predicate<? extends PlayerEvent> getIgnoreHandNull() {
        return IGNORE_HAND_NULL;
    }
}
