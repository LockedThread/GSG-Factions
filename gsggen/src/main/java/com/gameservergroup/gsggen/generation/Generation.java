package com.gameservergroup.gsggen.generation;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.objs.Gen;
import org.bukkit.Material;

public abstract class Generation {

    private BlockPosition startingBlockPosition, currentBlockPosition;
    private Gen gen;
    private int length;

    public Generation(BlockPosition startingBlockPosition, Gen gen) {
        this.startingBlockPosition = startingBlockPosition;
        this.currentBlockPosition = startingBlockPosition;
        this.gen = gen;
        this.length = gen.getLength();
        System.out.println("instantiated Generation object");
    }

    public abstract boolean generate();

    public void enable() {
        GSGGen.getInstance().getUnitGen().getGenerations().add(this);
        System.out.println("enabled generation");
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public BlockPosition getStartingBlockPosition() {
        return startingBlockPosition;
    }

    public Material getMaterial() {
        return gen.getMaterial();
    }

    public BlockPosition getCurrentBlockPosition() {
        return currentBlockPosition;
    }

    public void setCurrentBlockPosition(BlockPosition currentBlockPosition) {
        this.currentBlockPosition = currentBlockPosition;
    }

    public Gen getGen() {
        return gen;
    }
}
