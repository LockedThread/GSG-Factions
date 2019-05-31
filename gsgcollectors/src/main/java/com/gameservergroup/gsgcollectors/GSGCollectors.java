package com.gameservergroup.gsgcollectors;

import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.SellPriceModifier;
import com.gameservergroup.gsgcollectors.integration.ShopGUIPlusIntegration;
import com.gameservergroup.gsgcollectors.integration.aceoutposts.AceOutpostsSellPriceModifier;
import com.gameservergroup.gsgcollectors.integration.impl.ShopGUIPlusImpl;
import com.gameservergroup.gsgcollectors.units.UnitCollectors;
import com.gameservergroup.gsgcore.plugin.Module;

public class GSGCollectors extends Module {

    private static GSGCollectors instance;
    private UnitCollectors unitCollectors;
    private ShopGUIPlusIntegration shopGUIPlusIntegration;
    private SellPriceModifier sellPriceModifier;

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
            this.sellPriceModifier = new AceOutpostsSellPriceModifier();
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

    public SellPriceModifier getSellPriceModifier() {
        return sellPriceModifier;
    }
}
