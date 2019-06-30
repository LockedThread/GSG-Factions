package com.massivecraft.factions.zcore.factionstatistics;

import com.gameservergroup.gsgcore.utils.Utils;

public enum FactionStatistic {

    KILLS,
    DEATHS,
    BLOCKS_PLACED,
    BLOCKS_BROKEN,
    TIME_PLAYED;

    public String toPrettyName() {
        return Utils.toTitleCasing(name().replace("_", " "));
    }
}
