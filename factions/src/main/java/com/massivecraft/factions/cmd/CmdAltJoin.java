package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdAltJoin extends FCommand {

    private static final int MAX_ALTS_PER_FACTION = 100;

    public CmdAltJoin() {
        super();
        this.aliases.add("altjoin");

        this.requiredArgs.add("faction name");
        this.optionalArgs.put("player", "you");

        this.permission = Permission.ALTS_JOIN.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = this.argAsFaction(0);
        if (faction == null) {
            return;
        }

        FPlayer fplayer = this.argAsBestFPlayerMatch(1, fme, false);
        boolean samePlayer = fplayer == fme;

        if (!samePlayer && !Permission.ALTS_JOIN_OTHERS.has(sender, false)) {
            msg(TL.COMMAND_ALTS_CANNOTFORCE);
            return;
        }

        if (!faction.isNormal()) {
            msg(TL.COMMAND_ALTS_SYSTEMFACTION);
            return;
        }

        if (fplayer.hasAltFaction()) {
            msg("<b>You are already registered as an alt account for a Faction!");
            return;
        }

        if (faction.getAltSize() >= MAX_ALTS_PER_FACTION) {
            msg("<b>" + faction.getTag() + " has reached the Faction alt limit of " + MAX_ALTS_PER_FACTION + " accounts!");
            return;
        }

        if (fplayer.hasFaction()) {
            msg(TL.COMMAND_JOIN_INOTHERFACTION, fplayer.describeTo(fme, true), (samePlayer ? "your" : "their"));
            return;
        }

        if (!faction.isAltInvited(fplayer)) {
            msg("<b>You have not been invited to join " + faction.getTag() + " as an alt account");
            return;
        }

        // Check for ban
        if (!fme.isAdminBypassing() && faction.isBanned(fme)) {
            fme.msg(TL.COMMAND_JOIN_BANNED, faction.getTag(fme));
            return;
        }

        msg("<a>You have successfully joined " + faction.getTag() + " as an alt account");
        faction.msg("<a>" + fplayer.getName() + " has joined " + faction.getTag() + " as an alt account");
        fplayer.setAltFaction(faction);
        faction.addAltPlayer(fplayer);
        faction.deinviteAlt(fplayer);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_JOIN_DESCRIPTION;
    }
}
