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
import com.gameservergroup.gsgprinter.objs.PrintingData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class UnitPrinter extends Unit {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.00");
    private static final GSGPrinter GSG_PRINTER = GSGPrinter.getInstance();
    private static final ImmutableSet<Material> BANNED_INTERACTABLES = Sets.immutableEnumSet(Material.MONSTER_EGG, Material.EGG,
            Material.MOB_SPAWNER, Material.BEACON, Material.BEDROCK, Material.BOW, Material.POTION,
            Material.ENDER_PEARL, Material.SNOW_BALL, Material.EXP_BOTTLE, Material.ENDER_CHEST, Material.INK_SACK,
            Material.EYE_OF_ENDER, Material.ACACIA_DOOR_ITEM, Material.DARK_OAK_DOOR_ITEM, Material.BIRCH_DOOR_ITEM,
            Material.IRON_DOOR, Material.JUNGLE_DOOR_ITEM, Material.SPRUCE_DOOR_ITEM, Material.WOODEN_DOOR, Material.IRON_TRAPDOOR,
            Material.ARMOR_STAND, Material.RECORD_3, Material.RECORD_4, Material.RECORD_5, Material.RECORD_6, Material.RECORD_7,
            Material.RECORD_8, Material.RECORD_9, Material.RECORD_10, Material.RECORD_11, Material.RECORD_12, Material.GOLD_RECORD,
            Material.GREEN_RECORD, Material.BOAT, Material.MINECART, Material.MINECART, Material.COMMAND_MINECART, Material.EXPLOSIVE_MINECART,
            Material.HOPPER_MINECART, Material.POWERED_MINECART, Material.STORAGE_MINECART, Material.BED, Material.DOUBLE_PLANT, Material.LONG_GRASS);
    private boolean useNcp;
    private ImmutableSet<String> whitelistedInventoryTitles;
    private ImmutableSet<String> whitelistedCommands;
    private ImmutableSet<String> blacklistedKeywords;
    private ImmutableList<String> enablePrinterCommands, disablePrinterCommands;
    private ConcurrentHashMap<UUID, PrintingData> printingPlayers;
    private boolean blockBreakEnabled;

    public static ImmutableSet<Material> getBannedInteractables() {
        return BANNED_INTERACTABLES;
    }

    private boolean startsWith(Collection<String> strings, String data) {
        for (String string : strings) {
            if (string.toLowerCase().startsWith(data.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean sift(Collection<String> strings, String data) {
        return strings.stream().anyMatch(string -> string.toLowerCase().contains(data.toLowerCase()));
    }

    private <T extends PlayerEvent> void cancelEvents(Predicate<T> predicate, Class<T>[] playerEvents) {
        for (Class<T> playerEvent : playerEvents) {
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

    @Override
    public void setup() {
        this.printingPlayers = new ConcurrentHashMap<>();
        this.useNcp = GSG_PRINTER.getConfig().getBoolean("use-ncp");
        this.whitelistedCommands = ImmutableSet.copyOf(GSG_PRINTER.getConfig().getStringList("allowed-commands"));
        this.blacklistedKeywords = ImmutableSet.copyOf(GSG_PRINTER.getConfig().getStringList("blacklisted-keywords"));
        if (GSG_PRINTER.getConfig().getBoolean("whitelisted-inventories.enabled")) {
            this.whitelistedInventoryTitles = ImmutableSet.copyOf(GSG_PRINTER.getConfig().getStringList("whitelisted-inventories.titles"));
        }
        List<String> list = GSG_PRINTER.getConfig().getStringList("enable-printer-commands");
        if (!list.isEmpty()) {
            this.enablePrinterCommands = ImmutableList.copyOf(list);
        }
        if (!(list = GSG_PRINTER.getConfig().getStringList("disable-printer-commands")).isEmpty()) {
            this.disablePrinterCommands = ImmutableList.copyOf(list);
        }

        hookDisable(new CallBack() {
            @Override
            public void call() {
                printingPlayers.keySet().forEach(uuid -> disablePrinter(Bukkit.getPlayer(uuid), false, false));
            }
        });
        cancelEvents(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()), new Class[]{PlayerPickupItemEvent.class, PlayerDropItemEvent.class, PlayerFishEvent.class, PlayerInteractEntityEvent.class, PlayerItemConsumeEvent.class, PlayerBucketEmptyEvent.class, PlayerBucketFillEvent.class, PlayerInteractAtEntityEvent.class, PlayerArmorStandManipulateEvent.class, PlayerShearEntityEvent.class, PlayerEditBookEvent.class, PlayerEggThrowEvent.class});
        if (GSG_PRINTER.getFactionsIntegration() != null) {
            GSG_PRINTER.getFactionsIntegration().setupListeners();
        } else {
            CommandPost.of()
                    .builder()
                    .assertPlayer()
                    .assertPermission("gsgprinter.toggle")
                    .handler(commandContext -> {
                        Player player = commandContext.getSender();
                        if (printingPlayers.containsKey(player.getUniqueId())) {
                            disablePrinter(player, true);
                        } else if (GSG_PRINTER.isEnableCombatTagPlusIntegration() && GSG_PRINTER.getCombatIntegration().isTagged(player)) {
                            commandContext.reply(PrinterMessages.YOU_ARE_IN_COMBAT);
                        } else if (!Utils.playerInventoryIsEmpty(player)) {
                            commandContext.reply(PrinterMessages.INVENTORY_MUST_BE_EMPTY);
                        } else {
                            enablePrinter(player, true);
                        }
                    }).post(GSG_PRINTER, "print", "printer", "printermode");
        }
        EventPost.of(PlayerInteractEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()))
                .filter(event -> {
                    if (event.getItem() != null) {
                        if (event.getItem().getType() == Material.MONSTER_EGG || event.getItem().getType() == Material.MONSTER_EGGS) {
                            return true;
                        }
                    }
                    if (event.getItem() != null && (BANNED_INTERACTABLES.contains(event.getItem().getType()) || event.getItem().hasItemMeta())) {
                        return true;
                    } else if (event.getClickedBlock() != null) {
                        if (event.getClickedBlock().isLiquid()) {
                            return true;
                        }
                        if (event.getClickedBlock().getState() instanceof InventoryHolder) {
                            return !event.getPlayer().isSneaking();
                        }
                    }
                    return event.getAction() == Action.RIGHT_CLICK_AIR;
                }).handle(event -> {
            event.setCancelled(true);
            event.getPlayer().setItemInHand(null);
        }).post(GSG_PRINTER);

        EventPost.of(PlayerQuitEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()))
                .handle(event -> disablePrinter(event.getPlayer(), true))
                .post(GSG_PRINTER);

        EventPost.of(PlayerKickEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()))
                .handle(event -> disablePrinter(event.getPlayer(), true))
                .post(GSG_PRINTER);

        EventPost.of(BlockPlaceEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getBlockPlaced() != null)
                .handle(event -> {
                    PrintingData printingData = printingPlayers.get(event.getPlayer().getUniqueId());
                    if (printingData != null) {
                        if (GSG_CORE.canBuild(event.getPlayer(), event.getBlockPlaced())) {
                            if (chargePlayer(event.getPlayer(), printingData, event.getBlockPlaced().getType())) {
                                printingData.addBlock(event.getBlock());
                            } else {
                                event.setCancelled(true);
                            }
                        }
                    }
                }).post(GSG_PRINTER);

        EventPost.of(ProjectileLaunchEvent.class)
                .filter(event -> event.getEntity().getShooter() instanceof Player)
                .filter(event -> printingPlayers.containsKey(((Player) event.getEntity().getShooter()).getUniqueId()))
                .handle(event -> event.setCancelled(true))
                .post(GSG_PRINTER);

        EventPost.of(PlayerExpChangeEvent.class)
                .filter(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()))
                .filter(event -> event.getAmount() > 0)
                .handle(event -> event.setAmount(0))
                .post(GSG_PRINTER);

        EventPost.of(PlayerChangedWorldEvent.class)
                .filter(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()))
                .handle(event -> disablePrinter(event.getPlayer(), true))
                .post(GSG_PRINTER);

        EventPost.of(HangingBreakByEntityEvent.class)
                .filter(event -> event.getRemover() instanceof Player)
                .filter(event -> printingPlayers.containsKey(event.getRemover().getUniqueId()))
                .handle(event -> event.setCancelled(true))
                .post(GSG_PRINTER);

        EventPost.of(ProjectileLaunchEvent.class, EventPriority.LOWEST)
                .filter(event -> event.getEntity() != null && event.getEntity().getShooter() != null)
                .filter(event -> event.getEntity().getShooter() instanceof Player)
                .filter(event -> ((Player) event.getEntity().getShooter()).getUniqueId() != null)
                .filter(event -> printingPlayers.containsKey(((Player) event.getEntity().getShooter()).getUniqueId()))
                .handle(event -> {
                    event.getEntity().remove();
                    event.setCancelled(true);
                }).post(GSG_PRINTER);


        EventPost.of(PotionSplashEvent.class, EventPriority.LOWEST)
                .filter(event -> event.getEntity() != null && event.getEntity().getShooter() != null)
                .filter(event -> event.getEntity().getShooter() instanceof Player)
                .filter(event -> ((Player) event.getEntity().getShooter()).getUniqueId() != null)
                .filter(event -> printingPlayers.containsKey(((Player) event.getEntity().getShooter()).getUniqueId()))
                .handle(event -> event.setCancelled(true))
                .post(GSG_PRINTER);

        EventPost.of(InventoryCreativeEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.containsKey(event.getWhoClicked().getUniqueId()))
                .handle(event -> {
                    if (event.getCurrentItem() != null) {
                        if (event.getCurrentItem().getType() == Material.MONSTER_EGG || event.getCurrentItem().getType() == Material.MONSTER_EGGS) {
                            event.setCancelled(true);
                        }
                    }
                    if (event.getCursor() != null) {
                        if (event.getCurrentItem().getType() == Material.MONSTER_EGG || event.getCurrentItem().getType() == Material.MONSTER_EGGS) {
                            event.setCancelled(true);
                        }
                    }
                }).post(GSG_PRINTER);

        EventPost.of(InventoryClickEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.containsKey(event.getWhoClicked().getUniqueId()))
                .handle(event -> {
                    if (event.getView().getType() == InventoryType.CREATIVE) {
                        if (event.getCurrentItem() != null) {
                            if (event.getCurrentItem().getType() == Material.MONSTER_EGG || event.getCurrentItem().getType() == Material.MONSTER_EGGS) {
                                event.setCancelled(true);
                                event.setCurrentItem(null);
                                event.setCursor(null);
                            }
                        }
                    } else {
                        event.setCancelled(true);
                        event.getWhoClicked().closeInventory();
                    }
                }).post(GSG_PRINTER);

        EventPost.of(InventoryOpenEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()))
                .filter(event -> event.getView().getType() != InventoryType.CREATIVE)
                .handle(event -> {
                    if (whitelistedInventoryTitles == null || whitelistedInventoryTitles.contains(event.getInventory().getTitle())) {
                        event.setCancelled(true);
                        event.getView().close();
                    }
                }).post(GSG_PRINTER);

        EventPost.of(PlayerCommandPreprocessEvent.class, EventPriority.LOWEST)
                .filter(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()))
                .handle(event -> {
                    String message = event.getMessage().toLowerCase().trim();
                    if (!startsWith(whitelistedCommands, message) || sift(blacklistedKeywords, message)) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Text.toColor("&cYou can't use that command in /printer!"));
                    }
                }).post(GSG_PRINTER);

        EventPost.of(EntityDamageByEntityEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getDamager() instanceof Player)
                .filter(event -> printingPlayers.containsKey(event.getDamager().getUniqueId()))
                .handle(event -> {
                    event.setCancelled(true);
                    disablePrinter((Player) event.getDamager(), true);
                }).post(GSG_PRINTER);

        EventPost.of(PlayerInteractEntityEvent.class, EventPriority.LOWEST)
                .filter(event -> event.getRightClicked() instanceof Player)
                .filter(event -> printingPlayers.containsKey(event.getRightClicked().getUniqueId()))
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
                .filter(event -> printingPlayers.containsKey(event.getWhoClicked().getUniqueId()))
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
                .filter(event -> printingPlayers.containsKey(event.getEntity().getUniqueId()))
                .handle(event -> {
                    event.getDrops().clear();
                    event.setDroppedExp(0);
                }).post(GSG_PRINTER);

        this.blockBreakEnabled = GSG_PRINTER.getConfig().getBoolean("enable-blockbreak");
        if (blockBreakEnabled) {
            EventPost.of(BlockBreakEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .handle(event -> {
                        PrintingData printingData = printingPlayers.get(event.getPlayer().getUniqueId());
                        if (printingData != null) {
                            if (BANNED_INTERACTABLES.contains(event.getBlock().getType()) || event.getBlock().getY() < 1) {
                                event.setCancelled(true);
                                event.getPlayer().sendMessage(PrinterMessages.YOU_CANT_BREAK_THIS_BLOCK.toString());
                            } else if (!printingData.hasPlacedBlock(event.getBlock())) {
                                event.getPlayer().sendMessage(PrinterMessages.YOU_CANT_BREAK_THIS_BLOCK.toString());
                                event.setCancelled(true);
                            }
                        }
                    }).post(GSG_PRINTER);
        } else {
            EventPost.of(BlockBreakEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> printingPlayers.containsKey(event.getPlayer().getUniqueId()))
                    .handle(event -> event.setCancelled(true))
                    .post(GSG_PRINTER);
        }
    }

    public void enablePrinter(Player player, boolean notify) {
        player.performCommand("f fly y");
        printingPlayers.put(player.getUniqueId(), new PrintingData());
        player.closeInventory();
        player.setGameMode(GameMode.CREATIVE);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (notify) {
            player.sendMessage(PrinterMessages.PRINTER_ENABLE.toString());
        }
        if (enablePrinterCommands != null) {
            enablePrinterCommands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName())));
        }
        if (disablePrinterCommands != null) {
            disablePrinterCommands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName())));
        }

        if (useNcp) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ncp exempt " + player.getName() + " net");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ncp exempt " + player.getName() + " blockplace");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ncp exempt " + player.getName() + " blockinteract");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ncp exempt " + player.getName() + " moving_morepackets");
        }
    }

    private boolean chargePlayer(Player player, PrintingData printingData, Material material) {
        double price = GSG_PRINTER.getSellIntegration().getBuyPrice(material);
        if (price == 0.0) {
            player.sendMessage(PrinterMessages.THIS_BLOCK_ISNT_PLACEABLE.toString());
            return false;
        }
        if (GSG_PRINTER.getConfig().getBoolean("async-payments")) {
            double balance = Module.getEconomy().getBalance(player);
            if (balance < price) {
                player.sendMessage(PrinterMessages.YOU_DONT_HAVE_ENOUGH_MONEY.toString().replace("{material}", material.name().toLowerCase().replace("_", " ")));
                disablePrinter(player, false);
                return false;
            } else {
                GSG_PRINTER.getServer().getScheduler().runTaskAsynchronously(GSG_PRINTER, () -> Module.getEconomy().withdrawPlayer(player, price));
            }
        } else {
            EconomyResponse economyResponse = Module.getEconomy().withdrawPlayer(player, price);
            if (!economyResponse.transactionSuccess()) {
                player.sendMessage(PrinterMessages.YOU_DONT_HAVE_ENOUGH_MONEY.toString().replace("{material}", material.name().toLowerCase().replace("_", " ")));
                disablePrinter(player, false);
                return false;
            }
        }
        printingData.getPlacedBlocks().computeIfPresent(material, (material1, integer) -> integer + 1);
        printingData.getPlacedBlocks().putIfAbsent(material, 1);
        return true;
    }

    public void disablePrinter(Player player, boolean notify) {
        disablePrinter(player, notify, true);
    }

    public void disablePrinter(Player player, boolean notify, boolean nofall) {
        PrintingData printingData = printingPlayers.get(player.getUniqueId());
        if (printingData == null) {
            return;
        }
        if (notify) {
            player.sendMessage(PrinterMessages.PRINTER_DISABLE.toString());
            if (printingData.hasPlacedBlocks()) {
                EnumMap<Material, Integer> map = printingData.getPlacedBlocks();
                if (!map.isEmpty()) {
                    double total = 0.0;
                    player.sendMessage(PrinterMessages.SOLD_HEADER.toString());
                    for (Map.Entry<Material, Integer> entry : map.entrySet()) {
                        double buyPrice = GSG_PRINTER.getSellIntegration().getBuyPrice(entry.getKey(), entry.getValue());
                        player.sendMessage(PrinterMessages.SOLD_LINE.toString().replace("{material}", Utils.toTitleCasing(entry.getKey().name().replace("_", " "))).replace("{money}", DECIMAL_FORMAT.format(buyPrice)));
                        total += buyPrice;
                    }
                    player.sendMessage(PrinterMessages.SOLD_TOTAL.toString().replace("{money}", DECIMAL_FORMAT.format(total)));
                }
            }
            player.sendMessage(PrinterMessages.TIME_SPENT.toString().replace("{time}", printingData.getTime()));
        }
        printingPlayers.remove(player.getUniqueId());
        player.performCommand("f fly n");
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        if (nofall) {
            player.setMetadata("nofalldamage", new FixedMetadataValue(GSG_PRINTER, true));
            GSG_PRINTER.getServer().getScheduler().runTaskLater(GSG_PRINTER, () -> player.removeMetadata("nofalldamage", GSG_PRINTER), 200L);
        }
        if (useNcp) {
            GSG_PRINTER.getServer().dispatchCommand(GSG_PRINTER.getServer().getConsoleSender(), "ncp unexempt " + player.getName());
        }
    }

    public Map<UUID, PrintingData> getPrintingPlayers() {
        return printingPlayers;
    }
}
