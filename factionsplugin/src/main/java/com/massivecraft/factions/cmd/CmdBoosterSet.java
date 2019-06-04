package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.factionboosters.Booster;
import com.massivecraft.factions.zcore.util.TL;

public class CmdBoosterSet extends FCommand {

    public CmdBoosterSet() {
        super();
        this.aliases.add("set");

        this.permission = Permission.BOOSTER.node;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.requiredArgs.add("faction");
        this.requiredArgs.add("booster-id");
    }

    @Override
    public void perform() {
        Faction faction = argAsFaction(0, myFaction);
        if (args.size() == 2) {
            if (faction.isWilderness() || faction.isSafeZone() || faction.isWarZone()) {
                msg("&c" + faction.getTag() + " is a system faction. You can't give them any boosters.");
            } else {
                String boosterName = argAsString(1);
                Booster booster = Booster.getBoosterMap().get(boosterName);
                System.out.println("booster = " + booster);
                if (booster != null) {
                    if (faction.getBoosters().containsKey(boosterName)) {
                        msg("&aReset " + faction.getTag() + "'s booster time");
                        System.out.println("System.currentTimeMillis() = " + System.currentTimeMillis());
                        long value = System.currentTimeMillis() + booster.getTime();
                        System.out.println("value = " + value);
                        faction.setBooster(booster);
                    } else {
                        msg("&aApplied " + boosterName + " booster to " + faction.getTag());
                        booster.startBooster(faction);
                    }
                } else {
                    msg("&cUnable to find booster with name " + boosterName);
                }
            }
        } else {
            msg("&cInvalid arguments.");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BOOSTER_SET_DESCRIPTION;
    }
}
