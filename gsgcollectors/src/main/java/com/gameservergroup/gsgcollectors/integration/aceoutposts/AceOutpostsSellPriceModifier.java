package com.gameservergroup.gsgcollectors.integration.aceoutposts;

import com.gameservergroup.gsgcollectors.integration.SellPriceModifier;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import me.aceix8.outposts.AceOutposts;
import org.bukkit.entity.Player;

public class AceOutpostsSellPriceModifier implements SellPriceModifier {

    @Override
    public double getModifiedPrice(Faction faction, double regularSellPrice) {
        return (AceOutposts.getInstance().getApi().isFactionControllingAnOutpost(faction) ? 2 : 1) * regularSellPrice;
    }

    @Override
    public double getModifiedPrice(Player player, double regularSellPrice) {
        return (AceOutposts.getInstance().getApi().isFactionControllingAnOutpost(FPlayers.getInstance().getByPlayer(player).getFaction()) ? 2 : 1) * regularSellPrice;
    }
}
