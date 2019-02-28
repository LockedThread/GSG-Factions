package com.gameservergroup.gsgcore.events;

import org.bukkit.event.Event;

import java.util.function.Consumer;

public interface EventConsumer<T extends Event> extends Consumer<T> {

    void setActive(boolean active);

    boolean isActive();

}
