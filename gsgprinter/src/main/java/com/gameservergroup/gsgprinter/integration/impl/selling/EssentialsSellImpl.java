package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.earth2me.essentials.IEssentials;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;

public class EssentialsSellImpl implements SellIntegration {

    private IEssentials iEssentials;

    public EssentialsSellImpl(Plugin plugin) {
        if (plugin instanceof IEssentials) {
            this.iEssentials = (IEssentials) plugin;
        } else {
            GSGPrinter.getInstance().getPluginLoader().disablePlugin(GSGPrinter.getInstance());
            throw new RuntimeException("Unable to parse plugin as Essentials, do you have the wrong version?");
        }
    }

    @Override
    public double getBuyPrice(ItemStack itemStack) {
        BigDecimal price = iEssentials.getWorth().getPrice(iEssentials, itemStack);
        return price == null ? 0.00 : price.doubleValue();
    }
}
