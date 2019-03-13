package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdClearOwner extends FCommand {

    public CmdClearOwner() {
        super();
        this.aliases.add("clearowner");
        this.aliases.add("clearaccess");

        this.requiredArgs.add("player name");

        this.permission = Permission.OWNER.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = true;
    }

    @Override
    public void perform() {
        boolean hasBypass = fme.isAdminBypassing();

        if (!hasBypass && !assertHasFaction()) {
            return;
        }

        if (!Conf.ownedAreasEnabled) {
            fme.msg(TL.COMMAND_OWNER_DISABLED);
            return;
        }

        FPlayer target = this.argAsBestFPlayerMatch(0, fme);
        if (target == null) {
            return;
        }

        String playerName = target.getName();

        if (target.getFaction() != myFaction) {
            fme.msg(TL.COMMAND_OWNER_NOTMEMBER, playerName);
            return;
        }

        myFaction.clearClaimOwnership(target);
        fme.msg(TL.COMMAND_OWNER_ALL_CLEARED, playerName);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_OWNER_DESCRIPTION;
    }
}
