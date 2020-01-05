package com.massivecraft.factions;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.StrikeInfo;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.factionchest.FactionChest;
import com.massivecraft.factions.zcore.factionshields.FactionShield;
import com.massivecraft.factions.zcore.factionupgrades.FactionUpgrade;
import com.massivecraft.factions.zcore.factionupgrades.FactionUpgradeMenu;
import com.massivecraft.factions.zcore.factionwarps.WarpMenu;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface Faction extends EconomyParticipator {

    long getLastShieldChange();

    /**
     * LockedThread's ADDITIONS
     */

    void setLastShieldChange(long lastShieldChange);

    int getInvitesToday();

    void setInvitesToday(int invitesToday);

    FactionShield getFactionShield();

    void setFactionShield(FactionShield factionShield);

    boolean getFactionShieldCachedValue();

    void setFactionShieldCachedValue(boolean factionShieldCachedValue);

    WarpMenu getWarpMenu();

    FactionUpgradeMenu getFactionUpgradeMenu();

    EnumMap<FactionUpgrade, Integer> getUpgrades();

    long getCheckReminderMinutes();

    void setCheckReminderMinutes(long minutes);

    void sendCheckRemind();

    String getPayPal();

    void setPayPal(String email);

    HashMap<String, List<String>> getAnnouncements();

    ConcurrentHashMap<String, LazyLocation> getWarps();

    LazyLocation getWarp(String name);

    String getTrueWarp(String name);

    void setWarp(String name, LazyLocation loc);

    boolean isWarp(String name);

    boolean hasWarpPassword(String warp);

    boolean isWarpPassword(String warp, String password);

    void setWarpPassword(String warp, String password);

    boolean removeWarp(String name);

    void clearWarps();

    void addAnnouncement(FPlayer fPlayer, String msg);

    void sendUnreadAnnouncements(FPlayer fPlayer);

    void removeAnnouncements(FPlayer fPlayer);

    Set<String> getInvites();

    String getId();

    void setId(String id);

    void invite(FPlayer fplayer);

    void deinvite(FPlayer fplayer);

    boolean isInvited(FPlayer fplayer);

    void ban(FPlayer target, FPlayer banner);

    void unban(FPlayer player);

    boolean isBanned(FPlayer player);

    Set<BanInfo> getBannedPlayers();

    void strike(CommandSender executor, String description);

    // removes the most recent strike issued
    void destrike(CommandSender executor);

    Set<StrikeInfo> getStrikes();

    boolean getOpen();

    void setOpen(boolean isOpen);

    boolean isPeaceful();

    void setPeaceful(boolean isPeaceful);

    boolean getPeacefulExplosionsEnabled();

    void setPeacefulExplosionsEnabled(boolean val);

    boolean noExplosionsInTerritory();

    boolean isPermanent();

    void setPermanent(boolean isPermanent);

    String getTag();

    void setTag(String str);

    String getTag(String prefix);

    String getTag(com.massivecraft.factions.Faction otherFaction);

    String getTag(FPlayer otherFplayer);

    String getComparisonTag();

    String getDescription();

    void setDescription(String value);

    boolean hasHome();

    Location getHome();

    void setHome(Location home);

    long getFoundedDate();

    void setFoundedDate(long newDate);

    void confirmValidHome();

    String getAccountId();

    Integer getPermanentPower();

    void setPermanentPower(Integer permanentPower);

    boolean hasPermanentPower();

    double getPowerBoost();

    void setPowerBoost(double powerBoost);

    boolean noPvPInTerritory();

    boolean noMonstersInTerritory();

    boolean isNormal();

    @Deprecated
    boolean isNone();

    boolean isWilderness();

    boolean isSafeZone();

    boolean isWarZone();

    boolean isPlayerFreeType();

    boolean isPowerFrozen();

    void setLastDeath(long time);

    int getKills();

    int getDeaths();

    Access getAccess(Permissable permissable, PermissableAction permissableAction);

    Access getAccess(FPlayer player, PermissableAction permissableAction);

    void setPermission(Permissable permissable, PermissableAction permissableAction, Access access);

    void resetPerms();

    Map<Permissable, Map<PermissableAction, Access>> getPermissions();

    int getTntBankBalance();

    boolean setTntBankBalance(int tntBankBalance);

    int getTntBankLimit();

    void setTntBankLimit(int tntBankLimit);

    int getVaultRows();

    void setVaultRows(int vaultRows);

    FactionChest getFactionChest();

    int getMaxWarps();

    void setMaxWarps(int maxWarps);

    int getMaxMembers();

    void setMaxMembers(int maxMembers);

    boolean isAltInvitesOpen();

    void setAltInvitesOpen(boolean altInvitesOpen);

    void inviteAlt(FPlayer fplayer);

    void deinviteAlt(FPlayer fplayer);

    void deinviteAllAlts();

    boolean isAltInvited(FPlayer fplayer);

    List<String> getAltInvites();

    boolean addAltPlayer(FPlayer player);

    boolean removeAltPlayer(FPlayer player);

    Set<FPlayer> getAltPlayers();

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    int getAltSize();

    @Override
    String describeTo(RelationParticipator that, boolean ucfirst);

    @Override
    String describeTo(RelationParticipator that);

    @Override
    Relation getRelationTo(RelationParticipator rp);

    @Override
    Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

    @Override
    ChatColor getColorTo(RelationParticipator rp);

    Relation getRelationWish(com.massivecraft.factions.Faction otherFaction);

    void setRelationWish(com.massivecraft.factions.Faction otherFaction, Relation relation);

    int getRelationCount(Relation relation);

    // ----------------------------------------------//
    // Power
    // ----------------------------------------------//
    double getPower();

    double getPowerMax();

    int getPowerRounded();

    int getPowerMaxRounded();

    int getLandRounded();

    int getLandRoundedInWorld(String worldName);

    // -------------------------------
    // FPlayers
    // -------------------------------

    boolean hasLandInflation();

    // maintain the reference list of FPlayers in this faction
    void refreshFPlayers();

    boolean addFPlayer(FPlayer fplayer);

    boolean removeFPlayer(FPlayer fplayer);

    int getSize();

    Set<FPlayer> getFPlayers();

    Set<FPlayer> getFPlayersWhereOnline(boolean online);

    Set<FPlayer> getFPlayersWhereOnline(boolean online, FPlayer viewer);

    FPlayer getFPlayerAdmin();

    ArrayList<FPlayer> getFPlayersWhereRole(Role role);

    ArrayList<Player> getOnlinePlayers();

    // slightly faster check than getOnlinePlayers() if you just want to see if
    // there are any players online
    boolean hasPlayersOnline();

    void memberLoggedOff();

    // used when current leader is about to be removed from the faction;
    // promotes new leader, or disbands faction if no other members left
    void promoteNewLeader();

    Role getDefaultRole();

    void setDefaultRole(Role role);

    // ----------------------------------------------//
    // Messages
    // ----------------------------------------------//
    void msg(String message, Object... args);

    void sendMessage(String message);

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//

    void sendMessage(List<String> messages);

    Map<com.massivecraft.factions.FLocation, Set<String>> getClaimOwnership();

    void clearAllClaimOwnership();

    void clearClaimOwnership(com.massivecraft.factions.FLocation loc);

    void clearClaimOwnership(FPlayer player);

    void grantAllClaimOwnership(FPlayer player);

    int getCountOfClaimsWithOwners();

    boolean doesLocationHaveOwnersSet(com.massivecraft.factions.FLocation loc);

    boolean isPlayerInOwnerList(FPlayer player, com.massivecraft.factions.FLocation loc);

    void setPlayerAsOwner(FPlayer player, com.massivecraft.factions.FLocation loc);

    void removePlayerAsOwner(FPlayer player, com.massivecraft.factions.FLocation loc);

    Set<String> getOwnerList(com.massivecraft.factions.FLocation loc);

    String getOwnerListString(com.massivecraft.factions.FLocation loc);

    boolean playerHasOwnershipRights(FPlayer fplayer, com.massivecraft.factions.FLocation loc);

    boolean playerHasExplicitOwnershipRights(FPlayer fplayer, FLocation loc);

    // ----------------------------------------------//
    // Persistance and entity management
    // ----------------------------------------------//
    void remove();

    Set<FLocation> getAllClaims();
}
