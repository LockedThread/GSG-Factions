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

import java.util.ArrayList;
import java.util.List;

public enum Role implements Permissable {
    ADMIN(4, TL.ROLE_ADMIN),
    COLEADER(3, TL.ROLE_COLEADER),
    MODERATOR(2, TL.ROLE_MODERATOR),
    NORMAL(1, TL.ROLE_NORMAL),
    RECRUIT(0, TL.ROLE_RECRUIT);

    public final int value;
    public final String nicename;
    public final TL translation;


    Role(final int value, final TL translation) {
        this.value = value;
        this.nicename = translation.toString();
        this.translation = translation;
    }

    public static com.massivecraft.factions.struct.Role getRelative(com.massivecraft.factions.struct.Role role, int relative) {
        return getByValue(role.value + relative);
    }

    public static com.massivecraft.factions.struct.Role getByValue(int value) {
        switch (value) {
            case 0:
                return RECRUIT;
            case 1:
                return NORMAL;
            case 2:
                return MODERATOR;
            case 3:
                return COLEADER;
            case 4:
                return ADMIN;
        }

        return null;
    }

    public static com.massivecraft.factions.struct.Role fromString(String check) {
        switch (check.toLowerCase()) {
            case "leader":
            case "owner":
            case "admin":
                return ADMIN;
            case "coleader":
            case "coowner":
                return COLEADER;
            case "mod":
            case "moderator":
                return MODERATOR;
            case "normal":
            case "member":
                return NORMAL;
            case "recruit":
            case "rec":
                return RECRUIT;
        }

        return null;
    }

    public boolean isAtLeast(com.massivecraft.factions.struct.Role role) {
        return this.value >= role.value;
    }

    public boolean isAtMost(com.massivecraft.factions.struct.Role role) {
        return this.value <= role.value;
    }

    @Override
    public String toString() {
        return this.nicename;
    }

    public TL getTranslation() {
        return translation;
    }

    public String getPrefix() {
        if (this == ADMIN) {
            return Conf.prefixAdmin;
        }

        if (this == COLEADER) {
            return Conf.prefixColeader;
        }

        if (this == MODERATOR) {
            return Conf.prefixMod;
        }

        if (this == NORMAL) {
            return Conf.prefixNormal;
        }

        if (this == RECRUIT) {
            return Conf.prefixRecruit;
        }

        return "";
    }

    // Utility method to build items for F Perm GUI
    @Override
    public ItemStack buildItem() {
        final ConfigurationSection RELATION_CONFIG = P.p.getConfig().getConfigurationSection("fperm-gui.relation");

        String displayName = replacePlaceholders(RELATION_CONFIG.getString("placeholder-item.name", ""));
        List<String> lore = new ArrayList<>();

        Material material = Material.matchMaterial(RELATION_CONFIG.getString("materials." + name().toLowerCase(), "STAINED_CLAY"));
        if (material == null) {
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        for (String loreLine : RELATION_CONFIG.getStringList("placeholder-item.lore")) {
            lore.add(replacePlaceholders(loreLine));
        }

        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String replacePlaceholders(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);

        String permissableName = nicename.substring(0, 1).toUpperCase() + nicename.substring(1);

        string = string.replace("{relation-color}", ChatColor.GREEN.toString());
        string = string.replace("{relation}", permissableName);

        return string;
    }

}
