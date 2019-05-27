package com.gameservergroup.gsgcollectors.units;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.enums.CollectionType;
import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
import com.gameservergroup.gsgcollectors.integration.impl.FactionsUUIDImpl;
import com.gameservergroup.gsgcollectors.integration.impl.LockedThreadFactionsBankImpl;
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
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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

import java.util.EnumSet;
import java.util.HashMap;

public class UnitCollectors extends Unit {

    private static final GSGCollectors GSG_COLLECTORS = GSGCollectors.getInstance();

    //Menu
    private String collectorMenuName;
    private int collectorMenuSize;
    private boolean fillMenu;
    private ItemStack fillItemStack;

    //Integrations
    private FactionsBankIntegration factionsBankIntegration = null;

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
                new FactionsUUIDImpl().setupListeners(this);
            } else if (factions.getDescription().getAuthors().contains("drtshock")) {
                new FactionsUUIDImpl().setupListeners(this);
            } else {
                GSG_COLLECTORS.getLogger().severe("TNTBank will not work for you! Purchase LockedThread's FactionsFork for support!");
            }
        } else {
            EventPost.of(PlayerInteractEvent.class, EventPriority.HIGHEST)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> event.getClickedBlock() != null)
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
        TaskSave taskSave = new TaskSave(GSG_COLLECTORS.getConfig().getLong("options.save-task-delay"));
        taskSave.start();
        hookDisable(new CallBack() {
            @Override
            public void call() {
                taskSave.stop();
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
                                        if (GSG_CORE.canBuild(event.getPlayer(), next)) {
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
            this.harvesterHoeCustomItem = CustomItem.of(GSG_COLLECTORS.getConfig().getConfigurationSection("harvesterhoe-item")).setBreakEventConsumer(event -> {
                Block block = event.getBlock();
                if (block.getType() == Material.SUGAR_CANE_BLOCK) {
                    event.setCancelled(true);
                    Block next = block;
                    int sugarCaneAmount = 0;
                    while (next.getType() == Material.SUGAR_CANE_BLOCK) {
                        next.setTypeIdAndData(Material.AIR.getId(), (byte) 0, false);
                        sugarCaneAmount++;
                        next = next.getRelative(BlockFace.UP);
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

        this.collectorItem = CustomItem.of(GSG_COLLECTORS.getConfig().getConfigurationSection("collector-item"))
                .setPlaceEventConsumer(event -> {
                    Collector collector = getCollector(event.getBlockPlaced().getLocation());
                    if (collector == null) {
                        createCollector(event.getBlockPlaced().getLocation()).setLandOwner(getFactionsBankIntegration() != null ? getFactionsBankIntegration().getFaction(event.getPlayer()).getTag() + "'s" : event.getPlayer().getName() + "'s");
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
                    }
                });
        CustomItem.of(GSG_COLLECTORS.getConfig().getConfigurationSection("sellwand-item")).setInteractEventConsumer(event -> {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (GSG_COLLECTORS.getConfig().getString("sellwand-item.material").equalsIgnoreCase(event.getClickedBlock().getType().name())) {
                    Collector collector = getCollector(event.getClickedBlock().getLocation());
                    if (collector != null && collector.getBlockPosition().equals(BlockPosition.of(event.getClickedBlock()))) {
                        collector.sellAll(event.getPlayer());
                        event.setCancelled(true);
                    }
                }
            }
        });
        CustomItem.of(GSG_COLLECTORS.getConfig().getConfigurationSection("tntwand-item")).setInteractEventConsumer(event -> {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Collector collector = getCollector(event.getClickedBlock().getLocation());
                if (collector != null && collector.getBlockPosition().equals(BlockPosition.of(event.getClickedBlock()))) {
                    collector.depositTnt(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        });
        EnumSet<CollectionType> collectionTypes = EnumSet.noneOf(CollectionType.class);
        ConfigurationSection collectionTypeSection = GSG_COLLECTORS.getConfig().getConfigurationSection("collection-types");
        for (String key : collectionTypeSection.getKeys(false)) {
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

    public Collector createCollector(Location location) {
        Collector collector = new Collector(location);
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
}
