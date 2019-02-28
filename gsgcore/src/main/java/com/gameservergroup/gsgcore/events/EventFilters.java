package com.gameservergroup.gsgcore.events;


import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Predicate;

public class EventFilters {

    public static final Predicate<PlayerMoveEvent> IGNORE_SAME_CHUNK = event -> event.getFrom().getChunk().getX() == event.getTo().getChunk().getX() && event.getFrom().getChunk().getZ() == event.getTo().getChunk().getZ();
    public static final Predicate<? extends Cancellable> IGNORE_CANCELLED = cancellable -> !cancellable.isCancelled();
    public static final Predicate<? extends PlayerEvent> IGNORE_HAND_NULL = event -> event.getPlayer().getItemInHand() != null;

}
