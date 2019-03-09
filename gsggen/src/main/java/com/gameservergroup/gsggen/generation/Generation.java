package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import org.bukkit.Material;

public abstract class Generation {

    private final Material material;
    private final Direction direction;
    private BlockPosition startingBlockPosition, currentBlockPosition;
    private int length;

    public Generation(BlockPosition startingBlockPosition, Material material, Direction direction) {
        this.startingBlockPosition = startingBlockPosition;
        this.currentBlockPosition = startingBlockPosition;
        this.material = material;
        this.direction = direction;
    }

    public abstract boolean generate();

    public BlockPosition getStartingBlockPosition() {
        return startingBlockPosition;
    }

    public Material getMaterial() {
        return material;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public BlockPosition getCurrentBlockPosition() {
        return currentBlockPosition;
    }

    public void setCurrentBlockPosition(BlockPosition currentBlockPosition) {
        this.currentBlockPosition = currentBlockPosition;
    }
}
