package com.gameservergroup.gsgprinter.integration.impl.combat;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.gameservergroup.gsgprinter.integration.CombatIntegration;
import org.bukkit.entity.Player;

public class CombatLogXImpl implements CombatIntegration {

    @Override
    public boolean isTagged(Player player) {
        return CombatUtil.isInCombat(player);
    }
}
