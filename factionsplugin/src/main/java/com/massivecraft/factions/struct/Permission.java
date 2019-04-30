package com.massivecraft.factions.struct;

import com.massivecraft.factions.P;
import org.bukkit.command.CommandSender;

public enum Permission {

    MANAGE_SAFE_ZONE("managesafezone"),
    MANAGE_WAR_ZONE("managewarzone"),
    OWNERSHIP_BYPASS("ownershipbypass"),
    ADMIN("admin"),
    ADMIN_ANY("admin.any"),
    AHOME("ahome"),
    ALTS_LIST("alts.list"),
    ALTS_LIST_ANY("alts.list.any"),
    ALTS_INVITE("alts.invite"),
    ALTS_JOIN("alts.join"),
    ALTS_JOIN_OTHERS("alts.join.others"),
    ALTS_REVOKE("alts.revoke"),
    ALTS_INVITE_LIST("alts.invitelist"),
    ALTS_INVITE_LIST_ANY("alts.invitelist.any"),
    ANNOUNCE("announce"),
    AUTOCLAIM("autoclaim"),
    AUTO_LEAVE_BYPASS("autoleavebypass"),
    BAN("ban"),
    BYPASS("bypass"),
    CHAT("chat"),
    CHATSPY("chatspy"),
    CLAIM("claim"),
    CLAIMAT("claimat"),
    CLAIM_LINE("claim.line"),
    CLAIM_RADIUS("claim.radius"),
    COLEADER("coleader"),
    COLEADER_ANY("coleader.any"),
    CONFIG("config"),
    CREATE("create"),
    DEFAULTRANK("defaultrank"),
    DEINVITE("deinvite"),
    DESCRIPTION("description"),
    DISBAND("disband"),
    DISBAND_ANY("disband.any"),
    FLY("fly.member"),
    HELP("help"),
    HOME("home"),
    INVITE("invite"),
    INSPECT("inspect"),
    JOIN("join"),
    JOIN_ANY("join.any"),
    JOIN_OTHERS("join.others"),
    KICK("kick"),
    KICK_ANY("kick.any"),
    LEAVE("leave"),
    LIST("list"),
    LOCK("lock"),
    MAP("map"),
    MAPHEIGHT("mapheight"),
    MOD("mod"),
    MOD_ANY("mod.any"),
    MODIFY_POWER("modifypower"),
    MONEY_BALANCE("money.balance"),
    MONEY_BALANCE_ANY("money.balance.any"),
    MONEY_DEPOSIT("money.deposit"),
    MONEY_WITHDRAW("money.withdraw"),
    MONEY_WITHDRAW_ANY("money.withdraw.any"),
    MONEY_F2F("money.f2f"),
    MONEY_F2P("money.f2p"),
    MONEY_P2F("money.p2f"),
    MONITOR_LOGINS("monitorlogins"),
    NO_BOOM("noboom"),
    OPEN("open"),
    OWNER("owner"),
    OWNERLIST("ownerlist"),
    SET_PEACEFUL("setpeaceful"),
    SET_PERMANENT("setpermanent"),
    SET_PERMANENTPOWER("setpermanentpower"),
    SHOW_INVITES("showinvites"),
    PERMISSIONS("permissions"),
    POWERBOOST("powerboost"),
    POWER("power"),
    POWER_ANY("power.any"),
    PROMOTE("promote"),
    RELATION("relation"),
    RELOAD("reload"),
    SAVE("save"),
    SETHOME("sethome"),
    SETHOME_ANY("sethome.any"),
    SHOW("show"),
    STATUS("status"),
    STRIKE("strike"),
    STUCK("stuck"),
    TAG("tag"),
    TITLE("title"),
    TITLE_COLOR("title.color"),
    TNT("tnt"),
    TNTFILL("tntfill"),
    TOGGLE_ALLIANCE_CHAT("togglealliancechat"),
    UNCLAIM("unclaim"),
    UNCLAIM_ALL("unclaimall"),
    VERSION("version"),
    SCOREBOARD("scoreboard"),
    SEECHUNK("seechunk"),
    SETWARP("setwarp"),
    TOP("top"),
    VAULT("vault"),
    NEAR("near"),
    WARP("warp"),
    STEALTH("stealth"),
    PAYPAL("paypal"),
    SET_PAYPAL("setpaypal"),
    MUTECHAT("mutechat"),
    CHECK("check"),
    FLY_WILDERNESS("fly.wilderness"),
    FLY_SAFEZONE("fly.safezone"),
    FLY_WARZONE("fly.warzone"),
    UPGRADES("upgrades"),
    CORNER_LIST("cornerlist"),
    STATISTICS("statistics");

    public final String node;

    Permission(final String node) {
        this.node = "factions." + node;
    }

    public boolean has(CommandSender sender, boolean informSenderIfNot) {
        return P.p.perm.has(sender, this.node, informSenderIfNot);
    }

    public boolean has(CommandSender sender) {
        return has(sender, false);
    }
}
