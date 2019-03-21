package com.gameservergroup.gsgcollectors.units;

import com.gameservergroup.gsgcollectors.GSGCollectors;
import com.gameservergroup.gsgcollectors.enums.CollectionType;
import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
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
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
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
    private Role atLeastRole;
    private boolean accessNotYours;
    private boolean roleRestricted;
    private boolean editWhilstFactionless;
    private boolean useTitles;
    private boolean preventNormalFarms;

    //Items
    private CustomItem collectorItem;

    @Override
    public void setup() {
        jsonFile = new JsonFile<>(GSG_COLLECTORS.getDataFolder(), "collectors", new TypeToken<HashMap<ChunkPosition, Collector>>() {
        });
        this.collectorHashMap = jsonFile.load().orElse(new HashMap<>());
        TaskSave taskSave = new TaskSave(GSG_COLLECTORS.getConfig().getLong("options.save-task-delay"));
        taskSave.start();
        hookDisable(new CallBack() {
            @Override
            public void call() {
                taskSave.interrupt();
                jsonFile.save(collectorHashMap);
            }
        });
        this.collectorMenuSize = GSG_COLLECTORS.getConfig().getInt("menu.size");
        this.collectorMenuName = GSG_COLLECTORS.getConfig().getString("menu.name");
        this.atLeastRole = Role.fromString(GSG_COLLECTORS.getConfig().getString("options.at-least-role", "COLEADER"));
        this.accessNotYours = GSG_COLLECTORS.getConfig().getBoolean("options.can-access-not-yours");
        this.editWhilstFactionless = GSG_COLLECTORS.getConfig().getBoolean("options.can-edit-whilst-factionless");
        this.roleRestricted = GSG_COLLECTORS.getConfig().getBoolean("options.is-role-restricted", true);
        this.useTitles = GSG_COLLECTORS.getConfig().getBoolean("options.use-titles", true);
        this.preventNormalFarms = GSG_COLLECTORS.getConfig().getBoolean("options.prevent-normal-farms", true);
        if (this.fillMenu = GSG_COLLECTORS.getConfig().getBoolean("menu.fill.enabled")) {
            if (GSG_COLLECTORS.getConfig().getBoolean("menu.fill.enchanted")) {
                this.fillItemStack = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.valueOf(GSG_COLLECTORS.getConfig().getString("menu.fill.glass-pane-color")))
                        .setDisplayName(" ")
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .addEnchant(Enchantment.DURABILITY, 1)
                        .build();
            } else {
                this.fillItemStack = ItemStackBuilder.of(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.valueOf(GSG_COLLECTORS.getConfig().getString("menu.fill.glass-pane-color")))
                        .setDisplayName(" ")
                        .build();
            }
        }

        this.collectorItem = CustomItem.of(GSG_COLLECTORS.getConfig().getConfigurationSection("collector-item")).setPlaceEventConsumer(event -> {
            if (getCollector(event.getBlockPlaced().getLocation()) == null) {
                createCollector(event.getBlockPlaced().getLocation());
                if (!CollectorMessages.TITLE_COLLECTOR_PLACE.toString().isEmpty()) {
                    if (useTitles) {
                        event.getPlayer().sendTitle(Title.builder().title(CollectorMessages.TITLE_COLLECTOR_PLACE.toString()).fadeIn(5).fadeOut(5).stay(25).build());
                    } else {
                        event.getPlayer().sendMessage(CollectorMessages.TITLE_COLLECTOR_PLACE.toString());
                    }
                }
            } else {
                event.getPlayer().sendMessage(CollectorMessages.ALREADY_ONE_HERE.toString());
                event.setCancelled(true);
            }
        });
        if (GSG_COLLECTORS.getServer().getPluginManager().getPlugin("Factions") != null) {
            if (GSG_COLLECTORS.getServer().getPluginManager().getPlugin("Factions").getDescription().getAuthors().contains("LockedThread")) {
                this.factionsBankIntegration = new LockedThreadFactionsBankImpl();
                GSG_COLLECTORS.getLogger().info("Enabled LockedThread FactionsBank implementation");
            } else {
                GSG_COLLECTORS.getLogger().severe("TNTBank will not work for you! Purchase LockedThread's FactionsFork for support!");
            }
        }
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

        EventPost.of(BlockBreakEvent.class, EventPriority.HIGHEST)
                .filter(EventFilters.getIgnoreCancelled())
                .handle(event -> {
                    Collector collector = getCollector(event.getBlock().getLocation());
                    if (collector != null) {
                        if (collector.getBlockPosition().equals(BlockPosition.of(event.getBlock()))) {
                            removeCollector(event.getBlock().getLocation(), true);
                            event.setCancelled(true);
                            if (!CollectorMessages.TITLE_COLLECTOR_BREAK.toString().isEmpty()) {
                                if (useTitles) {
                                    event.getPlayer().sendTitle(Title.builder().title(CollectorMessages.TITLE_COLLECTOR_BREAK.toString()).fadeIn(5).fadeOut(5).stay(25).build());
                                } else {
                                    event.getPlayer().sendMessage(CollectorMessages.TITLE_COLLECTOR_BREAK.toString());
                                }
                            }
                        } else if (event.getBlock().getType() == Material.SUGAR_CANE_BLOCK && getCollectionTypes().contains(CollectionType.SUGAR_CANE)) {
                            collector.addAmount(CollectionType.SUGAR_CANE, 1);
                        }
                    }
                }).post(GSG_COLLECTORS);

        EventPost.of(PlayerInteractEvent.class, EventPriority.LOWEST)
                .filter(EventFilters.getIgnoreCancelled())
                .filter(event -> event.getClickedBlock() != null)
                .handle(event -> {
                    Collector collector = getCollector(event.getClickedBlock().getLocation());
                    if (collector != null && collector.getBlockPosition().equals(BlockPosition.of(event.getClickedBlock()))) {
                        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
                        Faction myFaction = fPlayer.getFaction();
                        if (!editWhilstFactionless && myFaction.isWilderness() && !fPlayer.isAdminBypassing()) {
                            event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_FACTIONLESS.toString());
                        } else {
                            FLocation fLocation = new FLocation(event.getClickedBlock());
                            Faction factionThere = Board.getInstance().getFactionAt(fLocation);
                            if (accessNotYours && factionThere.getRelationTo(myFaction) != Relation.MEMBER && !fPlayer.isAdminBypassing()) {
                                event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_NOT_YOURS.toString());
                            } else if (roleRestricted && !fPlayer.getRole().isAtLeast(atLeastRole) && !fPlayer.isAdminBypassing()) {
                                event.getPlayer().sendMessage(CollectorMessages.NO_ACCESS_NO_PERMISSIONS.toString().replace("{role}", atLeastRole.toString()));
                            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                if (event.getPlayer().isSneaking() && event.getPlayer().hasPermission("gsgcollector.clicktosell")) {
                                    collector.sellAll(event.getPlayer());
                                } else {
                                    event.getPlayer().openInventory(collector.getMenuCollector().getInventory());
                                }
                                event.setCancelled(true);
                            }
                        }
                    }
                }).post(GSG_COLLECTORS);
        EventPost.of(BlockExplodeEvent.class)
                .filter(EventFilters.getIgnoreCancelled())
                .handle(event -> event.blockList()
                        .stream()
                        .filter(block -> block.getType() == Material.BEACON)
                        .forEach(block -> event.blockList().remove(block)))
                .post(GSG_COLLECTORS);

        if (preventNormalFarms) {
            EventPost.of(SpawnerPreSpawnEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .handle(event -> {
                        Collector collector = collectorHashMap.get(ChunkPosition.of(event.getLocation().getChunk()));
                        if (collector != null) {
                            if (collectionTypes.contains(CollectionType.fromEntityType(event.getSpawnedType()))) {
                                event.setCancelled(true);
                                if (event.getSpawnedType() == EntityType.CREEPER) {
                                    collector.addAmount(CollectionType.TNT, (int) CollectionType.TNT.getPrice());
                                } else {
                                    collector.addAmount(CollectionType.fromEntityType(event.getSpawnedType()), 1);
                                }
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
                            if (event.getSpawnedType() == EntityType.CREEPER) {
                                collector.addAmount(CollectionType.TNT, (int) CollectionType.TNT.getPrice());
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

    public Role getAtLeastRole() {
        return atLeastRole;
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
}
