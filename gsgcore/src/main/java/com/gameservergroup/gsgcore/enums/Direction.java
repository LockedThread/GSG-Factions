package com.gameservergroup.gsgcore.enums;

import org.bukkit.block.BlockFace;

public enum Direction {

    VERTICAL_UP(BlockFace.UP),
    VERTICAL_DOWN(BlockFace.DOWN),
    NORTH(BlockFace.NORTH),
    SOUTH(BlockFace.SOUTH),
    EAST(BlockFace.EAST),
    WEST(BlockFace.WEST);

    private BlockFace blockFace;

    Direction(BlockFace blockFace) {
        this.blockFace = blockFace;
    }

    public static Direction fromBlockFace(BlockFace blockFace) {
        try {
            return valueOf(blockFace.name());
        } catch (IllegalArgumentException ignored) {
        }
        for (Direction direction : values()) {
            if (direction.blockFace == blockFace) {
                return direction;
            }
        }
        return null;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
