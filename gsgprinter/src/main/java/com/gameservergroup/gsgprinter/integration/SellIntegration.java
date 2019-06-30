package com.gameservergroup.gsgprinter.integration;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface SellIntegration {

    default double getBuyPrice(Material material, int amount) {
        return getBuyPrice(material) * amount;
    }

    double getBuyPrice(Material material);

    default double getBuyPrice(ItemStack itemStack) {
        return getBuyPrice(itemStack.getType());
    }
}
