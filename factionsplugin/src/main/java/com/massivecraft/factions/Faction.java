package com.massivecraft.factions;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.StrikeInfo;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.factionchest.FactionChest;
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

    /**
     * LockedThread's ADDITIONS
     */

    public Map<String, Long> getShopCooldown();

    public int getPoints();

    public void setPoints(int points);

    public WarpMenu getWarpMenu();

    public FactionUpgradeMenu getFactionUpgradeMenu();

    public EnumMap<FactionUpgrade, Integer> getUpgrades();

    public long getCheckReminderMinutes();

    public void setCheckReminderMinutes(long minutes);

    public void sendCheckRemind();

    public String getPayPal();

    public void setPayPal(String email);

    public HashMap<String, List<String>> getAnnouncements();

    public ConcurrentHashMap<String, LazyLocation> getWarps();

    public LazyLocation getWarp(String name);

    public String getTrueWarp(String name);

    public void setWarp(String name, LazyLocation loc);

    public boolean isWarp(String name);

    public boolean hasWarpPassword(String warp);

    public boolean isWarpPassword(String warp, String password);

    public void setWarpPassword(String warp, String password);

    public boolean removeWarp(String name);

    public void clearWarps();

    public void addAnnouncement(FPlayer fPlayer, String msg);

    public void sendUnreadAnnouncements(FPlayer fPlayer);

    public void removeAnnouncements(FPlayer fPlayer);

    public Set<String> getInvites();

    public String getId();

    public void setId(String id);

    public void invite(FPlayer fplayer);

    public void deinvite(FPlayer fplayer);

    public boolean isInvited(FPlayer fplayer);

    public void ban(FPlayer target, FPlayer banner);

    public void unban(FPlayer player);

    public boolean isBanned(FPlayer player);

    public Set<BanInfo> getBannedPlayers();

    public void strike(CommandSender executor, String description);

    // removes the most recent strike issued
    public void destrike(CommandSender executor);

    public Set<StrikeInfo> getStrikes();

    public boolean getOpen();

    public void setOpen(boolean isOpen);

    public boolean isPeaceful();

    public void setPeaceful(boolean isPeaceful);

    public boolean getPeacefulExplosionsEnabled();

    public void setPeacefulExplosionsEnabled(boolean val);

    public boolean noExplosionsInTerritory();

    public boolean isPermanent();

    public void setPermanent(boolean isPermanent);

    public String getTag();

    public void setTag(String str);

    public String getTag(String prefix);

    public String getTag(com.massivecraft.factions.Faction otherFaction);

    public String getTag(FPlayer otherFplayer);

    public String getComparisonTag();

    public String getDescription();

    public void setDescription(String value);

    public boolean hasHome();

    public Location getHome();

    public void setHome(Location home);

    public long getFoundedDate();

    public void setFoundedDate(long newDate);

    public void confirmValidHome();

    public String getAccountId();

    public Integer getPermanentPower();

    public void setPermanentPower(Integer permanentPower);

    public boolean hasPermanentPower();

    public double getPowerBoost();

    public void setPowerBoost(double powerBoost);

    public boolean noPvPInTerritory();

    public boolean noMonstersInTerritory();

    public boolean isNormal();

    @Deprecated
    public boolean isNone();

    public boolean isWilderness();

    public boolean isSafeZone();

    public boolean isWarZone();

    public boolean isPlayerFreeType();

    public boolean isPowerFrozen();

    public void setLastDeath(long time);

    public int getKills();

    public int getDeaths();

    public Access getAccess(Permissable permissable, PermissableAction permissableAction);

    public Access getAccess(FPlayer player, PermissableAction permissableAction);

    public void setPermission(Permissable permissable, PermissableAction permissableAction, Access access);

    public void resetPerms();

    public Map<Permissable, Map<PermissableAction, Access>> getPermissions();

    public int getTntBankBalance();

    public boolean setTntBankBalance(int tntBankBalance);

    public int getTntBankLimit();

    public void setTntBankLimit(int tntBankLimit);

    public int getVaultRows();

    public void setVaultRows(int vaultRows);

    public FactionChest getFactionChest();

    public int getMaxWarps();

    public void setMaxWarps(int maxWarps);

    public int getMaxMembers();

    public void setMaxMembers(int maxMembers);

    public void inviteAlt(FPlayer fplayer);

    public void deinviteAlt(FPlayer fplayer);

    public void deinviteAllAlts();

    public boolean isAltInvited(FPlayer fplayer);

    public List<String> getAltInvites();

    public boolean addAltPlayer(FPlayer player);

    public boolean removeAltPlayer(FPlayer player);

    public Set<FPlayer> getAltPlayers();

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    public int getAltSize();

    @Override
    public String describeTo(RelationParticipator that, boolean ucfirst);

    @Override
    public String describeTo(RelationParticipator that);

    @Override
    public Relation getRelationTo(RelationParticipator rp);

    @Override
    public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

    @Override
    public ChatColor getColorTo(RelationParticipator rp);

    public Relation getRelationWish(com.massivecraft.factions.Faction otherFaction);

    public void setRelationWish(com.massivecraft.factions.Faction otherFaction, Relation relation);

    public int getRelationCount(Relation relation);

    // ----------------------------------------------//
    // Power
    // ----------------------------------------------//
    public double getPower();

    public double getPowerMax();

    public int getPowerRounded();

    public int getPowerMaxRounded();

    public int getLandRounded();

    public int getLandRoundedInWorld(String worldName);

    // -------------------------------
    // FPlayers
    // -------------------------------

    public boolean hasLandInflation();

    // maintain the reference list of FPlayers in this faction
    public void refreshFPlayers();

    public boolean addFPlayer(FPlayer fplayer);

    public boolean removeFPlayer(FPlayer fplayer);

    public int getSize();

    public Set<FPlayer> getFPlayers();

    public Set<FPlayer> getFPlayersWhereOnline(boolean online);

    public Set<FPlayer> getFPlayersWhereOnline(boolean online, FPlayer viewer);

    public FPlayer getFPlayerAdmin();

    public ArrayList<FPlayer> getFPlayersWhereRole(Role role);

    public ArrayList<Player> getOnlinePlayers();

    // slightly faster check than getOnlinePlayers() if you just want to see if
    // there are any players online
    public boolean hasPlayersOnline();

    public void memberLoggedOff();

    // used when current leader is about to be removed from the faction;
    // promotes new leader, or disbands faction if no other members left
    public void promoteNewLeader();

    public Role getDefaultRole();

    public void setDefaultRole(Role role);

    // ----------------------------------------------//
    // Messages
    // ----------------------------------------------//
    public void msg(String message, Object... args);

    public void sendMessage(String message);

    // ----------------------------------------------//
    // Ownership of specific claims
    // ----------------------------------------------//

    public void sendMessage(List<String> messages);

    public Map<com.massivecraft.factions.FLocation, Set<String>> getClaimOwnership();

    public void clearAllClaimOwnership();

    public void clearClaimOwnership(com.massivecraft.factions.FLocation loc);

    public void clearClaimOwnership(FPlayer player);

    public void grantAllClaimOwnership(FPlayer player);

    public int getCountOfClaimsWithOwners();

    public boolean doesLocationHaveOwnersSet(com.massivecraft.factions.FLocation loc);

    public boolean isPlayerInOwnerList(FPlayer player, com.massivecraft.factions.FLocation loc);

    public void setPlayerAsOwner(FPlayer player, com.massivecraft.factions.FLocation loc);

    public void removePlayerAsOwner(FPlayer player, com.massivecraft.factions.FLocation loc);

    public Set<String> getOwnerList(com.massivecraft.factions.FLocation loc);

    public String getOwnerListString(com.massivecraft.factions.FLocation loc);

    public boolean playerHasOwnershipRights(FPlayer fplayer, com.massivecraft.factions.FLocation loc);

    public boolean playerHasExplicitOwnershipRights(FPlayer fplayer, FLocation loc);

    // ----------------------------------------------//
    // Persistance and entity management
    // ----------------------------------------------//
    public void remove();

    public Set<FLocation> getAllClaims();
}
