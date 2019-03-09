package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import org.bukkit.Material;

public class GenerationVerticalUp extends Generation {

    public GenerationVerticalUp(BlockPosition startingBlockPosition, Material material) {
        super(startingBlockPosition, material, Direction.VERTICAL_UP);
    }

    @Override
    public boolean generate() {
        return false;
    }
}
