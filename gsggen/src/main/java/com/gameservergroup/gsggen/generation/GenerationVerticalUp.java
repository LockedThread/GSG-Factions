package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsggen.objs.Gen;

public class GenerationVerticalUp extends Generation {

    public GenerationVerticalUp(BlockPosition startingBlockPosition, Gen gen) {
        super(startingBlockPosition, gen);
    }

    @Override
    public boolean generate() {
        return false;
    }
}
