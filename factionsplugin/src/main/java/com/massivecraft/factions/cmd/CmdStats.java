package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.factionstatistics.FactionStatistic;
import com.massivecraft.factions.zcore.util.TL;

import java.util.Map;

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
        String value = "";
        for (Map.Entry<FactionStatistic, Integer> entry : fPlayer.getFactionStatisticMap().entrySet()) {
            switch (entry.getKey()) {
                case KILLS:
                    value = String.valueOf(fPlayer.getKills());
                    break;
                case DEATHS:
                    value = String.valueOf(fPlayer.getDeaths());
                    break;
                case BLOCKS_PLACED:
                    value = String.valueOf()
                    break;
                case BLOCKS_BROKEN:
                    break;
                case TIME_PLAYED:
                    break;
            }
        }
        if (entry.getKey() == FactionStatistic.TIME_PLAYED) msg(TL.COMMAND_STATISTICS_ROW.toString()
                .replace("{index}", entry.getKey().toPrettyName())
                .replace("{amount}", ""));
        else msg(TL.COMMAND_STATISTICS_ROW.toString()
                .replace("{index}", entry.getKey().toPrettyName())
                .replace("{amount}", String.valueOf(entry.getValue())));

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STATISTICS_DESCRIPTION;
    }


}
