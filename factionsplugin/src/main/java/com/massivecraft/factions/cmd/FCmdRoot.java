package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;

import java.util.Collections;
import java.util.logging.Level;

public class FCmdRoot extends FCommand {

    public CmdAdmin cmdAdmin = new CmdAdmin();
    public CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
    public CmdBoom cmdBoom = new CmdBoom();
    public CmdBypass cmdBypass = new CmdBypass();
    public CmdChat cmdChat = new CmdChat();
    public CmdChatSpy cmdChatSpy = new CmdChatSpy();
    public CmdClaim cmdClaim = new CmdClaim();
    public CmdConfig cmdConfig = new CmdConfig();
    public CmdCreate cmdCreate = new CmdCreate();
    public CmdDeinvite cmdDeinvite = new CmdDeinvite();
    public CmdDescription cmdDescription = new CmdDescription();
    public CmdDisband cmdDisband = new CmdDisband();
    public CmdFly cmdFly = new CmdFly();
    public CmdHelp cmdHelp = new CmdHelp();
    public CmdHome cmdHome = new CmdHome();
    public CmdInvite cmdInvite = new CmdInvite();
    public CmdJoin cmdJoin = new CmdJoin();
    public CmdKick cmdKick = new CmdKick();
    public CmdLeave cmdLeave = new CmdLeave();
    public CmdList cmdList = new CmdList();
    public CmdMap cmdMap = new CmdMap();
    public CmdMod cmdMod = new CmdMod();
    public CmdMoney cmdMoney = new CmdMoney();
    public CmdOpen cmdOpen = new CmdOpen();
    public CmdOwner cmdOwner = new CmdOwner();
    public CmdOwnerList cmdOwnerList = new CmdOwnerList();
    public CmdPeaceful cmdPeaceful = new CmdPeaceful();
    public CmdPermanent cmdPermanent = new CmdPermanent();
    public CmdPermanentPower cmdPermanentPower = new CmdPermanentPower();
    public CmdPowerBoost cmdPowerBoost = new CmdPowerBoost();
    public CmdPower cmdPower = new CmdPower();
    public CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
    public CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
    public CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
    public CmdRelationTruce cmdRelationTruce = new CmdRelationTruce();
    public CmdReload cmdReload = new CmdReload();
    public CmdSafeunclaimall cmdSafeunclaimall = new CmdSafeunclaimall();
    public CmdSaveAll cmdSaveAll = new CmdSaveAll();
    public CmdSethome cmdSethome = new CmdSethome();
    public CmdShow cmdShow = new CmdShow();
    public CmdStatus cmdStatus = new CmdStatus();
    public CmdTag cmdTag = new CmdTag();
    public CmdTitle cmdTitle = new CmdTitle();
    public CmdToggleAllianceChat cmdToggleAllianceChat = new CmdToggleAllianceChat();
    public CmdUnclaim cmdUnclaim = new CmdUnclaim();
    public CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
    public CmdVersion cmdVersion = new CmdVersion();
    public CmdWarunclaimall cmdWarunclaimall = new CmdWarunclaimall();
    public CmdSB cmdSB = new CmdSB();
    public CmdShowInvites cmdShowInvites = new CmdShowInvites();
    public CmdAnnounce cmdAnnounce = new CmdAnnounce();
    public CmdSeeChunk cmdSeeChunk = new CmdSeeChunk();
    public CmdConvert cmdConvert = new CmdConvert();
    public CmdFWarp cmdFWarp = new CmdFWarp();
    public CmdSetFWarp cmdSetFWarp = new CmdSetFWarp();
    public CmdDelFWarp cmdDelFWarp = new CmdDelFWarp();
    public CmdModifyPower cmdModifyPower = new CmdModifyPower();
    public CmdLogins cmdLogins = new CmdLogins();
    public CmdClaimLine cmdClaimLine = new CmdClaimLine();
    public CmdAHome cmdAHome = new CmdAHome();
    public CmdPerm cmdPerm = new CmdPerm();
    public CmdPromote cmdPromote = new CmdPromote();
    public CmdDemote cmdDemote = new CmdDemote();
    public CmdSetDefaultRole cmdSetDefaultRole = new CmdSetDefaultRole();
    public CmdMapHeight cmdMapHeight = new CmdMapHeight();
    public CmdClaimAt cmdClaimAt = new CmdClaimAt();
    public CmdBan cmdBan = new CmdBan();
    public CmdUnban cmdUnban = new CmdUnban();
    public CmdBanlist cmdbanlist = new CmdBanlist();
    public CmdColeader cmdColeader = new CmdColeader();
    public CmdNear cmdNear = new CmdNear();
    public CmdStealth cmdStealth = new CmdStealth();
    public CmdOwnerAll cmdOwnerAll = new CmdOwnerAll();
    public CmdClearOwner cmdClearOwner = new CmdClearOwner();
    public CmdNotifications cmdNotifications = new CmdNotifications();
    public CmdTnt cmdTnt = new CmdTnt();
    public CmdTntfill cmdTntfill = new CmdTntfill("tntfill");
    public CmdSetVaultRows cmdSetVaultRows = new CmdSetVaultRows();
    public CmdSetMaxWarps cmdSetMaxWarps = new CmdSetMaxWarps();
    public CmdSetMaxMembers cmdSetMaxMembers = new CmdSetMaxMembers();
    public CmdStrike cmdStrike = new CmdStrike();
    public CmdFWarpOther cmdFWarpOther = new CmdFWarpOther();
    public CmdAlts cmdAlts = new CmdAlts();
    public CmdAltJoin cmdAltJoin = new CmdAltJoin();
    public CmdCorner cmdCorner = new CmdCorner();
    public CmdPayPal cmdPayPal = new CmdPayPal();
    public CmdSetPayPal cmdSetPayPal = new CmdSetPayPal();
    public CmdLock cmdLock = new CmdLock();
    public CmdVault cmdVault = new CmdVault();
    public CmdSotw cmdSotw = new CmdSotw();
    public CmdSetMaxTNTBankBalance cmdSetMaxTNTBankBalance = new CmdSetMaxTNTBankBalance();
    public CmdMuteChat cmdMuteChat = new CmdMuteChat();
    public CmdCheck cmdCheck = new CmdCheck();
    public CmdUpgrades cmdUpgrades = new CmdUpgrades();
    public CmdCornerList cmdCornerList = new CmdCornerList();
    public CmdCornerReload cmdCornerReload = new CmdCornerReload();
    public CmdStats cmdStats = new CmdStats();
    public CmdShop cmdShop = new CmdShop();
    public CmdPoints cmdPoints = new CmdPoints();
    public CmdSetPoints cmdSetPoints = new CmdSetPoints();

