package com.gameservergroup.gsgcore.events;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventPost<T extends Event> {

    private Class<T> eventClass;
    private HashSet<Predicate<T>> filters = new HashSet<>();
    private EventPriority eventPriority;
    private Consumer<T> eventConsumer;

    public static <T extends Event> EventPost<T> of(Class<T> eventClass, EventPriority eventPriority) {
        return new EventPost<>(eventClass, eventPriority);
    }

    public static <T extends Event> EventPost<T> of(Class<T> eventClass) {
        return new EventPost<>(eventClass, EventPriority.NORMAL);
    }

    private EventPost(Class<T> eventClass, EventPriority eventPriority) {
        this.eventClass = eventClass;
        this.eventPriority = eventPriority;
    }

    public EventPost<T> filter(Predicate<T> event) {
        filters.add(event);
        return this;
    }

    HashSet<Predicate<T>> getFilters() {
        return filters;
    }

    Class<? extends Event> getEventClass() {
        return eventClass;
    }

    public EventPoster handle(Consumer<T> event) {
        this.eventConsumer = event;
        return plugin -> new EventPostExecutor<>(EventPost.this).registerListener(plugin);
    }

    EventPriority getEventPriority() {
        return eventPriority;
    }

    Consumer<T> getEventConsumer() {
        return eventConsumer;
    }
}
