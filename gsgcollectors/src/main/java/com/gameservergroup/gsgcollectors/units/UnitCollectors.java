package com.gameservergroup.gsgcollectors.units;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.enums.CollectionType;
import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
import com.gameservergroup.gsgcollectors.integration.LandOwnerIntegration;
import com.gameservergroup.gsgcollectors.integration.impl.LockedThreadFactionsBankImpl;
import com.gameservergroup.gsgcollectors.integration.impl.landowner.LandOwnerASkyBlockImpl;
import com.gameservergroup.gsgcollectors.integration.impl.landowner.LandOwnerFabledSkyBlockImpl;
import com.gameservergroup.gsgcollectors.integration.impl.landowner.LandOwnerFactionsUUIDImpl;
import com.gameservergroup.gsgcollectors.obj.Collector;
import com.gameservergroup.gsgcollectors.task.TaskSave;
import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.storage.JsonFile;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.CallBack;
import com.gameservergroup.gsgcore.utils.NBTItem;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.github.paperspigot.Title;

import java.util.*;
import java.util.stream.Collectors;

public class UnitCollectors extends Unit {

    private static final GSGCollectors GSG_COLLECTORS = GSGCollectors.getInstance();

    //Menu
    private String collectorMenuName;
    private int collectorMenuSize;
    private boolean fillMenu;
    private ItemStack fillItemStack;

    //Integrations
    private FactionsBankIntegration factionsBankIntegration = null;
    private LandOwnerIntegration landOwnerIntegration = null;

    //Storage
    private EnumSet<CollectionType> collectionTypes;
    private HashMap<ChunkPosition, Collector> collectorHashMap;
    private JsonFile<HashMap<ChunkPosition, Collector>> jsonFile;

    //Options
    private boolean accessNotYours;
    private boolean roleRestricted;
    private boolean editWhilstFactionless;
    private boolean useTitles;
    private boolean preventNormalFarms;
    private boolean creepersCollectTNT;
    private boolean harvesterHoesEnabled;
    private boolean harvesterHoesCollectDoubleSugarcane;
    private boolean autoPickupSugarcaneNormally;
    private boolean collectToInventoryWithNoCollector;

    //Items
    private CustomItem collectorItem;
    private CustomItem harvesterHoeCustomItem;

