package com.gameservergroup.gsgcore.units;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.plugin.Module;
import org.bukkit.plugin.Plugin;

public class UnitReload extends Unit {

    @Override
    public void setup() {
        CommandPost.of()
                .builder()
                .assertPermission("gsgcore.reload")
                .handler(c -> {
                    if (c.getRawArgs().length == 0) {
                        c.reply("&e/gsgreload [plugin-name]");
                    } else if (c.getRawArgs().length == 1) {
                        Plugin plugin = GSG_CORE.getServer().getPluginManager().getPlugin(c.getRawArg(0));
                        if (plugin != null) {
                            if (plugin instanceof Module) {
                                Module module = (Module) plugin;
                                module.reload();
                                c.reply("&aYou have reloaded " + module.getName() + ", if there's any errors please contact LockedThread");
                            } else {
                                c.reply("&cYou may only reload plugins owned by LockedThread.");
                            }
                        } else {
                            c.reply("&cUnable to find plugin " + c.getRawArg(0));
                        }
                    } else {
                        c.reply("&e/gsgreload [plugin-name]");
                    }
                }).post(GSG_CORE, "gsgreload");
    }
}