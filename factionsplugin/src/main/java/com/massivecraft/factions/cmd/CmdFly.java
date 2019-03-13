package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.FlightDisableUtil;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;

public class CmdFly extends FCommand {

    public CmdFly() {
        super();
        this.aliases.add("fly");

        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.FLY.node;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        if (!fme.hasFaction() && !fme.hasAltFaction()) {
            msg("&cYou are not a member of any faction.");
            return;
        }

        if (p.flightTask.isCancelled()) {
            msg("&cFlight is currently disabled!");
            return;
        }

        if (args.size() == 0) {
            toggleFlight(!fme.isFlying());
        } else if (args.size() == 1) {
            toggleFlight(argAsBool(0));
        }
    }

    private void toggleFlight(final boolean toggle) {
        // If false do nothing besides set
        if (!toggle) {
            fme.setFlying(false);
            return;
        }
        // Do checks if true
        if (!fme.canFlyAtLocation()) {
            Faction factionAtLocation = Board.getInstance().getFactionAt(fme.getLastStoodAt());
            fme.msg(TL.COMMAND_FLY_NO_ACCESS, factionAtLocation.getTag(fme));
            return;
        } else if (FlightDisableUtil.enemiesNearby(fme, P.p.getConfig().getInt("f-fly.enemy-radius", 7))) {
            fme.msg(TL.COMMAND_FLY_ENEMY_NEARBY);
            return;
        }

        this.doWarmUp(WarmUpUtil.Warmup.FLIGHT, TL.WARMUPS_NOTIFY_FLIGHT, "Fly", new Runnable() {
            @Override
            public void run() {
                fme.setFlying(true);
            }
        }, this.p.getConfig().getLong("warmups.f-fly", 0));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}
