package com.gameservergroup.gsgcore.enums;

import com.gameservergroup.gsgcore.utils.Text;

public enum TrenchMessages {

    CANT_BREAK("&cSorry, you can't break blocks here!"),
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
