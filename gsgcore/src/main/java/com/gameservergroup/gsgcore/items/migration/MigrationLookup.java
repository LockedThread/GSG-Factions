package com.gameservergroup.gsgcore.items.migration;

import org.bukkit.inventory.ItemStack;

public interface MigrationLookup {

    boolean isMigratableItemStack(ItemStack itemStack, Migration migration);
}
