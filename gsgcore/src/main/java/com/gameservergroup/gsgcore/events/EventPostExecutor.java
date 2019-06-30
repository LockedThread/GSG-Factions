package com.gameservergroup.gsgcore.events;

import com.gameservergroup.gsgcore.plugin.Module;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

public class EventPostExecutor<T extends Event> implements EventExecutor, Listener {

    private EventPost<T> eventPost;
    private Plugin plugin;

    public EventPostExecutor(EventPost<T> eventPost, Plugin plugin) {
        this.eventPost = eventPost;
        this.plugin = plugin;
    }

    public void registerListener() {
        plugin.getServer().getPluginManager().registerEvent(eventPost.getEventClass(), this, eventPost.getEventPriority(), this, plugin);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Listener listener, Event event) {
        if (eventPost.isDisabled()) {
            if (plugin instanceof Module) {
                ((Module) plugin).getEventPosts().remove(eventPost);
            }
            event.getHandlers().unregister(this);
            return;
        }

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
