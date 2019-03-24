package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import net.brcdev.shopgui.ShopGuiPlugin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class ShopGUIPlusSellImpl implements SellIntegration {

    private EnumMap<Material, Double> prices = new EnumMap<>(Material.class);

    public ShopGUIPlusSellImpl() {
        List<String> shops = GSGPrinter.getInstance().getConfig().getStringList("shopguiplus.blacklisted-shops").stream().map(String::toLowerCase).collect(Collectors.toList());
        ShopGuiPlugin.getInstance().getShopManager()
                .shops
                .entrySet()
                .stream()
                .filter(entry -> !shops.contains(entry.getKey().toLowerCase()))
                .flatMap(entry -> entry.getValue().getShopItems().stream())
                .forEach(shopItem -> prices.putIfAbsent(shopItem.getItem().getType(), shopItem.getBuyPrice()));
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
