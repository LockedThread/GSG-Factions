package com.gameservergroup.gsgprinter;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgprinter.enums.PrinterMessages;
import com.gameservergroup.gsgprinter.integration.CombatIntegration;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import com.gameservergroup.gsgprinter.integration.combat.impl.CombatTagPlusImpl;
import com.gameservergroup.gsgprinter.integration.impl.selling.ConfigSellImpl;
import com.gameservergroup.gsgprinter.integration.impl.selling.EssentialsSellImpl;
import com.gameservergroup.gsgprinter.units.UnitPrinter;
import org.bukkit.plugin.Plugin;

public class GSGPrinter extends Module {

    private static GSGPrinter instance;
    private SellIntegration sellIntegration;
    private boolean enableCombatTagPlusIntegration;
    private CombatIntegration combatIntegration;

    public static GSGPrinter getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getBoolean("enable-combattagplus-integration")) {
            Plugin combatTagPlus = getServer().getPluginManager().getPlugin("CombatTagPlus");
            if (combatTagPlus != null) {
                getLogger().info("Enabled CombatTagPlus Integration");
                this.enableCombatTagPlusIntegration = getConfig().getBoolean("enable-combattagplus-integration");
                this.combatIntegration = new CombatTagPlusImpl(combatTagPlus);
            } else {
                getLogger().severe("You don't have CombatTagPlus installed, if you don't wish to use it disable the integration in GSGPrinter's config.yml");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }
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

    public CombatIntegration getCombatIntegration() {
        return combatIntegration;
    }

    public boolean isEnableCombatTagPlusIntegration() {
        return enableCombatTagPlusIntegration;
    }
}
