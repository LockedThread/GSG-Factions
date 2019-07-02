package com.gameservergroup.gsgcore;

import it.unimi.dsi.util.XoRoShiRo128PlusRandom;
import org.junit.Test;

import java.util.UUID;

public class RandomUUIDBenchMark {

    private static final XoRoShiRo128PlusRandom RANDOM = new XoRoShiRo128PlusRandom();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void randomUUIDBenchMark() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            new UUID(RANDOM.nextLong(), RANDOM.nextLong());
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Benchmark with new UUID(RANDOM.nextLong(), RANDOM.nextLong()) with XoRoShiRo128PlusRandom = " + time + "ms");
        time = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            UUID.randomUUID();
        }

        time = System.currentTimeMillis() - time;
        System.out.println("Benchmark with UUID.randomUUID() = " + time + "ms");
    }
}
