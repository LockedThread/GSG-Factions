package com.gameservergroup.gsgvouchers.objs;

import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Voucher extends CustomItem {

    private List<String> commands;

    public Voucher(String name, ItemStack itemStack, List<String> strings) {
        super(name, itemStack);
        this.commands = strings;
        setInteractEventConsumer(event -> {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                ItemStack itemInHand = event.getPlayer().getItemInHand();
                if (itemInHand.getAmount() == 1) {
                    event.getPlayer().setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }
                commands.stream().map(command -> command.replace("{player}", event.getPlayer().getName())).forEach(command -> {
                    if (command.startsWith("@broadcast")) {
                        Bukkit.broadcastMessage(Text.toColor(command.substring(11)));
                    } else if (command.startsWith("@message")) {
                        Bukkit.broadcastMessage(Text.toColor(command.substring(8)));
                    } else {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    }
                });
            }
        });
    }
}
