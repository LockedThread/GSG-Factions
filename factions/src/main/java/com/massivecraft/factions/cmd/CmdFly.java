package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.FlightUtil;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdFly extends FCommand {

    public CmdFly() {
        super();
        this.aliases.add("fly");

        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.FLY.node;
        this.senderMustBePlayer = true;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        if (!fme.hasFaction() && !fme.hasAltFaction()) {
            msg("&cYou are not a member of any faction.");
            return;
        }

        if (args.size() == 0) {
            toggleFlight(!fme.isFlying());
        } else if (args.size() == 1) {
            toggleFlight(argAsBool(0));
        }
    }

    private void toggleFlight(final boolean toggle) {
        if (P.p.isSotw()) {
            msg(TL.SOTW_IS_ENABLED);
            return;
        }

        if (!toggle) {
            fme.setFlying(false);
            return;
        }

        // Do checks if true
        FLocation loc = new FLocation(me.getLocation());
        boolean canFlyAtLocation = fme.canFlyAtLocation(loc);
        if (!canFlyAtLocation) {
            Faction factionAtLocation = Board.getInstance().getFactionAt(loc);
            fme.msg(TL.COMMAND_FLY_NO_ACCESS, factionAtLocation.getTag(fme));
            return;
        } else if (FlightUtil.instance().enemiesTask.enemiesNearby(fme)) {
            fme.msg(TL.COMMAND_FLY_ENEMY_NEARBY);
            return;
        }

        this.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", () -> fme.setFlying(true), this.p.getConfig().getLong("warmups.f-fly", 0));
    }

    private boolean flyTest(FPlayer fPlayer, boolean notify) {
        if (!fPlayer.canFlyAtLocation()) {
            if (notify) {
                Faction factionAtLocation = Board.getInstance().getFactionAt(fPlayer.getLastStoodAt());
                fPlayer.msg(TL.COMMAND_FLY_NO_ACCESS, factionAtLocation.getTag(fPlayer));
            }
            return false;
        } else if (FlightUtil.instance().enemiesNearby(fPlayer)) {
            if (notify) {
                fPlayer.msg(TL.COMMAND_FLY_ENEMY_NEARBY);
            }
            return false;
        }
        return true;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}
