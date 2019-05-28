package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import com.gameservergroup.gsgprinter.units.UnitPrinter;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class ConfigSellImpl implements SellIntegration {

    private YamlConfiguration yamlConfiguration;
    private Int2DoubleMap prices;

    public ConfigSellImpl(File dir, String fileName) {
        File file = new File(dir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                this.prices = new Int2DoubleOpenHashMap();
                this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
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
            this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            this.prices = yamlConfiguration.getKeys(false).stream().collect(Collectors.toMap(key -> Material.matchMaterial(key.replace("-", "_").toUpperCase()).getId(), key -> yamlConfiguration.getDouble(key), (a, b) -> b, Int2DoubleOpenHashMap::new));
            GSGPrinter.getInstance().getLogger().info("Successfully loaded " + prices.size() + " blocks into PrinterMode!");
        }
    }

    @Override
    public double getBuyPrice(ItemStack itemStack) {
        return getBuyPrice(itemStack.getType());
    }

    @Override
    public double getBuyPrice(Material material) {
        return prices.getOrDefault(material.getId(), 0.0);
    }
}
