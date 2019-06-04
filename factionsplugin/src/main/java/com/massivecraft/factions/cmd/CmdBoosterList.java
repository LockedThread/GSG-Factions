package com.massivecraft.factions.cmd;

import com.google.common.base.Joiner;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.factionboosters.Booster;
import com.massivecraft.factions.zcore.util.TL;

public class CmdBoosterList extends FCommand {

    public CmdBoosterList() {
        super();
        this.aliases.add("list");

        this.permission = Permission.BOOSTER.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        msg("&eFaction Boosters: " + Joiner.on(", ").skipNulls().join(Booster.getBoosterMap().keySet()));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BOOSTER_LIST_DESCRIPTION;
    }
}