package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetMaxTNTBankBalance extends FCommand {

    public CmdSetMaxTNTBankBalance() {
        this.aliases.add("setmaxtntbankbalance");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("new max tntbank balance");

        this.permission = Permission.BYPASS.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = this.argAsFaction(0, myFaction);
        if (faction == null) {
            return;
        }

        int newSize = this.argAsInt(1, -2);

        if (faction.getTntBankBalance() > newSize) {
            faction.setTntBankBalance(newSize);
        }

        faction.setTntBankLimit(newSize);
        msg("<i>Successfully updated " + faction.getTag() + "'s max tntbank balance to " + newSize);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETMAXTNTBANKBALANCE_DESCRIPTION;
    }
}
