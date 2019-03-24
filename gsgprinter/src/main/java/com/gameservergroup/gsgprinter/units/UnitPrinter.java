package com.gameservergroup.gsgprinter.units;

import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.CallBack;
import com.gameservergroup.gsgcore.utils.Text;
import com.gameservergroup.gsgcore.utils.Utils;
import com.gameservergroup.gsgprinter.GSGPrinter;
import com.gameservergroup.gsgprinter.enums.PrinterMessages;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.event.FactionDisbandEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.function.Predicate;

public class UnitPrinter extends Unit {

    private static final GSGPrinter GSG_PRINTER = GSGPrinter.getInstance();
    private static final EnumSet<Material> BANNED_INTERACTABLES = EnumSet.of(Material.MONSTER_EGG, Material.EGG,
            Material.MOB_SPAWNER, Material.BEACON, Material.BEDROCK, Material.BOW, Material.POTION,
            Material.ENDER_PEARL, Material.SNOW_BALL, Material.EXP_BOTTLE, Material.ENDER_CHEST, Material.INK_SACK,
            Material.EYE_OF_ENDER, Material.ACACIA_DOOR_ITEM, Material.DARK_OAK_DOOR_ITEM, Material.BIRCH_DOOR_ITEM,
            Material.IRON_DOOR, Material.JUNGLE_DOOR_ITEM, Material.SPRUCE_DOOR_ITEM, Material.WOODEN_DOOR, Material.IRON_TRAPDOOR,
            Material.ARMOR_STAND, Material.RECORD_3, Material.RECORD_4, Material.RECORD_5, Material.RECORD_6, Material.RECORD_7,
            Material.RECORD_8, Material.RECORD_9, Material.RECORD_10, Material.RECORD_11, Material.RECORD_12, Material.GOLD_RECORD,
            Material.GREEN_RECORD, Material.BOAT, Material.MINECART, Material.MINECART, Material.COMMAND_MINECART, Material.EXPLOSIVE_MINECART,
            Material.HOPPER_MINECART, Material.POWERED_MINECART, Material.STORAGE_MINECART, Material.BED, Material.DOUBLE_PLANT, Material.LONG_GRASS);
    private boolean useNcp;
    private HashSet<String> allowedCommands;
    private HashSet<String> blacklistedKeywords;
    private Set<UUID> printingPlayers;

    public static GSGPrinter getGsgPrinter() {
        return GSG_PRINTER;
    }

    private boolean startsWith(Collection<String> strings, String data) {
        return strings.stream().anyMatch(string -> string.toLowerCase().startsWith(data.toLowerCase()));
    }

    private boolean sift(Collection<String> strings, String data) {
        return strings.stream().anyMatch(string -> string.contains(data));
    }

    private <T extends PlayerEvent> void cancelEvents(Predicate<T> predicate, Class<T>[] playerEvents) {
        for (Class<T> playerEvent : playerEvents) {
            System.out.println("cancelling " + playerEvent.getSimpleName());
            EventPost.of(playerEvent)
                    .filter(predicate)
                    .handle(event -> {
                        if (event instanceof Cancellable) {
                            event.getPlayer().sendMessage(PrinterMessages.YOU_CANT_DO_THIS.toString());
                            ((Cancellable) event).setCancelled(true);
                        }
                    }).post(GSG_PRINTER);
        }
    }

    public static EnumSet<Material> getBannedInteractables() {
        return BANNED_INTERACTABLES;
    }

