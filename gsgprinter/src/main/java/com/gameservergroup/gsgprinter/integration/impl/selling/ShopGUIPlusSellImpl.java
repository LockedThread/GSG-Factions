package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import net.brcdev.shopgui.ShopGuiPlugin;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopGUIPlusSellImpl implements SellIntegration {

    private EnumMap<Material, Double> prices = new EnumMap<>(Material.class);

    public ShopGUIPlusSellImpl() {
        List<String> shops = GSGPrinter.getInstance().getConfig().getStringList("shopguiplus.blacklisted-shops").stream().map(String::toLowerCase).collect(Collectors.toList());
        for (Map.Entry<String, Shop> entry : ShopGuiPlugin.getInstance().getShopManager().shops.entrySet()) {
            if (!shops.contains(entry.getKey().toLowerCase())) {
                for (ShopItem shopItem : entry.getValue().getShopItems()) {
                    if (shopItem.getItem().getType() == Material.STRING) {
                        prices.putIfAbsent(Material.TRIPWIRE, shopItem.getBuyPrice());
                    }
                    if (shopItem.getItem().getType() == Material.REDSTONE) {
                        prices.putIfAbsent(Material.REDSTONE_WIRE, shopItem.getBuyPrice());
                    }
                    if (shopItem.getItem().getType() == Material.DIODE) {
                        prices.putIfAbsent(Material.DIODE, shopItem.getBuyPrice());
                        prices.putIfAbsent(Material.DIODE_BLOCK_OFF, shopItem.getBuyPrice());
                        prices.putIfAbsent(Material.DIODE_BLOCK_ON, shopItem.getBuyPrice());
                    }
                    prices.putIfAbsent(shopItem.getItem().getType(), shopItem.getBuyPrice());
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
        return prices.getOrDefault(material, 0.0);
    }
}
