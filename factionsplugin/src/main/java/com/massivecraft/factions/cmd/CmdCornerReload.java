package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdCornerReload extends FCommand {

    public CmdCornerReload() {
        super();
        this.aliases.add("cornerreload");

        this.permission = Permission.BYPASS.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        msg(p.txt.titleize("Corners"));
        for (FLocation fLocation : p.getFactionsPlayerListener().getCorners()) {
            msg("&e" + fLocation.getWorldName() + " : " + getFormattedCornerCoords(fLocation) + " : " + Board.getInstance().getFactionAt(fLocation).getTag());
        }
    }

    private String getFormattedCornerCoords(FLocation fLocation) {
        return (fLocation.getX() < 0 ? "-" : "+") + (fLocation.getZ() < 0 ? "-" : "+");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CORNER_DESCRIPTION;
    }
}
