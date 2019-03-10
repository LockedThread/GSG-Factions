package com.gameservergroup.gsggen.menu;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.objs.Gen;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class GenMenu extends Menu {

    private static final GSGGen GSG_GEN = GSGGen.getInstance();

    public GenMenu() {
        super(GSG_GEN.getConfig().getString("menu.name"), GSG_GEN.getConfig().getInt("menu.size"));
    }

    @Override
    public void initialize() {
        for (String key : GSG_GEN.getConfig().getConfigurationSection("menu.items").getKeys(false)) {
            Gen gen = GSG_GEN.getUnitGen().getGenHashMap().get(GSG_GEN.getConfig().getString("menu.items." + key));
            if (gen != null) {
                setItem(Integer.parseInt(key), gen.getMenuItem());
            } else {
                throw new RuntimeException("Unable to find Gen called \"" + key + "\"");
            }
        }
        if (GSG_GEN.getConfig().getBoolean("menu.fill.enabled")) {
            DyeColor dyeColor = DyeColor.valueOf(GSG_GEN.getConfig().getString("menu.fill.glass-pane-color").toUpperCase());
            ItemStackBuilder itemStackBuilder = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).setDyeColor(dyeColor).setDisplayName(" ");
            if (GSG_GEN.getConfig().getBoolean("menu.fill.enchanted")) {
                itemStackBuilder.addEnchant(Enchantment.DURABILITY, 1);
                itemStackBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            setItem(getInventory().firstEmpty(), itemStackBuilder.build());
        }
    }
}
