package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;

public class CmdAlts extends FCommand {

    public CmdAltsList cmdAltsList = new CmdAltsList();
    public CmdAltsInvite cmdAltsInvite = new CmdAltsInvite();
    public CmdAltsRevoke cmdAltsRevoke = new CmdAltsRevoke();
    public CmdAltsInviteList cmdAltsInviteList = new CmdAltsInviteList();
    public CmdAltsOpen cmdAltsOpen = new CmdAltsOpen();

    public CmdAlts() {
        super();
        this.aliases.add("alts");

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.helpLong.add(p.txt.parseTags(TL.COMMAND_ALTS_LONG.toString()));

        this.addSubCommand(this.cmdAltsList);
        this.addSubCommand(this.cmdAltsInvite);
        this.addSubCommand(this.cmdAltsRevoke);
        this.addSubCommand(this.cmdAltsInviteList);
        this.addSubCommand(this.cmdAltsOpen);
    }

    @Override
    public void perform() {
        this.commandChain.add(this);
        P.p.cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALTS_DESCRIPTION;
    }

}
