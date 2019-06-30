package com.gameservergroup.gsgcore.enums;

import org.bukkit.block.BlockFace;

public enum Direction {

    VERTICAL_UP(BlockFace.UP),
    VERTICAL_DOWN(BlockFace.DOWN),
    HORIZONTAL(BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH);

    private BlockFace[] blockFaces;

    Direction(BlockFace... blockFaces) {
        this.blockFaces = blockFaces;
    }

    public static Direction fromBlockFace(BlockFace blockFace) {
        try {
            return valueOf(blockFace.name());
        } catch (IllegalArgumentException ignored) {
        }
        for (Direction direction : values()) {
            for (BlockFace face : direction.blockFaces) {
                if (blockFace == face) {
                    return direction;
                }
            }
        }
        return null;
    }

    public BlockFace[] getBlockFaces() {
        return blockFaces;
    }
}
