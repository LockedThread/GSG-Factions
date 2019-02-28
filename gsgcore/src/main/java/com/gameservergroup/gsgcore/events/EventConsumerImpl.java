package com.gameservergroup.gsgcore.events;

import org.bukkit.event.Event;

public abstract class EventConsumerImpl<T extends Event> implements EventConsumer<T> {

    private boolean active = true;

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }


    public abstract void accept(T t);
}
