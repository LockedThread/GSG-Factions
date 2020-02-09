package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdCheck extends FCommand {

    public CmdCheck() {
        this.aliases.add("check");
        this.requiredArgs.add("minutes");

        this.permission = Permission.CHECK.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHECK_DESCRIPTION;
    }

    @Override
    public void perform() {
        Access access = myFaction.getAccess(fme, PermissableAction.CHECK);
        if ((access == Access.DENY || (access == Access.UNDEFINED && !assertMinRole(Role.ADMIN))) && !fme.isAdminBypassing()) {
            fme.msg(TL.GENERIC_NOPERMISSION, "check");
            return;
        }
        try {
            int minutes = argAsInt(0);
            if (minutes <= 0) {
                msg(TL.COMMAND_CHECK_NEGATIVE_MINUTES);
            } else {
                myFaction.setCheckReminderMinutes(minutes);
                msg(TL.COMMAND_CHECK_SUCCESS.format(minutes));
            }
        } catch (Exception e) {
            msg(TL.COMMAND_INVALID_ARGUMENTS);
        }
    }
}
