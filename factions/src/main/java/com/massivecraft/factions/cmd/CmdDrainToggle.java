package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdDrainToggle extends FCommand {

    public CmdDrainToggle() {
        super();
        this.aliases.add("toggle");

        this.permission = Permission.DRAIN_TOGGLE.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
    }

    @Override
    public void perform() {
        Access access = myFaction.getAccess(fme, PermissableAction.F_DRAIN_TOGGLE);
        if (access == Access.DENY || (access == Access.UNDEFINED && !assertMinRole(Role.MODERATOR))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "f drain toggle");
            return;
        }
        if (fme.isDrainEnabled()) {
            msg(TL.COMMAND_DRAIN_TOGGLE_DISABLED);
        } else {
            msg(TL.COMMAND_DRAIN_TOGGLE_ENABLED);
        }
        fme.setDrainEnabled(!fme.isDrainEnabled());
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
