package com.massivecraft.factions.zcore.factionchest;

import com.gameservergroup.gsgcore.utils.Text;
import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class FactionChest implements InventoryHolder {

    private Inventory inventory;
    private int rows;

    protected FactionChest() {
    }

    public FactionChest(int rows) {
        this.rows = rows;
        this.inventory = Bukkit.createInventory(this, rows * 9, Text.toColor(P.p.getConfig().getString("faction-chest-title", "&eFaction Chest")));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getRows() {
        if (rows == 0) {
            rows = 3;
        }
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        final ItemStack[] contents = inventory.getContents() == null ? new ItemStack[0] : inventory.getContents();
        Inventory upgradedInventory = Bukkit.createInventory(this, rows * 9, Text.toColor(P.p.getConfig().getString("faction-chest-title", "&eFaction Chest")));
        upgradedInventory.setContents(contents);
        this.inventory = upgradedInventory;
        for (HumanEntity viewer : inventory.getViewers()) {
            viewer.closeInventory();
            viewer.openInventory(getInventory());
        }
    }
}
