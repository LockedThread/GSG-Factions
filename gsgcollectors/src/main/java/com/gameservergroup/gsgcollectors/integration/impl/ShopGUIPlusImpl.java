package com.gameservergroup.gsgcollectors.integration.impl;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.integration.ShopGUIPlusIntegration;
import net.brcdev.shopgui.ShopGuiPlugin;
import net.brcdev.shopgui.shop.ShopItem;
import org.bukkit.Material;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopGUIPlusImpl implements ShopGUIPlusIntegration {

    private Map<Material, Double> prices;

    public ShopGUIPlusImpl() {
        List<String> shops = GSGCollectors.getInstance().getConfig().getStringList("options.shop-gui-plus.blacklisted-shops")
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        this.prices = ShopGuiPlugin.getInstance().getShopManager()
                .shops
                .entrySet()
                .stream()
                .filter(entry -> !shops.contains(entry.getKey().toLowerCase()))
                .flatMap(entry -> entry.getValue().getShopItems().stream())
                .collect(Collectors.toMap(shopItem -> shopItem.getItem().getType(), ShopItem::getSellPrice, (a, b) -> a, () -> new EnumMap<>(Material.class)));
    }

    @Override
    public double getSellPrice(Material material) {
        return material == null ? 0.0 : prices.getOrDefault(material, 0.0);
    }
}