    @Override
    public void setup() {
        Plugin factions = GSG_COLLECTORS.getServer().getPluginManager().getPlugin("Factions");
        if (factions != null) {
            if (factions.getDescription().getAuthors().contains("LockedThread")) {
                this.factionsBankIntegration = new LockedThreadFactionsBankImpl();
                GSG_COLLECTORS.getLogger().info("Enabled LockedThread FactionsBank implementation");
                (this.landOwnerIntegration = new LandOwnerFactionsUUIDImpl()).setupListeners(this);
            } else if (factions.getDescription().getAuthors().contains("drtshock")) {
                (this.landOwnerIntegration = new LandOwnerFactionsUUIDImpl()).setupListeners(this);
            } else {
                GSG_COLLECTORS.getLogger().severe("TNTBank will not work for you! Purchase LockedThread's FactionsFork for support!");
            }
        } else if (GSG_COLLECTORS.getServer().getPluginManager().getPlugin("ASkyBlock") != null) {
            if (GSG_COLLECTORS.getConfig().getBoolean("options.landowner.askyblock.enabled")) {
                (this.landOwnerIntegration = new LandOwnerASkyBlockImpl()).setupListeners(this);
                GSG_COLLECTORS.getLogger().info("Enabled ASkyBlock implementation!");
            }
        } else if (GSG_COLLECTORS.getServer().getPluginManager().getPlugin("FabledSkyBlock") != null) {
            if (GSG_COLLECTORS.getConfig().getBoolean("options.landowner.fabled-skyblock.enabled")) {
                (this.landOwnerIntegration = new LandOwnerFabledSkyBlockImpl()).setupListeners(this);
                GSG_COLLECTORS.getLogger().info("Enabled FabledSkyBlock implementation!");
            }
        } else {
            EventPost.of(PlayerInteractEvent.class, EventPriority.HIGHEST)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> event.getClickedBlock() != null)
                    .filter(event -> event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    .handle(event -> {
                        Collector collector = getCollector(event.getClickedBlock().getLocation());
                        if (collector != null && collector.getBlockPosition().equals(BlockPosition.of(event.getClickedBlock()))) {
                            if (event.getPlayer().isSneaking() && event.getPlayer().hasPermission("gsgcollector.clicktosell")) {
                                collector.sellAll(event.getPlayer());
                            } else {
                                if (collector.getMenuCollector().getInventory().getViewers().isEmpty()) {
                                    collector.getMenuCollector().refresh();
                                }
                                event.getPlayer().openInventory(collector.getMenuCollector().getInventory());
                            }
                            event.setCancelled(true);
                        }
                    }).post(GSG_COLLECTORS);
        }

        this.jsonFile = new JsonFile<>(GSG_COLLECTORS.getDataFolder(), "collectors.json", new TypeToken<HashMap<ChunkPosition, Collector>>() {
        });
        this.collectorHashMap = jsonFile.load().orElse(new HashMap<>());
        TaskSave taskSave = new TaskSave();
        taskSave.runTaskTimer(GSG_COLLECTORS, GSG_COLLECTORS.getConfig().getLong("options.save-task-delay"), GSG_COLLECTORS.getConfig().getLong("options.save-task-delay"));
        hookDisable(new CallBack() {
            @Override
            public void call() {
                taskSave.cancel();
                jsonFile.save(collectorHashMap);
            }
        });
        load();
        EventPost.of(BlockBreakEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .handle(event -> {
                    Block block = event.getBlock();
                    Collector collector = getCollector(block.getLocation());
                    if (collector != null) {
                        if (collector.getBlockPosition().equals(BlockPosition.of(block))) {
                            removeCollector(block.getLocation(), true);
                            event.setCancelled(true);
                            if (!CollectorMessages.TITLE_COLLECTOR_BREAK.toString().isEmpty()) {
                                if (useTitles) {
                                    event.getPlayer().sendTitle(Title.builder().title(CollectorMessages.TITLE_COLLECTOR_BREAK.toString()).fadeIn(5).fadeOut(5).stay(25).build());
                                } else {
                                    event.getPlayer().sendMessage(CollectorMessages.TITLE_COLLECTOR_BREAK.toString());
                                }
                            }
                        } else if (autoPickupSugarcaneNormally && block.getType() == Material.SUGAR_CANE_BLOCK && getCollectionTypes().contains(CollectionType.SUGAR_CANE)) {
                            CustomItem customItem;
                            if (event.getPlayer().getItemInHand() == null || ((customItem = CustomItem.findCustomItem(event.getPlayer().getItemInHand())) == null || !customItem.equals(harvesterHoeCustomItem))) {
                                Block next = block;
                                int sugarCaneAmount = 0;
                                while (next.getType() == Material.SUGAR_CANE_BLOCK) {
                                    next.setTypeIdAndData(Material.AIR.getId(), (byte) 0, false);
                                    sugarCaneAmount++;
                                    next = next.getRelative(BlockFace.UP);
                                    if (GSG_COLLECTORS.getConfig().getBoolean("options.harvester-hoes.protected-region-check-for-iterated-sugar-cane")) {
                                        if (!GSG_CORE.canBuild(event.getPlayer(), next)) {
                                            break;
                                        }
                                    }
                                }
                                collector.addAmount(CollectionType.SUGAR_CANE, sugarCaneAmount);
                            }
                        }
                    }
                }).post(GSG_COLLECTORS);

        EventPost.of(EntityExplodeEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.blockList() != null)
                .handle(event -> event.blockList().removeIf(block -> block.getType() == Material.BEACON))
                .post(GSG_COLLECTORS);

        EventPost.of(BlockExplodeEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.blockList() != null)
                .handle(event -> event.blockList().removeIf(block -> block.getType() == Material.BEACON))
                .post(GSG_COLLECTORS);

        if (preventNormalFarms) {
            EventPost.of(SpawnerPreSpawnEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> collectionTypes.contains(CollectionType.fromEntityType(event.getSpawnedType())))
                    .handle(event -> {
                        Collector collector = collectorHashMap.get(ChunkPosition.of(event.getLocation().getChunk()));
                        if (collector != null) {
                            event.setCancelled(true);
                            if (event.getSpawnedType() == EntityType.CREEPER && (CollectionType.CREEPER.getItemStack() != null || CollectionType.TNT.getItemStack() != null)) {
                                if (creepersCollectTNT) {
                                    collector.addAmount(CollectionType.TNT, (int) CollectionType.TNT.getPrice());
                                } else {
                                    collector.addAmount(CollectionType.GUN_POWDER, 1);
                                }
                            } else {
                                collector.addAmount(CollectionType.fromEntityType(event.getSpawnedType()), 1);
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    }).post(GSG_COLLECTORS);
        } else {
            EventPost.of(SpawnerPreSpawnEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .handle(event -> {
                        Collector collector = collectorHashMap.get(ChunkPosition.of(event.getLocation().getChunk()));
                        if (collector != null && collectionTypes.contains(CollectionType.fromEntityType(event.getSpawnedType()))) {
                            event.setCancelled(true);
                            if (event.getSpawnedType() == EntityType.CREEPER && (CollectionType.CREEPER.getItemStack() != null || CollectionType.TNT.getItemStack() != null)) {
                                if (creepersCollectTNT) {
                                    collector.addAmount(CollectionType.TNT, (int) CollectionType.TNT.getPrice());
                                } else {
                                    collector.addAmount(CollectionType.GUN_POWDER, 1);
                                }
                            } else {
                                collector.addAmount(CollectionType.fromEntityType(event.getSpawnedType()), 1);
                            }
                        }
                    }).post(GSG_COLLECTORS);
        }

        if (collectionTypes.contains(CollectionType.CACTUS)) {
            EventPost.of(BlockGrowEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> event.getNewState().getType() == Material.CACTUS)
                    .filter(event -> !canGrow(event.getBlock()))
                    .handle(event -> {
                        Collector collector = getCollector(event.getBlock().getLocation());
                        event.setCancelled(true);
                        if (collector != null) {
                            collector.addAmount(CollectionType.CACTUS, 1);
                        }
                    }).post(GSG_COLLECTORS);
        }
    }

    private List<String> defaultSellWandLore = null;

    public void load() {
        this.collectorMenuSize = GSG_COLLECTORS.getConfig().getInt("menu.size");
        this.collectorMenuName = GSG_COLLECTORS.getConfig().getString("menu.name");
        this.accessNotYours = GSG_COLLECTORS.getConfig().getBoolean("options.can-access-not-yours");
        this.editWhilstFactionless = GSG_COLLECTORS.getConfig().getBoolean("options.can-edit-whilst-factionless");
        this.roleRestricted = GSG_COLLECTORS.getConfig().getBoolean("options.is-role-restricted", true);
        this.useTitles = GSG_COLLECTORS.getConfig().getBoolean("options.use-titles", true);
        this.preventNormalFarms = GSG_COLLECTORS.getConfig().getBoolean("options.prevent-normal-farms", true);
        this.creepersCollectTNT = GSG_COLLECTORS.getConfig().getBoolean("options.creepers-collect-tnt");
        this.autoPickupSugarcaneNormally = GSG_COLLECTORS.getConfig().getBoolean("options.auto-pickup-sugar-cane-normally");
        if (this.harvesterHoesEnabled = GSG_COLLECTORS.getConfig().getBoolean("options.harvester-hoes.enabled")) {
            this.collectToInventoryWithNoCollector = GSG_COLLECTORS.getConfig().getBoolean("options.harvester-hoes.collect-to-inventory-with-no-collector");
            this.harvesterHoesCollectDoubleSugarcane = GSG_COLLECTORS.getConfig().getBoolean("options.harvester-hoes.collect-double-sugar-cane");
            this.harvesterHoeCustomItem = CustomItem.of(GSG_COLLECTORS, GSG_COLLECTORS.getConfig().getConfigurationSection("harvesterhoe-item")).setBreakEventConsumer(event -> {
                Block block = event.getBlock();
                if (block.getType() == Material.SUGAR_CANE_BLOCK) {
                    //event.setCancelled(true);
                    Block next = block;
                    int sugarCaneAmount = 0;
                    while (next.getType() == Material.SUGAR_CANE_BLOCK) {
                        next.setTypeIdAndData(Material.AIR.getId(), (byte) 0, false);
                        sugarCaneAmount++;
                        next = next.getRelative(BlockFace.UP);
                    }
                    if (GSG_COLLECTORS.getMcMMOIntegration() != null) {
                        for (int i = 0; i < sugarCaneAmount; i++) {
                            GSG_COLLECTORS.getMcMMOIntegration().addMcMMOExp(event.getPlayer(), "herbalism", GSG_COLLECTORS.getConfig().getFloat("options.harvester-hoes.mcmmo.preset-herbalism-exp"));
                        }
                    }
                    sugarCaneAmount *= harvesterHoesCollectDoubleSugarcane ? 2 : 1;
                    Collector collector = getCollector(block.getLocation());
                    if (collector != null) {
                        collector.addAmount(CollectionType.SUGAR_CANE, sugarCaneAmount);
                    } else if (collectToInventoryWithNoCollector) {
                        event.getPlayer().getInventory().addItem(new ItemStack(Material.SUGAR_CANE, sugarCaneAmount));
                    }
                }
            });
        }
        if (this.fillMenu = GSG_COLLECTORS.getConfig().getBoolean("menu.fill.enabled")) {
            this.fillItemStack = GSG_COLLECTORS.getConfig().getBoolean("menu.fill.enchanted") ? ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                    .setDyeColor(DyeColor.valueOf(GSG_COLLECTORS.getConfig().getString("menu.fill.glass-pane-color")))
                    .setDisplayName(" ")
                    .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    .addEnchant(Enchantment.DURABILITY, 1)
                    .build() : ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                    .setDyeColor(DyeColor.valueOf(GSG_COLLECTORS.getConfig().getString("menu.fill.glass-pane-color")))
                    .setDisplayName(" ")
                    .build();
        }

        if (!(landOwnerIntegration instanceof LandOwnerFactionsUUIDImpl)) {
            this.collectorItem = CustomItem.of(GSG_COLLECTORS, GSG_COLLECTORS.getConfig().getConfigurationSection("collector-item"))
                    .setPlaceEventConsumer(event -> {
                        Collector collector = getCollector(event.getBlockPlaced().getLocation());
                        if (collector == null) {
                            createCollector(event.getBlockPlaced().getLocation(), getFactionsBankIntegration() != null ? getFactionsBankIntegration().getFaction(event.getPlayer()).getTag() : event.getPlayer().getName());
                            if (!CollectorMessages.TITLE_COLLECTOR_PLACE.toString().isEmpty()) {
                                if (useTitles) {
                                    event.getPlayer().sendTitle(Title.builder().title(CollectorMessages.TITLE_COLLECTOR_PLACE.toString()).fadeIn(5).fadeOut(5).stay(25).build());
                                } else {
                                    event.getPlayer().sendMessage(CollectorMessages.TITLE_COLLECTOR_PLACE.toString());
                                }
                            }
                        } else {
                            collector.getBlockPosition().getBlock().setType(Material.AIR);
                            collector.setBlockPosition(BlockPosition.of(event.getBlockPlaced()));
                            event.getPlayer().sendMessage(CollectorMessages.UPDATED_COLLECTOR_BLOCKPOSITION.toString());
                            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                                event.getPlayer().setItemInHand(event.getItemInHand());
                            }
                        }
                    });
        }
        CustomItem sellWandCustomItem = CustomItem.of(GSG_COLLECTORS, GSG_COLLECTORS.getConfig().getConfigurationSection("sellwand-item")).setInteractEventConsumer(event -> {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && GSG_COLLECTORS.getConfig().getString("collector-item.material").equalsIgnoreCase(event.getClickedBlock().getType().name())) {
                Collector collector = getCollector(event.getClickedBlock().getLocation());
                Player player = event.getPlayer();
                if (collector != null && collector.getBlockPosition().equals(BlockPosition.of(event.getClickedBlock()))) {
                    if (landOwnerIntegration != null) {
                        if (landOwnerIntegration.canAccessCollector(player, collector, event.getClickedBlock().getLocation(), true)) {
                            checkSellWandUses(event.getItem(), collector, player);
                        }
                    } else {
                        checkSellWandUses(event.getItem(), collector, player);
                    }
                }
                event.setCancelled(true);

            }
        });
        sellWandCustomItem.setRequestedNBT("uses", "multiplier");
        sellWandCustomItem.setItemEdit(new CustomItem.ItemEdit() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public ItemStack getEditedItemStack() {
                return getEditedItemStack(null);
            }

            @Override
            public ItemStack getEditedItemStack(Map<String, Object> map) {
                int uses = (int) map.get("uses");
                uses = uses == 0 ? -1 : uses;

                double multiplier = (double) map.get("multiplier");
                multiplier = multiplier == 0 ? 1.0 : multiplier;

                final ItemStack itemStack = sellWandCustomItem.getOriginalItemStack();
                NBTItem nbtItem = new NBTItem(itemStack)
                        .set("uses", uses)
                        .set("multiplier", multiplier);
                List<String> list = new ArrayList<>();
                for (String s : itemStack.getItemMeta().getLore()) {
                    list.add(s.replace("{uses}", uses == -1 ? GSG_COLLECTORS.getConfig().getString("sellwand-item.options.negative-1-keyword") : String.valueOf(uses))
                            .replace("{multiplier}", String.valueOf(multiplier)));
                }
                return ItemStackBuilder.of(nbtItem.buildItemStack()).setLore(list).build();
            }
        });

        CustomItem.of(GSG_COLLECTORS, GSG_COLLECTORS.getConfig().

                getConfigurationSection("tntwand-item")).

                setInteractEventConsumer(event ->

                {
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && GSG_COLLECTORS.getConfig().getString("collector-item.material").equalsIgnoreCase(event.getClickedBlock().getType().name())) {
                        Collector collector = getCollector(event.getClickedBlock().getLocation());
                        if (collector != null && collector.getBlockPosition().equals(BlockPosition.of(event.getClickedBlock()))) {
                            if (landOwnerIntegration.canAccessCollector(event.getPlayer(), collector, event.getClickedBlock().getLocation(), true)) {
                                collector.depositTnt(event.getPlayer());
                            }
                            event.setCancelled(true);
                        }
                    }
                });
        EnumSet<CollectionType> collectionTypes = EnumSet.noneOf(CollectionType.class);
        ConfigurationSection collectionTypeSection = GSG_COLLECTORS.getConfig().getConfigurationSection("collection-types");
        for (
                String key : collectionTypeSection.getKeys(false)) {
            CollectionType collectionType;
            try {
                collectionType = CollectionType.valueOf(key.toUpperCase().replace("-", "_"));
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to find CollectionType enum constant called " + key);
                e.printStackTrace();
                continue;
            }
            collectionType.init(key);
            collectionTypes.add(collectionType);
        }
        this.collectionTypes = collectionTypes;
    }

