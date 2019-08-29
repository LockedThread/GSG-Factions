package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.database.types.JsonDatabase;
import org.junit.Test;

import java.util.Map;

public class DatabaseTest {

    @Test
    public void testDatabase() {
        JsonDatabase<Map<String, Integer>> jsonDatabase = new JsonDatabase<>();
        jsonDatabase.connect();
    }
}
