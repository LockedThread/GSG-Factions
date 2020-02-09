package com.gameservergroup.gsgbots.units;

import com.gameservergroup.gsgbots.GSGBots;
import com.gameservergroup.gsgbots.entities.EntityBot;
import com.gameservergroup.gsgbots.objs.Bot;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.units.Unit;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class UnitBots extends Unit {

    @Override
    public void setup() {
        EventPost.of(PlayerInteractAtEntityEvent.class)
                .filter(event -> event.getRightClicked() != null && event.getRightClicked() instanceof Villager && ((CraftVillager) event.getRightClicked()).getHandle() instanceof EntityBot)
                .handle(event -> {
                    System.out.println("event.getRightClicked() = " + event.getRightClicked());
                    System.out.println("event.getRightClicked().getType() = " + event.getRightClicked().getType());
                    Bot bot = Bot.getBotMap().get(event.getRightClicked().getUniqueId());
                    System.out.println("bot = " + bot);
                    if (bot != null) {
                        event.getPlayer().openInventory(bot.getMenuBot().getInventory());
                    }
                    event.setCancelled(true);
                }).post(GSGBots.getInstance());

        CustomItem.of(GSGBots.getInstance(), GSGBots.getInstance().getConfig().getConfigurationSection("bot-egg-item")).setInteractEventConsumer(event -> {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Player player = event.getPlayer();
                event.setCancelled(true);
                Bot bot = new Bot(event.getClickedBlock().getRelative(BlockFace.UP).getLocation(), player, 0);
                Bot.getBotMap().put(bot.getBotUUID(), bot);
                ItemStack itemInHand = player.getItemInHand();
                if (itemInHand.getAmount() == 1) {
                    player.setItemInHand(null);
                } else {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }
                System.out.println("bot.toString() = " + bot.toString());
            }
        });
    }
}
