package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdGivePoints extends FCommand {

    public CmdGivePoints() {
        super();
        this.aliases.add("givepoints");
        this.permission = Permission.SETPOINTS.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
        requiredArgs.add("faction");
        requiredArgs.add("points");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_GIVEPOINTS_DESCRIPTION;
    }

    @Override
    public void perform() {
        Faction faction = argAsFaction(0);
        int points = argAsInt(1);
        faction.setPoints(faction.getPoints() + points);
        msg(TL.COMMAND_GIVEPOINTS_SET.format(points, faction.getTag()));
    }
}
