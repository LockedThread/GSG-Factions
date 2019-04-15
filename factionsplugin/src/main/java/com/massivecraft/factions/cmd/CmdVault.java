package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdVault extends FCommand {

    public CmdVault() {
        this.aliases.add("vault");
        this.aliases.add("chest");

        this.permission = Permission.VAULT.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Access access = myFaction.getAccess(fme, PermissableAction.CHEST);
        if (access == Access.DENY || (access == Access.UNDEFINED && !assertMinRole(Role.MODERATOR))) {
            if (!fme.isAdminBypassing()) {
                fme.msg(TL.GENERIC_NOPERMISSION, "chest");
                return;
            }
        }
        me.openInventory(myFaction.getFactionChest().getInventory());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VAULT_DESCRIPTION;
    }
}
