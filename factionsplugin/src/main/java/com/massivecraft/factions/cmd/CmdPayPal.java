package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPayPal extends FCommand {

    public CmdPayPal() {
        super();
        this.aliases.add("paypal");

        this.permission = Permission.PAYPAL.node;
        this.optionalArgs.put("faction", "you");

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = true;
        senderMustBeAdmin = true;
    }

    @Override
    public void perform() {
        if (args.size() == 0) {
            if (myFaction.getPayPal().isEmpty()) {
                msg(TL.COMMAND_PAYPAL_NO_PAYPAL_SET);
            } else {
                msg(TL.COMMNAD_PAYPAL_INFO, myFaction.getPayPal());
            }
        } else if (args.size() == 1) {
            if (fme.isAdminBypassing() && Permission.BYPASS.has(me)) {
                Faction faction = argAsFaction(0);
                if (faction != null) {
                    if (faction.getPayPal().isEmpty()) {
                        msg(TL.COMMAND_PAYPAL_ADMIN_NO_PAYPAL_SET, faction.getTag());
                    } else {
                        msg(TL.COMMAND_PAYPAL_ADMIN_INFO, faction.getTag(), faction.getPayPal());
                    }
                }
            } else {
                msg(TL.GENERIC_NOPERMISSION, "execute /paypal [faction]");
            }
        } else {
            msg(TL.COMMAND_INVALID_ARGUMENTS);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PAYPAL_DESCRIPTION;
    }
}