    public FCmdRoot() {
        super();
        this.aliases.addAll(Conf.baseCommandAliases);
        this.aliases.removeAll(Collections.<String>singletonList(null));  // remove any nulls from extra commas
        this.allowNoSlashAccess = Conf.allowNoSlashCommand;

        //this.requiredArgs.add("");
        //this.optionalArgs.put("","")

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.setHelpShort("The faction base command");
        this.helpLong.add(p.txt.parseTags("<i>This command contains all faction stuff."));

        //this.subCommands.add(p.cmdHelp);

        this.addSubCommand(this.cmdAdmin);
        this.addSubCommand(this.cmdAutoClaim);
        this.addSubCommand(this.cmdBoom);
        this.addSubCommand(this.cmdBypass);
        this.addSubCommand(this.cmdChat);
        this.addSubCommand(this.cmdToggleAllianceChat);
        this.addSubCommand(this.cmdChatSpy);
        this.addSubCommand(this.cmdClaim);
        this.addSubCommand(this.cmdConfig);
        this.addSubCommand(this.cmdCreate);
        this.addSubCommand(this.cmdDeinvite);
        this.addSubCommand(this.cmdDescription);
        this.addSubCommand(this.cmdDisband);
        this.addSubCommand(this.cmdHelp);
        this.addSubCommand(this.cmdHome);
        this.addSubCommand(this.cmdInvite);
        this.addSubCommand(this.cmdJoin);
        this.addSubCommand(this.cmdKick);
        this.addSubCommand(this.cmdLeave);
        this.addSubCommand(this.cmdList);
        this.addSubCommand(this.cmdMap);
        this.addSubCommand(this.cmdMod);
        this.addSubCommand(this.cmdMoney);
        this.addSubCommand(this.cmdOpen);
        this.addSubCommand(this.cmdOwner);
        this.addSubCommand(this.cmdOwnerList);
        this.addSubCommand(this.cmdPeaceful);
        this.addSubCommand(this.cmdPermanent);
        this.addSubCommand(this.cmdPermanentPower);
        this.addSubCommand(this.cmdPower);
        this.addSubCommand(this.cmdPowerBoost);
        this.addSubCommand(this.cmdRelationAlly);
        this.addSubCommand(this.cmdRelationEnemy);
        this.addSubCommand(this.cmdRelationNeutral);
        this.addSubCommand(this.cmdRelationTruce);
        this.addSubCommand(this.cmdReload);
        this.addSubCommand(this.cmdSafeunclaimall);
        this.addSubCommand(this.cmdSaveAll);
        this.addSubCommand(this.cmdSethome);
        this.addSubCommand(this.cmdShow);
        this.addSubCommand(this.cmdStatus);
        this.addSubCommand(this.cmdTag);
        this.addSubCommand(this.cmdTitle);
        this.addSubCommand(this.cmdUnclaim);
        this.addSubCommand(this.cmdUnclaimall);
        this.addSubCommand(this.cmdVersion);
        this.addSubCommand(this.cmdWarunclaimall);
        this.addSubCommand(this.cmdSB);
        this.addSubCommand(this.cmdShowInvites);
        this.addSubCommand(this.cmdAnnounce);
        this.addSubCommand(this.cmdSeeChunk);
        this.addSubCommand(this.cmdConvert);
        this.addSubCommand(this.cmdFWarp);
        this.addSubCommand(this.cmdSetFWarp);
        this.addSubCommand(this.cmdDelFWarp);
        this.addSubCommand(this.cmdModifyPower);
        this.addSubCommand(this.cmdLogins);
        this.addSubCommand(this.cmdClaimLine);
        this.addSubCommand(this.cmdAHome);
        this.addSubCommand(this.cmdPerm);
        this.addSubCommand(this.cmdPromote);
        this.addSubCommand(this.cmdDemote);
        this.addSubCommand(this.cmdSetDefaultRole);
        this.addSubCommand(this.cmdMapHeight);
        this.addSubCommand(this.cmdClaimAt);
        this.addSubCommand(this.cmdBan);
        this.addSubCommand(this.cmdUnban);
        this.addSubCommand(this.cmdbanlist);
        this.addSubCommand(this.cmdColeader);
        this.addSubCommand(this.cmdNear);
        this.addSubCommand(this.cmdOwnerAll);
        this.addSubCommand(this.cmdClearOwner);
        this.addSubCommand(this.cmdNotifications);
        this.addSubCommand(this.cmdTnt);
        this.addSubCommand(this.cmdTntfill);
        this.addSubCommand(this.cmdSetVaultRows);
        this.addSubCommand(this.cmdSetMaxWarps);
        this.addSubCommand(this.cmdSetMaxMembers);
        this.addSubCommand(this.cmdStrike);
        this.addSubCommand(this.cmdFWarpOther);
        this.addSubCommand(this.cmdAlts);
        this.addSubCommand(this.cmdAltJoin);
        this.addSubCommand(this.cmdCorner);
        this.addSubCommand(this.cmdPayPal);
        this.addSubCommand(this.cmdSetPayPal);
        this.addSubCommand(this.cmdVault);
        this.addSubCommand(this.cmdLock);
        this.addSubCommand(this.cmdSotw);
        this.addSubCommand(this.cmdSetMaxTNTBankBalance);
        this.addSubCommand(this.cmdMuteChat);
        this.addSubCommand(this.cmdCheck);
        this.addSubCommand(this.cmdUpgrades);
        this.addSubCommand(this.cmdCornerList);
        this.addSubCommand(this.cmdCornerReload);
        this.addSubCommand(this.cmdStats);
        this.addSubCommand(this.cmdShop);
        this.addSubCommand(this.cmdSetPoints);
        this.addSubCommand(this.cmdPoints);

        if (p.getConfig().getBoolean("inspect.enable") && p.getServer().getPluginManager().getPlugin("CoreProtect") != null) {
            this.addSubCommand(new CmdInspect());
            p.log(Level.INFO, "Enabling /f inspect command");
        } else {
            p.log(Level.WARNING, "Faction Inspect set to false in config.yml. Not enabling /f inspect command.");
        }

        if (p.getConfig().getBoolean("f-fly.enable", false)) {
            this.addSubCommand(this.cmdFly);
            this.addSubCommand(this.cmdStealth);
            p.log(Level.INFO, "Enabling /f fly command");
        } else {
            p.log(Level.WARNING, "Faction flight set to false in config.yml. Not enabling /f fly command.");
        }
        if (P.p.getConfig().getDouble("f-fly.trails.spawn-rate") >= 1.0) {
            this.addSubCommand(new CmdTrail());
            p.log(Level.INFO, "Enabled /f trail");
        } else {
            p.log(Level.INFO, "Disabled /f trail due to trails being disabled in the config");
        }
    }

    @Override
    public void perform() {
        this.commandChain.add(this);
        this.cmdHelp.execute(this.sender, this.args, this.commandChain);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}
