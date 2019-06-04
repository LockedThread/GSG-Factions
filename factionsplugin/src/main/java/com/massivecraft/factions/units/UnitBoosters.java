package com.massivecraft.factions.units;

import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.massivecraft.factions.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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

                    } else {

                    }



                    /*
                               if ((pair = boosters.get(BoosterType.DAMAGE_INCREASE_IN_MEMBER_TERRITORY)) != null) {
                                multiplier *= pair.getValue();
                            }
                            if ((pair = boosters.get(BoosterType.DAMAGE_REDUCTION_IN_MEMBER_TERRITORY)) != null) {
                                multiplier *= pair.getValue();
                            }
                            if (multiplier != 1.0) {
                                event.setDamage(event.getDamage() * multiplier);
                            }
                     */
                }).post(P.p);
    }
}
