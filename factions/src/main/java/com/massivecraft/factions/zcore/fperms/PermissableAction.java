package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public enum PermissableAction {
    ALTS("alts"),
    BAN("ban"),
    BUILD("build"),
    DESTROY("destroy"),
    PAIN_BUILD("pain build"),
    DOOR("door"),
    BUTTON("button"),
    LEVER("lever"),
    CONTAINER("container"),
    INVITE("invite"),
    KICK("kick"),
    ITEM("items"), // generic for most items
    SETHOME("Faction Sethome"),
    WITHDRAW("withdraw"),
    TERRITORY("territory"),
    ACCESS("access"),
    PROMOTE("promote"),
    SETWARP("setwarp"),
    WARP("warp"),
    FLY("fly"),
    CHEST("chest"),
    TAG("tag"),
    TNTBANK("TNTBank"),
    TNTFILL("TNTFill"),
    F_PERMS("F Perms"),
    CHECK("F Check"),
    INSPECT("F Inspect"),
    F_DRAIN_TOGGLE("F Drain Toggle"),
    ;

    private String name;

    PermissableAction(String name) {
        this.name = name;
    }

    /**
     * Case insensitive check for action.
     *
     * @param check
     * @return
     */
    public static PermissableAction fromString(String check) {
        for (PermissableAction permissableAction : values()) {
            if (permissableAction.name().equalsIgnoreCase(check)) {
                if (permissableAction == PermissableAction.F_DRAIN_TOGGLE && !P.p.drainingEnabled) {
                    return null;
                }
                return permissableAction;
            }
        }

        return null;
    }

    // Utility method to build items for F Perm GUI
    public ItemStack buildItem(Faction faction, Permissable permissable) {
        final ConfigurationSection section = P.p.getConfig().getConfigurationSection("fperm-gui.action");

        if (section == null) {
            P.p.log(Level.WARNING, "Attempted to build f perm GUI but config section not present.");
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return new ItemStack(Material.AIR);
        }

        String displayName = replacePlaceholers(section.getString("placeholder-item.name"), faction, permissable);

        if (section.getString("materials." + name().toLowerCase().replace('_', '-')) == null) {
            return null;
        }
        Material material = Material.matchMaterial(section.getString("materials." + name().toLowerCase().replace('_', '-')));
        if (material == null) {
            material = Material.STAINED_CLAY;
        }

        Access access = faction.getAccess(permissable, this);
        if (access == null) {
            access = Access.UNDEFINED;
        }
        DyeColor dyeColor = null;
        try {
            dyeColor = DyeColor.valueOf(section.getString("access." + access.name().toLowerCase()));
        } catch (Exception ignored) {
        }

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        if (dyeColor != null) {
            item.setDurability(dyeColor.getWoolData());
        }

        List<String> lore = section.getStringList("placeholder-item.lore").stream().map(loreLine -> replacePlaceholers(loreLine, faction, permissable)).collect(Collectors.toList());

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    /**
     * Get the friendly name of this action. Used for editing in commands.
     *
     * @return friendly name of the action as a String.
     */
    public String getName() {
        return this.name;
    }

    public String replacePlaceholers(String string, Faction faction, Permissable permissable) {
        // Run Permissable placeholders
        string = permissable.replacePlaceholders(string);

        String actionName = name.substring(0, 1).toUpperCase() + name.substring(1);
        string = string.replace("{action}", actionName);

        Access access = faction.getAccess(permissable, this);
        if (access == null) {
            access = Access.UNDEFINED;
        }
        String actionAccess = access.getName();
        string = string.replace("{action-access}", actionAccess);
        string = string.replace("{action-access-color}", access.getColor().toString());

        return string;
    }

    @Override
    public String toString() {
        return name;
    }

}
