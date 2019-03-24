package com.gameservergroup.gsgprinter;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgprinter.enums.PrinterMessages;
import com.gameservergroup.gsgprinter.integration.CombatIntegration;
import com.gameservergroup.gsgprinter.integration.FactionsIntegration;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import com.gameservergroup.gsgprinter.integration.combat.impl.CombatTagPlusImpl;
import com.gameservergroup.gsgprinter.integration.factions.impl.LockedThreadFactionsUUIDImpl;
import com.gameservergroup.gsgprinter.integration.impl.selling.ConfigSellImpl;
import com.gameservergroup.gsgprinter.integration.impl.selling.EssentialsSellImpl;
import com.gameservergroup.gsgprinter.integration.impl.selling.ShopGUIPlusSellImpl;
import com.gameservergroup.gsgprinter.units.UnitPrinter;
import org.bukkit.plugin.Plugin;

public class GSGPrinter extends Module {

    private static GSGPrinter instance;
    private boolean enableCombatTagPlusIntegration;
    private CombatIntegration combatIntegration;
    private SellIntegration sellIntegration;
    private FactionsIntegration factionsIntegration;
    private UnitPrinter unitPrinter;

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
        } else if (getConfig().getString("sell-integration").equalsIgnoreCase("shopguiplus")) {
            sellIntegration = new ShopGUIPlusSellImpl();
        } else {
            getLogger().info("Unable to find a supported sell integration, either download one or change your sell-integration!");
            getPluginLoader().disablePlugin(this);
            return;
        }
        if (getServer().getPluginManager().getPlugin("Factions") != null) {
            if (getServer().getPluginManager().getPlugin("Factions").getDescription().getAuthors().contains("LockedThread")) {
                getLogger().info("Using LockedThread's Factions Fork");
                this.factionsIntegration = new LockedThreadFactionsUUIDImpl();
            } else {
                this.factionsIntegration = callBack -> {
                };
            }
        } else {
            getLogger().severe("Unable to find factions plugin!");
            getPluginLoader().disablePlugin(this);
            return;
        }
        for (PrinterMessages printerMessages : PrinterMessages.values()) {
            if (getConfig().isSet("messages." + printerMessages.getKey())) {
                printerMessages.setMessage(getConfig().getString("messages." + printerMessages.getKey()));
            } else {
                getConfig().set("messages." + printerMessages.getKey(), printerMessages.getValue());
            }
        }
        saveConfig();
        registerUnits(unitPrinter = new UnitPrinter());
    }

    @Override
    public void reload() {
        reloadConfig();
        if (getConfig().getString("sell-integration").equalsIgnoreCase("essentials")) {
            sellIntegration = new EssentialsSellImpl(getServer().getPluginManager().getPlugin("Essentials"));
        } else if (getConfig().getString("sell-integration").equalsIgnoreCase("config")) {
            sellIntegration = new ConfigSellImpl(getDataFolder(), "prices.yml");
        } else {
            getLogger().info("Unable to find a supported sell integration, either download one or change your sell-integration!");
            getPluginLoader().disablePlugin(this);
        }
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

    public UnitPrinter getUnitPrinter() {
        return unitPrinter;
    }

    public boolean isEnableCombatTagPlusIntegration() {
        return enableCombatTagPlusIntegration;
    }

    public FactionsIntegration getFactionsIntegration() {
        return factionsIntegration;
    }
}
