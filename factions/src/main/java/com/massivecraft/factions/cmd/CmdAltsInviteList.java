package com.massivecraft.factions.cmd;

import com.google.common.base.Joiner;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CmdAltsInviteList extends FCommand {

    private static final int DISPLAY_PER_PAGE = 20;

    public CmdAltsInviteList() {
        super();
        this.aliases.add("invlist");
        this.aliases.add("invitelist");

        this.optionalArgs.put("page", "1");
        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.ALTS_INVITE_LIST.node;

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
        if (this.argIsSet(1)) {
            faction = this.argAsFaction(1);
        }

        if (faction == null) {
            return;
        }
        if (faction != myFaction && !Permission.ALTS_INVITE_LIST_ANY.has(sender, true)) {
            return;
        }

        List<String> invites = faction.getAltInvites();

        p.getServer().getScheduler().runTaskAsynchronously(p, () -> {
            int page = argAsInt(0, 1);
            int total = invites.size();
            int pages = total / DISPLAY_PER_PAGE;
            if (total % DISPLAY_PER_PAGE != 0)
                pages++;
            if (page > pages) {
                page = pages;
            }
            if (page < 1) {
                page = 1;
            }

            List<String> toDisplay = invites.subList(((page - 1) * 20), Math.min((page) * 20, invites.size()));
            String names = Joiner.on("&3, &b").join(toDisplay.stream().map(id -> FPlayers.getInstance().getById(id)).filter(Objects::nonNull)
                    .map(FPlayer::getName).collect(Collectors.toList()));

            msg("&3Pending invites &7(Page " + page + "/" + pages + ")");
            msg("&b" + names);
        });
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_INVLIST;
    }

}
