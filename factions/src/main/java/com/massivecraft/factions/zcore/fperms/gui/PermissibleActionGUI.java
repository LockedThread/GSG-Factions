package com.massivecraft.factions.zcore.fperms.gui;

import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.relocations.fastutil.ints.Int2ObjectOpenHashMap;
import com.gameservergroup.gsgcore.utils.Text;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PermissibleActionGUI extends Menu {

    private static final ConfigurationSection ACTION_SECTION = P.p.getConfig().getConfigurationSection("fperm-gui.action");
    private static Int2ObjectOpenHashMap<ItemStack> DUMMY_ITEMS;

    private final Faction faction;
    private final Permissable permissable;

    public PermissibleActionGUI(Faction faction, Permissable permissable) {
        super(ACTION_SECTION.getString("name", "FactionPerms"), Math.min(ACTION_SECTION.getInt("rows", 3), 5) * 9);
        this.faction = faction;
        this.permissable = permissable;
        initialize();
    }

    private static void buildDummyItems() {
        if (ACTION_SECTION == null) {
            P.p.log(Level.WARNING, "Attempted to build f perm GUI but config section not present.");
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return;
        }

        for (String key : ACTION_SECTION.getConfigurationSection("dummy-items").getKeys(false)) {
            int dummyId;
            try {
                dummyId = Integer.parseInt(key);
            } catch (NumberFormatException exception) {
                P.p.log(Level.WARNING, "Invalid dummy item id: " + key.toUpperCase());
                continue;
            }

            ItemStack dummyItem = buildDummyItem(dummyId);
            if (dummyItem == null) {
                continue;
            }

            ItemMeta meta = dummyItem.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            dummyItem.setItemMeta(meta);

            int rows = Math.min(ACTION_SECTION.getInt("rows", 3), 5) * 9;
            for (int slot : ACTION_SECTION.getIntegerList("dummy-items." + key)) {
                if (slot + 1 > rows || slot < 0) {
                    P.p.log(Level.WARNING, "Invalid slot: " + slot + " for dummy item: " + key);
                    continue;
                }
                DUMMY_ITEMS.put(slot, dummyItem);
            }
        }
    }

    private static ItemStack buildDummyItem(int id) {
        final ConfigurationSection dummySection = P.p.getConfig().getConfigurationSection("fperm-gui.dummy-items." + id);

        if (dummySection == null) {
            P.p.log(Level.WARNING, "Attempted to build dummy items for F PERM GUI but config section not present.");
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return new ItemStack(Material.AIR);
        }

        return PermissibleActionGUI.getDummyItemStack(id, dummySection);
    }

    public static ItemStack getDummyItemStack(int id, ConfigurationSection dummySection) {
        Material material = Material.matchMaterial(dummySection.getString("material", ""));
        if (material == null) {
            P.p.log(Level.WARNING, "Invalid material for dummy item: " + id);
            return null;
        }

        ItemStack itemStack = new ItemStack(material);

        DyeColor color;
        try {
            color = DyeColor.valueOf(dummySection.getString("color", ""));
        } catch (IllegalArgumentException exception) {
            color = null;
        }
        if (color != null) {
            itemStack.setDurability(color.getWoolData());
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Text.toColor(dummySection.getString("name", " ")));
        List<String> lore = dummySection.getStringList("lore")
                .stream()
                .map(Text::toColor)
                .collect(Collectors.toList());
        itemMeta.setLore(lore);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void initialize() {
        for (String key : ACTION_SECTION.getConfigurationSection("slots").getKeys(false)) {
            int slot = ACTION_SECTION.getInt("slots." + key);
            if (slot + 1 > this.getInventory().getSize() || slot < 0) {
                P.p.log(Level.WARNING, "Invalid slot for: " + key.toUpperCase());
                continue;
            }

            if (key.equalsIgnoreCase("back")) {
                ConfigurationSection backButtonConfig = P.p.getConfig().getConfigurationSection("fperm-gui.back-item");

                ItemStack backButton = new ItemStack(Material.matchMaterial(backButtonConfig.getString("material")));
                ItemMeta backButtonMeta = backButton.getItemMeta();

                backButtonMeta.setDisplayName(Text.toColor(backButtonConfig.getString("name")));
                List<String> lore = backButtonConfig.getStringList("lore")
                        .stream()
                        .map(Text::toColor)
                        .collect(Collectors.toList());

                backButtonMeta.setLore(lore);
                backButtonMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

                backButton.setItemMeta(backButtonMeta);

                setItem(slot, MenuItem.of(backButton).setInventoryClickEventConsumer(event -> {
                    event.getWhoClicked().openInventory(PermissibleRelationGUI.getInstance().getInventory());
                    event.setCancelled(true);
                }));
            } else if (key.equalsIgnoreCase("relation")) {
                setItem(slot, permissable.buildItem());
            } else {
                PermissableAction permissableAction = PermissableAction.fromString(key.toUpperCase().replace('-', '_'));
                if (permissableAction == null) {
                    P.p.log(Level.WARNING, "Invalid permissable action: " + key.toUpperCase());
                    continue;
                }

                ItemStack item = permissableAction.buildItem(faction, permissable);

                if (item == null) {
                    P.p.log(Level.WARNING, "Invalid item for: " + permissableAction.toString().toUpperCase());
                    continue;
                }
                setItem(slot, MenuItem.of(item).setInventoryClickEventConsumer(event -> {
                    event.setCancelled(true);
                    Access access;
                    switch (event.getClick()) {
                        case LEFT:
                            access = Access.ALLOW;
                            break;
                        case RIGHT:
                            access = Access.DENY;
                            break;
                        case MIDDLE:
                            access = Access.UNDEFINED;
                            break;
                        default:
                            return;
                    }
                    Map<PermissableAction, Access> permissableActionAccessMap = faction.getPermissions().get(permissable);
                    if (permissableActionAccessMap != null) {
                        Access cachedAccess = permissableActionAccessMap.get(permissableAction);
                        if (cachedAccess == access) {
                            return;
                        }
                    }

                    faction.setPermission(permissable, permissableAction, access);

                    ItemStack itemStack = permissableAction.buildItem(faction, permissable);
                    this.getInventory().setItem(slot, itemStack);
                    this.getMenuItem(slot).get().setItemStack(itemStack);
                    event.getWhoClicked().sendMessage(TL.COMMAND_PERM_SET.format(permissableAction.name(), access.name(), permissable.name()));
                    P.p.log(TL.COMMAND_PERM_SET.format(permissableAction.name(), access.name(), permissable.name()) + " for faction " + faction.getTag());
                }));
            }
        }
        if (DUMMY_ITEMS == null) {
            DUMMY_ITEMS = new Int2ObjectOpenHashMap<>();
            buildDummyItems();
        }
        DUMMY_ITEMS.forEach(this::setItem);
    }
}
