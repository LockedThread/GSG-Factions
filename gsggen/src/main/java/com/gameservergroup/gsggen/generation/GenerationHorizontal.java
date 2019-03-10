package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsggen.objs.Gen;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class GenerationHorizontal extends Generation {

    private int length;
    private BlockFace blockFace;

    public GenerationHorizontal(BlockPosition startingBlockPosition, Gen gen, BlockFace blockFace) {
        super(startingBlockPosition, gen);
        this.length = 0;
        this.blockFace = blockFace;
    }

    @Override
    public boolean generate() {
        final Block relative = getStartingBlockPosition().getBlock().getRelative(blockFace);
        if (relative.getType() != Material.AIR && !getGen().isPatch()) {
            return false;
        }

        if (getGen().isPatch() && relative.getType() != Material.WATER || relative.getType() != Material.STATIONARY_WATER || relative.getType() != Material.LAVA || relative.getType() != Material.COBBLESTONE || relative.getType() != Material.OBSIDIAN || relative.getType() != Material.AIR) {
            return false;
        }

        if (length == getGen().getLength()) {
            return false;
        }
        relative.setTypeIdAndData(getGen().getMaterial().getId(), (byte) 0, false);
        length++;
        setCurrentBlockPosition(BlockPosition.of(relative));
        return true;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }
}
