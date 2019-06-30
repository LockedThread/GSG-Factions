package com.massivecraft.factions.cmd;

import com.massivecraft.factions.zcore.util.TL;

public class CmdNotifications extends FCommand {

    public CmdNotifications() {
        super();
        this.aliases.add("notifications");
        this.aliases.add("notifs");
        this.aliases.add("messages");

        this.optionalArgs.put("on/off", "flip");

        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        if (args.size() == 0) {
            toggleNotifications(!fme.hasNotificationsEnabled());
        } else if (args.size() == 1) {
            toggleNotifications(argAsBool(0));
        }
    }

    private void toggleNotifications(final boolean toggle) {
        fme.setNotificationsEnabled(toggle);
        if (toggle) {
            fme.msg("<i>You have turned Faction land notifications ON");
        } else {
            fme.msg("<b>You have turned Faction land notifications OFF");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_NOTIFICATIONS_DESCRIPTION;
    }
}
