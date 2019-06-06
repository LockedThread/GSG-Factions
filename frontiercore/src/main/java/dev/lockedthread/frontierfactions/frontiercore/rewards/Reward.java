package dev.lockedthread.frontierfactions.frontiercore.rewards;

import com.gameservergroup.gsgcore.utils.Text;
import dev.lockedthread.frontierfactions.frontiercore.FrontierCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class Reward {

    private final double chance;
    private final List<String> commands;

    public Reward(double chance, List<String> commands) {
        this.chance = chance;
        this.commands = commands;
    }

    public double getChance() {
        return chance;
    }

    public List<String> getCommands() {
        return commands;
    }

    public boolean executeChanceCalculation(double executedChance) {
        return executedChance <= chance;
    }

    public void executeCommands(Player player, RewardType rewardType) {
        Bukkit.getScheduler().runTask(FrontierCore.getInstance(), () -> {
            for (String command : commands) {
                String replace = command.replace("{player}", player.getName()).replace("{reward-type}", rewardType.toPrettyName());
                if (replace.startsWith("@broadcast")) {
                    Bukkit.broadcastMessage(Text.toColor(replace.substring(11)));
                } else if (replace.startsWith("@message")) {
                    player.sendMessage(Text.toColor(replace.substring(8)));
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replace);
                }
            }
        });
    }

    @Override
    public String toString() {
        return "Reward{" +
                "chance=" + chance +
                ", commands=" + commands +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reward reward = (Reward) o;
        return Double.compare(reward.chance, chance) == 0 && Objects.equals(commands, reward.commands);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(chance);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (commands != null ? commands.hashCode() : 0);
        return result;
    }
}
