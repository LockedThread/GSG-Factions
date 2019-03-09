package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import org.bukkit.Material;

public class GenerationVerticalDown extends Generation {

    public GenerationVerticalDown(BlockPosition startingBlockPosition, Material material) {
        super(startingBlockPosition, material, Direction.VERTICAL_DOWN);
    }

    @Override
    public boolean generate() {
        return false;
    }
}
