package com.massivecraft.factions.zcore.factionboosters;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Booster implements IBooster {

    private static final Map<String, Booster> BOOSTER_MAP = new HashMap<>();
    private static YamlConfiguration boosterConfig;

    private final String id;
    private final BoosterType boosterType;
    private final Map<String, Object> meta;
    private final long time;
    private final int hash;

    public Booster(ConfigurationSection section) {
        this(section.getName(),
                BoosterType.valueOf(section.getString("booster-type").toUpperCase()),
                section.getConfigurationSection("meta").getKeys(false).stream().collect(Collectors.toMap(key -> key, key -> section.get("meta." + key), (a, b) -> b)),
                section.getInt("time"));
    }

    public Booster(String id, BoosterType boosterType, Map<String, Object> meta, long time) {
        this.id = id;
        this.boosterType = boosterType;
        this.meta = meta;
        this.time = time;

        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (boosterType != null ? boosterType.hashCode() : 0);
        result = 31 * result + (meta != null ? meta.hashCode() : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        this.hash = result;
    }

    @Override
    public BoosterType getBoosterType() {
        return boosterType;
    }

    public static void initializeBoosters() {
        try {
            File file = new File(P.p.getDataFolder(), "boosters.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), inputStreamToString(P.p.getResource("boosters.yml")).getBytes());
            boosterConfig = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = boosterConfig.getConfigurationSection("faction-boosters");
            for (String key : section.getKeys(false)) {
                BOOSTER_MAP.put(key, new Booster(section.getConfigurationSection(key)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String inputStreamToString(InputStream is) throws IOException {
        byte[] buffer = new byte[is.available()];
        return new String(buffer, 0, is.read(buffer), StandardCharsets.UTF_8);
    }

    public static Map<String, Booster> getBoosterMap() {
        return BOOSTER_MAP;
    }

    @Override
    public Map<String, Object> getMeta() {
        return meta;
    }

    @Override
    public void startBooster(Faction faction) {
        System.out.println("System.currentTimeMillis() = " + System.currentTimeMillis());
        long value = System.currentTimeMillis() + time;
        System.out.println("value = " + value);
        faction.setBooster(this);
    }

    @Override
    public void stopBooster(Faction faction) {
        faction.getBoosters().remove(boosterType, id);
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booster booster = (Booster) o;
        return time == booster.time && Objects.equals(id, booster.id) && boosterType == booster.boosterType && hash == booster.hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return "Booster{" +
                "id='" + id + '\'' +
                ", boosterType=" + boosterType +
                ", meta=" + meta +
                ", time=" + time +
                ", hash=" + hash +
                '}';
    }
}
