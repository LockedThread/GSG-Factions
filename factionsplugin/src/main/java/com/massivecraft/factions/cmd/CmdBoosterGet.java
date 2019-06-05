package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.factionboosters.Booster;
import com.massivecraft.factions.zcore.util.TL;
import org.apache.commons.lang3.tuple.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CmdBoosterGet extends FCommand {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE, MMM d, HH:mm:ss z");

    public CmdBoosterGet() {
        super();
        this.aliases.add("get");

        this.permission = Permission.BOOSTER.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.requiredArgs.add("faction");
    }

    @Override
    public void perform() {
        Faction faction = argAsFaction(0, myFaction);
        if (faction.isWilderness() || faction.isSafeZone() || faction.isWarZone()) {
            msg("&c" + faction.getTag() + " doesn't have a any boosters");
        } else if (faction.getBoosters().isEmpty()) {
            msg("&c" + faction.getTag() + " doesn't have any active boosters");
        } else {
            msg("&e" + faction.getTag() + "'s Active Boosters");
            for (Pair<Booster, Long> pair : faction.getBoosters().values()) {
                msg("&e" + pair.getKey().getId() + " - " + SIMPLE_DATE_FORMAT.format(new Date(pair.getValue())));
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BOOSTER_GET_DESCRIPTION;
    }
}
