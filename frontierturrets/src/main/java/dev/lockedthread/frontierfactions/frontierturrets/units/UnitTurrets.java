package dev.lockedthread.frontierfactions.frontierturrets.units;

import com.gameservergroup.gsgcore.storage.JsonFile;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import com.gameservergroup.gsgcore.units.Unit;
import com.google.gson.reflect.TypeToken;
import dev.lockedthread.frontierfactions.frontierturrets.FrontierTurrets;
import dev.lockedthread.frontierfactions.frontierturrets.objs.Turret;

import java.util.HashMap;

public class UnitTurrets extends Unit {

    private static final FrontierTurrets FRONTIER_TURRETS = FrontierTurrets.getInstance();

    private HashMap<ChunkPosition, Turret> turretMap;
    private JsonFile<HashMap<ChunkPosition, Turret>> jsonFile;

    @Override
    public void setup() {
        this.jsonFile = new JsonFile<>(FRONTIER_TURRETS.getDataFolder(), "turrets.json", new TypeToken<HashMap<ChunkPosition, Turret>>() {
        });
        this.turretMap = this.jsonFile.load().orElse(new HashMap<>());

    }


    public HashMap<ChunkPosition, Turret> getTurretMap() {
        return turretMap;
    }
}
