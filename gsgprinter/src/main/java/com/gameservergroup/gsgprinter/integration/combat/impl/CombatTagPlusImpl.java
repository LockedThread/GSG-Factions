package com.gameservergroup.gsgprinter.integration.combat.impl;

import com.gameservergroup.gsgprinter.integration.CombatIntegration;
import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CombatTagPlusImpl implements CombatIntegration {

    private CombatTagPlus combatTagPlus;

    public CombatTagPlusImpl(Plugin plugin) {
        if (plugin instanceof CombatTagPlus) {
            this.combatTagPlus = (CombatTagPlus) plugin;
        } else {
            throw new RuntimeException("Unable to parse plugin as CombatTagPlus, do you have the wrong version?");
        }
    }

    @Override
    public boolean isTagged(Player player) {
        return combatTagPlus.getTagManager().isTagged(player.getUniqueId());
    }
}
