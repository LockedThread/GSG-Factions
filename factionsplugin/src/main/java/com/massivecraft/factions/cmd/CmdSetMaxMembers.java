package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetMaxMembers extends FCommand {

    public CmdSetMaxMembers() {
        this.aliases.add("setmaxmembers");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("new max members");

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
            msg("<b>New max members limit must be greater than or equal to -1");
            return;
        }

        faction.setMaxMembers(newSize);
        msg("<i>Successfully updated " + faction.getTag() + "'s max members limit to " + newSize);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_INFINITY;
    }

}