    @Override
    public void setup() {
        GSG_PRINTER.getFactionsIntegration().hookFlightDisable(new CallBack<Player>() {
            @Override
            public void call(Player player) {
                if (player != null && printingPlayers.contains(player.getUniqueId())) {
                    disablePrinter(player, true, true);
                }
            }
        });

        EventPost.of(FactionDisbandEvent.class)
                .handle(factionDisbandEvent -> {
                    for (Player onlinePlayer : factionDisbandEvent.getFaction().getOnlinePlayers()) {
                        printingPlayers.remove(onlinePlayer.getUniqueId());
                    }
                }).post(GSG_PRINTER);
        this.printingPlayers = new HashSet<>();
        this.useNcp = GSG_PRINTER.getConfig().getBoolean("use-ncp");
        this.allowedCommands = new HashSet<>(GSG_PRINTER.getConfig().getStringList("allowed-commands"));
        this.blacklistedKeywords = new HashSet<>(GSG_PRINTER.getConfig().getStringList("blacklisted-keywords"));
        hookDisable(new CallBack() {
            @Override
            public void call() {
                printingPlayers.forEach(uuid -> disablePrinter(Bukkit.getPlayer(uuid), false, false));
            }
        });
        cancelEvents(event -> printingPlayers.contains(event.getPlayer().getUniqueId()), new Class[]{PlayerPickupItemEvent.class, PlayerDropItemEvent.class, PlayerFishEvent.class, PlayerInteractEntityEvent.class, PlayerItemConsumeEvent.class, PlayerBucketEmptyEvent.class, PlayerBucketFillEvent.class, PlayerInteractAtEntityEvent.class, PlayerArmorStandManipulateEvent.class, PlayerShearEntityEvent.class, PlayerEditBookEvent.class, PlayerEggThrowEvent.class});
        CommandPost.of()
                .build()
                .assertPlayer()
                .assertPermission("gsgprinter.toggle")
                .handler(commandContext -> {
                    Player player = commandContext.getSender();
                    if (printingPlayers.contains(player.getUniqueId())) {
                        disablePrinter(player, true);
                    } else if (GSG_PRINTER.isEnableCombatTagPlusIntegration() && GSG_PRINTER.getCombatIntegration().isTagged(player)) {
                        commandContext.reply(PrinterMessages.YOU_ARE_IN_COMBAT);
                    } else if (Board.getInstance().getFactionAt(new FLocation(player.getLocation())) != FPlayers.getInstance().getByPlayer(player).getFaction()) {
                        commandContext.reply(PrinterMessages.MUST_BE_IN_FRIENDLY_TERRITORY);
                    } else if (FPlayers.getInstance().getByPlayer(player).getFaction().isWilderness()) {
                        commandContext.reply(PrinterMessages.YOU_ARE_FACTIONLESS);
                    } else if (!Utils.playerInventoryIsEmpty(player)) {
                        commandContext.reply(PrinterMessages.INVENTORY_MUST_BE_EMPTY);
                    } else {
                        enablePrinter(player, true);
                    }
                }).post(GSG_PRINTER, "print", "printer", "printermode");

        EventPost.of(PlayerInteractEvent.class, EventPriority.LOWEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                .handle(event -> {
                    ItemStack item = event.getItem();
                    if (item != null && (BANNED_INTERACTABLES.contains(item.getType()) || (item.hasItemMeta() && (item.getItemMeta().hasLore() || item.getItemMeta().hasDisplayName()))) || event.getAction() == Action.LEFT_CLICK_AIR || event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof InventoryHolder) {
                        event.setCancelled(true);
                    }
                }).post(GSG_PRINTER);

        EventPost.of(PlayerQuitEvent.class)
                .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                .handle(event -> disablePrinter(event.getPlayer(), true))
                .post(GSG_PRINTER);

        EventPost.of(BlockPlaceEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getBlockPlaced() != null)
                .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                .handle(event -> {
                    if (GSG_CORE.canBuild(event.getPlayer(), event.getBlockPlaced())) {
                        if (!chargePlayer(event.getPlayer(), event.getBlockPlaced().getType())) {
                            event.setCancelled(true);
                        }
                    }
                }).post(GSG_PRINTER);

        EventPost.of(PlayerExpChangeEvent.class)
                .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                .filter(event -> event.getAmount() > 0)
                .handle(event -> event.setAmount(0))
                .post(GSG_PRINTER);

        EventPost.of(PlayerChangedWorldEvent.class)
                .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                .handle(event -> disablePrinter(event.getPlayer(), true))
                .post(GSG_PRINTER);

        EventPost.of(HangingBreakByEntityEvent.class)
                .filter(event -> event.getRemover() instanceof Player)
                .filter(event -> printingPlayers.contains(event.getRemover().getUniqueId()))
                .handle(event -> event.setCancelled(true))
                .post(GSG_PRINTER);

        EventPost.of(ProjectileLaunchEvent.class, EventPriority.LOWEST)
                .filter(event -> event.getEntity().getShooter() instanceof Player)
                .filter(event -> printingPlayers.contains(((Player) event.getEntity().getShooter()).getUniqueId()))
                .handle(event -> {
                    event.getEntity().remove();
                    event.setCancelled(true);
                }).post(GSG_PRINTER);

        EventPost.of(InventoryClickEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.contains(event.getWhoClicked().getUniqueId()))
                .filter(event -> event.getView().getType() != InventoryType.CREATIVE)
                .handle(event -> {
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                }).post(GSG_PRINTER);

        EventPost.of(InventoryOpenEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                .filter(event -> event.getView().getType() != InventoryType.CREATIVE)
                .handle(event -> {
                    event.setCancelled(true);
                    event.getView().close();
                }).post(GSG_PRINTER);

        EventPost.of(PlayerCommandPreprocessEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                .handle(event -> {
                    String message = event.getMessage().toLowerCase().trim();
                    if (!startsWith(allowedCommands, message) || sift(blacklistedKeywords, message)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Text.toColor("&cYou can't use that command in /printer!"));
                    }
                }).post(GSG_PRINTER);

        EventPost.of(EntityDamageByEntityEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getDamager() instanceof Player)
                .filter(event -> printingPlayers.contains(event.getDamager().getUniqueId()))
                .handle(event -> {
                    event.setCancelled(true);
                    disablePrinter((Player) event.getDamager(), true);
                }).post(GSG_PRINTER);

        EventPost.of(PlayerInteractEntityEvent.class, EventPriority.LOWEST)
                .filter(event -> event.getRightClicked() instanceof Player)
                .filter(event -> printingPlayers.contains(event.getRightClicked().getUniqueId()))
                .handle(event -> {
                    event.setCancelled(true);
                    disablePrinter((Player) event.getRightClicked(), true);
                }).post(GSG_PRINTER);

        EventPost.of(EntityDamageEvent.class)
                .filter(event -> event.getEntity() instanceof Player)
                .filter(event -> event.getCause() == EntityDamageEvent.DamageCause.FALL)
                .filter(event -> event.getEntity().hasMetadata("nofalldamage"))
                .handle(event -> event.setCancelled(true))
                .post(GSG_PRINTER);

        EventPost.of(InventoryCreativeEvent.class)
                .filter(event -> printingPlayers.contains(event.getWhoClicked().getUniqueId()))
                .filter(event -> (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) || (event.getCursor() != null && event.getCursor().hasItemMeta()))
                .handle(event -> {
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .filter(player -> player.hasPermission("gsg.staff"))
                            .forEach(player -> player.sendMessage(Text.toColor("&c[Hacked-Client] &e" + event.getWhoClicked().getName() + " &ctried to use an item with NBT in creative! They're using a hacked client or exploit!")));
                    GSG_PRINTER.getLogger().severe("[Hacked-Client] " + event.getWhoClicked().getName() + " tried to use an item with NBT in creative! They're using a hacked client or exploit!");
                    event.getWhoClicked().sendMessage(Text.toColor("&cYou're not allowed to interact with that item!"));
                    event.setCancelled(true);
                }).post(GSG_PRINTER);

        EventPost.of(PlayerDeathEvent.class)
                .filter(event -> printingPlayers.contains(event.getEntity().getUniqueId()))
                .handle(event -> {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }).post(GSG_PRINTER);

        if (GSG_PRINTER.getConfig().getBoolean("enable-blockbreak")) {
            EventPost.of(BlockBreakEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                    .handle(event -> event.setCancelled(true))
                    .post(GSG_PRINTER);
        } else {
            EventPost.of(BlockBreakEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> printingPlayers.contains(event.getPlayer().getUniqueId()))
                    .filter(event -> BANNED_INTERACTABLES.contains(event.getBlock().getType()))
                    .handle(event -> {
                        if (BANNED_INTERACTABLES.contains(event.getBlock().getType()) || event.getBlock().getY() < 1) {
                            event.setCancelled(true);
                        }
                    }).post(GSG_PRINTER);
        }
    }

    public void enablePrinter(Player player, boolean notify) {
        player.performCommand("f fly y");
        printingPlayers.add(player.getUniqueId());
        player.setGameMode(GameMode.CREATIVE);
        player.closeInventory();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (notify) {
            player.sendMessage(PrinterMessages.PRINTER_ENABLE.toString());
        }
        if (useNcp) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ncp exempt " + player.getName() + " net");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ncp exempt " + player.getName() + " blockplace");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ncp exempt " + player.getName() + " blockinteract");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ncp exempt " + player.getName() + " moving_morepackets");
        }
    }