    public void checkSellWandUses(ItemStack itemStack, Collector collector, Player player) {
        NBTItem nbtItem = new NBTItem(itemStack);
        int uses = nbtItem.getInt("uses");
        if (uses == 0) {
            if (isUseTitles()) {
                player.sendTitle(Title.builder().title(CollectorMessages.YOUR_SELLWAND_IS_BROKEN.toString()).fadeIn(5).fadeOut(5).stay(25).build());
            } else {
                player.sendMessage(CollectorMessages.YOUR_SELLWAND_IS_BROKEN.toString());
            }
        } else {
            double multiplier = nbtItem.getDouble("multiplier");
            if (uses == -1) {
                collector.sellAll(player, multiplier);
            } else {
                collector.sellAll(player, multiplier);
                nbtItem.set("uses", uses -= 1);

                itemStack = nbtItem.buildItemStack();

                player.setItemInHand(ItemStackBuilder.of(itemStack).setLore(getReformattedLore(uses, multiplier)).build());
            }
        }
    }

    private List<String> getReformattedLore(int uses, double multiplier) {
        if (defaultSellWandLore == null) {
            defaultSellWandLore = CustomItem.getCustomItem("sellwand-item").getOriginalItemStack().getItemMeta().getLore();
        }
        return defaultSellWandLore.stream()
                .map(k -> k.replace("{uses}", String.valueOf(uses)).replace("{multiplier}", String.valueOf(multiplier)))
                .collect(Collectors.toList());
    }

