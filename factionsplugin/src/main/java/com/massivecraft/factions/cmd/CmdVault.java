package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
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
        if (fme.canUseFactionVault()) {
            me.openInventory(myFaction.getFactionChest().getInventory());
        } else {
            fme.msg(TL.GENERIC_NOPERMISSION, "chest");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VAULT_DESCRIPTION;
    }
}
