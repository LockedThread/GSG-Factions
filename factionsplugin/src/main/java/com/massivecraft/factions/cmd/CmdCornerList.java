package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.HashSet;
import java.util.Set;

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
        Set<FLocation> corners = p.getFactionsPlayerListener().getCorners();
        int changed = 0;
        for (World world : p.getServer().getWorlds()) {
            WorldBorder border = world.getWorldBorder();
            if (border != null) {
                int cornerCoord = (int) ((border.getSize() - 1D) / 2D);
                Set<FLocation> local = new HashSet<>(4);
                local.add(new FLocation(world.getName(), FLocation.blockToChunk(cornerCoord), FLocation.blockToChunk(cornerCoord)));
                local.add(new FLocation(world.getName(), FLocation.blockToChunk(cornerCoord), FLocation.blockToChunk(-cornerCoord)));
                local.add(new FLocation(world.getName(), FLocation.blockToChunk(-cornerCoord), FLocation.blockToChunk(cornerCoord)));
                local.add(new FLocation(world.getName(), FLocation.blockToChunk(-cornerCoord), FLocation.blockToChunk(-cornerCoord)));

                // check if claimed
                local.removeIf(floc -> {
                    Faction at = Board.getInstance().getFactionAt(floc);
                    if (corners.contains(floc)) {
                        corners.remove(floc);
                        return true;
                    }
                    return (at != null && at.isNormal());
                });

                if (local.size() > 0) {
                    corners.addAll(local);
                    changed += local.size();
                }
            }
        }
        if (changed == 0) {
            msg("&cThere are no changed corners to reload!");
        } else {
            msg("&aYou have reload " + changed + " corners");
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
