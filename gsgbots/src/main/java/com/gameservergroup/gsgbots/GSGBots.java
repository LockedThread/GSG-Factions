package com.gameservergroup.gsgbots;

import com.gameservergroup.gsgbots.entities.EntityBot;
import com.gameservergroup.gsgcore.plugin.Module;
import org.bukkit.entity.Villager;

public class GSGBots extends Module {

    private static GSGBots instance;

    public static GSGBots getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

        EntityBot.setVillagerProfession(Villager.Profession.valueOf(getConfig().getString("bot.entity.villager-profession")));
        EntityBot.registerEntityBot();
    }

    @Override
    public void disable() {
        instance = null;
    }
}
