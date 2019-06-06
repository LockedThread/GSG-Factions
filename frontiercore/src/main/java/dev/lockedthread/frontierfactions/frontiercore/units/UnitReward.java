package dev.lockedthread.frontierfactions.frontiercore.units;

import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.Text;
import dev.lockedthread.frontierfactions.frontiercore.FrontierCore;
import dev.lockedthread.frontierfactions.frontiercore.rewards.Reward;
import dev.lockedthread.frontierfactions.frontiercore.rewards.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class UnitReward extends Unit {

    private static final FrontierCore FRONTIER_CORE = FrontierCore.getInstance();

    @Override
    public void setup() {
        if (FRONTIER_CORE.getConfig().getBoolean("rewards.mining.enabled")) {
            ConfigurationSection section = FRONTIER_CORE.getConfig().getConfigurationSection("rewards.mining.rewards");
            Reward[] rewards = section.getKeys(false)
                    .stream()
                    .map(key -> new Reward(section.getDouble(key + ".chance"), section.getStringList(key + ".commands")))
                    .toArray(Reward[]::new);
            FRONTIER_CORE.getRewardMap().put(RewardType.MINING, rewards);

            EventPost.of(BlockBreakEvent.class, EventPriority.valueOf(FRONTIER_CORE.getConfig().getString("rewards.mining.event-priority")))
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> event.getPlayer().getGameMode() == GameMode.SURVIVAL && FRONTIER_CORE.getConfig().getBoolean("rewards.mining.only-survival"))
                    .handle(event -> executeRewardCalculations(event.getPlayer(), RewardType.MINING))
                    .post(FRONTIER_CORE);
            FRONTIER_CORE.getLogger().info("Enabling Mining Rewards");
        } else {
            FRONTIER_CORE.getLogger().warning("Not enabling Mining Rewards");
        }
        if (FRONTIER_CORE.getConfig().getBoolean("rewards.fishing.enabled")) {
            ConfigurationSection section = FRONTIER_CORE.getConfig().getConfigurationSection("rewards.fishing.rewards");
            Reward[] rewards = section.getKeys(false)
                    .stream()
                    .map(key -> new Reward(section.getDouble(key + ".chance"), section.getStringList(key + ".commands")))
                    .toArray(Reward[]::new);
            FRONTIER_CORE.getRewardMap().put(RewardType.FISHING, rewards);

            EventPost.of(PlayerFishEvent.class, EventPriority.valueOf(FRONTIER_CORE.getConfig().getString("rewards.fishing.event-priority")))
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> event.getPlayer().getGameMode() == GameMode.SURVIVAL && FRONTIER_CORE.getConfig().getBoolean("rewards.fishing.only-survival"))
                    .filter(event -> event.getCaught() != null)
                    .handle(event -> executeRewardCalculations(event.getPlayer(), RewardType.FISHING))
                    .post(FRONTIER_CORE);
            FRONTIER_CORE.getLogger().info("Enabling Fishing Rewards");
        } else {
            FRONTIER_CORE.getLogger().warning("Not enabling Fishing Rewards");
        }
        ConfigurationSection section = FRONTIER_CORE.getConfig().getConfigurationSection("trivia.rewards");
        Reward[] rewards = section.getKeys(false)
                .stream()
                .map(key -> new Reward(section.getDouble(key + ".chance"), section.getStringList(key + ".commands")))
                .toArray(Reward[]::new);
        FRONTIER_CORE.getRewardMap().put(RewardType.TRIVIA, rewards);
    }

    private void executeRewardCalculations(Player player, RewardType rewardType) {
        double nominalChance = rewardType.getNominalChance(player);
        if (nominalChance > 0.0 && nominalChance <= FRONTIER_CORE.getRandom().nextDouble() * 100) {
            return;
        }
        final double random = FRONTIER_CORE.getRandom().nextDouble() * 100;
        if (FRONTIER_CORE.isVerbose()) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOp()) {
                    onlinePlayer.sendMessage(Text.toColor("&cFrontierCore&8&lVERBOOSE &8> &e" + player.getName() + " got random " + random + " for " + rewardType.name()));
                }
            }
        }
        Reward[] rewardArray = FRONTIER_CORE.getRewardMap().get(rewardType);
        Reward reward = rewardArray[FRONTIER_CORE.getRandom().nextInt(rewardArray.length - 1)];
        if (reward.executeChanceCalculation(random)) {
            reward.executeCommands(player, rewardType);
        }
    }
}
