package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdOwnerAll extends FCommand {

    public CmdOwnerAll() {
        super();
        this.aliases.add("ownerall");
        this.aliases.add("accessall");

        this.requiredArgs.add("player name");

        this.permission = Permission.OWNER.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
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

        if (!hasBypass && Conf.ownedAreasLimitPerFaction > 0 && myFaction.getCountOfClaimsWithOwners() >= Conf.ownedAreasLimitPerFaction) {
            fme.msg(TL.COMMAND_OWNER_LIMIT, Conf.ownedAreasLimitPerFaction);
            return;
        }

        Access access = myFaction.getAccess(fme, PermissableAction.ACCESS);
        if (access == Access.DENY || (access == Access.UNDEFINED && !assertMinRole(Role.COLEADER))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "access");
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

        myFaction.grantAllClaimOwnership(target);
        fme.msg(TL.COMMAND_OWNER_ALL_ADDED, playerName);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_OWNER_DESCRIPTION;
    }
}
