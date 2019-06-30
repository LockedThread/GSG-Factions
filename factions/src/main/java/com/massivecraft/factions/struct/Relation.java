package com.massivecraft.factions.struct;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;


public enum Relation implements Permissable {
    MEMBER(4, TL.RELATION_MEMBER_SINGULAR.toString()),
    ALLY(3, TL.RELATION_ALLY_SINGULAR.toString()),
    TRUCE(2, TL.RELATION_TRUCE_SINGULAR.toString()),
    NEUTRAL(1, TL.RELATION_NEUTRAL_SINGULAR.toString()),
    ENEMY(0, TL.RELATION_ENEMY_SINGULAR.toString());

    public final int value;
    public final String nicename;

    Relation(final int value, final String nicename) {
        this.value = value;
        this.nicename = nicename;
    }

    public static com.massivecraft.factions.struct.Relation fromString(String s) {
        /*
        // Because Java 6 doesn't allow String switches :(
        if (s.equalsIgnoreCase(MEMBER.nicename)) {
            return MEMBER;
        } else if (s.equalsIgnoreCase(ALLY.nicename)) {
            return ALLY;
        } else if (s.equalsIgnoreCase(TRUCE.nicename)) {
            return TRUCE;
        } else if (s.equalsIgnoreCase(ENEMY.nicename)) {
            return ENEMY;
        } else {
            return NEUTRAL; // If they somehow mess things up, go back to default behavior.
        }*/

        // The above is the old method which is quite redundant. Just replaced with a for.
        for (Relation relation : values()) {
            if (relation.nicename.equalsIgnoreCase(s)) {
                return relation;
            }
        }
        return NEUTRAL;
    }

    @Override
    public String toString() {
        return this.nicename;
    }

    public String getTranslation() {
        try {
            return TL.valueOf("RELATION_" + name() + "_SINGULAR").toString();
        } catch (IllegalArgumentException e) {
            return toString();
        }
    }

    public String getPluralTranslation() {
        for (TL t : TL.values()) {
            if (t.name().equalsIgnoreCase("RELATION_" + name() + "_PLURAL")) {
                return t.toString();
            }
        }
        return toString();
    }

    public boolean isMember() {
        return this == MEMBER;
    }

    public boolean isAlly() {
        return this == ALLY;
    }

    public boolean isTruce() {
        return this == TRUCE;
    }

    public boolean isNeutral() {
        return this == NEUTRAL;
    }

    public boolean isEnemy() {
        return this == ENEMY;
    }

    public boolean isAtLeast(com.massivecraft.factions.struct.Relation relation) {
        return this.value >= relation.value;
    }

    public boolean isAtMost(com.massivecraft.factions.struct.Relation relation) {
        return this.value <= relation.value;
    }

    public ChatColor getColor() {
        // Replaced with ternary expression
        return this == MEMBER ? Conf.colorMember : this == ALLY ? Conf.colorAlly : this == NEUTRAL ? Conf.colorNeutral : this == TRUCE ? Conf.colorTruce : Conf.colorEnemy;
    }

    // return appropriate Conf setting for DenyBuild based on this relation and their online status
    public boolean confDenyBuild(boolean online) {
        // Replaced with ternary expression
        return !isMember() && (online ? isEnemy() ? Conf.territoryEnemyDenyBuild : isAlly() ? Conf.territoryAllyDenyBuild : isTruce() ? Conf.territoryTruceDenyBuild : Conf.territoryDenyBuild : isEnemy() ? Conf.territoryEnemyDenyBuildWhenOffline : isAlly() ? Conf.territoryAllyDenyBuildWhenOffline : isTruce() ? Conf.territoryTruceDenyBuildWhenOffline : Conf.territoryDenyBuildWhenOffline);
    }

    // return appropriate Conf setting for PainBuild based on this relation and their online status
    public boolean confPainBuild(boolean online) {
        return !isMember() && (online ? isEnemy() ? Conf.territoryEnemyPainBuild : isAlly() ? Conf.territoryAllyPainBuild : isTruce() ? Conf.territoryTrucePainBuild : Conf.territoryPainBuild : isEnemy() ? Conf.territoryEnemyPainBuildWhenOffline : isAlly() ? Conf.territoryAllyPainBuildWhenOffline : isTruce() ? Conf.territoryTrucePainBuildWhenOffline : Conf.territoryPainBuildWhenOffline);

    }

    // return appropriate Conf setting for DenyUseage based on this relation
    public boolean confDenyUseage() {
        return !isMember() && (isEnemy() ? Conf.territoryEnemyDenyUseage : isAlly() ? Conf.territoryAllyDenyUseage : isTruce() ? Conf.territoryTruceDenyUseage : Conf.territoryDenyUseage);
    }

    public double getRelationCost() {
        return isEnemy() ? Conf.econCostEnemy : isAlly() ? Conf.econCostAlly : isTruce() ? Conf.econCostTruce : Conf.econCostNeutral;
    }

    // Utility method to build items for F Perm GUI
    @Override
    public ItemStack buildItem() {
        final ConfigurationSection RELATION_CONFIG = P.p.getConfig().getConfigurationSection("fperm-gui.relation");

        String displayName = replacePlaceholders(RELATION_CONFIG.getString("placeholder-item.name", ""));

        Material material = Material.matchMaterial(RELATION_CONFIG.getString("materials." + name().toLowerCase()));
        if (material == null) {
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(RELATION_CONFIG.getStringList("placeholder-item.lore").stream().map(this::replacePlaceholders).collect(Collectors.toList()));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String replacePlaceholders(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);

        String permissableName = nicename.substring(0, 1).toUpperCase() + nicename.substring(1);

        string = string.replace("{relation-color}", getColor().toString());
        string = string.replace("{relation}", permissableName);

        return string;
    }
}
