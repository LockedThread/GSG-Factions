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

public class CmdAltsRevoke extends FCommand {

    public CmdAltsRevoke() {
        super();
        this.aliases.add("r");
        this.aliases.add("revoke");

        this.requiredArgs.add("players|all");

        this.permission = Permission.ALTS_REVOKE.node;
        this.setHelpShort(TL.COMMAND_ALTS_REVOKE.toString());

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
        if (!argAsString(0).equalsIgnoreCase("all")) {
            for (String arg : args) {
                FPlayer target = strAsBestFPlayerMatch(arg, null, true);
                if (target != null) {
                    alts.add(target);
                }
            }

            if (alts.isEmpty()) {
                return;
            }
        }

        if (alts.isEmpty()) {
            myFaction.deinviteAllAlts();
            msg("<a>Successfully revoked &nall alt invites");
        } else {
            for (FPlayer alt : alts) {
                myFaction.deinviteAlt(alt);
            }
            msg("<a>Successfully revoked invites for " + Joiner.on(", ").join(alts.stream().map(FPlayer::getName).collect(Collectors.toList())));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_REVOKE;
    }

}
