package com.gameservergroup.gsgcollectors.task;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.obj.Collector;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class TaskSave extends BukkitRunnable {

    @Override
    public void run() {
        System.out.println("Starting Collector Save Task");
        final long startTime = System.currentTimeMillis();
        HashMap<ChunkPosition, Collector> collectorHashMap = GSGCollectors.getInstance().getUnitCollectors().getCollectorHashMap();
        GSGCollectors.getInstance().getUnitCollectors().getJsonFile().save(collectorHashMap);
        System.out.println("Saved " + collectorHashMap.size() + " collectors (" + (System.currentTimeMillis() - startTime) + "ms)");
    }
}
