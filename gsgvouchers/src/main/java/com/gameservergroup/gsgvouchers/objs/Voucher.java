package com.gameservergroup.gsgvouchers.objs;

import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Voucher {

    private CustomItem customItem;
    private List<String> commands;

    public Voucher(ConfigurationSection configurationSection) {
        this.customItem = CustomItem.of(configurationSection).setInteractEventConsumer(event -> {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                execute(event.getPlayer());
            }
        });
        if (configurationSection.isSet("commands") && configurationSection.isList("commands")) {
            this.commands = configurationSection.getStringList("commands");
        }
    }

    public Voucher(CustomItem customItem, List<String> commands) {
        this.customItem = customItem;
        this.commands = commands;
    }

    public void execute(Player player) {
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        }
        commands.stream().map(command -> command.replace("{player}", player.getName())).forEach(command -> {
            if (command.startsWith("@broadcast")) {
                Bukkit.broadcastMessage(Text.toColor(command.substring(11)));
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        });
    }

    public CustomItem getCustomItem() {
        return customItem;
    }
}
