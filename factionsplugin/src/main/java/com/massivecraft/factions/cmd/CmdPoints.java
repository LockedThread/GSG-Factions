package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPoints extends FCommand {

    public CmdPoints() {
        super();
        this.aliases.add("points");
        this.permission = Permission.POINTS.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_POINTS_DESCRIPTION;
    }

    @Override
    public void perform() {
        msg(TL.COMMAND_POINTS_GET.format(myFaction.getPoints()));
    }
}
