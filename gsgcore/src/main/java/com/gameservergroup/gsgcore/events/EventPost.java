package com.gameservergroup.gsgcore.events;

import com.gameservergroup.gsgcore.plugin.Module;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventPost<T extends Event> {

    private boolean disabled = false;
    private final Class<T> eventClass;
    private final EventPriority eventPriority;
    private LinkedList<Predicate<T>> filters;
    private Consumer<T> eventConsumer;

    private EventPost(Class<T> eventClass, EventPriority eventPriority) {
        this.eventClass = eventClass;
        this.eventPriority = eventPriority;
    }

    public static <T extends Event> EventPost<T> of(Class<T> eventClass, EventPriority eventPriority) {
        return new EventPost<>(eventClass, eventPriority);
    }

    public static <T extends Event> EventPost<T> of(Class<T> eventClass) {
        return new EventPost<>(eventClass, EventPriority.NORMAL);
    }

    public EventPost<T> filter(Predicate<T> event) {
        if (filters == null) {
            filters = new LinkedList<>();
        }
        filters.add(event);
        return this;
    }

    LinkedList<Predicate<T>> getFilters() {
        return filters;
    }

    Class<? extends Event> getEventClass() {
        return eventClass;
    }

    public EventPoster handle(Consumer<T> event) {
        this.eventConsumer = event;
        return plugin -> {
            if (plugin instanceof Module) {
                Module module = (Module) plugin;
                module.getEventPosts().add(EventPost.this);
            }
            new EventPostExecutor<>(EventPost.this, plugin).registerListener();
        };
    }

    EventPriority getEventPriority() {
        return eventPriority;
    }

    Consumer<T> getEventConsumer() {
        return eventConsumer;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
