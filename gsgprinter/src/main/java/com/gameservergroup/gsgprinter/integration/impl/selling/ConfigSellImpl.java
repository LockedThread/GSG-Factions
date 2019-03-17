package com.gameservergroup.gsgprinter.integration.impl.selling;

import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.integration.SellIntegration;
import com.gameservergroup.gsgprinter.units.UnitPrinter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.stream.Collectors;

public class ConfigSellImpl implements SellIntegration {

    private File file;
    private YamlConfiguration yamlConfiguration;
    private EnumMap<Material, Double> prices;

    public ConfigSellImpl(File dir, String fileName) {
        this.file = new File(dir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                this.prices = new EnumMap<>(Material.class);
                this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                for (Material material : Material.values()) {
                    if (material.isBlock() && !UnitPrinter.getBannedInteractables().contains(material)) {
                        yamlConfiguration.set(material.name().toLowerCase().replace("_", "-"), 1.0);
                        prices.put(material, 0.0);
                    }
                }
                prices.put(Material.TRIPWIRE, 1.0);
                yamlConfiguration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            this.prices = yamlConfiguration.getKeys(false).stream().collect(Collectors.toMap(key -> Material.matchMaterial(key.replace("-", "_").toUpperCase()), key -> yamlConfiguration.getDouble(key), (a, b) -> b, () -> new EnumMap<>(Material.class)));
            GSGPrinter.getInstance().getLogger().info("Successfully loaded " + prices.size() + " blocks into PrinterMode!");
        }
    }

    @Override
    public double getBuyPrice(ItemStack itemStack) {
        return getBuyPrice(itemStack.getType());
    }

    @Override
    public double getBuyPrice(Material material) {
        return prices.getOrDefault(material, 0.0);
    }
}
