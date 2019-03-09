package com.gameservergroup.gsgcore.items;

import org.bukkit.event.block.Action;

public enum CustomItemType {

    RIGHT_CLICK_AIR,
    RIGHT_CLICK_BLOCK,
    LEFT_CLICK_BLOCK,
    LEFT_CLICK_AIR,
    BLOCK_BREAK,
    BLOCK_PLACE,
    ENTITY_DAMAGE,
    BUCKET_EMPTY;

    public static CustomItemType fromAction(Action action) {
        try {
            return valueOf(action.name());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }
}
