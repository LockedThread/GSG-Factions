package com.gameservergroup.gsgcore.items.migration;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public enum MigrationType implements MigrationLookup {

    NAME,
    NBT_TAG,
    ;

    @Override
    public boolean isMigratableItemStack(ItemStack itemStack, Migration migration) {
        switch (this) {
            case NAME:
                return itemStack.hasItemMeta() &&
                        itemStack.getItemMeta().hasDisplayName() &&
                        ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(Joiner.on(" ").skipNulls().join(migration.getArguments())));
            case NBT_TAG:
            default:
                return false;
        }
    }
}
