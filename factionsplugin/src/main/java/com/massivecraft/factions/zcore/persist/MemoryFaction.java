package com.massivecraft.factions.zcore.persist;

import com.gameservergroup.gsgcore.utils.Text;
import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.*;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.Message;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.zcore.factionchest.FactionChest;
import com.massivecraft.factions.zcore.factionupgrades.FactionUpgrade;
import com.massivecraft.factions.zcore.factionupgrades.FactionUpgradeMenu;
import com.massivecraft.factions.zcore.factionwarps.WarpMenu;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class MemoryFaction implements Faction, EconomyParticipator {

    protected String id = null;
    protected boolean peacefulExplosionsEnabled;
    protected boolean permanent;
    protected String tag;
    protected String description;
    protected boolean open;
    protected boolean peaceful;
    protected Integer permanentPower;
    protected LazyLocation home;
    protected long foundedDate;
    protected double money;
    protected double powerBoost;
    protected Map<String, Relation> relationWish = new HashMap<>();
    protected Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<>();
    protected Set<String> invites = new HashSet<>();
    protected HashMap<String, List<String>> announcements = new HashMap<>();
    protected ConcurrentHashMap<String, LazyLocation> warps = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<String, String> warpPasswords = new ConcurrentHashMap<>();
    protected Role defaultRole;
    protected Map<Permissable, Map<PermissableAction, Access>> permissions = new HashMap<>();
    protected Set<BanInfo> bans = new HashSet<>();
    protected int tntBankBalance;
    protected int tntBankLimit;
    protected FactionChest factionChest;
    protected int maxWarps;
    protected int maxMembers;
    protected Set<StrikeInfo> strikes = new HashSet<>();
    protected List<String> altInvites = new ArrayList<>();

    protected transient long lastPlayerLoggedOffTime;
    protected transient Set<FPlayer> fplayers = new HashSet<>();
    protected transient Set<FPlayer> altPlayers = new HashSet<>();

    /**
     * LockedThread's ADDITIONS
     */

    protected String payPalEmail;
    private long lastDeath;
    protected long checkReminderMinutes = 0;
    protected EnumMap<FactionUpgrade, Integer> upgradeMap;

    protected transient WarpMenu warpMenu;
    protected transient FactionUpgradeMenu factionUpgradeMenu;

    // -------------------------------------------- //
    // Construct
    // -------------------------------------------- //
    public MemoryFaction() {
    }

    public MemoryFaction(String id) {
        this.id = id;
        this.open = Conf.newFactionsDefaultOpen;
        this.tag = "???";
        this.description = "Wow, you're using a default description!";
        this.lastPlayerLoggedOffTime = 0;
        this.peaceful = false;
        this.peacefulExplosionsEnabled = false;
        this.permanent = false;
        this.money = 0.0;
        this.powerBoost = 0.0;
        this.foundedDate = System.currentTimeMillis();
        this.defaultRole = Role.NORMAL;
        this.tntBankBalance = 0;
        this.tntBankLimit = FactionUpgrade.FACTION_TNTBANK_STORAGE.isEnabled() ? FactionUpgrade.FACTION_TNTBANK_STORAGE.getMetaInteger("default-amount") : P.p.getDefaultTntBankBalance();
        this.maxWarps = FactionUpgrade.FACTION_WARP_LIMIT.isEnabled() ? FactionUpgrade.FACTION_WARP_LIMIT.getMetaInteger("default-amount") : -1;
        this.maxMembers = FactionUpgrade.FACTION_MEMBER_LIMIT.isEnabled() ? FactionUpgrade.FACTION_MEMBER_LIMIT.getMetaInteger("default-members") : -1;
        this.factionChest = new FactionChest(FactionUpgrade.FACTION_CHEST_ROWS.isEnabled() ? FactionUpgrade.FACTION_CHEST_ROWS.getMetaInteger("default-rows") : Conf.defaultFactionChestRows);
        this.altPlayers = new HashSet<>();
        this.altInvites = new ArrayList<>();
        this.payPalEmail = "";
        this.checkReminderMinutes = 0;
        this.upgradeMap = new EnumMap<>(FactionUpgrade.class);
        resetPerms(); // Reset on new Faction so it has default values.
    }

    public MemoryFaction(com.massivecraft.factions.zcore.persist.MemoryFaction old) {
        id = old.id;
        peacefulExplosionsEnabled = old.peacefulExplosionsEnabled;
        permanent = old.permanent;
        tag = old.tag;
        description = old.description;
        open = old.open;
        foundedDate = old.foundedDate;
        peaceful = old.peaceful;
        permanentPower = old.permanentPower;
        home = old.home;
        lastPlayerLoggedOffTime = old.lastPlayerLoggedOffTime;
        money = old.money;
        powerBoost = old.powerBoost;
        relationWish = old.relationWish;
        claimOwnership = old.claimOwnership;
        fplayers = new HashSet<>();
        invites = old.invites;
        announcements = old.announcements;
        this.defaultRole = Role.NORMAL;
        this.tntBankBalance = 0;
        this.tntBankLimit = FactionUpgrade.FACTION_TNTBANK_STORAGE.isEnabled() ? FactionUpgrade.FACTION_TNTBANK_STORAGE.getMetaInteger("default-amount") : P.p.getDefaultTntBankBalance();
        this.maxWarps = FactionUpgrade.FACTION_WARP_LIMIT.isEnabled() ? FactionUpgrade.FACTION_WARP_LIMIT.getMetaInteger("default-amount") : -1;
        this.maxMembers = FactionUpgrade.FACTION_MEMBER_LIMIT.isEnabled() ? FactionUpgrade.FACTION_MEMBER_LIMIT.getMetaInteger("default-members") : -1;
        this.factionChest = new FactionChest(FactionUpgrade.FACTION_CHEST_ROWS.isEnabled() ? FactionUpgrade.FACTION_CHEST_ROWS.getMetaInteger("default-rows") : Conf.defaultFactionChestRows);
        this.altPlayers = new HashSet<>();
        this.altInvites = new ArrayList<>();
        this.payPalEmail = "";
        this.checkReminderMinutes = 0;
        this.upgradeMap = new EnumMap<>(FactionUpgrade.class);
        resetPerms(); // Reset on new Faction so it has default values.
    }


    @Override
    public WarpMenu getWarpMenu() {
        return warpMenu == null ? warpMenu = new WarpMenu(this) : warpMenu;
    }

    @Override
    public FactionUpgradeMenu getFactionUpgradeMenu() {
        return factionUpgradeMenu == null ? factionUpgradeMenu = new FactionUpgradeMenu(this) : factionUpgradeMenu;
    }

    @Override
    public EnumMap<FactionUpgrade, Integer> getUpgrades() {
        return upgradeMap;
    }

    @Override
    public long getCheckReminderMinutes() {
        return checkReminderMinutes;
    }

    @Override
    public void setCheckReminderMinutes(long minutes) {
        this.checkReminderMinutes = minutes;
    }

    @Override
    public void sendCheckRemind() {
        sendMessage(TL.CHECK_REMIND_MESSAGE.toString());
    }

    @Override
    public String getPayPal() {
        return payPalEmail;
    }

    @Override
    public void setPayPal(String email) {
        this.payPalEmail = email == null ? "" : email;
    }

    public HashMap<String, List<String>> getAnnouncements() {
        return this.announcements;
    }

    @Override
    public FactionChest getFactionChest() {
        return factionChest;
    }

    public void addAnnouncement(FPlayer fPlayer, String msg) {
        List<String> list = announcements.containsKey(fPlayer.getId()) ? announcements.get(fPlayer.getId()) : new ArrayList<>();
        list.add(msg);
        announcements.put(fPlayer.getId(), list);
    }

    public void sendUnreadAnnouncements(FPlayer fPlayer) {
        if (!announcements.containsKey(fPlayer.getId())) {
            return;
        }
        fPlayer.msg(TL.FACTIONS_ANNOUNCEMENT_TOP);
        for (String s : announcements.get(fPlayer.getPlayer().getUniqueId().toString())) {
            fPlayer.sendMessage(s);
        }
        fPlayer.msg(TL.FACTIONS_ANNOUNCEMENT_BOTTOM);
        announcements.remove(fPlayer.getId());
    }

    public void removeAnnouncements(FPlayer fPlayer) {
        announcements.remove(fPlayer.getId());
    }

    public ConcurrentHashMap<String, LazyLocation> getWarps() {
        return this.warps;
    }

    public LazyLocation getWarp(String name) {
        return this.warps.get(name);
    }

    public String getTrueWarp(String name) {
        return this.warps.keySet().stream().filter(warp -> warp.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void setWarp(String name, LazyLocation loc) {
        this.warps.put(name, loc);
        WarpMenu warpMenu = getWarpMenu();
        if (warpMenu.getInventory().firstEmpty() == -1) {
            warpMenu.setInventory(warpMenu.getInventory().getName(), WarpMenu.getSlots(this));
            warpMenu.initialize();
        } else {
            warpMenu.setItem(warpMenu.getInventory().firstEmpty(), warpMenu.buildItem(name));
        }
    }

    public boolean isWarp(String name) {
        return this.warps.containsKey(name);
    }

    public boolean removeWarp(String name) {
        warpPasswords.remove(name); // remove password no matter what.
        return warps.remove(name) != null;
    }

    public boolean isWarpPassword(String warp, String password) {
        return hasWarpPassword(warp) && warpPasswords.get(warp.toLowerCase()).equals(password);
    }

    public boolean hasWarpPassword(String warp) {
        return warpPasswords.containsKey(warp.toLowerCase());
    }

    public void setWarpPassword(String warp, String password) {
        warpPasswords.put(warp.toLowerCase(), password);
    }

    public void clearWarps() {
        warps.clear();
    }

    public Set<String> getInvites() {
        return invites;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void invite(FPlayer fplayer) {
        this.invites.add(fplayer.getId());
    }

    public void deinvite(FPlayer fplayer) {
        this.invites.remove(fplayer.getId());
    }

    public boolean isInvited(FPlayer fplayer) {
        return this.invites.contains(fplayer.getId());
    }

    public void inviteAlt(FPlayer fplayer) {
        this.altInvites.add(fplayer.getId());
    }

    public void deinviteAlt(FPlayer fplayer) {
        this.altInvites.remove(fplayer.getId());
    }

    public void deinviteAllAlts() {
        this.altInvites.clear();
    }

    public boolean isAltInvited(FPlayer fplayer) {
        return this.altInvites.contains(fplayer.getId());
    }

    public List<String> getAltInvites() {
        return altInvites;
    }

    public void ban(FPlayer target, FPlayer banner) {
        BanInfo info = new BanInfo(banner.getId(), target.getId(), System.currentTimeMillis());
        this.bans.add(info);
    }

    public void unban(FPlayer player) {
        bans.removeIf(banInfo -> banInfo.getBanned().equalsIgnoreCase(player.getId()));
    }

    public boolean isBanned(FPlayer player) {
        return bans.stream().anyMatch(info -> info.getBanned().equalsIgnoreCase(player.getId()));
    }

    public Set<BanInfo> getBannedPlayers() {
        return this.bans;
    }

    public void strike(CommandSender executor, String description) {
        StrikeInfo strike = new StrikeInfo(System.currentTimeMillis(), executor.getName(), getTag(), description);
        this.strikes.add(strike);
        Bukkit.getLogger().info(executor.getName() + " has striked " + getTag() + "(" + getId() + ") with strike: " + strike.toString());

        for (Player player : getOnlinePlayers()) {
            player.sendMessage(" ");
            player.sendMessage(Message.center(Text.toColor("&4&lFACTION STRIKE")));
            player.sendMessage(Message.center(Text.toColor("&eYour Faction has received a Strike from " + executor.getName())));
            player.sendMessage(Message.center(Text.toColor("&eStrike reason: &c" + description)));
            player.sendMessage(" ");
        }
    }

    // removes the most recent strike issued
    public void destrike(CommandSender executor) {
        if (strikes.isEmpty()) {
            return;
        }

        StrikeInfo recent = null;
        for (StrikeInfo strike : strikes) {
            if (recent == null || strike.getIssuedAt() > recent.getIssuedAt()) {
                recent = strike;
            }
        }

        if (recent != null) {
            strikes.remove(recent);
            Bukkit.getLogger().info(executor.getName() + " has destriked " + getTag() + "(" + getId() + ") of strike: " + recent.toString());
        }
    }

    public Set<StrikeInfo> getStrikes() {
        return this.strikes;
    }

    public boolean getOpen() {
        return open;
    }

    public void setOpen(boolean isOpen) {
        open = isOpen;
    }

    public boolean isPeaceful() {
        return this.peaceful;
    }

    public void setPeaceful(boolean isPeaceful) {
        this.peaceful = isPeaceful;
    }

    public boolean getPeacefulExplosionsEnabled() {
        return this.peacefulExplosionsEnabled;
    }

    public void setPeacefulExplosionsEnabled(boolean val) {
        peacefulExplosionsEnabled = val;
    }

    public boolean noExplosionsInTerritory() {
        return this.peaceful && !peacefulExplosionsEnabled;
    }

    public boolean isPermanent() {
        return permanent || !this.isNormal();
    }

    public void setPermanent(boolean isPermanent) {
        permanent = isPermanent;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String str) {
        if (Conf.factionTagForceUpperCase) {
            str = str.toUpperCase();
        }
        this.tag = str;
    }

    public String getTag(String prefix) {
        return prefix + this.tag;
    }

    public String getTag(Faction otherFaction) {
        if (otherFaction == null) {
            return getTag();
        }
        return this.getTag(this.getColorTo(otherFaction).toString());
    }

    public String getTag(FPlayer otherFplayer) {
        if (otherFplayer == null) {
            return getTag();
        }
        return this.getTag(this.getColorTo(otherFplayer).toString());
    }

    public String getComparisonTag() {
        return MiscUtil.getComparisonString(this.tag);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public boolean hasHome() {
        return this.getHome() != null;
    }

    public Location getHome() {
        confirmValidHome();
        return (this.home != null) ? this.home.getLocation() : null;
    }

    public void setHome(Location home) {
        this.home = new LazyLocation(home);
    }

    public long getFoundedDate() {
        if (this.foundedDate == 0) {
            setFoundedDate(System.currentTimeMillis());
        }
        return this.foundedDate;
    }

    public void setFoundedDate(long newDate) {
        this.foundedDate = newDate;
    }

    public void confirmValidHome() {
        if (!Conf.homesMustBeInClaimedTerritory || this.home == null || (this.home.getLocation() != null && Board.getInstance().getFactionAt(new FLocation(this.home.getLocation())) == this)) {
            return;
        }

        msg("<b>Your faction home has been un-set since it is no longer in your territory.");
        this.home = null;
    }

    public String getAccountId() {
        String aid = "faction-" + this.getId();

        // We need to override the default money given to players.
        if (!Econ.hasAccount(aid)) {
            System.out.println("[FactionsBank] Resetting balance for " + getTag() + " (" + getId() + ") with AID " + aid + " and balance: $" + Econ.getBalance(aid));
            Econ.setBalance(aid, 0);
        }

        return aid;
    }

    public Integer getPermanentPower() {
        return this.permanentPower;
    }

    public void setPermanentPower(Integer permanentPower) {
        this.permanentPower = permanentPower;
    }

    public boolean hasPermanentPower() {
        return this.permanentPower != null;
    }

    public double getPowerBoost() {
        return this.powerBoost;
    }

    public void setPowerBoost(double powerBoost) {
        this.powerBoost = powerBoost;
    }

    public boolean isPowerFrozen() {
        int freezeSeconds = P.p.getConfig().getInt("hcf.powerfreeze", 0);
        return freezeSeconds != 0 && System.currentTimeMillis() - lastDeath < freezeSeconds * 1000;

    }

    public long getLastDeath() {
        return this.lastDeath;
    }

    public void setLastDeath(long time) {
        this.lastDeath = time;
    }

    public int getKills() {
        return getFPlayers().stream().mapToInt(FPlayer::getKills).sum();
    }

    public int getDeaths() {
        return getFPlayers().stream().mapToInt(FPlayer::getDeaths).sum();
    }

    public int getTntBankBalance() {
        return this.tntBankBalance;
    }

    @Override
    public boolean setTntBankBalance(int tntBankBalance) {
        if (tntBankBalance > tntBankLimit) {
            msg(TL.COMMAND_TNT_FULL);
            return false;
        }
        this.tntBankBalance = tntBankBalance;
        return true;
    }

    public int getTntBankLimit() {
        return this.tntBankLimit;
    }

    public void setTntBankLimit(int tntBankLimit) {
        this.tntBankLimit = tntBankLimit;
    }

    public int getVaultRows() {
        return factionChest.getRows();
    }

    public void setVaultRows(int vaultRows) {
        factionChest.setRows(vaultRows);
    }

    public int getMaxWarps() {
        return maxWarps < 5 ? 5 : maxWarps;
    }

    // -------------------------------------------- //
    // F Permissions stuff
    // -------------------------------------------- //

    public void setMaxWarps(int maxWarps) {
        this.maxWarps = maxWarps;
    }

    public int getMaxMembers() {
        return maxMembers < 1 ? Conf.factionMemberLimit : maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Access getAccess(Permissable permissable, PermissableAction permissableAction) {
        if (permissable == null || permissableAction == null) {
            return Access.UNDEFINED;
        }

        Map<PermissableAction, Access> accessMap = permissions.get(permissable);
        return accessMap != null && accessMap.containsKey(permissableAction) ? accessMap.get(permissableAction) : Access.UNDEFINED;

    }

    /**
     * Get the Access of a player. Will use player's Role if they are a faction member. Otherwise, uses their Relation.
     *
     * @param player
     * @param permissableAction
     * @return
     */
    public Access getAccess(FPlayer player, PermissableAction permissableAction) {
        if (player == null || permissableAction == null) {
            return Access.UNDEFINED;
        }

        Permissable perm = player.getFaction() == this ? player.getRole() : player.getFaction().getRelationTo(this);

        Map<PermissableAction, Access> accessMap = permissions.get(perm);
        return accessMap != null && accessMap.containsKey(permissableAction) ? accessMap.get(permissableAction) : Access.UNDEFINED;

    }

    public void setPermission(Permissable permissable, PermissableAction permissableAction, Access access) {
        Map<PermissableAction, Access> accessMap = permissions.get(permissable);
        if (accessMap == null) {
            accessMap = new HashMap<>();
        }

        accessMap.put(permissableAction, access);
        System.out.println("[FPerms] setPermission(" + permissable.name() + ", " + permissableAction.name() + ", " + access.name() + ") for " + getTag() + "(" + getId() + ")");
    }

    public void resetPerms() {
        P.p.log(Level.WARNING, "Resetting permissions for Faction: " + tag);

        permissions.clear();

        // First populate a map with undefined as the permission for each action.
        Map<PermissableAction, Access> freshMap = Arrays.stream(PermissableAction.values()).collect(Collectors.toMap(permissableAction -> permissableAction, permissableAction -> Access.UNDEFINED, (a, b) -> b));

        // Put the map in there for each relation.
        for (Relation relation : Relation.values()) {
            if (relation != Relation.MEMBER) {
                permissions.put(relation, new HashMap<>(freshMap));
            }
        }

        // And each role.
        for (Role role : Role.values()) {
            if (role != Role.ADMIN) {
                permissions.put(role, new HashMap<>(freshMap));
            }
        }
    }

    /**
     * Read only map of Permissions.
     *
     * @return
     */
    public Map<Permissable, Map<PermissableAction, Access>> getPermissions() {
        return Collections.unmodifiableMap(permissions);
    }

    public Role getDefaultRole() {
        return this.defaultRole;
    }

    public void setDefaultRole(Role role) {
        this.defaultRole = role;
    }

    // -------------------------------------------- //
    // Extra Getters And Setters
    // -------------------------------------------- //
    public boolean noPvPInTerritory() {
        return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisablePVP);
    }

    public boolean noMonstersInTerritory() {
        return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisableMonsters);
    }

    // -------------------------------
    // Understand the types
    // -------------------------------

    public boolean isNormal() {
        return !(this.isWilderness() || this.isSafeZone() || this.isWarZone());
    }

    public boolean isNone() {
        return this.getId().equals("0");
    }

    public boolean isWilderness() {
        return this.getId().equals("0");
    }

    public boolean isSafeZone() {
        return this.getId().equals("-1");
    }

    public boolean isWarZone() {
        return this.getId().equals("-2");
    }

    public boolean isPlayerFreeType() {
        return this.isSafeZone() || this.isWarZone();
    }

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    @Override
    public String describeTo(RelationParticipator that, boolean ucfirst) {
        return RelationUtil.describeThatToMe(this, that, ucfirst);
    }

    @Override
    public String describeTo(RelationParticipator that) {
        return RelationUtil.describeThatToMe(this, that);
    }

    @Override
    public Relation getRelationTo(RelationParticipator rp) {
        return RelationUtil.getRelationTo(this, rp);
    }

    @Override
    public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful) {
        return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
    }

    @Override
    public ChatColor getColorTo(RelationParticipator rp) {
        return RelationUtil.getColorOfThatToMe(this, rp);
    }

    public Relation getRelationWish(Faction otherFaction) {
        if (this.relationWish.containsKey(otherFaction.getId())) {
            return this.relationWish.get(otherFaction.getId());
        }
        return Relation.fromString(P.p.getConfig().getString("default-relation", "neutral")); // Always default to old behavior.
    }

    public void setRelationWish(Faction otherFaction, Relation relation) {
        if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Relation.NEUTRAL)) {
            this.relationWish.remove(otherFaction.getId());
        } else {
            this.relationWish.put(otherFaction.getId(), relation);
        }
    }

    public int getRelationCount(Relation relation) {
        int count = 0;
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.getRelationTo(this) == relation) {
                count++;
            }
        }
        return count;
    }

    // ----------------------------------------------//
    // Power
    // ----------------------------------------------//
    public double getPower() {
        if (this.hasPermanentPower()) {
            return this.getPermanentPower();
        }

        double ret = 0;
        for (FPlayer fplayer : fplayers) {
            ret += fplayer.getPower();
        }
        if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
            ret = Conf.powerFactionMax;
        }
        return ret + this.powerBoost;
    }

    public double getPowerMax() {
        if (this.hasPermanentPower()) {
            return this.getPermanentPower();
        }

        double ret = 0;
        for (FPlayer fplayer : fplayers) {
            ret += fplayer.getPowerMax();
        }
        if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
            ret = Conf.powerFactionMax;
        }
        return ret + this.powerBoost;
    }

    public int getPowerRounded() {
        return (int) Math.round(this.getPower());
    }

    public int getPowerMaxRounded() {
        return (int) Math.round(this.getPowerMax());
    }

    public int getLandRounded() {
        return Board.getInstance().getFactionCoordCount(this);
    }

    public int getLandRoundedInWorld(String worldName) {
        return Board.getInstance().getFactionCoordCountInWorld(this, worldName);
    }

    public boolean hasLandInflation() {
        return this.getLandRounded() > this.getPowerRounded();
    }

    // -------------------------------
    // FPlayers
    // -------------------------------

    // maintain the reference list of FPlayers in this faction
    public void refreshFPlayers() {
        fplayers.clear();
        altPlayers.clear();
        if (this.isPlayerFreeType()) {
            return;
        }

        for (FPlayer fplayer : FPlayers.getInstance().getAllFPlayers()) {
            System.out.println("Scanning " + fplayer.getName() + " | " + fplayer.getFactionId() + " | " + fplayer.getAltFactionId());
            if (fplayer.getFactionId().equalsIgnoreCase(id)) {
                System.out.println("Tracking member!");
                fplayers.add(fplayer);
            } else if (fplayer.getAltFactionId().equalsIgnoreCase(id)) {
                System.out.println("Tracking alt-account!");
                altPlayers.add(fplayer);
            }
        }
    }

    public boolean addFPlayer(FPlayer fplayer) {
        return !this.isPlayerFreeType() && fplayers.add(fplayer);
    }

    public boolean removeFPlayer(FPlayer fplayer) {
        return !this.isPlayerFreeType() && fplayers.remove(fplayer);
    }

    public int getSize() {
        return fplayers.size();
    }

    public Set<FPlayer> getFPlayers() {
        // return a shallow copy of the FPlayer list, to prevent tampering and
        // concurrency issues
        return new HashSet<>(fplayers);
    }

    public Set<FPlayer> getFPlayersWhereOnline(boolean online) {
        Set<FPlayer> ret = new HashSet<>();
        if (!this.isNormal()) {
            return ret;
        }

        for (FPlayer fplayer : fplayers) {
            if (fplayer.isOnline() == online) {
                ret.add(fplayer);
            }
        }

        return ret;
    }

    public Set<FPlayer> getFPlayersWhereOnline(boolean online, FPlayer viewer) {
        Set<FPlayer> ret = new HashSet<>();
        if (!this.isNormal()) {
            return ret;
        }

        for (FPlayer viewed : fplayers) {
            // Add if their online status is what we want
            if (viewed.isOnline() == online) {
                // If we want online, check to see if we are able to see this player
                // This checks if they are in vanish.
                if (online
                        && viewed.getPlayer() != null
                        && viewer.getPlayer() != null
                        && viewer.getPlayer().canSee(viewed.getPlayer())) {
                    ret.add(viewed);
                    // If we want offline, just add them.
                    // Prob a better way to do this but idk.
                } else if (!online) {
                    ret.add(viewed);
                }
            }
        }

        return ret;
    }

    public FPlayer getFPlayerAdmin() {
        if (!this.isNormal()) {
            return null;
        }

        for (FPlayer fplayer : fplayers) {
            if (fplayer.getRole() == Role.ADMIN) {
                return fplayer;
            }
        }
        return null;
    }

    public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
        ArrayList<FPlayer> ret = new ArrayList<>();
        if (!this.isNormal()) {
            return ret;
        }

        for (FPlayer fplayer : fplayers) {
            if (fplayer.getRole() == role) {
                ret.add(fplayer);
            }
        }

        return ret;
    }

    public ArrayList<Player> getOnlinePlayers() {
        ArrayList<Player> ret = new ArrayList<>();
        if (this.isPlayerFreeType()) {
            return ret;
        }

        for (Player player : P.p.getServer().getOnlinePlayers()) {
            FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
            if (fplayer.getFaction() == this) {
                ret.add(player);
            }
        }

        return ret;
    }

    // slightly faster check than getOnlinePlayers() if you just want to see if
    // there are any players online
    public boolean hasPlayersOnline() {
        // only real factions can have players online, not safe zone / war zone
        if (this.isPlayerFreeType()) {
            return false;
        }

        for (FPlayer player : getFPlayers()) {
            if (player.isOnline()) {
                return true;
            }
        }

        // even if all players are technically logged off, maybe someone was on
        // recently enough to not consider them officially offline yet
        return Conf.considerFactionsReallyOfflineAfterXMinutes > 0 && System.currentTimeMillis() < lastPlayerLoggedOffTime + (Conf.considerFactionsReallyOfflineAfterXMinutes * 60000);
    }

    public void memberLoggedOff() {
        if (this.isNormal()) {
            lastPlayerLoggedOffTime = System.currentTimeMillis();
        }
    }

    // used when current leader is about to be removed from the faction;
    // promotes new leader, or disbands faction if no other members left
    public void promoteNewLeader() {
        if (!this.isNormal()) {
            return;
        }
        if (this.isPermanent() && Conf.permanentFactionsDisableLeaderPromotion) {
            return;
        }

        FPlayer oldLeader = this.getFPlayerAdmin();

        // get list of coleaders, or mods, or list of normal members if there are no moderators
        ArrayList<FPlayer> replacements = this.getFPlayersWhereRole(Role.COLEADER);
        if (replacements == null || replacements.isEmpty()) {
            replacements = this.getFPlayersWhereRole(Role.MODERATOR);
        }

        if (replacements == null || replacements.isEmpty()) {
            replacements = this.getFPlayersWhereRole(Role.NORMAL);
        }

        if (replacements == null || replacements.isEmpty()) { // faction admin  is the only  member; one-man  faction
            if (this.isPermanent()) {
                if (oldLeader != null) {
                    oldLeader.setRole(Role.NORMAL);
                }
                return;
            }

            // no members left and faction isn't permanent, so disband it
            if (Conf.logFactionDisband) {
                P.p.log("The faction " + this.getTag() + " (" + this.getId() + ") has been disbanded since it has no members left.");
            }

            for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
                fplayer.msg("The faction %s<i> was disbanded.", this.getTag(fplayer));
            }

            Factions.getInstance().removeFaction(getId());
        } else { // promote new faction admin
            if (oldLeader != null) {
                oldLeader.setRole(Role.COLEADER);
            }

            // make sure we haven't got any other faction admins hiding somehow
            for (FPlayer fplayer : getFPlayersWhereRole(Role.ADMIN)) {
                fplayer.setRole(Role.COLEADER);
            }

            replacements.get(0).setRole(Role.ADMIN);
            this.msg("<i>Faction admin <h>%s<i> has been removed. %s<i> has been promoted as the new faction admin.", oldLeader == null ? "" : oldLeader.getName(), replacements.get(0).getName());
            P.p.log("Faction " + this.getTag() + " (" + this.getId() + ") admin was removed. Replacement admin: " + replacements.get(0).getName());
        }
    }

    public boolean addAltPlayer(FPlayer player) {
        return this.altPlayers.add(player);
    }

    public boolean removeAltPlayer(FPlayer player) {
        return this.altPlayers.remove(player);
    }

    public Set<FPlayer> getAltPlayers() {
        return new HashSet<>(altPlayers);
    }

    public int getAltSize() {
        return this.altPlayers.size();
    }

    // ----------------------------------------------//
    // Messages
    // ----------------------------------------------//
    public void msg(String message, Object... args) {
        message = P.p.txt.parse(message, args);

        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(message);
        }
    }

    public void msg(TL translation, Object... args) {
        msg(translation.toString(), args);
    }

    public void sendMessage(String message) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(message);
        }
    }

    public void sendMessage(List<String> messages) {
        for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
            fplayer.sendMessage(messages);
        }
    }

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//

    public Map<FLocation, Set<String>> getClaimOwnership() {
        return claimOwnership;
    }

    public void clearAllClaimOwnership() {
        claimOwnership.clear();
    }

    public void clearClaimOwnership(FLocation loc) {
        claimOwnership.remove(loc);
    }

    public void clearClaimOwnership(FPlayer player) {
        if (id == null || id.isEmpty()) {
            return;
        }

        Set<String> ownerData;

        for (Entry<FLocation, Set<String>> entry : claimOwnership.entrySet()) {
            ownerData = entry.getValue();

            if (ownerData == null) {
                continue;
            }

            Iterator<String> iter = ownerData.iterator();
            while (iter.hasNext()) {
                if (iter.next().equals(player.getId())) {
                    iter.remove();
                }
            }

            if (ownerData.isEmpty()) {
                claimOwnership.remove(entry.getKey());
            }
        }
    }

    public void grantAllClaimOwnership(FPlayer player) {
        if (id == null || id.isEmpty()) {
            return;
        }

        Set<String> ownerData;
        for (Entry<FLocation, Set<String>> entry : claimOwnership.entrySet()) {
            ownerData = entry.getValue();

            if (ownerData == null) {
                continue;
            }

            ownerData.add(player.getId());
        }
    }

    public int getCountOfClaimsWithOwners() {
        return claimOwnership.isEmpty() ? 0 : claimOwnership.size();
    }

    public boolean doesLocationHaveOwnersSet(FLocation loc) {
        if (claimOwnership.isEmpty() || !claimOwnership.containsKey(loc)) {
            return false;
        }

        Set<String> ownerData = claimOwnership.get(loc);
        return ownerData != null && !ownerData.isEmpty();
    }

    public boolean isPlayerInOwnerList(FPlayer player, FLocation loc) {
        if (claimOwnership.isEmpty()) {
            return false;
        }
        Set<String> ownerData = claimOwnership.get(loc);
        return ownerData != null && ownerData.contains(player.getId());
    }

    public void setPlayerAsOwner(FPlayer player, FLocation loc) {
        Set<String> ownerData = claimOwnership.get(loc);
        if (ownerData == null) {
            ownerData = new HashSet<>();
        }
        ownerData.add(player.getId());
        claimOwnership.put(loc, ownerData);
    }

    public void removePlayerAsOwner(FPlayer player, FLocation loc) {
        Set<String> ownerData = claimOwnership.get(loc);
        if (ownerData == null) {
            return;
        }
        ownerData.remove(player.getId());
        claimOwnership.put(loc, ownerData);
    }

    public Set<String> getOwnerList(FLocation loc) {
        return claimOwnership.get(loc);
    }

    public String getOwnerListString(FLocation loc) {
        Set<String> ownerData = claimOwnership.get(loc);
        if (ownerData == null || ownerData.isEmpty()) {
            return "";
        }

        StringBuilder ownerList = new StringBuilder();

        for (String anOwnerData : ownerData) {
            if (ownerList.length() > 0) {
                ownerList.append(", ");
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(anOwnerData));
            ownerList.append(offlinePlayer != null ? offlinePlayer.getName() : "null player");
        }
        return ownerList.toString();
    }

    // explicitly checks if the passed player is an owner of the pass FLocation - false otherwise
    public boolean playerHasExplicitOwnershipRights(FPlayer fplayer, FLocation loc) {
        // in own faction, with sufficient role or permission to bypass
        if (fplayer.getFaction() == this && (fplayer.getRole().isAtLeast(Role.ADMIN) || Permission.OWNERSHIP_BYPASS.has(fplayer.getPlayer()))) {
            return true; // always bypass
        }

        // make sure claimOwnership is initialized
        if (claimOwnership.isEmpty()) {
            return false; // no owners listed, this player can't be an owner then
        }

        // make sure the player has ownership data for this chunk
        Set<String> ownerData = claimOwnership.get(loc);
        return ownerData != null && ownerData.contains(fplayer.getId());
    }

    public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc) {
        // in own faction, with sufficient role or permission to bypass
        // ownership?
        if (fplayer.getFaction() == this && (fplayer.getRole().isAtLeast(Role.ADMIN) || Permission.OWNERSHIP_BYPASS.has(fplayer.getPlayer()))) {
            return true;
        }

        // make sure claimOwnership is initialized
        if (claimOwnership.isEmpty()) {
            return true;
        }

        // need to check the ownership list, then
        Set<String> ownerData = claimOwnership.get(loc);

        // if no owner list, owner list is empty, or player is in owner list,
        // they're allowed
        return ownerData == null || ownerData.isEmpty() || ownerData.contains(fplayer.getId());
    }

    // ----------------------------------------------//
    // Persistance and entity management
    // ----------------------------------------------//
    public void remove() {
        if (Econ.shouldBeUsed()) {
            Econ.setBalance(getAccountId(), 0);
        }

        // Clean the board
        ((MemoryBoard) Board.getInstance()).clean(id);

        for (FPlayer fPlayer : fplayers) {
            fPlayer.resetFactionData(false);
        }

        for (FPlayer fPlayer : altPlayers) {
            fPlayer.resetAltFactionData();
        }
    }

    public Set<FLocation> getAllClaims() {
        return Board.getInstance().getAllClaims(this);
    }
}
