package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsggen.objs.Gen;

public class GenerationVerticalDown extends Generation {

    public GenerationVerticalDown(BlockPosition startingBlockPosition, Gen gen) {
        super(startingBlockPosition, gen);
    }

    @Override
    public boolean generate() {
        return false;
    }
}