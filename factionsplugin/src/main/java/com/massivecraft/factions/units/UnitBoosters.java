package com.massivecraft.factions.units;

import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.pair.ImmutablePair;
import com.gameservergroup.gsgcore.units.Unit;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.factionboosters.Booster;
import com.massivecraft.factions.zcore.factionboosters.BoosterType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;

public class UnitBoosters extends Unit {

    @Override
    public void setup() {
        EventPost.of(EntityDamageByEntityEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getDamager() instanceof Player)
                .filter(event -> event.getEntity() instanceof Player)
                .handle(event -> {
                    Player damager = (Player) event.getDamager();
                    Player damaged = (Player) event.getEntity();

                    FPlayer damagerFPlayer = FPlayers.getInstance().getByPlayer(damager);
                    if (damagerFPlayer.hasFaction()) {
                        Faction faction = Board.getInstance().getFactionAt(new FLocation(damaged));
                        Relation relationTo = faction.getRelationTo(damagerFPlayer);
                        if (relationTo == Relation.MEMBER) {
                            Map<BoosterType, ImmutablePair<Booster, Long>> boosters = faction.getBoosters();
                            if (!boosters.isEmpty()) {
                                double multiplier = 1.0;
                                ImmutablePair<Booster, Long> boosterLongPair;
                                if ((boosterLongPair = boosters.get(BoosterType.DAMAGE_INCREASE_IN_MEMBER_TERRITORY)) != null) {
                                    if (boosterLongPair.getValue() >= System.currentTimeMillis()) {
                                        multiplier *= (double) boosterLongPair.getKey().getMeta().get("multiplier");
                                    } else {
                                        boosterLongPair.getKey().stopBooster(faction);
                                    }
                                }
                                if ((boosterLongPair = boosters.get(BoosterType.DAMAGE_REDUCTION_IN_MEMBER_TERRITORY)) != null) {
                                    if (boosterLongPair.getValue() >= System.currentTimeMillis()) {
                                        multiplier *= (double) boosterLongPair.getKey().getMeta().get("multiplier");
                                    } else {
                                        boosterLongPair.getKey().stopBooster(faction);
                                    }
                                }
                                if (multiplier != 1.0) {
                                    event.setDamage(event.getDamage() * multiplier);
                                }
                            }
                        } else if (relationTo == Relation.ENEMY) {
                            FPlayer damagedFPlayer = FPlayers.getInstance().getByPlayer(damaged);
                            if (damagedFPlayer.getRelationTo(damagerFPlayer) == Relation.ENEMY) {
                                Map<BoosterType, ImmutablePair<Booster, Long>> boosters = faction.getBoosters();
                                if (!boosters.isEmpty()) {
                                    double multiplier = 1.0;
                                    ImmutablePair<Booster, Long> boosterLongPair;
                                    if ((boosterLongPair = boosters.get(BoosterType.DAMAGE_INCREASE_IN_ENEMY_TERRITORY)) != null) {
                                        if (boosterLongPair.getValue() >= System.currentTimeMillis()) {
                                            multiplier *= (double) boosterLongPair.getKey().getMeta().get("multiplier");
                                        } else {
                                            boosterLongPair.getKey().stopBooster(faction);
                                        }
                                    }
                                    if ((boosterLongPair = boosters.get(BoosterType.DAMAGE_REDUCTION_IN_ENEMY_TERRITORY)) != null) {
                                        if (boosterLongPair.getValue() >= System.currentTimeMillis()) {
                                            multiplier *= (double) boosterLongPair.getKey().getMeta().get("multiplier");
                                        } else {
                                            boosterLongPair.getKey().stopBooster(faction);
                                        }
                                    }
                                    if (multiplier != 1.0) {
                                        event.setDamage(event.getDamage() * multiplier);
                                    }
                                }
                            }
                        }
                    }
                }).post(P.p);
    }
}
