package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.gameservergroup.gsgprinter.integration.SellIntegration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;

public class ShopGUIPlusSellImpl implements SellIntegration {

    private EnumMap<Material, Double> prices;

    public ShopGUIPlusSellImpl() {

    }

    @Override
    public double getBuyPrice(ItemStack itemStack) {
        return 0;
    }
}
