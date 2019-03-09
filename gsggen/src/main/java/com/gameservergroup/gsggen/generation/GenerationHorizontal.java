package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import org.bukkit.Material;

public class GenerationHorizontal extends Generation {

    public GenerationHorizontal(BlockPosition startingBlockPosition, Material material, Direction direction) {
        super(startingBlockPosition, material, direction);
    }

    @Override
    public void generate() {

    }
}
