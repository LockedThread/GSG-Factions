package com.massivecraft.factions.cmd.alts;

import com.google.common.base.Joiner;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

import java.util.stream.Collectors;

public class CmdAltsList extends FCommand {

    public CmdAltsList() {
        super();
        this.aliases.add("l");
        this.aliases.add("list");

        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.ALTS_LIST.node;
        this.setHelpShort(TL.COMMAND_ALTS_LIST.toString());

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
        Faction faction = myFaction;
        if (this.argIsSet(0)) {
            faction = this.argAsFaction(0);
        }

        if (faction == null) {
            return;
        }
        if (faction != myFaction && !Permission.ALTS_LIST_ANY.has(sender, true)) {
            return;
        }

        msg("<a>There are " + faction.getAltSize() + " alts in " + faction.getTag() + ":");
        msg("<i>" + Joiner.on(", ").join(faction.getAltPlayers().stream().map(FPlayer::getName).collect(Collectors.toList())));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_LIST;
    }

}
