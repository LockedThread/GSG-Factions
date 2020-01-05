package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


public class CmdMoneyDeposit extends FCommand {

    public CmdMoneyDeposit() {
        super();
        this.aliases.add("d");
        this.aliases.add("deposit");

        this.requiredArgs.add("amount");
        this.optionalArgs.put("faction", "yours");

        this.permission = Permission.MONEY_DEPOSIT.node;

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
        boolean success = Econ.transferMoney(fme, fme, faction, amount);

        if (amount < 0) {
            msg("<b>Amount must be positive!");
            return;
        }

        if (success && Conf.logMoneyTransactions) {
            P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYDEPOSIT_DEPOSITED.toString(), fme.getName(), Econ.moneyString(amount), faction.describeTo(null))));
            Econ.sendBalanceInfo(Bukkit.getConsoleSender(), faction);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYDEPOSIT_DESCRIPTION;
    }

}
