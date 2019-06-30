package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdLock extends FCommand {

    public CmdLock() {
        super();
        this.aliases.add("lock");

        //this.requiredArgs.add("");
        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.LOCK.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        p.setLocked(this.argAsBool(0, !p.getLocked()));
        msg(p.getLocked() ? TL.COMMAND_LOCK_LOCKED : TL.COMMAND_LOCK_UNLOCKED);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_LOCK_DESCRIPTION;
    }
}
