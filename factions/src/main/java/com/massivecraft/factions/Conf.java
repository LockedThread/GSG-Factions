package com.massivecraft.factions;

import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class Conf {


    public static boolean raidClaimEnabled;
    public static int raidClaimLimit;

    // /f corner
    public static int bufferSize = 20;

    // /f Chest
    public static int defaultFactionChestRows = 3;

    public static List<String> baseCommandAliases = new ArrayList<>();
    public static boolean allowNoSlashCommand = true;

    // Colors
    public static ChatColor colorMember = ChatColor.GREEN;
    public static ChatColor colorAlly = ChatColor.LIGHT_PURPLE;
    public static ChatColor colorTruce = ChatColor.DARK_PURPLE;
    public static ChatColor colorNeutral = ChatColor.WHITE;
    public static ChatColor colorEnemy = ChatColor.RED;

    public static ChatColor colorPeaceful = ChatColor.GOLD;
    public static ChatColor colorWilderness = ChatColor.GRAY;
    public static ChatColor colorSafezone = ChatColor.GOLD;
    public static ChatColor colorWar = ChatColor.DARK_RED;
    public static ChatColor colorWorldBorder = ChatColor.DARK_RED;

    // Power
    public static double powerPlayerMax = 10.0;
    public static double powerPlayerMin = -10.0;
    public static double powerPlayerStarting = 0.0;
    public static double powerPerMinute = 0.2; // Default health rate... it takes 5 min to heal one power
    public static double powerPerDeath = 4.0; // A death makes you lose 4 power
    public static boolean powerRegenOffline = false;  // does player power regenerate even while they're offline?
    public static double powerOfflineLossPerDay = 0.0;  // players will lose this much power per day offline
    public static double powerOfflineLossLimit = 0.0;  // players will no longer lose power from being offline once their power drops to this amount or less
    public static double powerFactionMax = 0.0;  // if greater than 0, the cap on how much power a faction can have (additional power from players beyond that will act as a "buffer" of sorts)

    public static String prefixAdmin = "***";
    public static String prefixColeader = "**";
    public static String prefixMod = "*";
    public static String prefixNormal = "+";
    public static String prefixRecruit = "-";

    public static boolean allowMultipleColeaders = false;

    public static int factionTagLengthMin = 3;
    public static int factionTagLengthMax = 10;
    public static boolean factionTagForceUpperCase = false;

    public static boolean newFactionsDefaultOpen = false;

    // when faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit
    public static int factionMemberLimit = 0;

    // what faction ID to start new players in when they first join the server; default is 0, "no faction"
    public static String newPlayerStartingFactionID = "0";

    public static boolean showMapFactionKey = true;
    public static boolean showNeutralFactionsOnMap = true;
    public static boolean showEnemyFactionsOnMap = true;
    public static boolean showTruceFactionsOnMap = true;
    public static boolean showOutsideBorder = false;
    public static String worldBorderMapIcon = "+";

    // Disallow joining/leaving/kicking while power is negative
    public static boolean canLeaveWithNegativePower = true;

    // Configuration for faction-only chat
    public static boolean factionOnlyChat = true;
    // Configuration on the Faction tag in chat messages.
    public static boolean chatTagEnabled = true;
    public static transient boolean chatTagHandledByAnotherPlugin = false;
    public static boolean chatTagRelationColored = true;
    public static String chatTagReplaceString = "[FACTION]";
    public static String chatTagInsertAfterString = "";
    public static String chatTagInsertBeforeString = "";
    public static int chatTagInsertIndex = 0;
    public static boolean chatTagPadBefore = true;
    public static boolean chatTagPadAfter = true;
    public static String chatTagFormat = "%s" + ChatColor.WHITE;
    public static boolean alwaysShowChatTag = true;
    public static String factionChatFormat = "%s:" + ChatColor.WHITE + " %s";
    public static String allianceChatFormat = ChatColor.LIGHT_PURPLE + "%s:" + ChatColor.WHITE + " %s";
    public static String truceChatFormat = ChatColor.DARK_PURPLE + "%s:" + ChatColor.WHITE + " %s";
    public static String modChatFormat = ChatColor.RED + "%s:" + ChatColor.WHITE + " %s";

    public static boolean broadcastDescriptionChanges = false;
    public static boolean broadcastTagChanges = false;

    public static double saveToFileEveryXMinutes = 30.0;

    public static double autoLeaveAfterDaysOfInactivity = 10.0;
    public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
    public static double autoInvitePerDayCheckEveryXMinutes = 30.0;
    public static double amountOfInvitesPerDay = 5;
    public static int autoLeaveRoutineMaxMillisecondsPerTick = 5;  // 1 server tick is roughly 50ms, so default max 10% of a tick
    public static boolean removePlayerDataWhenBanned = true;
    public static boolean autoLeaveDeleteFPlayerData = true; // Let them just remove player from Faction.

    public static boolean worldGuardChecking = false;
    public static boolean worldGuardBuildPriority = false;

    // server logging options
    public static boolean logFactionCreate = true;
    public static boolean logFactionDisband = true;
    public static boolean logFactionJoin = true;
    public static boolean logFactionKick = true;
    public static boolean logFactionLeave = true;
    public static boolean logLandClaims = true;
    public static boolean logLandUnclaims = true;
    public static boolean logMoneyTransactions = true;
    public static boolean logPlayerCommands = true;

    // prevent some potential exploits
    public static boolean handleExploitObsidianGenerators = true;
    public static boolean handleExploitEnderPearlClipping = true;
    public static boolean handleExploitInteractionSpam = true;
    public static boolean handleExploitTNTWaterlog = false;
    public static boolean handleExploitLiquidFlow = false;

    public static boolean homesEnabled = true;
    public static boolean homesMustBeInClaimedTerritory = true;
    public static boolean homesTeleportToOnDeath = true;
    public static boolean homesRespawnFromNoPowerLossWorlds = true;
    public static boolean homesTeleportCommandEnabled = true;
    public static boolean homesTeleportCommandEssentialsIntegration = true;
    public static boolean homesTeleportCommandSmokeEffectEnabled = true;
    public static float homesTeleportCommandSmokeEffectThickness = 3f;
    public static boolean homesTeleportAllowedFromEnemyTerritory = true;
    public static boolean homesTeleportAllowedFromDifferentWorld = true;
    public static double homesTeleportAllowedEnemyDistance = 32.0;
    public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;

    public static boolean disablePVPBetweenNeutralFactions = false;
    public static boolean disablePVPForFactionlessPlayers = false;
    public static boolean enablePVPAgainstFactionlessInAttackersLand = false;

    public static int noPVPDamageToOthersForXSecondsAfterLogin = 3;

    public static boolean peacefulTerritoryDisablePVP = true;
    public static boolean peacefulTerritoryDisableMonsters = false;
    public static boolean peacefulTerritoryDisableBoom = false;
    public static boolean peacefulMembersDisablePowerLoss = true;

    public static boolean permanentFactionsDisableLeaderPromotion = false;

    public static boolean claimsMustBeConnected = false;
    public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
    public static int claimsRequireMinFactionMembers = 1;
    public static int claimedLandsMax = 0;
    public static int lineClaimLimit = 5;

    // if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
    public static int radiusClaimFailureLimit = 9;

    public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;

    // commands which will be prevented if the player is a member of a permanent faction
    public static Set<String> permanentFactionMemberDenyCommands = new LinkedHashSet<>();

    // commands which will be prevented when in claimed territory of another faction
    public static Set<String> territoryNeutralDenyCommands = new LinkedHashSet<>();
    public static Set<String> territoryEnemyDenyCommands = new LinkedHashSet<>();
    public static Set<String> territoryAllyDenyCommands = new LinkedHashSet<>();
    public static Set<String> warzoneDenyCommands = new LinkedHashSet<>();
    public static Set<String> wildernessDenyCommands = new LinkedHashSet<>();

    public static boolean defaultFlyPermEnemy = false;
    public static boolean defaultFlyPermNeutral = false;
    public static boolean defaultFlyPermTruce = false;
    public static boolean defaultFlyPermAlly = true;
    public static boolean defaultFlyPermMember = true;

    public static boolean territoryDenyBuild = true;
    public static boolean territoryDenyBuildWhenOffline = true;
    public static boolean territoryPainBuild = false;
    public static boolean territoryPainBuildWhenOffline = false;
    public static boolean territoryDenyUseage = true;
    public static boolean territoryEnemyDenyBuild = true;
    public static boolean territoryEnemyDenyBuildWhenOffline = true;
    public static boolean territoryEnemyPainBuild = false;
    public static boolean territoryEnemyPainBuildWhenOffline = false;
    public static boolean territoryEnemyDenyUseage = true;
    public static boolean territoryEnemyProtectMaterials = true;
    public static boolean territoryAllyDenyBuild = true;
    public static boolean territoryAllyDenyBuildWhenOffline = true;
    public static boolean territoryAllyPainBuild = false;
    public static boolean territoryAllyPainBuildWhenOffline = false;
    public static boolean territoryAllyDenyUseage = true;
    public static boolean territoryAllyProtectMaterials = true;
    public static boolean territoryTruceDenyBuild = true;
    public static boolean territoryTruceDenyBuildWhenOffline = true;
    public static boolean territoryTrucePainBuild = false;
    public static boolean territoryTrucePainBuildWhenOffline = false;
    public static boolean territoryTruceDenyUseage = true;
    public static boolean territoryTruceProtectMaterials = true;
    public static boolean territoryBlockCreepers = false;
    public static boolean territoryBlockCreepersWhenOffline = false;
    public static boolean territoryBlockFireballs = false;
    public static boolean territoryBlockFireballsWhenOffline = false;
    public static boolean territoryBlockTNT = false;
    public static boolean territoryBlockTNTWhenOffline = false;
    public static boolean territoryDenyEndermanBlocks = true;
    public static boolean territoryDenyEndermanBlocksWhenOffline = true;

    public static boolean safeZoneDenyBuild = true;
    public static boolean safeZoneDenyUseage = true;
    public static boolean safeZoneBlockTNT = true;
    public static boolean safeZonePreventAllDamageToPlayers = false;
    public static boolean safeZoneDenyEndermanBlocks = true;

    public static boolean warZoneDenyBuild = true;
    public static boolean warZoneDenyUseage = true;
    public static boolean warZoneBlockCreepers = false;
    public static boolean warZoneBlockFireballs = false;
    public static boolean warZoneBlockTNT = true;
    public static boolean warZonePowerLoss = true;
    public static boolean warZoneFriendlyFire = false;
    public static boolean warZoneDenyEndermanBlocks = true;

    public static boolean wildernessDenyBuild = false;
    public static boolean wildernessDenyUseage = false;
    public static boolean wildernessBlockCreepers = false;
    public static boolean wildernessBlockFireballs = false;
    public static boolean wildernessBlockTNT = false;
    public static boolean wildernessPowerLoss = true;
    public static boolean wildernessDenyEndermanBlocks = false;

    // for claimed areas where further faction-member ownership can be defined
    public static boolean ownedAreasEnabled = true;
    public static int ownedAreasLimitPerFaction = 0;
    public static boolean ownedAreaDenyBuild = true;
    public static boolean ownedAreaPainBuild = false;
    public static boolean ownedAreaProtectMaterials = true;
    public static boolean ownedAreaDenyUseage = true;

    public static boolean ownedMessageOnBorder = true;
    public static boolean ownedMessageInsideTerritory = true;
    public static boolean ownedMessageByChunk = false;

    public static boolean pistonProtectionThroughDenyBuild = true;

    public static Set<Material> territoryProtectedMaterials = Sets.newHashSet();
    public static Set<Material> territoryDenyUseageMaterials = EnumSet.noneOf(Material.class);
    public static Set<Material> territoryProtectedMaterialsWhenOffline = EnumSet.noneOf(Material.class);
    public static Set<Material> territoryDenyUseageMaterialsWhenOffline = EnumSet.noneOf(Material.class);

    public static transient Set<EntityType> safeZoneNerfedCreatureTypes = EnumSet.noneOf(EntityType.class);
    // Economy settings
    public static boolean econEnabled = false;
    public static String econUniverseAccount = "";
    public static double econCostClaimWilderness = 30.0;
    public static double econCostClaimFromFactionBonus = 30.0;
    public static double econClaimAdditionalMultiplier = 0.5;
    public static double econClaimRefundMultiplier = 0.7;
    public static double econCostCreate = 100.0;
    public static double econCostOwner = 15.0;
    public static double econCostSethome = 30.0;
    public static double econCostJoin = 0.0;
    public static double econCostKick = 0.0;
    public static double econCostInvite = 0.0;
    public static double econCostHome = 0.0;
    public static double econCostTag = 0.0;
    public static double econCostDesc = 0.0;
    public static double econCostTitle = 0.0;
    public static double econCostList = 0.0;
    public static double econCostMap = 0.0;
    public static double econCostPower = 0.0;
    public static double econCostShow = 0.0;
    public static double econCostOpen = 0.0;
    public static double econCostAlly = 0.0;
    public static double econCostTruce = 0.0;
    public static double econCostEnemy = 0.0;
    public static double econCostNeutral = 0.0;
    public static double econCostNoBoom = 0.0;


    //Faction banks, to pay for land claiming and other costs instead of individuals paying for them
    public static boolean bankEnabled = true;
    public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
    public static boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome

    // mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections
    public static Set<String> playersWhoBypassAllProtection = new LinkedHashSet<>();

    public static Set<String> worldsNoClaiming = new LinkedHashSet<>();
    public static Set<String> worldsNoPowerLoss = new LinkedHashSet<>();
    public static Set<String> worldsIgnorePvP = new LinkedHashSet<>();
    public static Set<String> worldsNoWildernessProtection = new LinkedHashSet<>();

    public static Backend backEnd = Backend.JSON;

    public static int lastDayServerStarted = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

    // Taller and wider for "bigger f map"
    public static int mapHeight = 17;
    public static int mapWidth = 49;
    public static transient char[] mapKeyChrs = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();
    // -------------------------------------------- //
    // Persistance
    // -------------------------------------------- //
    private static transient com.massivecraft.factions.Conf i = new com.massivecraft.factions.Conf();

    static {
        baseCommandAliases.add("f");

        territoryEnemyDenyCommands.add("home");
        territoryEnemyDenyCommands.add("sethome");
        territoryEnemyDenyCommands.add("spawn");
        territoryEnemyDenyCommands.add("tpahere");
        territoryEnemyDenyCommands.add("tpaccept");
        territoryEnemyDenyCommands.add("tpa");

        territoryProtectedMaterials.add(Material.WOODEN_DOOR);
        territoryProtectedMaterials.add(Material.TRAP_DOOR);
        territoryProtectedMaterials.add(Material.FENCE_GATE);
        territoryProtectedMaterials.add(Material.DISPENSER);
        territoryProtectedMaterials.add(Material.CHEST);
        territoryProtectedMaterials.add(Material.FURNACE);
        territoryProtectedMaterials.add(Material.BURNING_FURNACE);
        territoryProtectedMaterials.add(Material.DIODE_BLOCK_OFF);
        territoryProtectedMaterials.add(Material.DIODE_BLOCK_ON);
        territoryProtectedMaterials.add(Material.JUKEBOX);
        territoryProtectedMaterials.add(Material.BREWING_STAND);
        territoryProtectedMaterials.add(Material.ENCHANTMENT_TABLE);
        territoryProtectedMaterials.add(Material.CAULDRON);
        territoryProtectedMaterials.add(Material.SOIL);
        territoryProtectedMaterials.add(Material.BEACON);
        territoryProtectedMaterials.add(Material.ANVIL);
        territoryProtectedMaterials.add(Material.TRAPPED_CHEST);
        territoryProtectedMaterials.add(Material.DROPPER);
        territoryProtectedMaterials.add(Material.HOPPER);

        territoryDenyUseageMaterials.add(Material.FIREBALL);
        territoryDenyUseageMaterials.add(Material.FLINT_AND_STEEL);
        territoryDenyUseageMaterials.add(Material.BUCKET);
        territoryDenyUseageMaterials.add(Material.WATER_BUCKET);
        territoryDenyUseageMaterials.add(Material.LAVA_BUCKET);

        territoryProtectedMaterialsWhenOffline.add(Material.WOODEN_DOOR);
        territoryProtectedMaterialsWhenOffline.add(Material.TRAP_DOOR);
        territoryProtectedMaterialsWhenOffline.add(Material.FENCE_GATE);
        territoryProtectedMaterialsWhenOffline.add(Material.DISPENSER);
        territoryProtectedMaterialsWhenOffline.add(Material.CHEST);
        territoryProtectedMaterialsWhenOffline.add(Material.FURNACE);
        territoryProtectedMaterialsWhenOffline.add(Material.BURNING_FURNACE);
        territoryProtectedMaterialsWhenOffline.add(Material.DIODE_BLOCK_OFF);
        territoryProtectedMaterialsWhenOffline.add(Material.DIODE_BLOCK_ON);
        territoryProtectedMaterialsWhenOffline.add(Material.JUKEBOX);
        territoryProtectedMaterialsWhenOffline.add(Material.BREWING_STAND);
        territoryProtectedMaterialsWhenOffline.add(Material.ENCHANTMENT_TABLE);
        territoryProtectedMaterialsWhenOffline.add(Material.CAULDRON);
        territoryProtectedMaterialsWhenOffline.add(Material.SOIL);
        territoryProtectedMaterialsWhenOffline.add(Material.BEACON);
        territoryProtectedMaterialsWhenOffline.add(Material.ANVIL);
        territoryProtectedMaterialsWhenOffline.add(Material.TRAPPED_CHEST);
        territoryProtectedMaterialsWhenOffline.add(Material.DROPPER);
        territoryProtectedMaterialsWhenOffline.add(Material.HOPPER);

        territoryDenyUseageMaterialsWhenOffline.add(Material.FIREBALL);
        territoryDenyUseageMaterialsWhenOffline.add(Material.FLINT_AND_STEEL);
        territoryDenyUseageMaterialsWhenOffline.add(Material.BUCKET);
        territoryDenyUseageMaterialsWhenOffline.add(Material.WATER_BUCKET);
        territoryDenyUseageMaterialsWhenOffline.add(Material.LAVA_BUCKET);

        safeZoneNerfedCreatureTypes.add(EntityType.BLAZE);
        safeZoneNerfedCreatureTypes.add(EntityType.CAVE_SPIDER);
        safeZoneNerfedCreatureTypes.add(EntityType.CREEPER);
        safeZoneNerfedCreatureTypes.add(EntityType.ENDER_DRAGON);
        safeZoneNerfedCreatureTypes.add(EntityType.ENDERMAN);
        safeZoneNerfedCreatureTypes.add(EntityType.GHAST);
        safeZoneNerfedCreatureTypes.add(EntityType.MAGMA_CUBE);
        safeZoneNerfedCreatureTypes.add(EntityType.PIG_ZOMBIE);
        safeZoneNerfedCreatureTypes.add(EntityType.SILVERFISH);
        safeZoneNerfedCreatureTypes.add(EntityType.SKELETON);
        safeZoneNerfedCreatureTypes.add(EntityType.SPIDER);
        safeZoneNerfedCreatureTypes.add(EntityType.SLIME);
        safeZoneNerfedCreatureTypes.add(EntityType.WITCH);
        safeZoneNerfedCreatureTypes.add(EntityType.WITHER);
        safeZoneNerfedCreatureTypes.add(EntityType.ZOMBIE);
    }

    public static void load() {
        P.p.persist.loadOrSaveDefault(i, Conf.class, "conf");
    }

    public static void save() {
        P.p.persist.save(i);
    }

    public enum Backend {
        JSON,
        //MYSQL,  TODO
        ;
    }
}

