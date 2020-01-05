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
import org.bukkit.entity.Player;


public class CmdMoneyTransferFf extends FCommand {

    public CmdMoneyTransferFf() {
        this.aliases.add("ff");

        this.requiredArgs.add("amount");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("faction");

        //this.optionalArgs.put("", "");

        this.permission = Permission.MONEY_F2F.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        double amount = this.argAsDouble(0, 0d);
        Faction from = this.argAsFaction(1);
        if (from == null) {
            return;
        }
        Faction to = this.argAsFaction(2);
        if (to == null) {
            return;
        }

        if (amount < 0) {
            msg("<b>Amount must be positive!");
            return;
        }

        boolean success = Econ.transferMoney(fme, from, to, amount);

        if (success && Conf.logMoneyTransactions) {
            String name = sender instanceof Player ? fme.getName() : sender.getName();
            P.p.log(ChatColor.stripColor(P.p.txt.parse(TL.COMMAND_MONEYTRANSFERFF_TRANSFER.toString(), name, Econ.moneyString(amount), from.describeTo(null), to.describeTo(null))));
            Econ.sendBalanceInfo(Bukkit.getConsoleSender(), from);
            Econ.sendBalanceInfo(Bukkit.getConsoleSender(), to);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MONEYTRANSFERFF_DESCRIPTION;
    }
}
