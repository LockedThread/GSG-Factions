package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


public class CmdMoneyWithdraw extends FCommand {

    public CmdMoneyWithdraw() {
        this.aliases.add("w");
        this.aliases.add("withdraw");

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.MONEY_WITHDRAW.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        Faction faction = this.argAsFaction(1, myFaction);
        if (faction == null) {
            return;
        }

        Access access = myFaction.getAccess(fme, PermissableAction.WITHDRAW);
        if (access == Access.DENY) {
            fme.msg(TL.GENERIC_NOPERMISSION, "withdraw");
            return;
        }

        if (amount < 0) {
            msg("<b>Amount must be positive!");
            return;
        }

        boolean success = Econ.transferMoney(fme, faction, fme, amount);

        if (success && Conf.logMoneyTransactions) {
            P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYWITHDRAW_WITHDRAW.toString(), fme.getName(), Econ.moneyString(amount), faction.describeTo(null))));
            Econ.sendBalanceInfo(Bukkit.getConsoleSender(), faction);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYWITHDRAW_DESCRIPTION;
    }
}
