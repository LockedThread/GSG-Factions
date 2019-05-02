package com.gameservergroup.gsgcollectors.menus;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.enums.CollectionType;
import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
import com.gameservergroup.gsgcollectors.obj.Collector;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.github.paperspigot.Title;

import java.util.stream.Collectors;

public class MenuCollector extends Menu {

    private Collector collector;

    public MenuCollector(Collector collector) {
        super(GSGCollectors.getInstance().getUnitCollectors().getCollectorMenuName().replace("{owner}", collector.getLandOwner() + "'s"), GSGCollectors.getInstance().getUnitCollectors().getCollectorMenuSize());
        this.collector = collector;
        initialize();
    }

    @Override
    public void initialize() {
        for (CollectionType collectionType : GSGCollectors.getInstance().getUnitCollectors().getCollectionTypes()) {
            setItem(collectionType.getGuiSlot(), MenuItem.of(ItemStackBuilder.of(collectionType
                    .getItemStack().clone()).consumeItemMeta(itemMeta -> itemMeta.setLore(itemMeta.getLore()
                    .stream()
                    .map(s -> Text.toColor(s.replace("{amount}", String.valueOf(collector.getAmounts().getOrDefault(collectionType, 0)))))
                    .collect(Collectors.toList())))
                    .build())
                    .setInventoryClickEventConsumer(event -> {
                        int amount = collector.getAmounts().getOrDefault(collectionType, 0);
                        event.setCancelled(true);
                        if (amount > 0) {
                            int remainder = sub10OrReturn0(amount, collectionType == CollectionType.TNT ? 64 : 100), amountToBeSubtracted = collectionType == CollectionType.TNT ? 64 : 100;
                            if (remainder > 0) amountToBeSubtracted = remainder;
                            Player player = (Player) event.getWhoClicked();
                            if (collectionType == CollectionType.TNT) {
                                FactionsBankIntegration factionsBankIntegration = GSGCollectors.getInstance().getUnitCollectors().getFactionsBankIntegration();
                                if (factionsBankIntegration != null) {
                                    if (!factionsBankIntegration.setTntBankBalance(factionsBankIntegration.getFaction(player), factionsBankIntegration.getTntBankBalance(factionsBankIntegration.getFaction(player)) + amountToBeSubtracted)) {
                                        return;
                                    }
                                } else if (player.getInventory().firstEmpty() == -1) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.TNT, amountToBeSubtracted));
                                } else {
                                    player.getInventory().addItem(new ItemStack(Material.TNT, amountToBeSubtracted));
                                }
                            } else {
                                double money = collectionType.getPrice() * amountToBeSubtracted;
                                Module.getEconomy().depositPlayer(player, money);
                                player.sendTitle(Title.builder().title(CollectorMessages.TITLE_SELL.toString().replace("{money}", String.valueOf(money))).fadeIn(5).fadeOut(5).stay(25).build());
                            }
                            update(collectionType, amount - amountToBeSubtracted);
                            collector.removeAmount(collectionType, amountToBeSubtracted);
                        }
                    }));
        }
        if (GSGCollectors.getInstance().getUnitCollectors().isFillMenu()) {
            while (getInventory().firstEmpty() != -1) {
                setItem(getInventory().firstEmpty(), GSGCollectors.getInstance().getUnitCollectors().getFillItemStack());
            }
        }
    }

    public void update(CollectionType collectionType, int replace) {
        getMenuItem(collectionType.getGuiSlot()).ifPresent(menuItem -> {
            ItemStack item = ItemStackBuilder.of(collectionType.getItemStack().clone()).consumeItemMeta(itemMeta -> itemMeta.setLore(itemMeta.getLore().stream().map(s -> Text.toColor(s.replace("{amount}", String.valueOf(replace)))).collect(Collectors.toList()))).build();
            setItem(collectionType.getGuiSlot(), menuItem.setItemStack(item));
            getInventory().getViewers().forEach(viewer -> ((Player) viewer).updateInventory());
        });
    }

    private int sub10OrReturn0(int i, int divisor) {
        return i < 0 ? -1 : i % divisor > 0 && i < divisor ? i % divisor : 0;
    }
}
