package com.massivecraft.factions.cmd;

import com.gameservergroup.gsgcore.utils.TimeUtil;
import com.google.common.base.Joiner;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.StrikeInfo;
import com.massivecraft.factions.zcore.util.TL;

import java.util.Set;

public class CmdStrike extends FCommand {

    public CmdStrike() {
        super();

        this.aliases.add("strike");
        this.aliases.add("strikes");

        this.optionalArgs.put("faction", "you");
        this.errorOnToManyArgs = false;

        this.permission = Permission.STRIKE.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (senderIsConsole || sender.hasPermission(Permission.BYPASS.node)) {
            handleAuthorizedInput();
        } else {
            handleRegularInput();
        }
    }

    private void handleAuthorizedInput() {
        if (!argIsSet(1)) {
            msg("&cToo few arguments. &eUse like this:");
            msg("&b/f strike <give|take|view> <faction> [details]");
            return;
        }

        Faction target = argAsFaction(1, null);
        if (target == null) {
            msg("&cNo Faction found matching '" + argAsString(1) + "'");
            return;
        }

        if (argAsString(0).equalsIgnoreCase("give")) {
            if (!argIsSet(3)) {
                msg("&cYou must enter a few words to describe what their Strike is for!");
                return;
            }
            String reason = Joiner.on(" ").join(args.subList(2, args.size()));
            target.strike(sender, reason);
            msg("&aYou have successfully given " + target.getTag() + " a Strike for '" + reason + "'");
        } else if (argAsString(0).equalsIgnoreCase("take")) {
            if (target.getStrikes().isEmpty()) {
                msg("&c" + target.getTag() + " doesn't have any Strikes!");
                return;
            }

            target.destrike(sender);
            msg("&aSuccessfully removed the most recent Strike from " + target.getTag());
        } else if (argAsString(0).equalsIgnoreCase("view")) {
            if (target.getStrikes().isEmpty()) {
                msg("&c" + target.getTag() + " doesn't have any Strikes!");
                return;
            }

            Set<StrikeInfo> strikes = target.getStrikes();
            msg("&e" + target.getTag() + " currently has " + strikes.size() + " strike" + (strikes.size() == 1 ? ":" : (strikes.size() == 0 ? "s" : "s:")));
            for (StrikeInfo strike : strikes) {
                msg("   &a" + getTimeString(strike.getIssuedAt()) + "&f from &a" + strike.getIssuerName() + "&f for &c" + strike.getDescription());
            }
        } else {
            msg("&b/f strike <give|take> <faction>");
        }
    }

    private void handleRegularInput() {
        if (!fme.hasFaction()) {
            msg("&cYou must be in a Faction to use that command!");
            return;
        }

        Set<StrikeInfo> strikes = myFaction.getStrikes();
        msg("&eYour Faction currently has " + strikes.size() + " strike" + (strikes.size() == 1 ? ":" : (strikes.size() == 0 ? "s" : "s:")));
        for (StrikeInfo strike : strikes) {
            msg("   &a" + getTimeString(strike.getIssuedAt()) + "&f from &a" + strike.getIssuerName() + "&f for &c" + strike.getDescription());
        }
    }

    private String getTimeString(long timestamp) {
        return TimeUtil.toShortForm((System.currentTimeMillis() - timestamp) / 1000) + " ago";
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STRIKE_DESCRIPTION;
    }
}
