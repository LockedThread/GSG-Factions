package com.massivecraft.factions.cmd.chat;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdMuteChat extends FCommand {

    public CmdMuteChat() {
        super();
        this.aliases.add("mutechat");

        this.permission = Permission.MUTECHAT.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
    }

    @Override
    public void perform() {
        if (fme.isMutedChatEnabled()) {
            msg(TL.COMMAND_MUTECHAT_DISABLED);
            fme.setMutedChat(false);
        } else {
            msg(TL.COMMAND_MUTECHAT_ENABLED);
            fme.setMutedChat(true);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MUTECHAT_DESCRIPTION;
    }

}
