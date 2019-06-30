package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdUpgrades extends FCommand {

    public CmdUpgrades() {
        this.aliases.add("upgrade");
        this.aliases.add("upgrades");

        this.permission = Permission.UPGRADES.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        fme.openFactionUpgradeMenu();
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UPGRADES_DESCRIPTION;
    }
}
