package com.gameservergroup.gsgoutpost;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.JsonFile;
import com.gameservergroup.gsgcore.utils.Text;
import com.gameservergroup.gsgoutpost.enums.OutpostMessages;
import com.gameservergroup.gsgoutpost.menus.MenuOutpost;
import com.gameservergroup.gsgoutpost.objs.Outpost;
import com.gameservergroup.gsgoutpost.rewards.Reward;
import com.gameservergroup.gsgoutpost.tasks.TaskOutpost2;
import com.gameservergroup.gsgoutpost.units.UnitOutpost;
import com.gameservergroup.gsgoutpost.utils.ActionbarUtil;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GSGOutpost extends Module {

    private static GSGOutpost instance;
    private Map<String, Reward> rewardMap;
    private Map<String, Outpost> outpostMap;
    private JsonFile<Map<String, Outpost>> jsonFileOutpost;
    private MenuOutpost menuOutpost;

    public static GSGOutpost getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        for (OutpostMessages outpostMessages : OutpostMessages.values()) {
            if (getConfig().isSet("messages." + outpostMessages.getKey())) {
                outpostMessages.setMessage(getConfig().getString("messages." + outpostMessages.getKey()));
            } else {
                getConfig().set("messages." + outpostMessages.getKey(), outpostMessages.getValue());
            }
        }
        ConfigurationSection section = getConfig().getConfigurationSection("outpost.rewards");
        this.rewardMap = section.getKeys(false)
                .stream()
                .filter(key -> section.getBoolean(key + ".enabled"))
                .collect(Collectors.toMap(key -> key, key -> new Reward(section.getConfigurationSection(key)), (a, b) -> b));

        this.jsonFileOutpost = new JsonFile<>(getDataFolder(), "outposts.json", new TypeToken<Map<String, Outpost>>() {
        });
        this.outpostMap = jsonFileOutpost.load().orElse(new HashMap<>());
        for (Map.Entry<String, Outpost> entry : outpostMap.entrySet()) {
            Optional<ProtectedRegion> protectedRegion = entry.getValue().getProtectedRegion();
            if (protectedRegion.isPresent()) {
                entry.getValue().init();
                //entry.getValue().startTask();
            } else {
                outpostMap.remove(entry.getKey());
                getLogger().severe("Removed Outpost \"" + entry.getKey() + "\" because the worldguard region was unable to be found!");
            }
        }
        long delay = instance.getConfig().getLong("outpost.check-delay");
        getServer().getScheduler().runTaskTimer(instance, new TaskOutpost2(), delay, delay);

        registerUnits(new UnitOutpost());
    }

    @Override
    public void disable() {
        instance = null;
        jsonFileOutpost.save(outpostMap);
    }

    public Map<String, Outpost> getOutpostMap() {
        return outpostMap;
    }

    public JsonFile<Map<String, Outpost>> getJsonFileOutpost() {
        return jsonFileOutpost;
    }

    public Optional<ProtectedRegion> getProtectedRegion(String region) {
        for (World world : Bukkit.getWorlds()) {
            ProtectedRegion protectedRegion = WGBukkit.getPlugin().getRegionManager(world).getRegion(region);
            if (protectedRegion != null) {
                return Optional.of(protectedRegion);
            }
        }
        return Optional.empty();
    }

    public boolean isInRegion(Location location, String region) {
        for (ProtectedRegion protectedRegion : WGBukkit.getPlugin().getRegionManager(location.getWorld()).getApplicableRegions(location).getRegions()) {
            if (protectedRegion.getId().equalsIgnoreCase(region)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Outpost> getOutpost(Location location) {
        for (Outpost outpost : outpostMap.values()) {
            if (isInRegion(location, outpost.getUniqueIdentifier())) {
                return Optional.of(outpost);
            }
        }
        return Optional.empty();
    }

    public void sendUpdate(Collection<? extends Player> onlinePlayers, String message) {
        if (getConfig().getBoolean("outpost.notifications.use-actionbar")) {
            for (Player player : onlinePlayers) {
                ActionbarUtil.getActionbarUtil().sendActionBar(player, message);
            }
        } else {
            for (Player player : onlinePlayers) {
                player.sendMessage(Text.toColor(message));
            }
        }
    }

    public MenuOutpost getMenuOutpost() {
        return this.menuOutpost == null ? this.menuOutpost = new MenuOutpost() : this.menuOutpost;
    }

    public Map<String, Reward> getRewardMap() {
        return rewardMap;
    }
}
