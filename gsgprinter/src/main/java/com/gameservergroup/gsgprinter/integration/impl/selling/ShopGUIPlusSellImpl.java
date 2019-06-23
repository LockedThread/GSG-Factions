package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import net.brcdev.shopgui.ShopGuiPlugin;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopGUIPlusSellImpl implements SellIntegration {

    private Int2DoubleMap prices;

    public ShopGUIPlusSellImpl() {
        this.prices = new Int2DoubleOpenHashMap();
        List<String> shops = GSGPrinter.getInstance().getConfig().getStringList("shopguiplus.blacklisted-shops").stream().map(String::toLowerCase).collect(Collectors.toList());
        for (Map.Entry<String, Shop> entry : ShopGuiPlugin.getInstance().getShopManager().shops.entrySet()) {
            if (!shops.contains(entry.getKey().toLowerCase())) {
                for (ShopItem shopItem : entry.getValue().getShopItems()) {
                    if (shopItem.getItem().getType() == Material.STRING) {
                        prices.putIfAbsent(Material.STRING.getId(), shopItem.getBuyPrice());
                        prices.putIfAbsent(Material.TRIPWIRE.getId(), shopItem.getBuyPrice());
                    } else if (shopItem.getItem().getType() == Material.REDSTONE) {
                        prices.putIfAbsent(Material.REDSTONE.getId(), shopItem.getBuyPrice());
                        prices.putIfAbsent(Material.REDSTONE_WIRE.getId(), shopItem.getBuyPrice());
                    } else if (shopItem.getItem().getType() == Material.DIODE) {
                        prices.putIfAbsent(Material.DIODE.getId(), shopItem.getBuyPrice());
                        prices.putIfAbsent(Material.DIODE_BLOCK_OFF.getId(), shopItem.getBuyPrice());
                        prices.putIfAbsent(Material.DIODE_BLOCK_ON.getId(), shopItem.getBuyPrice());
                    } else if (shopItem.getItem().getType() == Material.REDSTONE_COMPARATOR) {
                        prices.putIfAbsent(Material.REDSTONE_COMPARATOR.getId(), shopItem.getBuyPrice());
                        prices.putIfAbsent(Material.REDSTONE_COMPARATOR_OFF.getId(), shopItem.getBuyPrice());
                        prices.putIfAbsent(Material.REDSTONE_COMPARATOR_ON.getId(), shopItem.getBuyPrice());
                    } else {
                        prices.putIfAbsent(shopItem.getItem().getType().getId(), shopItem.getBuyPrice());
                    }
                }
            }
        }
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
