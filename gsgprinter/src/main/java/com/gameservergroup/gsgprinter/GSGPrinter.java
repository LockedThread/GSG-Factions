package com.gameservergroup.gsgprinter;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgprinter.enums.PrinterMessages;
import com.gameservergroup.gsgprinter.integration.CombatIntegration;
import com.gameservergroup.gsgprinter.integration.FactionsIntegration;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import com.gameservergroup.gsgprinter.integration.impl.combat.CombatLogXImpl;
import com.gameservergroup.gsgprinter.integration.impl.combat.CombatTagPlusImpl;
import com.gameservergroup.gsgprinter.integration.impl.factions.FactionsUUIDImpl;
import com.gameservergroup.gsgprinter.integration.impl.factions.LockedThreadFactionsUUIDImpl;
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
    private UnitPrinter unitPrinter;
    private FactionsIntegration factionsIntegration;

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
                this.enableCombatTagPlusIntegration = true;
                this.combatIntegration = new CombatTagPlusImpl(combatTagPlus);
            } else {
                getLogger().severe("You don't have CombatTagPlus installed, if you don't wish to use it disable the integration in GSGPrinter's config.yml");
            }
        }
        if (getConfig().getBoolean("enable-combatlogx-integration")) {
            Plugin combatLogX = getServer().getPluginManager().getPlugin("CombatLogX");
            if (combatLogX != null) {
                getLogger().info("Enabled CombatLogX Integration");
                this.enableCombatTagPlusIntegration = true;
                this.combatIntegration = new CombatLogXImpl();
            } else {
                getLogger().severe("You don't have CombatTagX installed, if you don't wish to use it disable the integration in GSGPrinter's config.yml");
            }
        }
        if (getConfig().getString("sell-integration").equalsIgnoreCase("essentials")) {
            sellIntegration = new EssentialsSellImpl(getServer().getPluginManager().getPlugin("Essentials"));
            getLogger().info("Using Essentials Sell Prices");
        } else if (getConfig().getString("sell-integration").equalsIgnoreCase("config")) {
            sellIntegration = new ConfigSellImpl(getDataFolder(), "prices.yml");
            getLogger().info("Using prices.yml prices");
        } else if (getConfig().getString("sell-integration").equalsIgnoreCase("shopguiplus")) {
            sellIntegration = new ShopGUIPlusSellImpl();
            getLogger().info("Using ShopGUIPlus prices");
        } else {
            getLogger().info("Unable to find a supported sell integration, either download one or change your sell-integration!");
            getPluginLoader().disablePlugin(this);
            return;
        }
        if (getServer().getPluginManager().getPlugin("Factions") != null) {
            if (getServer().getPluginManager().getPlugin("Factions").getDescription().getAuthors().contains("LockedThread")) {
                getLogger().info("Using LockedThread's Factions Fork");
                (this.factionsIntegration = new LockedThreadFactionsUUIDImpl()).hookFlightDisable();
            } else {
                getLogger().warning("Using FactionsUUID, consider switching to LockedThread's Faction fork for better performance.");
                (this.factionsIntegration = new FactionsUUIDImpl()).hookFlightDisable();
            }
        } else {
            getLogger().severe("Unable to find factions plugin! Going to use non factions impl");
        }
        for (PrinterMessages printerMessages : PrinterMessages.values()) {
            if (getConfig().isSet("messages." + printerMessages.getKey())) {
                printerMessages.setMessage(getConfig().getString("messages." + printerMessages.getKey()));
            } else {
                getConfig().set("messages." + printerMessages.getKey(), printerMessages.getValue());
            }
        }
        saveConfig();
        registerUnits(this.unitPrinter = new UnitPrinter());
    }

    @Override
    public void reload() {
        reloadConfig();
        if (getConfig().getString("sell-integration").equalsIgnoreCase("essentials")) {
            sellIntegration = new EssentialsSellImpl(getServer().getPluginManager().getPlugin("Essentials"));
        } else if (getConfig().getString("sell-integration").equalsIgnoreCase("config")) {
            sellIntegration = new ConfigSellImpl(getDataFolder(), "prices.yml");
        } else if (getConfig().getString("sell-integration").equalsIgnoreCase("shopguiplus")) {
            sellIntegration = new ShopGUIPlusSellImpl();
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

    public boolean isEnableCombatTagPlusIntegration() {
        return enableCombatTagPlusIntegration;
    }

    public UnitPrinter getUnitPrinter() {
        return unitPrinter;
    }

    public FactionsIntegration getFactionsIntegration() {
        return factionsIntegration;
    }
}
