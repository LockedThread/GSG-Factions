package com.gameservergroup.gsgcore.enums;

import com.gameservergroup.gsgcore.utils.Text;

public enum TrenchMessages {

    CANT_BREAK("&cSorry, you can't break blocks here!"),
    YOU_CANT_HAVE_GIGADRILL_ENABLED("&cYou can't have giga drill enabled when using this."),
    YOU_CANT_HAVE_SUPER_BREAKER_ENABLED("&cYou can't have berserk enabled when using this."),
    ;


    private String message;

    TrenchMessages(String message) {
        this.message = message;
    }

    public String getKey() {
        return name().toLowerCase().replace("_", "-");
    }

    public String getValue() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return Text.toColor(message);
    }
}
