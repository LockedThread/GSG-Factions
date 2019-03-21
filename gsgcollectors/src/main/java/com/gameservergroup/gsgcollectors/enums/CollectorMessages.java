package com.gameservergroup.gsgcollectors.enums;

import com.gameservergroup.gsgcore.utils.Text;

public enum CollectorMessages {


    NO_ACCESS_NOT_YOURS("&cYou can't access this collector that's not yours!"),
    NO_ACCESS_NO_PERMISSIONS("&cYou can't access this collector since you're not at least a  &f{role}!"),
    NO_ACCESS_FACTIONLESS("&cYou can't edit collectors whilst factionless!"),
    TITLE_SELL("&a&l${money}"),
    TITLE_COLLECTOR_PLACE("&aYou have placed a collector!"),
    TITLE_COLLECTOR_BREAK("&cYou have broken a collector!"),
    ALREADY_ONE_HERE("&cThere's already a collector in this chunk!");

    private String message;

    CollectorMessages(String message) {
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