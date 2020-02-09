package com.massivecraft.factions.cmd.focus;

import com.google.common.base.Joiner;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.entity.Player;

public class CmdFocus extends FCommand {

    public CmdFocus() {
        super();
        this.aliases.add("focus");

        this.permission = Permission.FOCUS.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeAdmin = false;

        this.optionalArgs.put("player", "list");
    }

    @Override
    public void perform() {
        if (args.size() == 0) {
            msg(TL.COMMAND_FOCUS_INFO, Joiner.on(", ").skipNulls().join(myFaction.getFocusedPlayers()));
        } else if (args.size() == 1) {
            Player player = argAsPlayer(0);
            if (myFaction.getFocusedPlayers().contains(player.getName())) {
                myFaction.removeFocusedPlayer(player.getName());
                msg(TL.COMMAND_FOCUS_UNFOCUSED, player.getName());
            } else {
                myFaction.addFocusedPlayer(player.getName());
                msg(TL.COMMAND_FOCUS_FOCUSED, player.getName());
            }
        } else {
            msg(TL.GENERIC_ARGS_TOOMANY, TextUtil.implode(args.subList(this.requiredArgs.size() + this.optionalArgs.size(), args.size()), " "));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FOCUS_DESCRIPTION;
    }
}
