package com.massivecraft.factions.zcore.fperms.gui;

import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.gameservergroup.gsgcore.relocations.fastutil.ints.Int2ObjectOpenHashMap;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Permissable;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;


public class PermissibleRelationGUI extends Menu {

    private static final ConfigurationSection RELATION_SECTION = P.p.getConfig().getConfigurationSection("fperm-gui.relation");
    private static Int2ObjectOpenHashMap<ItemStack> DUMMY_ITEMS;
    private static PermissibleRelationGUI instance;

    public PermissibleRelationGUI() {
        super(RELATION_SECTION.getString("name", "FactionPermissions"), Math.min(RELATION_SECTION.getInt("rows", 3), 5) * 9);
        initialize();
    }

    @Override
    public void initialize() {
        for (String key : RELATION_SECTION.getConfigurationSection("slots").getKeys(false)) {
            int slot = RELATION_SECTION.getInt("slots." + key);
            if (slot + 1 > this.getInventory().getSize() && slot > 0) {
                P.p.log(Level.WARNING, "Invalid slot of " + key.toUpperCase() + " in relation GUI skipping it");
                continue;
            }

            Permissable permissable = getPermissable(key);
            if (permissable == null) {
                P.p.log(Level.WARNING, "Invalid permissable " + key.toUpperCase() + " skipping it");
                continue;
            }

            ItemStack item = permissable.buildItem();

            if (item == null) {
                P.p.log(Level.WARNING, "Invalid material for " + permissable.toString().toUpperCase() + " skipping it");
                continue;
            }

            setItem(slot, MenuItem.of(item).setInventoryClickEventConsumer(event -> {
                PermissibleActionGUI actionGUI = new PermissibleActionGUI(FPlayers.getInstance().getByPlayer((Player) event.getWhoClicked()).getFaction(), permissable);
                event.getWhoClicked().openInventory(actionGUI.getInventory());
                event.setCancelled(true);
            }));
        }
        if (DUMMY_ITEMS == null) {
            DUMMY_ITEMS = new Int2ObjectOpenHashMap<>();
            buildDummyItems();
        }
        DUMMY_ITEMS.forEach(this::setItem);
    }

    private static Permissable getPermissable(String name) {
        Role role = Role.fromString(name.toUpperCase());
        return role != null ? role : Relation.fromString(name.toUpperCase());
    }

    private static void buildDummyItems() {
        for (String key : RELATION_SECTION.getConfigurationSection("dummy-items").getKeys(false)) {
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

            int rows = Math.min(RELATION_SECTION.getInt("rows", 3), 5) * 9;
            for (int slot : RELATION_SECTION.getIntegerList("dummy-items." + key)) {
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
            P.p.log(Level.WARNING, "Attempted to build f perm GUI but config section not present.");
            P.p.log(Level.WARNING, "Copy your config, allow the section to generate, then copy it back to your old config.");
            return new ItemStack(Material.AIR);
        }

        return PermissibleActionGUI.getDummyItemStack(id, dummySection);
    }

    public static PermissibleRelationGUI getInstance() {
        return instance == null ? instance = new PermissibleRelationGUI() : instance;
    }
}
