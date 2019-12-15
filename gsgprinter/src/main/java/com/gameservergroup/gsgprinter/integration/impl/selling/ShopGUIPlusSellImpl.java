package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.gameservergroup.gsgcore.relocations.fastutil.ints.Int2DoubleMap;
import com.gameservergroup.gsgcore.relocations.fastutil.ints.Int2DoubleOpenHashMap;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import net.brcdev.shopgui.ShopGuiPlugin;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopGUIPlusSellImpl implements SellIntegration {

    private final Int2DoubleMap prices;
    private final int id;

    public ShopGUIPlusSellImpl() {
        this.prices = new Int2DoubleOpenHashMap();
        this.id = GSGPrinter.getInstance().getServer().getScheduler().runTaskTimer(GSGPrinter.getInstance(), () -> {
            if (ShopGuiPlugin.getInstance().getShopManager().shops.isEmpty()) {
                return;
            }
            List<String> shops = GSGPrinter.getInstance().getConfig().getStringList("shopguiplus.blacklisted-shops").stream().map(String::toLowerCase).collect(Collectors.toList());
            for (Map.Entry<String, Shop> entry : ShopGuiPlugin.getInstance().getShopManager().shops.entrySet()) {
                if (!shops.contains(entry.getKey().toLowerCase())) {
                    for (ShopItem shopItem : entry.getValue().getShopItems()) {
                        if (shopItem.getItem().getType().isBlock()) {
                            if (shopItem.getItem().getType() == Material.STRING) {
                                prices.put(Material.TRIPWIRE.getId(), shopItem.getBuyPrice());
                            } else if (shopItem.getItem().getType() == Material.REDSTONE) {
                                prices.put(Material.REDSTONE_WIRE.getId(), shopItem.getBuyPrice());
                            } else if (shopItem.getItem().getType() == Material.DIODE) {
                                prices.put(Material.DIODE_BLOCK_OFF.getId(), shopItem.getBuyPrice());
                                prices.put(Material.DIODE_BLOCK_ON.getId(), shopItem.getBuyPrice());
                            } else if (shopItem.getItem().getType() == Material.REDSTONE_COMPARATOR) {
                                prices.put(Material.REDSTONE_COMPARATOR_OFF.getId(), shopItem.getBuyPrice());
                                prices.put(Material.REDSTONE_COMPARATOR_ON.getId(), shopItem.getBuyPrice());
                            } else if (shopItem.getItem().getType() == Material.SUGAR_CANE) {
                                prices.put(Material.SUGAR_CANE_BLOCK.getId(), shopItem.getBuyPrice());
                            }
                            prices.put(shopItem.getItem().getType().getId(), shopItem.getBuyPrice());
                        }
                    }
                }
            }
            end();
        }, 40L, 40L).getTaskId();
    }

    private void end() {
        GSGPrinter.getInstance().getServer().getScheduler().cancelTask(id);
    }

    @Override
    public double getBuyPrice(ItemStack itemStack) {
        return getBuyPrice(itemStack.getType());
    }

    @Override
    public double getBuyPrice(Material material) {
        return prices.getOrDefault(material.getId(), 0.0);
    }
}
