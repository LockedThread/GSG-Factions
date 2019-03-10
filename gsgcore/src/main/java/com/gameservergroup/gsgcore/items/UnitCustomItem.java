package com.gameservergroup.gsgcore.items;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.google.common.base.Joiner;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class UnitCustomItem extends Unit {

    @Override
    public void setup() {
        CommandPost.of()
                .getCommandBuilder()
                .assertPermission("gsg.customitem")
                .handler(c -> {
                    if (c.getRawArgs().length == 0) {
                        c.reply("", "&d/customitem list", "&d/customitem give [player] [item] {amount}", "&d/customitem info [item]", "");
                    } else if (c.getRawArgs().length == 1) {
                        if (c.getRawArg(0).equalsIgnoreCase("list")) {
                            c.reply("", "&dCustomItems: &f" + Joiner.on(", ").skipNulls().join(CustomItem.getCustomItems().keySet()), "");
                        } else {
                            c.reply("&cInvalid argument");
                        }
                    } else if (c.getRawArgs().length == 2) {
                        if (c.getRawArg(0).equalsIgnoreCase("info")) {
                            CustomItem customItem = c.getArg(1).forceParse(CustomItem.class);
                            c.reply("",
                                    "&dName: &f" + customItem.getName(),
                                    "&dBlockBreakEvent: &f" + (customItem.getBreakEventConsumer() != null),
                                    "&dBlockPlaceEvent: &f" + (customItem.getPlaceEventConsumer() != null),
                                    "&dPlayerBucketEmptyEvent: &f" + (customItem.getBucketEmptyEventConsumer() != null),
                                    "&dPlayerInteractEvent: &f" + (customItem.getInteractEventConsumer() != null),
                                    "");
                        } else {
                            c.reply("&cInvalid argument");
                        }
                    } else if (c.getRawArgs().length == 3 || c.getRawArgs().length == 4) {
                        if (c.getRawArg(0).equalsIgnoreCase("give")) {
                            Player target = c.getArg(1).forceParse(Player.class);
                            CustomItem customItem = c.getArg(2).forceParse(CustomItem.class);
                            int amount = c.getArg(3).parse(int.class).orElse(1);
                            for (int i = 0; i < amount; i++) {
                                target.getInventory().addItem(customItem.getItemStack());
                            }
                            c.reply("&eYou have given " + target.getName() + " " + amount + " " + customItem + "s");
                        } else {
                            c.reply("&cInvalid argument");
                        }
                    } else {
                        c.reply("&cInvalid argument");
                    }
                }).post(GSG_CORE, "customitem", "customitems");

        EventPost.of(BlockBreakEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(EventFilters.getIgnoreHandNull())
                .handle(event -> {
                    final CustomItem customItem = CustomItem.findCustomItem(event.getPlayer().getItemInHand());
                    if (customItem != null) {
                        customItem.getBreakEventConsumer().accept(event);
                    }
                }).post(GSG_CORE);

        EventPost.of(BlockPlaceEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(EventFilters.getIgnoreHandNull())
                .handle(event -> {
                    final CustomItem customItem = CustomItem.findCustomItem(event.getItemInHand());
                    if (customItem != null) {
                        customItem.getPlaceEventConsumer().accept(event);
                    }
                }).post(GSG_CORE);

        EventPost.of(PlayerInteractEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(EventFilters.getIgnoreHandNull())
                .handle(event -> {
                    final CustomItem customItem = CustomItem.findCustomItem(event.getItem());
                    if (customItem != null) {
                        customItem.getInteractEventConsumer().accept(event);
                    }
                }).post(GSG_CORE);

        EventPost.of(PlayerBucketEmptyEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(EventFilters.getIgnoreHandNull())
                .handle(event -> {
                    final CustomItem customItem = CustomItem.findCustomItem(event.getItemStack());
                    if (customItem != null) {
                        customItem.getBucketEmptyEventConsumer().accept(event);
                    }
                }).post(GSG_CORE);

    }
}
