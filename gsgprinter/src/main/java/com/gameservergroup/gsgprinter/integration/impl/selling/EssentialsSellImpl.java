package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Worth;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Locale;

public class EssentialsSellImpl implements SellIntegration {

    private EssentialsConf config;
    private IEssentials iEssentials;

    public EssentialsSellImpl(Plugin plugin) {

        if (plugin instanceof IEssentials) {
            this.iEssentials = (IEssentials) plugin;
        } else {
            GSGPrinter.getInstance().getPluginLoader().disablePlugin(GSGPrinter.getInstance());
            throw new RuntimeException("Unable to parse plugin as Essentials, do you have the wrong version?");
        }
        try {
            Worth worth = this.iEssentials.getWorth();
            Field field = worth.getClass().getField("config");
            field.setAccessible(true);
            config = (EssentialsConf) field.get(worth);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException("Unable to access Worth.class, field \"config\".", ex);
        }
    }


    public double getPrice(Material material) {
        ConfigurationSection configurationSection = this.config.getConfigurationSection("worth." + material.toString().toLowerCase(Locale.ENGLISH).replace("_", ""));
        return configurationSection.getKeys(false).size() == 0 ? 0.0 : (double) configurationSection.getValues(false).entrySet().iterator().next().getValue();
    }

    @Override
    public double getBuyPrice(Material material) {
        return getPrice(material);
    }

    @Override
    public double getBuyPrice(ItemStack itemStack) {
        BigDecimal price = iEssentials.getWorth().getPrice(iEssentials, itemStack);
        return price == null ? 0.00 : price.doubleValue();
    }
}
