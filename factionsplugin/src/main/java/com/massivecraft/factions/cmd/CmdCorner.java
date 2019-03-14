package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.tasks.TaskCorner;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CmdCorner extends FCommand {

    public CmdCorner() {
        super();
        this.aliases.add("corner");

        this.permission = Permission.CLAIM_RADIUS.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        FLocation to = new FLocation(me.getLocation());

        if (p.getFactionsPlayerListener().getCorners().contains(to)) {
            Faction cornerAt = Board.getInstance().getFactionAt(to);

            if (cornerAt != null && cornerAt.isNormal() && !cornerAt.equals(fme.getFaction())) {
                msg(TL.COMMAND_CORNER_CANT_CLAIM);
            } else {
                msg(TL.COMMAND_CORNER_ATTEMPTING);
                List<FLocation> surrounding = new ArrayList<>(400);
                for (int x = 0; x < Conf.bufferSize; x++) {
                    for (int z = 0; z < Conf.bufferSize; z++) {
                        int newX = (int) (to.getX() > 0 ? (to.getX() - x) : (to.getX() + x));
                        int newZ = (int) (to.getZ() > 0 ? (to.getZ() - z) : (to.getZ() + z));

                        FLocation location = new FLocation(me.getWorld().getName(), newX, newZ);
                        Faction at = Board.getInstance().getFactionAt(location);
                        if (at == null || !at.isNormal()) {
                            surrounding.add(location);
                        }
                    }
                }

                surrounding.sort(Comparator.comparingInt(fLocation -> (int) fLocation.getDistanceTo(to)));

                if (surrounding.isEmpty()) {
                    msg(TL.COMMAND_CORNER_CANT_CLAIM);
                } else {
                    new TaskCorner(fme, surrounding).runTaskTimer(p, 1L, 1L);
                }
            }
        } else {
            msg(TL.COMMAND_CORNER_NOT_CORNER);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CORNER_DESCRIPTION;
    }
}
