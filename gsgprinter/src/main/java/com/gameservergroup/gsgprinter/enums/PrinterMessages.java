package com.gameservergroup.gsgprinter.enums;

import com.gameservergroup.gsgcore.utils.Text;

public enum PrinterMessages {

    YOU_ARE_IN_COMBAT("&cYou can't enabled Printer whilst in combat."),
    INVENTORY_MUST_BE_EMPTY("&cYour inventory and armor must be empty to enable printer!"),
    MUST_BE_IN_FRIENDLY_TERRITORY("&cYou must be in friendly territory to activate /printer!"),
    THIS_BLOCK_ISNT_PLACEABLE("&cYou are not allowed to edit this block!"),
    YOU_DONT_HAVE_ENOUGH_MONEY("&cYou don't have enough money to place a {material}"),
    PRINTER_ENABLE("&aYou have enabled PrinterMode!"),
    PRINTER_DISABLE("&cYou have disabled PrinterMode!"),
    YOU_CANT_DO_THIS("&cYou can't do this while printing!"),
    YOU_ARE_FACTIONLESS("&cYou can't enable printer whilst being factionless!");

    private String message;

    PrinterMessages(String message) {
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
