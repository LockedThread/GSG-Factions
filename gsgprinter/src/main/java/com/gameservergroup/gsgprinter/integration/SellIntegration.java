package com.gameservergroup.gsgprinter.integration;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface SellIntegration {

    default double getBuyPrice(Material material, int amount) {
        return getBuyPrice(material) * amount;
    }

    default double getBuyPrice(Material material) {
        return getBuyPrice(new ItemStack(material));
    }

    double getBuyPrice(ItemStack itemStack);
}
