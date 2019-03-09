package com.gameservergroup.gsgcore.units;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import org.bukkit.entity.Player;

public class UnitTest extends Unit {

    @Override
    public void setup() {
        CommandPost.of()
                .build()
                .assertPermission("gsgcore.test")
                .handler(c -> {
                    if (c.getRawArgs().length == 0) {
                        c.reply("", "&d/test locationserialize", "");
                    }
                    if (c.getRawArgs().length == 1) {
                        if (c.getRawArg(0).equalsIgnoreCase("locationserialize")) {
                            if (c.getSender() instanceof Player) {
                                c.reply("&dWritten and mapped your location as: &f" + GSG_CORE.getJsonObjectMapper().writeValueAsString(((Player) c.getSender()).getLocation()));
                            } else {
                                c.reply("&cYou must be a player to execute this!");
                            }
                        }
                    }

                }).post(GSG_CORE, "test");

    }
}
