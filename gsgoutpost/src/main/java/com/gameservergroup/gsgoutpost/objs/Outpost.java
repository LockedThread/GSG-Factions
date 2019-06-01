package com.gameservergroup.gsgoutpost.objs;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgoutpost.GSGOutpost;
import com.gameservergroup.gsgoutpost.enums.OutpostState;
import com.gameservergroup.gsgoutpost.exceptions.UnableToFindProtectedRegionException;
import com.gameservergroup.gsgoutpost.items.SerializableItem;
import com.gameservergroup.gsgoutpost.rewards.Reward;
import com.gameservergroup.gsgoutpost.tasks.TaskOutpost;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Outpost {

    private final String uniqueIdentifier;
    private final Map<String, Reward> rewardMap;
    private SerializableItem menuItem;
    private String captureMessage;
    private int slot;
    private BlockPosition warp;
    private UUID worldUID;

    // Transient Fields
    private transient Set<Player> players;
    private transient OutpostState outpostState;
    private transient int percentage;
    private transient Faction faction;
    private transient Faction capturedFaction;
    private transient Set<BukkitTask> bukkitTasks;

    public Outpost(String uniqueIdentifier) {
        this(uniqueIdentifier, new HashMap<>());
    }

    public Outpost(String uniqueIdentifier, Map<String, Reward> rewardMap) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.rewardMap = rewardMap;
        this.slot = -1;
        Optional<World> found = Optional.empty();
        for (World world : Bukkit.getWorlds()) {
            ProtectedRegion protectedRegion = WGBukkit.getRegionManager(world).getRegion(uniqueIdentifier);
            if (protectedRegion != null) {
                found = Optional.of(world);
                this.warp = BlockPosition.of(world, protectedRegion.getMinimumPoint().getBlockX(), protectedRegion.getMinimumPoint().getBlockY(), protectedRegion.getMinimumPoint().getBlockZ());
                break;
            }
        }
        this.worldUID = found.map(World::getUID).orElseThrow(() -> new UnableToFindProtectedRegionException("Unable to find region with id " + uniqueIdentifier));
        this.menuItem = new SerializableItem(Material.WOOL, "Default Name. Change with /outpost config", Collections.emptyList(), false);
    }

    public void init() {
        this.players = new HashSet<>();
        this.outpostState = OutpostState.NEUTRALIZE_WAITING;
        this.percentage = 100;
    }

    public void startTask() {
        GSGOutpost instance = GSGOutpost.getInstance();
        long delay = instance.getConfig().getLong("outpost.check-delay");
        instance.getServer().getScheduler().runTaskTimer(instance, new TaskOutpost(this), delay, delay);
    }

    public Faction findFaction() {
        return players.isEmpty() ? null : FPlayers.getInstance().getByPlayer(players.stream().findFirst().get()).getFaction();
    }

    public boolean isOneFaction() {
        for (Player player : players) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            for (Player player2 : players) {
                if (fPlayer.getFaction() != FPlayers.getInstance().getByPlayer(player2).getFaction()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(worldUID));
    }

    public Optional<ProtectedRegion> getProtectedRegion() {
        return getWorld().map(world -> WGBukkit.getRegionManager(world).getRegion(uniqueIdentifier));
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public Map<String, Reward> getRewardMap() {
        return rewardMap;
    }

    public SerializableItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(SerializableItem menuItem) {
        this.menuItem = menuItem;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(HashSet<Player> players) {
        this.players = players;
    }

    public OutpostState getOutpostState() {
        return outpostState;
    }

    public void setOutpostState(OutpostState outpostState) {
        this.outpostState = outpostState;
        if (outpostState == OutpostState.CAPTURED) {
            for (Reward reward : rewardMap.values()) {
                reward.apply(this);
            }
        } /*else if (outpostState == OutpostState.NEUTRALIZED) {
            if (rewardMap.containsKey(RewardType.TIMED_FACTION_REWARD)) {
                getTimedFactionReward().cancel();
                setTimedFactionReward(null);
            }
            if (rewardMap.containsKey(RewardType.TIMED_REWARD)) {
                getTimedReward().cancel();
                setTimedReward(null);
            }
        }*/
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public Faction getCapturedFaction() {
        return capturedFaction;
    }

    public void setCapturedFaction(Faction capturedFaction) {
        this.capturedFaction = capturedFaction;
    }

    public String getCapturerString() {
        return capturedFaction == null ? "none" : capturedFaction.getTag();
    }

    public String getCaptureMessage() {
        return captureMessage;
    }

    public void setCaptureMessage(String captureMessage) {
        this.captureMessage = captureMessage;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public BlockPosition getWarp() {
        return warp;
    }

    public void setWarp(BlockPosition warp) {
        this.warp = warp;
    }

    public UUID getWorldUID() {
        return worldUID;
    }

    public void setWorldUID(UUID worldUID) {
        this.worldUID = worldUID;
    }
}
