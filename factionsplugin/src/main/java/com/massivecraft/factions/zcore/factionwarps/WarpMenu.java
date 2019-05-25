package com.massivecraft.factions.zcore.factionwarps;

import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.menus.Menu;
import com.gameservergroup.gsgcore.menus.MenuItem;
import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.units.UnitFactionUpgrade;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.factionupgrades.FactionUpgrade;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.stream.Collectors;

public class WarpMenu extends Menu {

    private Faction faction;

    public WarpMenu(Faction faction) {
        super(P.p.getConfig().getString("fwarp-gui.name"), getSlots(faction));
        this.faction = faction;
        initialize();
    }

    public static int getSlots(Faction faction) {
        int amount;
        if (FactionUpgrade.FACTION_WARP_LIMIT.isEnabled()) {
            Integer level = faction.getUpgrades().get(FactionUpgrade.FACTION_WARP_LIMIT);
            amount = level == null ? FactionUpgrade.FACTION_WARP_LIMIT.getMetaInteger("default-amount") : FactionUpgrade.FACTION_WARP_LIMIT.getMetaInteger(level + "-amount");
        } else {
            amount = faction.getMaxWarps();
        }
        return amount <= 9 ? 9 : amount <= 18 ? 18 : amount <= 27 ? 27 : amount <= 36 ? 36 : amount <= 45 ? amount : 54;
    }

    @Override
    public void initialize() {
        for (Map.Entry<String, LazyLocation> entry : faction.getWarps().entrySet()) {
            setItem(getInventory().firstEmpty(), buildItem(entry.getKey()));
        }

        if (P.p.getConfig().getBoolean("fwarp-gui.fill.enabled")) {
            while (getInventory().firstEmpty() != -1) {
                setItem(getInventory().firstEmpty(), UnitFactionUpgrade.fillItemStack);
            }
        }
    }

    public MenuItem buildItem(String warp) {
        return MenuItem.of(ItemStackBuilder.of(P.p.getConfig().getConfigurationSection("fwarp-gui.warp-item"))
                .consumeItemMeta(itemMeta -> {
                    itemMeta.setDisplayName(replacePlaceholers(itemMeta.getDisplayName(), warp, faction));
                    itemMeta.setLore(itemMeta.getLore()
                            .stream()
                            .map(s -> ChatColor.translateAlternateColorCodes('&', replacePlaceholers(s, warp, faction)))
                            .collect(Collectors.toList()));
                }).build()).setInventoryClickEventConsumer(event -> {
            if (!faction.hasWarpPassword(warp)) {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) event.getWhoClicked());
                if (transact(fPlayer)) {
                    doWarmup(fPlayer, warp);
                }
            } else {
                event.getWhoClicked().sendMessage(TL.COMMAND_FWARP_PASSWORD_REQUIRED.toString());
                event.setCancelled(true);
            }
        });
    }

    private void doWarmup(FPlayer fPlayer, final String warp) {
        WarmUpUtil.process(fPlayer, WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warp, () -> {
            Player player = Bukkit.getPlayer(fPlayer.getPlayer().getUniqueId());
            if (player != null) {
                player.teleport(faction.getWarp(warp).getLocation());
                fPlayer.msg(TL.COMMAND_FWARP_WARPED, warp);
            }
        }, P.p.getConfig().getLong("warmups.f-warp", 0));
    }

    private boolean transact(FPlayer player) {
        if (!P.p.getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing()) {
            return true;
        }

        double cost = P.p.getConfig().getDouble("warp-cost.warp", 5);

        return !Econ.shouldBeUsed() || cost == 0.0 || player.isAdminBypassing() || (Conf.bankEnabled && Conf.bankFactionPaysCosts && player.hasFaction() ? Econ.modifyMoney(faction, -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString()) : Econ.modifyMoney(player, -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString()));
    }

    private String replacePlaceholers(String string, String warp, Faction faction) {
        string = ChatColor.translateAlternateColorCodes('&', string);
        string = string.replace("{warp}", warp);
        string = string.replace("{warp-protected}", faction.hasWarpPassword(warp) ? "Enabled" : "Disabled");
        string = string.replace("{warp-cost}", !P.p.getConfig().getBoolean("warp-cost.enabled", false) ? "Disabled" : Integer.toString(P.p.getConfig().getInt("warp-cost.warp", 5)));
        return string;
    }
}
