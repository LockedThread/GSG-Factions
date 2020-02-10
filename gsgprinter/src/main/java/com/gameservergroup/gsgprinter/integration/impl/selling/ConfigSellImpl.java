package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.gameservergroup.gsgcore.relocations.fastutil.ints.Int2DoubleMap;
import com.gameservergroup.gsgcore.relocations.fastutil.ints.Int2DoubleOpenHashMap;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import com.gameservergroup.gsgprinter.units.UnitPrinter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class ConfigSellImpl implements SellIntegration {

    private Int2DoubleMap prices;
    private final double defaultPrice;

    public ConfigSellImpl(File dir, String fileName) {
        YamlConfiguration yamlConfiguration;
        this.defaultPrice = GSGPrinter.getInstance().getConfig().getBoolean("default-price.enabled") ? GSGPrinter.getInstance().getConfig().getDouble("default-price.price") : -1;
        File file = new File(dir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                this.prices = new Int2DoubleOpenHashMap();
                yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                for (Material material : Material.values()) {
                    if (material.isBlock() && !UnitPrinter.getBannedInteractables().contains(material)) {
                        yamlConfiguration.set(material.name().toLowerCase().replace("_", "-"), 1.0);
                        prices.put(material.getId(), 0.0);
                    }
                }
                prices.put(Material.TRIPWIRE.getId(), 1.0);
                yamlConfiguration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            this.prices = yamlConfiguration.getKeys(false).stream().collect(Collectors.toMap(key -> Material.matchMaterial(key.replace("-", "_").toUpperCase()).getId(), key -> yamlConfiguration.getDouble(key), (a, b) -> b, Int2DoubleOpenHashMap::new));
            double v = prices.get(Material.STRING.getId());
            if (v != 0.0) {
                prices.put(Material.TRIPWIRE.getId(), v);
            }
            GSGPrinter.getInstance().getLogger().info("Successfully loaded " + prices.size() + " blocks into PrinterMode!");
        }
    }

    @Override
    public double getBuyPrice(ItemStack itemStack) {
        return getBuyPrice(itemStack.getType());
    }

    @Override
    public double getBuyPrice(Material material) {
        if (defaultPrice == -1) {
            return prices.getOrDefault(material.getId(), 0.0);
        }
        return prices.getOrDefault(material.getId(), defaultPrice);
    }
}
