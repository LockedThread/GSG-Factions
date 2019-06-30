package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.factionstatistics.FactionStatistic;
import com.massivecraft.factions.zcore.util.TL;

public class CmdStats extends FCommand {

    public CmdStats() {
        super();
        this.aliases.add("stats");
        this.aliases.add("statistics");

        this.permission = Permission.STATISTICS.node;

        this.optionalArgs.put("player", "you");

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FPlayer fPlayer = argAsFPlayer(0, fme);
        msg(p.txt.titleize(fPlayer.getName() + "'s Stats"));
        for (FactionStatistic factionStatistic : FactionStatistic.values()) {
            msg(TL.COMMAND_STATISTICS_ROW.toString()
                    .replace("{index}", factionStatistic.toPrettyName())
                    .replace("{amount}", factionStatistic == FactionStatistic.TIME_PLAYED ? fPlayer.getFormattedTimePlayed() + " hours" : String.valueOf(fPlayer.getFactionStatistic(factionStatistic))));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STATISTICS_DESCRIPTION;
    }


}
