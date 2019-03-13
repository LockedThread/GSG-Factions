package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.objs.Gen;
import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class Generation {

    private Block startingBlockPosition, currentBlockPosition;
    private Gen gen;
    private int length;

    public Generation(Block startingBlockPosition, Gen gen) {
        this.startingBlockPosition = startingBlockPosition;
        this.currentBlockPosition = startingBlockPosition;
        this.gen = gen;
        this.length = gen.getLength();
    }

    public abstract boolean generate();

    public void enable() {
        GSGGen.getInstance().getUnitGen().getGenerations().add(this);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Block getStartingBlockPosition() {
        return startingBlockPosition;
    }

    public Material getMaterial() {
        return gen.getMaterial();
    }

    public Block getCurrentBlockPosition() {
        return currentBlockPosition;
    }

    public void setCurrentBlockPosition(Block currentBlockPosition) {
        this.currentBlockPosition = currentBlockPosition;
    }

    public Gen getGen() {
        return gen;
    }
}
