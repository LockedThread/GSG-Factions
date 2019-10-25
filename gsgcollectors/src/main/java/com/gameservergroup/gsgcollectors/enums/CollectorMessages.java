package com.gameservergroup.gsgcollectors.enums;

import com.gameservergroup.gsgcore.utils.Text;

public enum CollectorMessages {

    NO_ACCESS_NOT_YOURS("&cYou can't access this collector that's not yours!"),
    NO_ACCESS_NO_PERMISSIONS("&cYou can't access this collector since you're not at least a  &f{role}!"),
    NO_ACCESS_FACTIONLESS("&cYou can't edit collectors whilst factionless!"),
    NO_ACCESS_WILDERNESS("&cYou can't place a collector in wilderness"),
    NO_ACCESS_NOT_ROLE("&cYou don't have permission to place this collector because you're not of high enough faction rank!"),
    TITLE_SELL("&a&l${money}"),
    TITLE_COLLECTOR_PLACE("&aYou have placed a collector!"),
    TITLE_COLLECTOR_BREAK("&cYou have broken a collector!"),
    CANT_SELL_NOTHING("&cYou can't sell nothing!"),
    CANT_DEPOSIT_NOTHING("&cYou can't deposit nothing!"),
    DEPOSITED_TNT("&aYou have deposited &f{tnt} &atnt"),
    UPDATED_COLLECTOR_BLOCKPOSITION("&aYou have updated your collector's block position!");

    private String message;

    CollectorMessages(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return name().toLowerCase().replace("_", "-");
    }

    public String getValue() {
        return message;
    }

    @Override
    public String toString() {
        return Text.toColor(message);
    }
}
