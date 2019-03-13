package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdLeave extends FCommand {

    public CmdLeave() {
        super();
        this.aliases.add("leave");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.LEAVE.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (!fme.hasFaction() && !fme.hasAltFaction()) {
            msg("<b>You are not a member of a Faction");
            return;
        }

        fme.leave(true);
        if (fme.hasAltFaction()) {
            fme.resetAltFactionData();
            fme.msg("<b>You have left your Faction as an alt account");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.LEAVE_DESCRIPTION;
    }

}
