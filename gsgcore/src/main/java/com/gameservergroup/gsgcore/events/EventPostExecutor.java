package com.gameservergroup.gsgcore.events;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

public class EventPostExecutor<T extends Event> implements EventExecutor, Listener {

    private EventPost<T> eventPost;

    public EventPostExecutor(EventPost<T> eventPost) {
        this.eventPost = eventPost;
    }

    public void registerListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvent(eventPost.getEventClass(), this, eventPost.getEventPriority(), this, plugin);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Listener listener, Event event) {
        if (!event.getClass().equals(eventPost.getEventClass())) {
            return;
        }
        for (Predicate<T> filter : eventPost.getFilters()) {
            if (!filter.test((T) event)) {
                return;
            }
        }
        T eventInstance = (T) eventPost.getEventClass().cast(event);
        eventPost.getEventConsumer().accept(eventInstance);
    }
}
