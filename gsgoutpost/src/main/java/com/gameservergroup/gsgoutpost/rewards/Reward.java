package com.gameservergroup.gsgoutpost.rewards;

import com.gameservergroup.gsgoutpost.objs.Outpost;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Reward {

    private final transient RewardType rewardType;
    private final transient Map<String, Object> meta;

    public Reward(ConfigurationSection section) {
        this.rewardType = RewardType.valueOf(section.getString("type"));
        this.meta = section.getConfigurationSection("meta").getKeys(false).stream().collect(Collectors.toMap(key -> key, key -> section.get("meta." + key), (a, b) -> b));
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public List<?> getMetaList(String key) {
        return (List<?>) meta.get(key);
    }

    public String getMetaString(String key) {
        return String.valueOf(meta.get(key));
    }

    public double getMetaDouble(String key) {
        return (double) meta.get(key);
    }

    public double getMetaInt(String key) {
        return (int) meta.get(key);
    }

    public long getMetaLong(String key) {
        return (long) meta.get(key);
    }

    public void apply(Outpost outpost) {/*
        switch (rewardType) {
            case TIMED_FACTION_REWARD:
                outpost.setTimedFactionReward(Bukkit.getScheduler().runTaskTimer(GSGOutpost.getInstance(), () -> {
                    for (Object command : getMetaList("commands")) {
                        for (FPlayer fPlayer : outpost.getFaction().getFPlayersWhereOnline(true)) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.valueOf(command).replace("{player}", fPlayer.getName()));
                        }
                    }
                    for (Object command : getMetaList("commands")) {
                        for (Player player : outpost.getPlayers()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.valueOf(command).replace("{player}", player.getName()));
                        }
                    }

                }, getMetaLong("interval"), getMetaLong("interval")));
                break;
            case TIMED_REWARD:
                outpost.setTimedReward(Bukkit.getScheduler().runTaskTimer(GSGOutpost.getInstance(), () -> {
                    for (Object command : getMetaList("commands")) {
                        for (Player player : outpost.getPlayers()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.valueOf(command).replace("{player}", player.getName()));
                        }
                    }
                }, getMetaLong("interval"), getMetaLong("interval")));
                break;
            case SHOP_GUI_PLUS_MULTIPLIER:
                System.out.println("Unsupported operation, SHOP_GUI_PLUS_MULTIPLIER");
                break;
            case COLLECTOR_MULTIPLIER:
                System.out.println("Unsupported operation, COLLECTOR_MULTIPLIER");
                break;*/

    }
}
