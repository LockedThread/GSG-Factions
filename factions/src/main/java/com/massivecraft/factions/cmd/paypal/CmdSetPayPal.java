package com.massivecraft.factions.cmd.paypal;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetPayPal extends FCommand {

    public CmdSetPayPal() {
        super();
        this.aliases.add("setpaypal");

        this.permission = Permission.SET_PAYPAL.node;

        this.optionalArgs.put("faction", "you");
        this.requiredArgs.add("email");

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (args.size() == 1) {
            if (isEmail(argAsString(0))) {
                myFaction.setPayPal(argAsString(0));
                msg(TL.COMMAND_SET_PAYPAL_SUCCESS, argAsString(0));
            } else {
                msg(TL.COMMAND_SET_PAYPAL_NOT_EMAIL, argAsString(0));
            }
        } else if (args.size() == 2) {
            if (fme.isAdminBypassing() && Permission.BYPASS.has(me)) {
                Faction faction = argAsFaction(1);
                if (faction != null) {
                    if (isEmail(argAsString(0))) {
                        myFaction.setPayPal(argAsString(0));
                        msg(TL.COMMAND_PAYPAL_ADMIN_INFO, faction.getTag(), argAsString(0));
                    } else {
                        msg(TL.COMMAND_PAYPAL_ADMIN_NO_PAYPAL_SET, argAsString(0));
                    }
                }
            } else {
                msg(TL.GENERIC_NOPERMISSION, "execute /setpaypal [faction] [email]");
            }
        } else {
            msg(TL.COMMAND_INVALID_ARGUMENTS);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SET_PAYPAL_DESCRIPTION;
    }

    private boolean isEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
