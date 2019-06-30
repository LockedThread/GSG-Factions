package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.tasks.TaskFlight;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

public class CmdStealth extends FCommand {

    public CmdStealth() {
        this.aliases.add("stealth");
        this.aliases.add("ninja");

        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.STEALTH.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        if (args.size() == 0) {
            toggleStealth(!fme.isStealth());
        } else if (args.size() == 1) {
            toggleStealth(argAsBool(0));
        }
    }

    private void toggleStealth(boolean toggle) {
        if (!toggle) {
            fme.setStealth(false);
            fme.sendMessage(ChatColor.YELLOW + "Faction stealth " + ChatColor.LIGHT_PURPLE + "disabled");
        } else if (TaskFlight.instance().enemiesTask.enemiesNearby(fme, P.p.getConfig().getInt("f-fly.enemy-radius"))) {
            msg("&cYou can not do that while around enemies.");
        } else {
            fme.setStealth(true);
            fme.sendMessage(ChatColor.YELLOW + "Faction stealth " + ChatColor.LIGHT_PURPLE + "enabled");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_STEALH_DESCRIPTION;
    }
}
