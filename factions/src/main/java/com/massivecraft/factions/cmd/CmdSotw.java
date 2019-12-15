package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.tasks.flight.TaskFlight;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class CmdSotw extends FCommand {

    public CmdSotw() {
        super();
        aliases.add("sotw");
        permission = Permission.BYPASS.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (p.isSotw()) {
            TaskFlight.start();
            p.log(Level.INFO, "Enabling enemy radius check for f fly every %s seconds", p.getFactionsFlightDelay() / 20);
            Bukkit.broadcastMessage(TL.COMMAND_SOTW_BROADCAST_OFF.toString());
        } else {
            TaskFlight.stop();
            p.log(Level.INFO, "Factions Flight is now disabled because /f sotw is enabled!");
            Bukkit.broadcastMessage(TL.COMMAND_SOTW_BROADCAST_ON.toString());
            FPlayers.getInstance().getOnlinePlayers().stream().filter(FPlayer::isFlying).forEach(fPlayer -> fPlayer.setFlying(false));
        }
        p.setSotw(!p.isSotw());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SOTW_DESCRIPTION;
    }


}
