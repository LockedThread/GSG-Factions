package com.massivecraft.factions.units;

import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.factionboosters.Booster;
import com.massivecraft.factions.zcore.factionboosters.BoosterType;
import org.apache.commons.lang3.tuple.Pair;
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
                    System.out.println(0);
                    Player damager = (Player) event.getDamager();
                    Player damaged = (Player) event.getEntity();

                    FPlayer damagerFPlayer = FPlayers.getInstance().getByPlayer(damager);
                    if (damagerFPlayer.hasFaction()) {
                        System.out.println(1);

                        Faction faction = Board.getInstance().getFactionAt(new FLocation(damaged));
                        System.out.println("faction.getRelationTo(damagerFPlayer) = " + faction.getRelationTo(damagerFPlayer));
                        Relation relationTo = faction.getRelationTo(damagerFPlayer);
                        if (relationTo == Relation.MEMBER) {
                            System.out.println(2);
                            Map<BoosterType, Pair<Booster, Long>> boosters = faction.getBoosters();
                            System.out.println("boosters = " + boosters);
                            if (!boosters.isEmpty()) {
                                System.out.println(3);
                                double multiplier = 1.0;
                                Pair<Booster, Long> boosterLongPair;
                                if ((boosterLongPair = boosters.get(BoosterType.DAMAGE_INCREASE_IN_MEMBER_TERRITORY)) != null) {
                                    multiplier *= (double) boosterLongPair.getKey().getMeta().get("multiplier");
                                    System.out.println(4);
                                }
                                System.out.println("boosterLongPair = " + boosterLongPair);
                                if ((boosterLongPair = boosters.get(BoosterType.DAMAGE_REDUCTION_IN_MEMBER_TERRITORY)) != null) {
                                    multiplier *= (double) boosterLongPair.getKey().getMeta().get("multiplier");
                                    System.out.println(5);
                                }
                                System.out.println("boosterLongPair = " + boosterLongPair);
                                if (multiplier != 1.0) {
                                    System.out.println(6);
                                    event.setDamage(event.getDamage() * multiplier);
                                }
                                System.out.println("multiplier = " + multiplier);
                            }
                        } else if (relationTo == Relation.ENEMY) {
                            System.out.println(7);
                            FPlayer damagedFPlayer = FPlayers.getInstance().getByPlayer(damaged);
                            System.out.println("damagedFPlayer.getRelationTo(damagerFPlayer) = " + damagedFPlayer.getRelationTo(damagerFPlayer));
                            if (damagedFPlayer.getRelationTo(damagerFPlayer) == Relation.ENEMY) {
                                System.out.println(8);
                                Map<BoosterType, Pair<Booster, Long>> boosters = faction.getBoosters();
                                System.out.println("boosters = " + boosters);
                                if (!boosters.isEmpty()) {
                                    System.out.println(9);
                                    double multiplier = 1.0;
                                    Pair<Booster, Long> boosterLongPair;
                                    if ((boosterLongPair = boosters.get(BoosterType.DAMAGE_INCREASE_IN_ENEMY_TERRITORY)) != null) {
                                        multiplier *= (double) boosterLongPair.getKey().getMeta().get("multiplier");
                                        System.out.println(10);
                                    }
                                    System.out.println("boosterLongPair = " + boosterLongPair);
                                    if ((boosterLongPair = boosters.get(BoosterType.DAMAGE_REDUCTION_IN_ENEMY_TERRITORY)) != null) {
                                        multiplier *= (double) boosterLongPair.getKey().getMeta().get("multiplier");
                                        System.out.println(11);
                                    }
                                    System.out.println("boosterLongPair = " + boosterLongPair);
                                    if (multiplier != 1.0) {
                                        System.out.println(12);
                                        event.setDamage(event.getDamage() * multiplier);
                                    }
                                    System.out.println("multiplier = " + multiplier);
                                }
                            }
                        } else {
                            System.out.println("Some how got to 13");
                        }
                    }
                }).post(P.p);
    }
}
