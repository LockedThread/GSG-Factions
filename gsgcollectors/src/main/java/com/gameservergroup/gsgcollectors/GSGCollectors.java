package com.gameservergroup.gsgcollectors;

import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.McMMOIntegration;
import com.gameservergroup.gsgcollectors.integration.SellPriceModifierIntegration;
import com.gameservergroup.gsgcollectors.integration.ShopGUIPlusIntegration;
import com.gameservergroup.gsgcollectors.integration.impl.AceOutpostsSellPriceModifierImpl;
import com.gameservergroup.gsgcollectors.integration.impl.McMMOImpl;
import com.gameservergroup.gsgcollectors.integration.impl.ShopGUIPlusImpl;
import com.gameservergroup.gsgcollectors.units.UnitCollectors;
import com.gameservergroup.gsgcore.plugin.Module;

public class GSGCollectors extends Module {

    private static GSGCollectors instance;
    private UnitCollectors unitCollectors;
    private ShopGUIPlusIntegration shopGUIPlusIntegration;
    private SellPriceModifierIntegration sellPriceModifierIntegration;
    private McMMOIntegration mcMMOIntegration;

    public static GSGCollectors getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        for (CollectorMessages collectorMessages : CollectorMessages.values()) {
            if (getConfig().isSet("messages." + collectorMessages.getKey())) {
                collectorMessages.setMessage(getConfig().getString("messages." + collectorMessages.getKey()));
            } else {
                getConfig().set("messages." + collectorMessages.getKey(), collectorMessages.getValue());
            }
        }
        saveConfig();
        if (getConfig().getBoolean("options.shop-gui-plus.hook")) {
            if (getServer().getPluginManager().getPlugin("ShopGUIPlus") != null) {
                shopGUIPlusIntegration = new ShopGUIPlusImpl();
            } else {
                getLogger().severe("Unable to hook into ShopGUIPlus because it's not enabled on the server!");
            }
        }
        if (getServer().getPluginManager().getPlugin("Ace-Outposts") != null) {
            this.sellPriceModifierIntegration = new AceOutpostsSellPriceModifierImpl();
        }

        if (getConfig().getBoolean("options.harvester-hoes.mcmmo.enabled")) {
            if (getServer().getPluginManager().getPlugin("McMMO") != null) {
                mcMMOIntegration = new McMMOImpl();
            } else {
                getLogger().severe("Unable to hook into McMMO because it's not enabled on the server!");
            }
        }

        registerUnits(unitCollectors = new UnitCollectors());
    }

    @Override
    public void reload() {
        reloadConfig();

    }

    @Override
    public void disable() {
        instance = null;
    }

    public UnitCollectors getUnitCollectors() {
        return unitCollectors;
    }

    public ShopGUIPlusIntegration getShopGUIPlusIntegration() {
        return shopGUIPlusIntegration;
    }

    public SellPriceModifierIntegration getSellPriceModifierIntegration() {
        return sellPriceModifierIntegration;
    }

    public McMMOIntegration getMcMMOIntegration() {
        return mcMMOIntegration;
    }
}
