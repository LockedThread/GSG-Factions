package com.massivecraft.factions;

import com.darkblade12.particleeffect.ParticleEffect;
import com.earth2me.essentials.IEssentials;
import com.gameservergroup.gsgcore.events.EventFilters;
import com.gameservergroup.gsgcore.events.EventPost;
import com.google.common.base.Joiner;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.integration.CombatIntegration;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.integration.combat.impl.CombatTagPlusImpl;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.tasks.TaskAutoLeave;
import com.massivecraft.factions.tasks.TaskFlight;
import com.massivecraft.factions.tasks.TaskWallCheckReminder;
import com.massivecraft.factions.units.UnitFactionUpgrade;
import com.massivecraft.factions.units.UnitWorldBorder;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.util.particle.BukkitParticleProvider;
import com.massivecraft.factions.util.particle.PacketParticleProvider;
import com.massivecraft.factions.util.particle.ParticleProvider;
import com.massivecraft.factions.zcore.MPlugin;
import com.massivecraft.factions.zcore.factionboosters.Booster;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class P extends MPlugin {

    // Our single plugin instance.
    // Single 4 life.
    public static com.massivecraft.factions.P p;
    public static Permission perms = null;
    // Commands
    public FCmdRoot cmdBase;
    public CmdAutoHelp cmdAutoHelp;
    // Persistence related
    private boolean locked = false;
    private boolean sotw = false;
    private long factionsFlightDelay;
    private FactionsPlayerListener factionsPlayerListener;
    private Integer autoLeaveTask = null;
    private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
    private boolean mvdwPlaceholderAPIManager = false;
    private int defaultTntBankBalance;
    private CombatIntegration combatIntegration;
    public SeeChunkUtil seeChunkUtil;
    public ParticleProvider particleProvider;

    public P() {
        p = this;
    }

    public boolean getLocked() {
        return this.locked;
    }

    public void setLocked(boolean val) {
        this.locked = val;
        this.setAutoSave(val);
    }

    @Override
    public void enable() {
        if (!preEnable()) {
            return;
        }
        this.loadSuccessful = false;
        saveDefaultConfig();

        // Load Conf from disk
        Conf.load();

        // Check for Essentials
        IEssentials ess = Essentials.setup();

        // We set the option to TRUE by default in the config.yml for new users,
        // BUT we leave it set to false for users updating that haven't added it to their config.
        if (ess != null && getConfig().getBoolean("delete-ess-homes", false)) {
            p.log(Level.INFO, "Found Essentials. We'll delete player homes in their old Faction's when kicked.");
            getServer().getPluginManager().registerEvents(new EssentialsListener(ess), this);
        }

        // Register units so that factionupgrades are loaded before factions are
        registerUnits(new UnitWorldBorder(), new UnitFactionUpgrade());

        FPlayers.getInstance().load();
        Factions.getInstance().load();
        for (FPlayer fPlayer : FPlayers.getInstance().getAllFPlayers()) {
            Faction faction = Factions.getInstance().getFactionById(fPlayer.getFactionId());
            if (faction == null) {
                log("Invalid faction id on " + fPlayer.getName() + ":" + fPlayer.getFactionId());
                fPlayer.resetFactionData(false);
                continue;
            }
            faction.addFPlayer(fPlayer);

            if (fPlayer.hasAltFaction()) {
                Faction altFaction = Factions.getInstance().getFactionById(fPlayer.getAltFactionId());
                altFaction.addAltPlayer(fPlayer);
            }
        }
        com.massivecraft.factions.Board.getInstance().load();
        Board.getInstance().clean();

        // Add Base Commands
        this.cmdBase = new FCmdRoot();
        this.cmdAutoHelp = new CmdAutoHelp();
        this.getBaseCommands().add(cmdBase);

        Econ.setup();
        setupPermissions();

        if (Conf.worldGuardChecking || Conf.worldGuardBuildPriority) {
            Worldguard.init(this);
        }

        // start up task which runs the autoLeaveAfterDaysOfInactivity routine
        startAutoLeaveTask(false);

        // Run before initializing listeners to handle reloads properly.
        if (Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14")) {
            particleProvider = new BukkitParticleProvider();
        } else {
            particleProvider = new PacketParticleProvider();
            log(Level.INFO, "Available particle effects: " + Joiner.on(", ").skipNulls().join(Arrays.stream(ParticleEffect.values()).map(ParticleEffect::getName).collect(Collectors.toList())));
        }
        log(Level.INFO, "Using %1s as a particle provider", particleProvider.name());


        // Register Event Handlers
        getServer().getPluginManager().registerEvents(factionsPlayerListener = new FactionsPlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new FactionsChatListener(this), this);
        getServer().getPluginManager().registerEvents(new FactionsEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new FactionsExploitListener(), this);
        getServer().getPluginManager().registerEvents(new FactionsBlockListener(this), this);

        // since some other plugins execute commands directly through this command interface, provide it
        this.getCommand(this.refCommand).setExecutor(this);

        this.defaultTntBankBalance = getConfig().getInt("default-tnt-bank-balance", 500000);

        this.sotw = getConfig().getBoolean("sotw");

        this.factionsFlightDelay = getConfig().getInt("f-fly.radius-check", 1) * 20;
        if (sotw) {
            log(Level.INFO, "Factions Flight is disabled because /f sotw is enabled!");
        } else if (getConfig().getBoolean("f-fly.enabled", true)) {
            TaskFlight.start();
            log(Level.INFO, "Enabling enemy radius check for f fly every %s seconds", factionsFlightDelay / 20);
        }

        if (P.p.getConfig().getBoolean("see-chunk.particles", true)) {
            double delay = Math.floor(getConfig().getDouble("f-fly.radius-check", 0.75) * 20);
            seeChunkUtil = new SeeChunkUtil();
            seeChunkUtil.runTaskTimer(this, 0, (long) delay);
        }

        new TaskWallCheckReminder().runTaskTimerAsynchronously(this, 0L, 1200L);
        log(Level.INFO, "Starting the Async Task Wall Check Reminder");

        new TitleAPI();
        setupPlaceholderAPI();
        Booster.initializeBoosters();
        postEnable();
        this.locked = getConfig().getBoolean("lock.enabled");
        if (getConfig().getBoolean("lock.block-place")) {
            EventPost.of(BlockPlaceEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> !event.getPlayer().isOp())
                    .filter(event -> locked)
                    .filter(event -> event.getBlockPlaced().getType() == Material.MOB_SPAWNER)
                    .handle(event -> {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(TL.LOCK_IS_ENABLED.toString());
                    }).post(this);
        }
        if (getConfig().getBoolean("lock.block-break")) {
            EventPost.of(BlockPlaceEvent.class)
                    .filter(EventFilters.getIgnoreCancelled())
                    .filter(event -> !event.getPlayer().isOp())
                    .filter(event -> locked)
                    .filter(event -> event.getBlockPlaced().getType() == Material.MOB_SPAWNER)
                    .handle(event -> {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(TL.LOCK_IS_ENABLED.toString());
                    }).post(this);
        }
        Plugin combatTagPlus = getServer().getPluginManager().getPlugin("CombatTagPlus");
        if (combatTagPlus != null) {
            getLogger().info("Enabled CombatTagPlus Integration");
            this.combatIntegration = new CombatTagPlusImpl(combatTagPlus);
        }
        this.loadSuccessful = true;
    }

    @Override
    public void disable() {
        if (getConfig().getBoolean("lock.enabled") != locked) {
            getConfig().set("lock.enabled", locked);
        }
        if (getConfig().getBoolean("sotw") != sotw) {
            getConfig().set("sotw", sotw);
        }
        saveConfig();
        if (this.loadSuccessful) {
            Conf.save();
        }
        if (autoLeaveTask != null) {
            this.getServer().getScheduler().cancelTask(autoLeaveTask);
            autoLeaveTask = null;
        }
        super.disable();
    }

    private void setupPlaceholderAPI() {
        Plugin clip = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (clip != null && clip.isEnabled()) {
            this.clipPlaceholderAPIManager = new ClipPlaceholderAPIManager();
            if (this.clipPlaceholderAPIManager.register()) {
                log(Level.INFO, "Successfully registered placeholders with PlaceholderAPI.");
            }
        }

        Plugin mvdw = getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI");
        if (mvdw != null && mvdw.isEnabled()) {
            this.mvdwPlaceholderAPIManager = true;
            log(Level.INFO, "Found MVdWPlaceholderAPI. Adding hooks.");
        }
    }

    public boolean isClipPlaceholderAPIHooked() {
        return this.clipPlaceholderAPIManager != null;
    }

    public boolean isMVdWPlaceholderAPIHooked() {
        return this.mvdwPlaceholderAPIManager;
    }

    private boolean setupPermissions() {
        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp != null) {
                perms = rsp.getProvider();
            }
        } catch (NoClassDefFoundError ex) {
            return false;
        }
        return perms != null;
    }

    @Override
    public GsonBuilder getGsonBuilder() {
        Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() {
        }.getType();

        Type accessTypeAdatper = new TypeToken<Map<Permissable, Map<PermissableAction, Access>>>() {
        }.getType();

        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).registerTypeAdapter(accessTypeAdatper, new PermissionsMapTypeAdapter()).registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter()).registerTypeAdapter(mapFLocToStringSetType, new MapFLocToStringSetTypeAdapter()).registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY);
    }

    public void startAutoLeaveTask(boolean restartIfRunning) {
        if (autoLeaveTask != null) {
            if (!restartIfRunning) {
                return;
            }
            this.getServer().getScheduler().cancelTask(autoLeaveTask);
        }

        if (Conf.autoLeaveRoutineRunsEveryXMinutes > 0.0) {
            long ticks = (long) (20 * 60 * Conf.autoLeaveRoutineRunsEveryXMinutes);
            autoLeaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new TaskAutoLeave(), ticks, ticks);
        }
    }

    @Override
    public void postAutoSave() {
        //Board.getInstance().forceSave(); Not sure why this was there as it's called after the board is already saved.
        Conf.save();
    }

    @Override
    public boolean logPlayerCommands() {
        return Conf.logPlayerCommands;
    }

    @Override
    public boolean handleCommand(CommandSender sender, String commandString, boolean testOnly) {
        return sender instanceof Player && FactionsPlayerListener.preventCommand(commandString, (Player) sender) || super.handleCommand(sender, commandString, testOnly);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (split.length == 0) {
            return handleCommand(sender, "/f help", false);
        }

        // otherwise, needs to be handled; presumably another plugin directly ran the command
        String cmd = Conf.baseCommandAliases.isEmpty() ? "/f" : "/" + Conf.baseCommandAliases.get(0);
        return handleCommand(sender, cmd + " " + TextUtil.implode(Arrays.asList(split), " "), false);
    }


    // -------------------------------------------- //
    // Functions for other plugins to hook into
    // -------------------------------------------- //

    // This value will be updated whenever new hooks are added
    public int hookSupportVersion() {
        return 3;
    }

    // If another plugin is handling insertion of chat tags, this should be used to notify Factions
    public void handleFactionTagExternally(boolean notByFactions) {
        Conf.chatTagHandledByAnotherPlugin = notByFactions;
    }

    // Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
    // enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()

    public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
        return event != null && (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage()));
    }

    // Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
    // local chat, or anything else which targets individual recipients, so Faction Chat can be done
    public boolean isPlayerFactionChatting(Player player) {
        if (player == null) {
            return false;
        }
        FPlayer me = FPlayers.getInstance().getByPlayer(player);

        return me != null && me.getChatMode().isAtLeast(ChatMode.ALLIANCE);
    }

    // Is this chat message actually a Factions command, and thus should be left alone by other plugins?

    // TODO: GET THIS BACK AND WORKING

    public boolean isFactionsCommand(String check) {
        return !(check == null || check.isEmpty()) && this.handleCommand(null, check, true);
    }

    // Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
    public String getPlayerFactionTag(Player player) {
        return getPlayerFactionTagRelation(player, null);
    }

    // Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
    public String getPlayerFactionTagRelation(Player speaker, Player listener) {
        String tag = "~";

        if (speaker == null) {
            return tag;
        }

        FPlayer me = FPlayers.getInstance().getByPlayer(speaker);
        if (me == null) {
            return tag;
        }

        // if listener isn't set, or config option is disabled, give back uncolored tag
        if (listener == null || !Conf.chatTagRelationColored) {
            tag = me.getChatTag().trim();
        } else {
            FPlayer you = FPlayers.getInstance().getByPlayer(listener);
            if (you == null) {
                tag = me.getChatTag().trim();
            } else  // everything checks out, give the colored tag
            {
                tag = me.getChatTag(you).trim();
            }
        }
        if (tag.isEmpty()) {
            tag = "~";
        }

        return tag;
    }

    // Get a player's title within their faction, mainly for usage by chat plugins for local/channel chat
    public String getPlayerTitle(Player player) {
        if (player == null) {
            return "";
        }

        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me == null) {
            return "";
        }

        return me.getTitle().trim();
    }

    // Get a list of all faction tags (names)
    public Set<String> getFactionTags() {
        return Factions.getInstance().getFactionTags();
    }

    // Get a list of all players in the specified faction
    public Set<String> getPlayersInFaction(String factionTag) {
        Set<String> players = new HashSet<>();
        Faction faction = Factions.getInstance().getByTag(factionTag);
        if (faction != null) {
            for (FPlayer fplayer : faction.getFPlayers()) {
                players.add(fplayer.getName());
            }
        }
        return players;
    }

    // Get a list of all online players in the specified faction
    public Set<String> getOnlinePlayersInFaction(String factionTag) {
        Set<String> players = new HashSet<>();
        Faction faction = Factions.getInstance().getByTag(factionTag);
        if (faction != null) {
            for (FPlayer fplayer : faction.getFPlayersWhereOnline(true)) {
                players.add(fplayer.getName());
            }
        }
        return players;
    }

    public String getPrimaryGroup(OfflinePlayer player) {
        return perms == null || !perms.hasGroupSupport() ? " " : perms.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
    }

    public void debug(Level level, String s) {
        if (getConfig().getBoolean("debug", false)) {
            getLogger().log(level, s);
        }
    }

    public void debug(String s) {
        debug(Level.INFO, s);
    }

    public FactionsPlayerListener getFactionsPlayerListener() {
        return factionsPlayerListener;
    }

    public boolean isSotw() {
        return sotw;
    }

    public void setSotw(boolean sotw) {
        this.sotw = sotw;
    }

    public long getFactionsFlightDelay() {
        return factionsFlightDelay;
    }

    public int getDefaultTntBankBalance() {
        return defaultTntBankBalance;
    }

    public CombatIntegration getCombatIntegration() {
        return combatIntegration;
    }
}