    private boolean chargePlayer(Player player, Material material) {
        double price = GSG_PRINTER.getSellIntegration().getBuyPrice(material);
        if (price == 0.0) {
            player.sendMessage(PrinterMessages.THIS_BLOCK_ISNT_PLACEABLE.toString());
            return false;
        }
        double balance = Module.getEconomy().getBalance(player);
        if (balance < price) {
            player.sendMessage(PrinterMessages.YOU_DONT_HAVE_ENOUGH_MONEY.toString().replace("{material}", material.name().toLowerCase().replace("_", " ")));
            disablePrinter(player, false);
            return false;
        }
        GSG_PRINTER.getServer().getScheduler().runTaskAsynchronously(GSG_PRINTER, () -> Module.getEconomy().withdrawPlayer(player, price));
        return true;
    }

    public void disablePrinter(Player player, boolean notify) {
        disablePrinter(player, notify, true);
    }

    public void disablePrinter(Player player, boolean notify, boolean nofall) {
        printingPlayers.remove(player.getUniqueId());
        player.performCommand("f fly n");
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        if (nofall) {
            player.setMetadata("nofalldamage", new FixedMetadataValue(GSG_PRINTER, true));
            GSG_PRINTER.getServer().getScheduler().runTaskLater(GSG_PRINTER, () -> player.removeMetadata("nofalldamage", GSG_PRINTER), 200L);
        }
        if (notify) {
            player.sendMessage(PrinterMessages.PRINTER_DISABLE.toString());
        }
        if (useNcp) {
            GSG_PRINTER.getServer().dispatchCommand(GSG_PRINTER.getServer().getConsoleSender(), "ncp unexempt " + player.getName());
        }
    }

    public boolean isUseNcp() {
        return useNcp;
    }

    public HashSet<String> getAllowedCommands() {
        return allowedCommands;
    }

    public HashSet<String> getBlacklistedKeywords() {
        return blacklistedKeywords;
    }

    public Set<UUID> getPrintingPlayers() {
        return printingPlayers;
    }
}
