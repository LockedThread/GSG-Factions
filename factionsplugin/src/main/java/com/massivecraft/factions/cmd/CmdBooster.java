package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdBooster extends FCommand {

    public CmdBooster() {
        super();
        this.aliases.add("booster");

        this.permission = Permission.BOOSTER.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.helpLong.add(p.txt.parseTags(TL.COMMAND_MONEY_LONG.toString()));

        this.addSubCommand(new CmdBoosterGet());
        this.addSubCommand(new CmdBoosterSet());
        this.addSubCommand(new CmdBoosterList());
    }

    @Override
    public void perform() {
        this.commandChain.add(this);
        P.p.cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BOOSTER_DESCRIPTION;
    }

}
