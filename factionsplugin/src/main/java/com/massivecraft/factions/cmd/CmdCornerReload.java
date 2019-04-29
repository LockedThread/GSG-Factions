package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.Set;

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
        Set<FLocation> corners = p.getFactionsPlayerListener().getCorners();
        corners.clear();
        for (World world : p.getServer().getWorlds()) {
            WorldBorder border = world.getWorldBorder();
            if (border != null) {
                int cornerCoord = (int) ((border.getSize() - 1D) / 2D);
                corners.add(new FLocation(world.getName(), FLocation.blockToChunk(cornerCoord), FLocation.blockToChunk(cornerCoord)));
                corners.add(new FLocation(world.getName(), FLocation.blockToChunk(cornerCoord), FLocation.blockToChunk(-cornerCoord)));
                corners.add(new FLocation(world.getName(), FLocation.blockToChunk(-cornerCoord), FLocation.blockToChunk(cornerCoord)));
                corners.add(new FLocation(world.getName(), FLocation.blockToChunk(-cornerCoord), FLocation.blockToChunk(-cornerCoord)));
            }
        }
        msg("&aYou have reload " + p.getServer().getWorlds().size() * 4 + " corners");
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CORNER_RELOAD_DESCRIPTION;
    }
}
