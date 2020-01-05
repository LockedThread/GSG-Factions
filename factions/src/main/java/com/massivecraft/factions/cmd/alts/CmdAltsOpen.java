package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdAltsOpen extends FCommand {

    public CmdAltsOpen() {
        super();
        this.aliases.add("open");

        this.permission = Permission.ALTS_OPEN.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Access access = myFaction.getAccess(fme, PermissableAction.ALTS);
        if (access == Access.DENY || (access == Access.UNDEFINED && !fme.getRole().isAtLeast(Role.COLEADER))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "alts");
            return;
        }

        msg(TL.COMMAND_ALTS_OPEN_TOGGLE.format(myFaction.isAltInvitesOpen() ? "&coff" : "&aon"));
        myFaction.setAltInvitesOpen(!myFaction.isAltInvitesOpen());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_OPEN_DESCRIPTION;
    }
}
