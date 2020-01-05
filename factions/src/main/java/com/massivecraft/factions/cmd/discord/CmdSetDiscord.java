package com.massivecraft.factions.cmd.discord;

import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetDiscord extends FCommand {

    public CmdSetDiscord() {
        super();
        this.aliases.add("setdiscord");

        this.permission = Permission.SET_DISCORD.node;

        this.requiredArgs.add("discord");

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (args.size() == 1) {
            if (isDiscordInvite(argAsString(0))) {
                myFaction.setDiscord(argAsString(0));
                msg(TL.COMMAND_SET_DISCORD_SUCCESS, argAsString(0));
            } else {
                msg(TL.COMMAND_SET_DISCORD_NOT_EMAIL, argAsString(0));
            }
        } else {
            msg(TL.COMMAND_INVALID_ARGUMENTS);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SET_DISCORD_DESCRIPTION;
    }

    private boolean isDiscordInvite(String invite) {
        return invite.contains("discord.gg/");
    }
}

