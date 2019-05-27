package com.gameservergroup.gsgoutpost.units;

import com.gameservergroup.gsgcore.commands.arguments.ArgumentRegistry;
import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgoutpost.GSGOutpost;
import com.gameservergroup.gsgoutpost.enums.OutpostMessages;
import com.gameservergroup.gsgoutpost.enums.OutpostState;
import com.gameservergroup.gsgoutpost.objs.Outpost;
import com.google.common.base.Joiner;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class UnitOutpost extends Unit {

    private static final GSGOutpost GSG_OUTPOST = GSGOutpost.getInstance();

    @Override
    public void setup() {
        ArgumentRegistry.getInstance().register(Outpost.class, () -> s -> Optional.ofNullable(GSG_OUTPOST.getOutpostMap().get(s.toLowerCase())));
        CommandPost.create()
                .builder()
                .handler(c -> {
                    if (c.getRawArgs().length == 0) {
                        if (c.isPlayer()) {
                            ((Player) c.getSender()).openInventory(GSG_OUTPOST.getMenuOutpost().getInventory());
                        } else {
                            c.reply("&b&m&l-&7&m&l----&e&m&l-------&r &b&lOutpost&r &e&m&l-------&7&m&l----&b&m&l-");
                            c.reply(" ");
                            GSG_OUTPOST.getOutpostMap().values().forEach(outpost -> {
                                c.reply("  &b&lOutpost Name: &7" + StringUtils.capitalize(outpost.getUniqueIdentifier()));
                                c.reply("  &b&lCapturer: &7" + outpost.getCapturerString());
                                c.reply("  &b&lState: &7" + outpost.getOutpostState().toString());
                                c.reply("  &b&lPercentage: &7" + outpost.getPercentage());
                                c.reply(" ");
                            });
                            c.reply("&b&m&l--&7&m&l-------&e&m&l--------------&7&m&l-------&b&m&l--");
                        }
                    } else if (c.getRawArgs().length == 1) {
                        if (c.getSender().hasPermission("gsgoutpost.admin")) {
                            if (c.getRawArg(0).equalsIgnoreCase("help")) {
                                c.reply("&b&m&l-&7&m&l----&e&m&l-----&r &b&lOutpost Help&r &e&m&l-----&7&m&l----&b&m&l-");
                                c.reply(" ");
                                c.reply(" &b/outpost list - &7Lists all outposts");
                                c.reply(" &b/outpost teleport {outpost} - &7Teleports you to an outpost");
                                c.reply(" &b/outpost create {worldguard-region} - &7Creates an outpost");
                                c.reply(" &b/outpost config {outpost-name} - &7Allows you to config a outpost");
                                c.reply(" ");
                                c.reply("&b&m&l--&7&m&l-------&e&m&l--------------&7&m&l-------&b&m&l--");
                            } else if (c.getRawArg(0).equalsIgnoreCase("list")) {
                                c.reply(OutpostMessages.COMMAND_LIST.toString().replace("{outposts}", GSG_OUTPOST.getOutpostMap().isEmpty() ? "N/A" : Joiner.on(", ").skipNulls().join(GSG_OUTPOST.getOutpostMap().keySet())));
                            }
                        } else {
                            c.reply(OutpostMessages.COMMAND_NO_PERMISSION);
                        }
                    } else if (c.getRawArgs().length >= 2) {
                        if (c.getSender().hasPermission("gsgoutpost.admin")) {
                            if (c.getRawArg(0).equalsIgnoreCase("teleport") || c.getRawArg(0).equalsIgnoreCase("tp")) {
                                Player target = c.isPlayer() ? (Player) c.getSender() : c.getArg(2).forceParse(Player.class);
                                Optional<Outpost> outpostOptional = c.getArg(1).parse(Outpost.class);
                                if (outpostOptional.isPresent()) {
                                    Outpost outpost = outpostOptional.get();
                                    if (outpost.getWarp() != null) {
                                        target.teleport(outpost.getWarp().getLocation());
                                        c.reply(OutpostMessages.COMMAND_TELEPORT_SENT.toString().replace("{outpost}", outpost.getUniqueIdentifier()));
                                    } else {
                                        if (outpost.getWorld().isPresent()) {
                                            ProtectedRegion protectedRegion = WGBukkit.getPlugin().getRegionManager(outpost.getWorld().get()).getRegion(outpost.getUniqueIdentifier());
                                            if (protectedRegion != null) {
                                                Location location = new Location(outpost.getWorld().get(), protectedRegion.getMinimumPoint().getX(), protectedRegion.getMinimumPoint().getY(), protectedRegion.getMinimumPoint().getBlockZ());
                                                target.teleport(location);
                                                c.reply(OutpostMessages.COMMAND_TELEPORT_SENT.toString().replace("{outpost}", outpost.getUniqueIdentifier()));
                                            } else {
                                                c.reply(OutpostMessages.COMMAND_TELEPORT_CANT_FIND_LOCATION.toString().replace("{outpost}", outpost.getUniqueIdentifier()));
                                            }
                                        } else {
                                            c.reply(OutpostMessages.COMMAND_TELEPORT_CANT_FIND_LOCATION.toString().replace("{outpost}", outpost.getUniqueIdentifier()));
                                        }
                                    }
                                } else {
                                    c.reply(OutpostMessages.COMMAND_TELEPORT_CANT_FIND.toString().replace("{outpost}", c.getRawArg(1)));
                                }
                            } else if (c.getRawArg(0).equalsIgnoreCase("create")) {
                                String regionName = c.getRawArg(1);
                                Outpost outpost = new Outpost(regionName);
                                GSG_OUTPOST.getOutpostMap().put(regionName, outpost);
                                outpost.init();
                                outpost.startTask();
                                c.reply(OutpostMessages.COMMAND_OUTPOST_CREATE.toString().replace("{outpost}", regionName));
                            }
                        } else {
                            c.reply(OutpostMessages.COMMAND_NO_PERMISSION);
                        }
                    }
                }).post(GSG_OUTPOST, "outpost");

        EventPost.of(PlayerQuitEvent.class)
                .handle(event -> {
                    for (Outpost outpost : GSG_OUTPOST.getOutpostMap().values()) {
                        for (Player player : outpost.getPlayers()) {
                            if (player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                                outpost.getPlayers().remove(event.getPlayer());
                            }
                        }
                    }
                }).post(GSG_OUTPOST);

        EventPost.of(PlayerMoveEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(EventFilters.getIgnoreSameBlock())
                .handle(event -> {
                    Player player = event.getPlayer();
                    if (player.isDead()) return;
                    FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
                    if (!fplayer.hasFaction())
                        return;

                    Faction faction = fplayer.getFaction();

                    Optional<Outpost> outpostOptional = GSG_OUTPOST.getOutpost(event.getFrom());
                    if (outpostOptional.isPresent()) {
                        Outpost outpost = outpostOptional.get();
                        if (!outpost.getPlayers().contains(player)) {
                            outpost.getPlayers().add(player);

                            player.sendMessage(OutpostMessages.OUTPOST_ENTERED_CAPTURE_ZONE.toString());

                            if (outpost.getOutpostState() == OutpostState.CAPTURED && faction.getTag().equals(outpost.getCapturedFaction().getTag()) || outpost.getOutpostState() == OutpostState.CAPTURING && faction.getTag().equals(outpost.getFaction().getTag()))
                                return;

                            if (outpost.getOutpostState() == OutpostState.CAPTURED && !faction.getTag().equals(outpost.getCapturedFaction().getTag())) {
                                Bukkit.broadcastMessage(OutpostMessages.OUTPOST_STATUS_LOST_CONTROL_BROADCAST.toString().replace("{faction}", faction.getTag()).replace("{outpost}", outpost.getUniqueIdentifier()));
                                outpost.setOutpostState(OutpostState.NEUTRALIZING);
                            } else if (outpost.getOutpostState() == OutpostState.CAPTURING && !faction.getTag().equals(outpost.getFaction().getTag())) {
                                outpost.setOutpostState(OutpostState.CAPTURING_PAUSED);
                            } else if (outpost.getOutpostState() == OutpostState.CAPTURE_WAITING && faction == outpost.getFaction()) {
                                outpost.setOutpostState(OutpostState.CAPTURING);
                                Bukkit.broadcastMessage(OutpostMessages.OUTPOST_STATUS_CAPTURING.toString().replace("{faction}", faction.getTag()).replace("{outpost}", outpost.getUniqueIdentifier()));
                            } else {
                                outpost.setOutpostState(OutpostState.NEUTRALIZING);
                            }
                        } else if (outpost.getPlayers().contains(player) && GSG_OUTPOST.getOutpost(event.getTo()).isPresent()) {
                            outpost.getPlayers().remove(player);
                        }
                    }
                }).post(GSG_OUTPOST);
    }
}
