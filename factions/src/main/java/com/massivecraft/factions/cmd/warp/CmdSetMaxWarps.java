package com.massivecraft.factions.cmd.warp;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetMaxWarps extends FCommand {

    public CmdSetMaxWarps() {
        this.aliases.add("setmaxwarps");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("new max warps");

        this.permission = Permission.BYPASS.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = this.argAsFaction(0, myFaction);
        if (faction == null) {
            return;
        }

        Integer newSize = this.argAsInt(1, -2);
        if (newSize < -1) {
            msg("<b>New max warp limit must be greater than or equal to -1");
            return;
        }

        faction.setMaxWarps(newSize);
        msg("<i>Successfully updated " + faction.getTag() + "'s max warps limit to " + newSize);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_INFINITY;
    }

}
