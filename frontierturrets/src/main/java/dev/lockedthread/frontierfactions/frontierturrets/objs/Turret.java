package dev.lockedthread.frontierfactions.frontierturrets.objs;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import dev.lockedthread.frontierfactions.frontierturrets.FrontierTurrets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Turret {

    private final BlockPosition position;
    private transient Faction faction;
    private transient FLocation fLocation;
    private boolean enabled;
    private long enabledTime;
    private double health;
    private int hash;

    public Turret(BlockPosition position) {
        this.position = position;
        this.health = FrontierTurrets.getInstance().getConfig().getDouble("turrets.health.max");
        this.enabled = false;
        refreshHashCode();
    }

    public void refreshHashCode() {
        int result = faction != null ? faction.hashCode() : 0;
        result = 31 * result + (fLocation != null ? fLocation.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        long temp = Double.doubleToLongBits(health);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        this.hash = result;
    }

    public void execute() {
        for (Entity entity : position.getChunk().getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (FPlayers.getInstance().getByPlayer(player).getRelationTo(faction) != Relation.MEMBER) {
                    player.setHealth(player.getHealth() - FrontierTurrets.getInstance().getConfig().getDouble("turrets.damage.amount"));
                }
            }
        }
    }

    public void tickHealth() {
        FileConfiguration config = FrontierTurrets.getInstance().getConfig();
        if (health < config.getDouble("turrets.health.max")) {
            if (health + config.getDouble("turrets.health.tick.increase.amount") > config.getDouble("turrets.health.max")) {
                health = config.getDouble("turrets.health.max");
            } else {
                health += config.getDouble("turrets.health.tick.increase.amount");
            }
        } else {
            health += config.getDouble("turrets.health.tick.increase.amount");
        }
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
        refreshHashCode();
    }

    public FLocation getFLocation() {
        return fLocation;
    }

    public void setFLocation(FLocation fLocation) {
        this.fLocation = fLocation;
        refreshHashCode();
    }

    public BlockPosition getPosition() {
        return position;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turret turret = (Turret) o;
        return Double.compare(turret.health, health) == 0 && hash == turret.hash;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            setEnabledTime(System.currentTimeMillis());
        }
    }

    public long getEnabledTime() {
        return enabledTime;
    }

    public void setEnabledTime(long enabledTime) {
        this.enabledTime = enabledTime;
    }
}
