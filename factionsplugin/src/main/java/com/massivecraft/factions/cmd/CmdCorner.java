package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

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
                int amount = 0;

                for (int x = -Conf.bufferSize; x < to.getX() + Conf.bufferSize; x++) {
                    for (int z = -Conf.bufferSize; z < to.getX() + Conf.bufferSize; z++) {
                        FLocation location = new FLocation(me.getWorld().getName(), x, z);
                        Faction at = Board.getInstance().getFactionAt(location);
                        if (at == null || at.isWilderness()) {
                            if (fme.attemptClaim(myFaction, location, false)) {
                                amount++;
                            }
                        }
                    }
                }
                if (amount == 0) {
                    msg(TL.COMMAND_CORNER_CANT_CLAIM);
                } else {
                    msg(TL.COMMAND_CORNER_SUCCESS, amount);
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