    private boolean canGrow(Block bukkitBlock) {
        final net.minecraft.server.v1_8_R3.BlockPosition blockPosition = new net.minecraft.server.v1_8_R3.BlockPosition(bukkitBlock.getX(), bukkitBlock.getY(), bukkitBlock.getZ());
        final net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) bukkitBlock.getWorld()).getHandle();

        for (EnumDirection enumDirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            if (nmsWorld.getType(blockPosition.shift(enumDirection)).getBlock().getMaterial().isBuildable()) {
                return false;
            }
        }

        final net.minecraft.server.v1_8_R3.Block block = nmsWorld.getType(blockPosition.down()).getBlock();
        return block == Blocks.CACTUS || block == Blocks.SAND;
    }

    public Collector getCollector(Location location) {
        return collectorHashMap.get(ChunkPosition.of(location.getChunk()));
    }

    public Collector createCollector(Location location, String owner) {
        Collector collector = new Collector(location, owner);
        collectorHashMap.put(ChunkPosition.of(location.getChunk()), collector);
        return collector;
    }

    public void removeCollector(Location location, boolean force) {
        collectorHashMap.remove(ChunkPosition.of(location.getChunk()));
        if (force) {
            if (!location.getChunk().isLoaded()) {
                location.getChunk().load();
            }
            location.getBlock().setType(Material.AIR);
            location.getWorld().dropItemNaturally(location, getCollectorItem().getItemStack());
        }
    }

    public boolean decrementSellWandUses(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        int uses = nbtItem.getInt("uses");
        if (uses == -1) return true;
        if (uses == 0) return false;

        nbtItem.set("uses", uses - 1);

        return true;
    }

    public String getCollectorMenuName() {
        return collectorMenuName;
    }

    public int getCollectorMenuSize() {
        return collectorMenuSize;
    }

    public EnumSet<CollectionType> getCollectionTypes() {
        return collectionTypes;
    }

    public FactionsBankIntegration getFactionsBankIntegration() {
        return factionsBankIntegration;
    }

    public boolean isEditWhilstFactionless() {
        return editWhilstFactionless;
    }

    public boolean isRoleRestricted() {
        return roleRestricted;
    }

    public boolean isAccessNotYours() {
        return accessNotYours;
    }

    public CustomItem getCollectorItem() {
        return collectorItem;
    }

    public boolean isUseTitles() {
        return useTitles;
    }

    public boolean isFillMenu() {
        return fillMenu;
    }

    public ItemStack getFillItemStack() {
        return fillItemStack;
    }

    public HashMap<ChunkPosition, Collector> getCollectorHashMap() {
        return collectorHashMap;
    }

    public JsonFile<HashMap<ChunkPosition, Collector>> getJsonFile() {
        return jsonFile;
    }

    public boolean isCreepersCollectTNT() {
        return creepersCollectTNT;
    }

    public boolean isHarvesterHoesEnabled() {
        return harvesterHoesEnabled;
    }

    public boolean isPreventNormalFarms() {
        return preventNormalFarms;
    }

    public boolean isHarvesterHoesCollectDoubleSugarcane() {
        return harvesterHoesCollectDoubleSugarcane;
    }

    public boolean isAutoPickupSugarcaneNormally() {
        return autoPickupSugarcaneNormally;
    }

    public boolean isCollectToInventoryWithNoCollector() {
        return collectToInventoryWithNoCollector;
    }

    public LandOwnerIntegration getLandOwnerIntegration() {
        return landOwnerIntegration;
    }

    public void setCollectorItem(CustomItem collectorItem) {
        this.collectorItem = collectorItem;
    }
}
