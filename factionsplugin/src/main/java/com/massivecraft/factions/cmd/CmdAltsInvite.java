package com.massivecraft.factions.cmd;

import com.google.common.base.Joiner;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CmdAltsInvite extends FCommand {

    public CmdAltsInvite() {
        super();
        this.aliases.add("i");
        this.aliases.add("inv");
        this.aliases.add("invite");

        this.requiredArgs.add("players");

        this.permission = Permission.ALTS_INVITE.node;

        senderMustBePlayer = false;
        senderMustBeMember = true;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Access access = myFaction.getAccess(fme, PermissableAction.ALTS);
        if (access == Access.DENY || (access == Access.UNDEFINED && !fme.getRole().isAtLeast(Role.COLEADER))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "alts");
            return;
        }
        Set<FPlayer> alts = new HashSet<>();
        for (String arg : args) {
            FPlayer target = strAsBestFPlayerMatch(arg, null, true);
            if (target != null) {
                if (target.hasFaction() || target.hasAltFaction()) {
                    msg("<b>" + target.getName() + " is already in a Faction!");
                } else {
                    alts.add(target);
                }
            }
        }

        if (alts.isEmpty()) {
            return;
        }

        for (FPlayer alt : alts) {
            myFaction.inviteAlt(alt);
            if (alt.isOnline()) {
                alt.msg("<a>You have been invited to join " + myFaction.getTag() + " as an alt account");
                alt.msg("<i>Use &n/f altjoin " + myFaction.getTag() + "<i> to join");
            }
        }

        msg("<a>Successfully invited " + Joiner.on(", ").join(alts.stream().map(FPlayer::getName).collect(Collectors.toList())));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_INVITE;
    }

}
