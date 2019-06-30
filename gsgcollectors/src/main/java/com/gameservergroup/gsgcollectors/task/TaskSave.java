package com.gameservergroup.gsgcollectors.task;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.obj.Collector;
import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TaskSave extends BukkitRunnable {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    @Override
    public void run() {
        System.out.println("Starting Collector Save Task");
        final long startTime = System.currentTimeMillis();
        HashMap<ChunkPosition, Collector> collectorHashMap = GSGCollectors.getInstance().getUnitCollectors().getCollectorHashMap();

        File directory = new File(GSGCollectors.getInstance().getDataFolder(), "backups");
        if (!directory.exists()) {
            directory.mkdir();
        }
        try {
            File file = new File(directory, "backup-" + SIMPLE_DATE_FORMAT.format(new Date(System.currentTimeMillis())) + ".json");
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), GSGCore.getInstance().getGson().toJson(GSGCollectors.getInstance().getUnitCollectors().getCollectorHashMap()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        GSGCollectors.getInstance().getUnitCollectors().getJsonFile().save(collectorHashMap);
        System.out.println("Saved " + collectorHashMap.size() + " collectors (" + (System.currentTimeMillis() - startTime) + "ms)");
    }
}
