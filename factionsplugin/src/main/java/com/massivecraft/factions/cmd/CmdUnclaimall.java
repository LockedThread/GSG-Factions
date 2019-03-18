package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

public class CmdUnclaimall extends FCommand {

    public CmdUnclaimall() {
        this.aliases.add("unclaimall");
        this.aliases.add("declaimall");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.UNCLAIM_ALL.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = true;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
//        if (Econ.shouldBeUsed()) {
//            double refund = Econ.calculateTotalLandRefund(myFaction.getLandRounded());
//            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
//                if (!Econ.modifyMoney(myFaction, refund, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
//                    return;
//                }
//            } else {
//                if (!Econ.modifyMoney(fme, refund, TL.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
//                    return;
//                }
//            }
//        }

        Access access = myFaction.getAccess(fme, PermissableAction.TERRITORY);
        if (access == Access.DENY || (access == Access.UNDEFINED && !assertMinRole(Role.MODERATOR))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "unclaimall");
            return;
        }

        LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(myFaction, fme);
        Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent);
        if (unclaimAllEvent.isCancelled()) {
            return;
        }

        for (FPlayer fPlayer : myFaction.getFPlayersWhereOnline(true)) {
            if (fPlayer.isFlying()) {
                fPlayer.setFlying(false);
            }
        }

        Board.getInstance().unclaimAll(myFaction.getId());
        myFaction.msg(TL.COMMAND_UNCLAIMALL_UNCLAIMED, fme.describeTo(myFaction, true));

        if (Conf.logLandUnclaims) {
            P.p.log(TL.COMMAND_UNCLAIMALL_LOG.format(fme.getName(), myFaction.getTag()));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIMALL_DESCRIPTION;
    }

}
