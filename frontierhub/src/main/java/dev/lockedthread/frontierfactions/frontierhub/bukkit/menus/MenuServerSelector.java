package dev.lockedthread.frontierfactions.frontierhub.bukkit.menus;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import dev.lockedthread.frontierfactions.frontierhub.bukkit.FrontierHubBukkit;
import dev.lockedthread.frontierfactions.frontierhub.bukkit.items.ServerMenuItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class MenuServerSelector extends Menu {

    public MenuServerSelector() {
        super(FrontierHubBukkit.getInstance().getConfig().getString("server-selector.menu.title"), FrontierHubBukkit.getInstance().getConfig().getInt("server-selector.menu.size"));
        initialize();
    }

    @Override
    public void initialize() {
        ConfigurationSection section = FrontierHubBukkit.getInstance().getConfig().getConfigurationSection("server-selector.menu.items");
        for (String key : section.getKeys(false)) {
            setItem(section.getInt(key + ".slot"), new ServerMenuItem(ItemStackBuilder.of(section.getConfigurationSection(key)).build(), section.getString("options.queue-join")));
        }
        if (FrontierHubBukkit.getInstance().getConfig().getBoolean("server-selector.menu.fill.enabled")) {
            DyeColor dyeColor = DyeColor.valueOf(FrontierHubBukkit.getInstance().getConfig().getString("server-selector.menu.fill.glass-pane-color").toUpperCase());
            ItemStackBuilder itemStackBuilder = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).setDyeColor(dyeColor).setDisplayName(" ");
            if (FrontierHubBukkit.getInstance().getConfig().getBoolean("server-selector.menu.fill.enchanted")) {
                itemStackBuilder.addEnchant(Enchantment.DURABILITY, 1);
                itemStackBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            while (getInventory().firstEmpty() != -1) {
                setItem(getInventory().firstEmpty(), itemStackBuilder.build());
            }
        }
    }
}
