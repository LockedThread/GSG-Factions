package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.events.EventConsumerImpl;
import com.gameservergroup.gsgcore.events.EventPost;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GSGCore extends JavaPlugin {

    private static GSGCore instance;

    @Override
    public void onEnable() {
        instance = this;

        EventConsumerImpl<PlayerJoinEvent> s = new EventConsumerImpl<PlayerJoinEvent>() {
            @Override
            public void accept(PlayerJoinEvent playerJoinEvent) {

            }
        };


        EventPost.of(AsyncPlayerChatEvent.class)
                .filter(event -> event.getMessage().contains("a"))
                .handle(event -> {

                }).post(this);
    }

    @Override
    public void onDisable() {
        instance = null;

    }

    public static GSGCore getInstance() {
        return instance;
    }

}
