package com.gameservergroup.gsggen.units;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.enums.Direction;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.storage.JsonFile;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.CallBack;
import com.gameservergroup.gsggen.GSGGen;
import com.gameservergroup.gsggen.generation.Generation;
import com.gameservergroup.gsggen.objs.Gen;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UnitGen extends Unit {

    private static final GSGGen GSG_GEN = GSGGen.getInstance();

    private boolean closeInventoryOnPurchase, closeInventoryOnNoMoney, disableLavaBucketPlayerUse;
    private Map<Generation, Boolean> generations;
    private HashMap<String, Gen> genHashMap = new HashMap<>();

    private JsonFile<Set<Generation>> generationJsonFile;

    public void setup() {
        load();
        this.generationJsonFile = new JsonFile<>(GSG_GEN.getDataFolder(), "gens.json", new TypeToken<Set<Generation>>() {
        });
        Optional<Set<Generation>> load = generationJsonFile.load();
        if (load.isPresent()) {
            this.generations = new ConcurrentHashMap<>();
            this.generations.putAll(Maps.asMap(load.get(), new Function<Generation, Boolean>() {
                @Nullable
                @Override
                public Boolean apply(@Nullable Generation generation) {
                    return true;
                }
            }));

            for (Generation generation : generations.keySet()) {
                generation.init();
            }
        } else {
            this.generations = Generation.ASYNC ? new ConcurrentHashMap<>() : new HashMap<>();
        }
        hookDisable(new CallBack() {
            @Override
            public void call() {
                generationJsonFile.save(generations.keySet());
            }
        });
        CommandPost.of()
                .builder()
                .assertPlayer()
                .handler(c -> c.getSender().openInventory(GSG_GEN.getGenMenu().getInventory()))
                .post(GSG_GEN, "gen", "gens", "genblock", "genblocks", "gb", "genbucket", "genbuckets");

        EventPost.of(PlayerBucketEmptyEvent.class)
                .filter(event -> disableLavaBucketPlayerUse)
                .filter(event -> event.getItemStack() != null && event.getItemStack().getType() == Material.LAVA_BUCKET)
                .handle(event -> event.setCancelled(true));

        EventPost.of(PlayerBucketFillEvent.class)
                .filter(event -> disableLavaBucketPlayerUse)
                .filter(event -> event.getItemStack() != null && event.getItemStack().getType() == Material.LAVA_BUCKET)
                .handle(event -> event.setCancelled(true));
    }

    public void load() {
        this.closeInventoryOnNoMoney = GSG_GEN.getConfig().getBoolean("menu.options.close-inventory-on-no-money");
        this.closeInventoryOnPurchase = GSG_GEN.getConfig().getBoolean("menu.options.close-inventory-on-purchase");
        this.disableLavaBucketPlayerUse = GSG_GEN.getConfig().getBoolean("disable-lava-bucket-player-use");
        if (!genHashMap.isEmpty()) {
            genHashMap.clear();
        }
        for (String genKey : GSG_GEN.getConfig().getConfigurationSection("gens").getKeys(false)) {
            int length = 0;
            double price = 0.0;
            Direction direction = null;
            boolean patch = false;
            Material material = null;
            ConfigurationSection section = GSG_GEN.getConfig().getConfigurationSection("gens." + genKey);
            for (String data : section.getKeys(false)) {
                if (data.equalsIgnoreCase("price")) {
                    price = section.getDouble(data);
                } else if (data.equalsIgnoreCase("direction")) {
                    direction = Direction.valueOf(section.getString(data).toUpperCase());
                } else if (data.equalsIgnoreCase("patch")) {
                    patch = section.getBoolean(data);
                } else if (data.equalsIgnoreCase("length")) {
                    length = section.getInt(data);
                } else if (data.equalsIgnoreCase("material")) {
                    material = Material.matchMaterial(section.getString(data));
                } else if (!data.equalsIgnoreCase("menu") && !data.equalsIgnoreCase("item")) {
                    throw new RuntimeException("We've found an un-parsable key in " + section.getCurrentPath() + " called " + data);
                }
            }
            genHashMap.put(genKey, length <= 0 ? new Gen(section, direction, price, patch, material) : new Gen(section, direction, price, patch, length, material));
        }
    }

    public Map<Generation, Boolean> getGenerations() {
        return generations;
    }

    public HashMap<String, Gen> getGenHashMap() {
        return genHashMap;
    }

    public boolean isCloseInventoryOnPurchase() {
        return closeInventoryOnPurchase;
    }

    public boolean isCloseInventoryOnNoMoney() {
        return closeInventoryOnNoMoney;
    }

    public boolean isDisableLavaBucketPlayerUse() {
        return disableLavaBucketPlayerUse;
    }
}
