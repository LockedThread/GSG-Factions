package com.massivecraft.factions;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.factionstatistics.FactionStatistic;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.List;


/**
 * Logged in players always have exactly one FPlayer instance. Logged out players may or may not have an FPlayer
 * instance. They will always have one if they are part of a faction. This is because only players with a faction are
 * saved to disk (in order to not waste disk space).
 * <p/>
 * The FPlayer is linked to a minecraft player using the player name.
 * <p/>
 * The same instance is always returned for the same player. This means you can use the == operator. No .equals method
 * necessary.
 */

public interface FPlayer extends EconomyParticipator {

    boolean isDrainEnabled();

    void setDrainEnabled(boolean drainingEnabled);

    boolean isPrinterMode();

    void setPrinterMode(boolean printerMode);

    boolean getFlyTrailsState();

    void setFlyTrailsState(boolean state);

    String getFlyTrailsEffect();

    void setFlyTrailsEffect(String effect);

    double getFormattedTimePlayed();

    EnumMap<FactionStatistic, Integer> getFactionStatisticMap();

    int getFactionStatistic(FactionStatistic factionStatistic);

    default void incrementFactionStatistic(FactionStatistic factionStatistic) {
        getFactionStatisticMap().compute(factionStatistic, (factionStatistic1, integer) -> integer == null ? 1 : integer + 1);
    }

    default void setFactionStatistic(FactionStatistic factionStatistic, int data) {
        getFactionStatisticMap().put(factionStatistic, data);
    }

    void openFactionUpgradeMenu();

    boolean isInspecting();

    void setInspecting(boolean inspecting);

    long getLastMapUse();

    void setLastMapUse(long lastMapUse);

    boolean isSeeingChunk();

    void setSeeingChunk(boolean seeingChunk);

    boolean isMutedChatEnabled();

    void setMutedChat(boolean mutedChat);

    void login();

    void logout();

    com.massivecraft.factions.Faction getFaction();

    void setFaction(com.massivecraft.factions.Faction faction);

    String getFactionId();

    boolean hasFaction();

    boolean willAutoLeave();

    void setAutoLeave(boolean autoLeave);

    void setMonitorJoins(boolean monitor);

    boolean isMonitoringJoins();

    Role getRole();

    void setRole(Role role);

    boolean shouldTakeFallDamage();

    void setTakeFallDamage(boolean fallDamage);

    double getPowerBoost();

    void setPowerBoost(double powerBoost);

    com.massivecraft.factions.Faction getAutoClaimFor();

    void setAutoClaimFor(com.massivecraft.factions.Faction faction);

    boolean isAutoSafeClaimEnabled();

    void setIsAutoSafeClaimEnabled(boolean enabled);

    boolean isAutoWarClaimEnabled();

    void setIsAutoWarClaimEnabled(boolean enabled);

    boolean isAdminBypassing();

    boolean isStealth();

    void setStealth(boolean stealth);

    boolean isVanished();

    void setIsAdminBypassing(boolean val);

    ChatMode getChatMode();

    void setChatMode(ChatMode chatMode);

    boolean isIgnoreAllianceChat();

    void setIgnoreAllianceChat(boolean ignore);

    boolean isSpyingChat();

    void setSpyingChat(boolean chatSpying);

    boolean showScoreboard();

    void setShowScoreboard(boolean show);

    // FIELD: account
    String getAccountId();

    void resetFactionData(boolean doSpoutUpdate);

    void resetFactionData();

    void resetAltFactionData();

    long getLastLoginTime();

    void setLastLoginTime(long lastLoginTime);

    boolean isMapAutoUpdating();

    void setMapAutoUpdating(boolean mapAutoUpdating);

    boolean hasLoginPvpDisabled();

    FLocation getLastStoodAt();

    void setLastStoodAt(FLocation flocation);

    String getTitle();

    void setTitle(CommandSender sender, String title);

    String getName();

    String getTag();

    boolean hasAltFaction();

    com.massivecraft.factions.Faction getAltFaction();

    void setAltFaction(com.massivecraft.factions.Faction faction);

    String getAltFactionId();

    // Base concatenations:

    String getNameAndSomething(String something);

    String getNameAndTitle();

    String getNameAndTag();

    // Colored concatenations:
    // These are used in information messages

    String getNameAndTitle(com.massivecraft.factions.Faction faction);

    String getNameAndTitle(com.massivecraft.factions.FPlayer fplayer);

    // Chat Tag:
    // These are injected into the format of global chat messages.

    String getChatTag();

    // Colored Chat Tag
    String getChatTag(com.massivecraft.factions.Faction faction);

    String getChatTag(com.massivecraft.factions.FPlayer fplayer);

    int getKills();

    int getDeaths();


    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    @Override
    String describeTo(RelationParticipator that, boolean ucfirst);

    @Override
    String describeTo(RelationParticipator that);

    @Override
    Relation getRelationTo(RelationParticipator rp);

    @Override
    Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

    Relation getRelationToLocation();

    @Override
    ChatColor getColorTo(RelationParticipator rp);

    //----------------------------------------------//
    // Health
    //----------------------------------------------//
    void heal(int amnt);


    //----------------------------------------------//
    // Power
    //----------------------------------------------//
    double getPower();

    void alterPower(double delta);

    double getPowerMax();

    double getPowerMin();

    int getPowerRounded();

    int getPowerMaxRounded();

    int getPowerMinRounded();

    void updatePower();

    void losePowerFromBeingOffline();

    void onDeath();

    //----------------------------------------------//
    // Territory
    //----------------------------------------------//
    boolean isInOwnTerritory();

    boolean isInOthersTerritory();

    boolean isInAllyTerritory();

    boolean isInNeutralTerritory();

    boolean isInEnemyTerritory();

    void sendFactionHereMessage(com.massivecraft.factions.Faction from);

    // -------------------------------
    // Actions
    // -------------------------------

    void leave(boolean makePay);

    boolean canClaimForFaction(com.massivecraft.factions.Faction forFaction);

    boolean canClaimForFactionAtLocation(com.massivecraft.factions.Faction forFaction, Location location, boolean notifyFailure);

    boolean canClaimForFactionAtLocation(com.massivecraft.factions.Faction forFaction, FLocation location, boolean notifyFailure);

    boolean attemptClaim(com.massivecraft.factions.Faction forFaction, Location location, boolean notifyFailure);

    boolean attemptClaim(Faction forFaction, FLocation location, boolean notifyFailure);

    void msg(String str, Object... args);

    String getId();

    void setId(String id);

    Player getPlayer();

    boolean isOnline();

    void sendMessage(String message);

    void sendMessage(List<String> messages);

    void sendFancyMessage(FancyMessage message);

    void sendFancyMessage(List<FancyMessage> message);

    int getMapHeight();

    void setMapHeight(int height);

    boolean isOnlineAndVisibleTo(Player me);

    void remove();

    boolean isOffline();

    boolean isFlying();

    void setFlying(boolean fly);

    void setFFlying(boolean fly, boolean damage);

    boolean canFlyAtLocation();

    boolean canFlyAtLocation(FLocation location);

    boolean canFlyAtLocation(FLocation location, boolean everywhereChecks);

    boolean canUseFactionVault();

    // -------------------------------
    // Warmups
    // -------------------------------

    boolean isWarmingUp();

    WarmUpUtil.Warmup getWarmupType();

    void addWarmup(WarmUpUtil.Warmup warmup, int taskId);

    void stopWarmup();

    void clearWarmup();

    void setNotificationsEnabled(boolean enabled);

    boolean hasNotificationsEnabled();

}
