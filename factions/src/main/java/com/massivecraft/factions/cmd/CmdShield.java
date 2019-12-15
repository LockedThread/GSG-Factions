package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdShield extends FCommand {

    public CmdShield() {
        super();
        this.aliases.add("shield");

        this.permission = Permission.SHIELD.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {

    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHIELD_DESCRIPTION;
    }
}
