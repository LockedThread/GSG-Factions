package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagReplacer;
import com.massivecraft.factions.zcore.util.TagUtil;
import mkremins.fanciful.FancyMessage;

import java.util.ArrayList;
import java.util.List;

public class CmdShow extends FCommand {

    private final List<String> defaults = new ArrayList<>();

    public CmdShow() {
        this.aliases.add("show");
        this.aliases.add("who");
        this.aliases.add("f");

        // add defaults to /f show in case config doesnt have it
        defaults.add("{header}");
        defaults.add("<a>Description: <i>{description}");
        defaults.add("<a>Joining: <i>{joining}    {peaceful}");
        defaults.add("<a>Land / Power / Maxpower: <i> {chunks} / {power} / {maxPower}");
        defaults.add("<a>Shield: {faction-shield}");
        defaults.add("<a>Founded: <i>{create-date}");
        defaults.add("<a>This faction is permanent, remaining even with no members.");
        defaults.add("<a>Land value: <i>{land-value} {land-refund}");
        defaults.add("<a>Balance: <i>{faction-balance}");
        defaults.add("<a>Allies(<i>{allies}<a>/<i>{max-allies}<a>): {allies-list}");
        defaults.add("<a>Members Online: (<i>{online}<a>/<i>{members}<a>): {online-list}");
        defaults.add("<a>Members Offline: (<i>{offline}<a>/<i>{members}<a>): {offline-list}");
        defaults.add("<a>Alts Online: (<i>{alts-online}<a>/<i>{alts}<a>): {alts-online-list}");
        defaults.add("<a>Alts Offline: (<i>{alts-offline}<a>/<i>{alts}<a>): {alts-offline-list}");

        // this.requiredArgs.add("");
        this.optionalArgs.put("faction tag", "yours");

        this.permission = Permission.SHOW.node;

        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Faction faction = myFaction;
        if (this.argIsSet(0)) {
            // check if it's a player first
            FPlayer fPlayer = argAsBestFPlayerMatch(0, null, false);
            if (fPlayer != null) {
                faction = fPlayer.hasAltFaction() ? fPlayer.getAltFaction() : fPlayer.getFaction();
            } else {
                faction = this.argAsFaction(0);
            }
        } else if (!faction.isNormal() && fme.hasAltFaction()) {
            faction = fme.getAltFaction();
        }
        if (faction == null) {
            return;
        }

        if (fme != null && !fme.getPlayer().hasPermission("factions.show.bypassexempt")
                && P.p.getConfig().getStringList("show-exempt").contains(faction.getTag())) {
            msg(TL.COMMAND_SHOW_EXEMPT);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!payForCommand(Conf.econCostShow, TL.COMMAND_SHOW_TOSHOW, TL.COMMAND_SHOW_FORSHOW)) {
            return;
        }

        List<String> show = P.p.getConfig().getStringList("show");
        if (show == null || show.isEmpty()) {
            show = defaults;
        }

        if (!faction.isNormal()) {
            String tag = faction.getTag(fme);
            // send header and that's all
            String header = show.get(0);
            if (TagReplacer.HEADER.contains(header)) {
                msg(p.txt.titleize(tag));
            } else {
                msg(p.txt.parse(TagReplacer.FACTION.replace(header, tag)));
            }
            return; // we only show header for non-normal factions
        }

        for (String raw : show) {
            String parsed = TagUtil.parsePlain(faction, fme, raw); // use relations
            if (parsed == null) {
                continue; // Due to minimal f show.
            }

//            Integer value = FactionsTopApi.getRank(faction.getId()).orElse(-1);
//            FactionsTopApi.Worth fworth = FactionsTopApi.getWorth(faction.getId()).orElse(null);
//            int worth = 0;
//            if(fworth != null) {
//                worth = (int) Math.floor(fworth.getTotal());
//            }
//            parsed = parsed.replace("{ftopvalue}", value > 0 ? value + " - ($" + df.format(worth) + ")" : "Not Ranked");

            if (fme != null) {
                parsed = TagUtil.parsePlaceholders(fme.getPlayer(), parsed);
            }

            if (fme != null && TagUtil.hasFancy(parsed)) {
                List<FancyMessage> fancy = TagUtil.parseFancy(faction, fme, parsed);
                if (fancy != null) {
                    sendFancyMessage(fancy);
                }
                continue;
            }
            if (!parsed.contains("{notFrozen}") && !parsed.contains("{notPermanent}")) {
                if (parsed.contains("{ig}")) {
                    // replaces all variables with no home TL
                    parsed = parsed.substring(0, parsed.indexOf("{ig}")) + TL.COMMAND_SHOW_NOHOME.toString();
                }
                if (parsed.contains("%")) {
                    parsed = parsed.replaceAll("%", ""); // Just in case it got in there before we disallowed it.
                }
                msg(p.txt.parse(parsed));
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOW_COMMANDDESCRIPTION;
    }

}