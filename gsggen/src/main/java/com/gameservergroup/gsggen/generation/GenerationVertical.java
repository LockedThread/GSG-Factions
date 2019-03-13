package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.objs.Gen;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class GenerationVertical extends Generation {

    private Direction direction;

    public GenerationVertical(Block startingBlockPosition, Gen gen, Direction direction) {
        super(startingBlockPosition, gen);
        this.direction = direction;
    }

    @Override
    public boolean generate() {
        final Chunk chunk = getCurrentBlockPosition().getChunk();
        if (!chunk.isLoaded()) {
            GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), chunk::load);
        }
        final Block relative = getCurrentBlockPosition().getRelative(direction.getBlockFaces()[0]);
        if (getLength() == 0) {
            return false;
        }
        if (getStartingBlockPosition().getType() != getMaterial()) {
            return false;
        }
        if (relative.getY() == 255) {
            return false;
        }
        if (relative.getType() != Material.AIR && !getGen().isPatch()) {
            return false;
        }
        if (getGen().isPatch() && (relative.getType() != Material.WATER ||
                relative.getType() != Material.STATIONARY_WATER ||
                relative.getType() != Material.LAVA ||
                relative.getType() != Material.COBBLESTONE ||
                relative.getType() != Material.OBSIDIAN ||
                relative.getType() != Material.AIR)) {
            return false;
        }

        setLength(getLength() - 1);
        GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), () -> relative.setTypeIdAndData(getGen().getMaterial().getId(), (byte) 0, false));
        setCurrentBlockPosition(relative);
        return true;
    }
}
