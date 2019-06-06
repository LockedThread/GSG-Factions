package dev.lockedthread.frontierfactions.frontiercore.units;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.CallBack;
import dev.lockedthread.frontierfactions.frontiercore.FrontierCore;
import org.bukkit.Material;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class UnitGracePeriod extends Unit {

    private boolean gracePeriod;

    @Override
    public void setup() {
        this.gracePeriod = FrontierCore.getInstance().getConfig().getBoolean("grace-period", false);
        hookDisable(new CallBack() {
            @Override
            public void call() {
                FrontierCore.getInstance().getConfig().set("grace-period", gracePeriod);
                FrontierCore.getInstance().saveConfig();
            }
        });
        EventPost.of(BlockPlaceEvent.class)
                .filter(event -> gracePeriod)
                .filter(event -> event.getBlockPlaced().getType() == Material.TNT)
                .handle(event -> event.setCancelled(true))
                .post(FrontierCore.getInstance());

        EventPost.of(EntityExplodeEvent.class)
                .filter(event -> gracePeriod)
                .handle(event -> event.setCancelled(true))
                .post(FrontierCore.getInstance());

        EventPost.of(BlockExplodeEvent.class)
                .filter(event -> gracePeriod)
                .handle(event -> event.setCancelled(true))
                .post(FrontierCore.getInstance());

        EventPost.of(BlockDispenseEvent.class)
                .filter(event -> gracePeriod)
                .handle(event -> event.setCancelled(true))
                .post(FrontierCore.getInstance());

        CommandPost.create()
                .builder()
                .assertPermission("frontierfactions.graceperiod")
                .handler(commandHandler -> {
                    if (commandHandler.getRawArgs().length == 0) {
                        commandHandler.reply("/graceperiod [on/off]");
                        commandHandler.reply("/graceperiod status");
                    } else if (commandHandler.getRawArgs().length == 1) {
                        if (commandHandler.getRawArg(0).equalsIgnoreCase("status")) {
                            commandHandler.reply("&eGraceperiod: " + (gracePeriod ? "&aEnabled" : "&cDisabled"));
                        } else if (commandHandler.getRawArg(0).equalsIgnoreCase("on")) {
                            commandHandler.reply("&eYou have now &aenabled &egraceperiod");
                            gracePeriod = true;
                        } else if (commandHandler.getRawArg(0).equalsIgnoreCase("off")) {
                            commandHandler.reply("&eYou have now &cdisabled &egraceperiod");
                            gracePeriod = false;
                        } else {
                            commandHandler.reply("&cUnable to find subcommand for /gracerperiod called " + commandHandler.getRawArg(0));
                        }
                    } else {
                        commandHandler.reply("&cThere is no option for a length of 2 subcommand for /graceperiod");
                    }
                }).post(FrontierCore.getInstance(), "graceperiod");
    }

    public boolean isGracePeriod() {
        return gracePeriod;
    }
}
