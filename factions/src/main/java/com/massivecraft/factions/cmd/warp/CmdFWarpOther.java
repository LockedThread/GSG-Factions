package com.massivecraft.factions.cmd.warp;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdFWarpOther extends FCommand {

    public CmdFWarpOther() {
        super();
        this.aliases.add("warpother");
        this.aliases.add("warpothers");
        this.aliases.add("warpo");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("warp name");
        this.optionalArgs.put("password", "password");

        this.permission = Permission.WARP.node;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        Faction faction = argAsFaction(0);
        if (faction == null) {
            msg("<b>No Faction found for '" + argAsString(0) + "'");
            return;
        }

        // Check for access first.
        Access access = faction.getAccess(fme, PermissableAction.WARP);
        if (access == Access.DENY || (access == Access.UNDEFINED && !myFaction.getId().equals(faction.getId()))) {
            fme.msg(TL.GENERIC_NOPERMISSION, "warp");
            return;
        }

        final String warpName = faction.getTrueWarp(argAsString(1));
        final String passwordAttempt = argAsString(2, null);

        if (warpName != null && faction.isWarp(warpName)) {
            // Check if requires password and if so, check if valid. CASE SENSITIVE
            if (faction.hasWarpPassword(warpName) && !faction.isWarpPassword(warpName, passwordAttempt)) {
                msg(TL.COMMAND_FWARP_INVALID_PASSWORD);
                return;
            }

            // Check transaction AFTER password check.
            if (!transact(fme)) {
                return;
            }

            final FPlayer fPlayer = fme;
            final UUID uuid = fme.getPlayer().getUniqueId();
            this.doWarmUp(WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warpName, () -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.teleport(faction.getWarp(warpName).getLocation());
                    fPlayer.msg(TL.COMMAND_FWARP_WARPED, warpName);
                }
            }, this.p.getConfig().getLong("warmups.f-warp", 0));
        } else {
            fme.msg(TL.COMMAND_FWARP_INVALID_WARP, warpName);
        }
    }

    private boolean transact(FPlayer player) {
        return !P.p.getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || payForCommand(P.p.getConfig().getDouble("warp-cost.warp", 5), TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FWARPOTHER_DESCRIPTION;
    }
}
