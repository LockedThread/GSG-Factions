package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsggen.objs.Gen;
import org.bukkit.block.Block;

public class GenerationSandComp extends Generation {

    public GenerationSandComp(Block startingBlockPosition, Gen gen) {
        super(startingBlockPosition, gen);
    }

    @Override
    public boolean generate() {
        return false;
    }
}
