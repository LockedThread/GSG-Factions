package com.massivecraft.factions.cmd;

import com.gameservergroup.gsgcore.plugin.Module;
import com.google.common.base.Joiner;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class CmdDrain extends FCommand {

    public CmdDrain() {
        super();
        this.aliases.add("drain");

        this.permission = Permission.DRAIN.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeAdmin = true;

        this.requiredArgs.add("drain until");

        addSubCommand(new CmdDrainToggle());
    }

    @Override
    public void perform() {
        double drainUntil = argAsDouble(0);
        double money = 0.0;
        Set<String> drainedPlayers = null;
        for (FPlayer fPlayer : myFaction.getFPlayers()) {
            if (fPlayer.getAccountId().equals(fme.getAccountId())) continue;
            Player player = fPlayer.getPlayer();
            if (fPlayer.isDrainEnabled() && !player.hasPermission(Permission.DRAIN_BYPASS.node)) {
                double balance = Module.getEconomy().getBalance(player);
                if (balance > drainUntil) {
                    EconomyResponse economyResponse = Module.getEconomy().withdrawPlayer(player, balance - drainUntil);
                    if (economyResponse.transactionSuccess()) {
                        if (drainedPlayers == null) {
                            drainedPlayers = new HashSet<>();
                        }
                        drainedPlayers.add(player.getName());
                        money += economyResponse.amount;
                    }
                }
            }
        }
        if (drainedPlayers != null) {
            msg(TL.COMMAND_DRAIN_FINISHED, Joiner.on(", ").skipNulls().join(drainedPlayers));
            Module.getEconomy().depositPlayer(me, money);
        } else {
            msg(TL.COMMAND_DRAIN_ERROR);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_DRAIN_DESCRIPTION;
    }
}
