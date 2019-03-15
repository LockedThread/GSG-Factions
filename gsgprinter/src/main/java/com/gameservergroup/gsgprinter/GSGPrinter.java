package com.gameservergroup.gsgprinter;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgprinter.enums.PrinterMessages;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import com.gameservergroup.gsgprinter.integration.impl.selling.ConfigSellImpl;
import com.gameservergroup.gsgprinter.integration.impl.selling.EssentialsSellImpl;
import com.gameservergroup.gsgprinter.units.UnitPrinter;

public class GSGPrinter extends Module {

    private static GSGPrinter instance;
    private SellIntegration sellIntegration;

    public static GSGPrinter getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getString("sell-integration").equalsIgnoreCase("essentials")) {
            sellIntegration = new EssentialsSellImpl(getServer().getPluginManager().getPlugin("Essentials"));
        } else if (getConfig().getString("sell-integration").equalsIgnoreCase("config")) {
            sellIntegration = new ConfigSellImpl(getDataFolder(), "prices.yml");
        } else {
            getLogger().info("Unable to find Essentials, either download essentials or change your sell-integration!");
            getPluginLoader().disablePlugin(this);
            return;
        }
        for (PrinterMessages printerMessages : PrinterMessages.values()) {
            getConfig().set("messages." + printerMessages.getKey(), printerMessages.getValue());
        }
        saveConfig();
        registerUnits(new UnitPrinter());
    }

    @Override
    public void disable() {
        instance = null;
    }

    public SellIntegration getSellIntegration() {
        return sellIntegration;
    }
}
