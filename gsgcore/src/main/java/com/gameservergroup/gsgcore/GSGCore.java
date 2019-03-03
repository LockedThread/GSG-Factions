package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.commands.post.CommandPostExecutor;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.utils.CommandMapUtil;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class GSGCore extends JavaPlugin {

    private static GSGCore instance;
    private HashSet<Module> modules;
    private CommandPostExecutor commandPostExecutor;

    @Override
    public void onEnable() {
        instance = this;
        this.modules = new HashSet<>();
        this.commandPostExecutor = new CommandPostExecutor();

        EventPost.of(PlayerJoinEvent.class)
                .filter(playerJoinEvent -> playerJoinEvent.getPlayer().getName().equalsIgnoreCase("Dumbass"))
                .handle(event -> event.getPlayer().sendMessage("You're gay"))
                .post(this);

        CommandPost.of()
                .build()
                .assertPlayer()
                .handler(c -> {
                    if (c.getRawArgs().length == 1) {
                        int shit = c.getArg(0).forceParse(int.class);
                        c.reply("You have execute /" + c.getLabel() + " " + shit);
                    } else {
                        c.reply("/test [num]");
                    }
                }).post(this, "test");


    }

    @Override
    public void onDisable() {
        instance = null;
        CommandMapUtil.unregisterCommands(this);
    }

    public static GSGCore getInstance() {
        return instance;
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    public void unregisterModule(Module module) {
        modules.remove(module);
        //CommandMapUtil.unregisterCommands(this);
    }

    public CommandPostExecutor getCommandPostExecutor() {
        return commandPostExecutor;
    }
}
