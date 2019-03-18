package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.utils.Utils;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.objs.Gen;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class GenerationHorizontal extends Generation {

    private BlockFace blockFace;
    private FLocation startingFLocation;

    public GenerationHorizontal(Block startingBlockPosition, Gen gen, BlockFace blockFace) {
        super(startingBlockPosition, gen);
        this.blockFace = blockFace;
        startingFLocation = new FLocation(getStartingBlockPosition().getLocation());
    }

    @Override
    public boolean generate() {
        final Block relative = getCurrentBlockPosition().getRelative(blockFace);
        if (!relative.getWorld().isChunkLoaded(relative.getX() >> 4, relative.getZ() >> 4)) {
            GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), relative.getChunk()::load);
        }
        if (getLength() == 0) {
            return false;
        }
        if (getStartingBlockPosition().getType() != getMaterial()) {
            return false;
        }
        if (relative.getType() != Material.AIR && !getGen().isPatch()) {
            return false;
        }
        if (getGen().isPatch() && relative.getType() != Material.WATER &&
                relative.getType() != Material.STATIONARY_WATER &&
                relative.getType() != Material.LAVA &&
                relative.getType() != Material.STATIONARY_LAVA &&
                relative.getType() != Material.OBSIDIAN &&
                relative.getType() != Material.SAND &&
                relative.getType() != Material.GRAVEL &&
                relative.getType() != Material.AIR &&
                relative.getType() != Material.COBBLESTONE) {
            return false;
        }
        if (!Board.getInstance().getFactionAt(startingFLocation).getTag().equals(Board.getInstance().getFactionAt(new FLocation(relative)).getTag())) {
            return false;
        }
        if (Utils.isOutsideBorder(relative.getLocation())) {
            return false;
        }

        setLength(getLength() - 1);
        GSGGen.getInstance().getServer().getScheduler().runTask(GSGGen.getInstance(), () -> relative.setTypeIdAndData(getGen().getMaterial().getId(), (byte) 0, relative.isLiquid()));
        setCurrentBlockPosition(relative);
        return true;
    }
}
