package com.gameservergroup.gsggen.units;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.storage.JsonFile;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.generation.Generation;
import com.gameservergroup.gsggen.objs.Gen;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UnitGen extends Unit {

    private static final GSGGen GSG_GEN = GSGGen.getInstance();

    private Set<Generation> generations;
    private HashMap<String, Gen> genHashMap = new HashMap<>();

    public void setup() {
        JsonFile<Set<Generation>> jsonFile = new JsonFile<>(GSGGen.getInstance().getDataFolder(), "gens", new TypeReference<Set<Generation>>() {
        });
        this.generations = jsonFile.load().orElse(new HashSet<>());

        for (String genKey : GSG_GEN
                .getConfig()
                .getConfigurationSection("gens")
                .getKeys(false)) {
            double price = 0.0;
            Direction direction = null;
            boolean patch = false;
            ConfigurationSection section = GSG_GEN.getConfig().getConfigurationSection("gens." + genKey);
            System.out.println("genKey=" + genKey);
            for (String data : section.getKeys(false)) {
                System.out.println("data=" + data);
                if (data.equalsIgnoreCase("price")) {
                    price = section.getDouble("price");
                } else if (data.equalsIgnoreCase("direction")) {
                    direction = Direction.valueOf(section.getString("direction").toUpperCase());
                } else if (data.equalsIgnoreCase("patch")) {
                    patch = section.getBoolean("patch");
                } else if (!data.equalsIgnoreCase("menu") && !data.equalsIgnoreCase("item")) {
                    throw new RuntimeException("We've found a un-parsable key in " + section.getCurrentPath() + " called " + data);
                }
            }
            genHashMap.put(genKey, new Gen(section, direction, price, patch));
            System.out.println();
        }

        CommandPost.of()
                .build()
                .assertPlayer()
                .handler(c -> c.getSender().openInventory(GSG_GEN.getGenMenu().getInventory()))
                .post(GSG_GEN, "gen", "gens", "genblock", "genblocks", "gb", "genbucket", "genbuckets");
    }

    public Set<Generation> getGenerations() {
        return generations;
    }

    public HashMap<String, Gen> getGenHashMap() {
        return genHashMap;
    }
}
