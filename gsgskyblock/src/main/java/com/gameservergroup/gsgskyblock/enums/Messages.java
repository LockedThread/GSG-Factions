package com.gameservergroup.gsgskyblock.enums;

import com.gameservergroup.gsgcore.utils.Text;

public enum Messages {

    COMMAND_NO_PERMISSION("&cYou don't have permission to execute this command!"),
    COMMAND_MUST_BE_PLAYER(""),
    COMMAND_MUST_BE_CREATIVE("");

    private String message;

    Messages(String message) {
        this.message = message;
    }

    public String getKey() {
        return name().toLowerCase().replace("_", "-");
    }

    public String getValue() {
        return message;
    }

    public String getMessage() {
        return Text.toColor(message);
    }
}
