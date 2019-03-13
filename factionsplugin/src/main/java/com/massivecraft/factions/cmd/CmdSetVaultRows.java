package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetVaultRows extends FCommand {

    public CmdSetVaultRows() {
        this.aliases.add("setvaultrows");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("new vault size");

        this.permission = Permission.BYPASS.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = true;
    }

    @Override
    public void perform() {
        Faction faction = this.argAsFaction(0, myFaction);
        if (faction == null) {
            return;
        }

        Integer newSize = this.argAsInt(1, -1);
        if (newSize <= faction.getVaultRows() || newSize > 6) {
            msg("<b>New vault size must be >" + faction.getVaultRows() + " and <7 for " + faction.getTag());
            return;
        }

        faction.setVaultRows(newSize);
        msg("<i>Successfully updated " + faction.getTag() + "'s vault row count to " + newSize);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_INFINITY;
    }

}
