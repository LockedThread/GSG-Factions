package com.massivecraft.factions.units;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.units.Unit;
import com.massivecraft.factions.P;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class UnitFactionMissions extends Unit {

    private static UnitFactionMissions instance;

    private MenuItem fillMenuItem;

    public UnitFactionMissions() {
        instance = this;
    }

    @Override
    public void setup() {
        if (P.p.getConfig().getBoolean("faction-missions.menus.root.fill.enabled")) {
            fillMenuItem = MenuItem.of(P.p.getConfig().getBoolean("faction-missions.menus.root.fill.enchanted") ? ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                    .setDyeColor(DyeColor.valueOf(P.p.getConfig().getString("faction-missions.menus.root.fill.color")))
                    .setDisplayName(" ")
                    .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    .addEnchant(Enchantment.DURABILITY, 1)
                    .build() : ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                    .setDyeColor(DyeColor.valueOf(P.p.getConfig().getString("faction-missions.menus.root.fill.color")))
                    .setDisplayName(" ")
                    .build());
        }
    }

    public MenuItem getFillMenuItem() {
        return fillMenuItem;
    }

    public static UnitFactionMissions getInstance() {
        return instance;
    }
}
