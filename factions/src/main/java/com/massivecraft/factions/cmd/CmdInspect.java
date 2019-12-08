package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdInspect extends FCommand {

    public CmdInspect() {
        super();

        this.aliases.add("inspect");

        this.permission = Permission.INSPECT.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_INSPECT_DESCRIPTION;
    }

    @Override
    public void perform() {
        Access access = myFaction.getAccess(fme, PermissableAction.INSPECT);
        if (access == Access.DENY || (access == Access.UNDEFINED && !assertMinRole(Role.MODERATOR))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "inspect");
            return;
        }

        if (fme.isInspecting()) {
            sendMessage(TL.COMMAND_INSPECT_DISABLED.toString());
            fme.setInspecting(false);
        } else {
            sendMessage(TL.COMMAND_INSPECT_ENABLED.toString());
            fme.setInspecting(true);
        }
    }
}
