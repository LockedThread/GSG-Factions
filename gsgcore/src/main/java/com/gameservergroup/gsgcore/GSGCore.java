package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.events.EventPost;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GSGCore extends JavaPlugin {

    private static GSGCore instance;

    @Override
    public void onEnable() {
        instance = this;

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
