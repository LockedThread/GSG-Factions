package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdTrail extends FCommand {

    public CmdTrail() {
        super();
        this.aliases.add("trail");
        this.aliases.add("trails");

        this.optionalArgs.put("on/off/effect", "flip");
        this.optionalArgs.put("particle", "particle");

        this.permission = Permission.FLY_TRAILS.node;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeAdmin = false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TRAIL_DESCRIPTION;
    }

    @Override
    public void perform() {
        if (!argIsSet(0)) {
            fme.setFlyTrailsState(!fme.getFlyTrailsState());
        } else {
            // They are setting a particle type
            if (argAsString(0).equalsIgnoreCase("effect")) {
                if (argIsSet(1)) {
                    String effectName = argAsString(1);
                    Object particleEffect = p.particleProvider.effectFromString(effectName);
                    if (particleEffect == null) {
                        fme.msg(TL.COMMAND_FLYTRAILS_PARTICLE_INVALID);
                        return;
                    }

                    if (p.perm.has(me, Permission.FLY_TRAILS.node + "." + effectName)) {
                        fme.setFlyTrailsEffect(effectName);
                    } else {
                        fme.msg(TL.COMMAND_FLYTRAILS_PARTICLE_PERMS, effectName);
                    }
                } else {
                    msg(getUsageTranslation());
                }
            } else {
                boolean state = argAsBool(0);
                fme.setFlyTrailsState(state);
            }
        }
    }
}
