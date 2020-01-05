package com.massivecraft.factions.cmd.chat;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdFF extends FCommand {

    public CmdFF() {
        super();

        this.permission = Permission.CHAT.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (fme.getChatMode() == ChatMode.FACTION) {
            fme.setChatMode(ChatMode.PUBLIC);
            msg(TL.COMMAND_CHAT_MODE_PUBLIC);
        } else {
            fme.setChatMode(ChatMode.FACTION);
            msg(TL.COMMAND_CHAT_MODE_FACTION);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CHAT_DESCRIPTION;
    }
}
