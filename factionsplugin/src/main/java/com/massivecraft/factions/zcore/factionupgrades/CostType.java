package com.massivecraft.factions.zcore.factionupgrades;

import com.massivecraft.factions.integration.Econ;
import org.bukkit.entity.Player;

public enum CostType implements UpgradePurchase {

    MONEY, XP_LEVELS, EXP;

    public static CostType fromString(String s) {
        try {
            return valueOf(s.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            for (CostType costType : values()) {
                if (costType.name().equalsIgnoreCase(s.replace("-", "_"))) {
                    return costType;
                }
            }
        }
        return null;
    }

    @Override
    public boolean purchase(Player player, double cost) {
        switch (this) {
            case MONEY:
                return Econ.withdraw(player.getName(), cost);
            case XP_LEVELS:
                if (player.getLevel() >= (int) cost) {
                    player.setLevel(player.getLevel() - (int) cost);
                    return true;
                }
                break;
            case EXP:
                if (player.getTotalExperience() >= cost) {
                    player.setTotalExperience(player.getTotalExperience() - (int) cost);
                    return true;
                }
                break;
        }
        return false;
    }
}
