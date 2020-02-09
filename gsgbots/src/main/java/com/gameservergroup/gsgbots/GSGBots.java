package com.gameservergroup.gsgbots;

import com.gameservergroup.gsgbots.entities.EntityBot;
import com.gameservergroup.gsgbots.menus.MenuBot;
import com.gameservergroup.gsgbots.objs.Bot;
import com.gameservergroup.gsgbots.units.UnitBots;
import com.gameservergroup.gsgcore.plugin.Module;
import org.bukkit.Material;
import org.bukkit.entity.Villager;

public class GSGBots extends Module {

    private static GSGBots instance;

    public static GSGBots getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

        saveDefaultConfig();
        registerUnits(new UnitBots());

        EntityBot.setVillagerProfession(Villager.Profession.valueOf(getConfig().getString("bot.entity.villager-profession")));
        EntityBot.registerEntityBot();
        MenuBot.MenuItems.init();
        Bot.setSearchMaterial(Material.matchMaterial(getConfig().getString("bot.search-material")));
        Bot.setSearchRadius(getConfig().getInt("bot.search-radius"));
        MenuBot.setSandPrice(getConfig().getDouble("bot.sand-price"));
    }

    @Override
    public void disable() {
        instance = null;
    }
}
