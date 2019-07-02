package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HashingTest {

    private static final String[] WORLDS = {"End", "Nether", "Overworld"};

    @Test
    public void testHashing() {
        Set<ChunkPosition> chunkPositions = new HashSet<>();
        List<ChunkPosition> duplicates = new ArrayList<>();
        for (String world : WORLDS) {
            for (int x = 0; x < 100; x++) {
                for (int z = 0; z < 100; z++) {
                    ChunkPosition chunkPosition = new ChunkPosition(world, x, z);
                    if (!chunkPositions.add(chunkPosition)) {
                        duplicates.add(chunkPosition);
                    }
                }
            }
        }
        for (ChunkPosition duplicate : duplicates) {
            System.out.println("duplicate = " + duplicate);
        }
    }
}
