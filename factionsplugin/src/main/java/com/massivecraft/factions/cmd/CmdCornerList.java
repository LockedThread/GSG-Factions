package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdCornerList extends FCommand {

    public CmdCornerList() {
        super();
        this.aliases.add("cornerlist");

        this.permission = Permission.CORNER_LIST.node;

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
        return TL.COMMAND_CORNERLIST_DESCRIPTION;
    }
}
