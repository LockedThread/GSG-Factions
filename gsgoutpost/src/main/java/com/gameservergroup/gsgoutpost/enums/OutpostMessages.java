package com.gameservergroup.gsgoutpost.enums;

import com.gameservergroup.gsgcore.utils.Text;

public enum OutpostMessages {

    OUTPOST_STATUS_DISABLED("&4&lOUTPOST DISABLED"),
    OUTPOST_STATUS_WAITING("&4&lOUTPOST WAITING TO BE NEUTRALIZED/CAPTURED"),
    OUTPOST_STATUS_NEUTRALIZING("&b&l&n{faction}&r &b&lis Neutralizing &7&l- &r{formatted-percentage} &7&l(&b&l{percentage}%&7&l)"),
    OUTPOST_STATUS_NEUTRALIZING_PAUSED("&4&lForeigner Entered &7&l- &7&l(&b&l{percentage}%&7&l) &7&l- &4&lNEUTRALIZING PAUSED"),
    OUTPOST_STATUS_NEUTRALIZED("&c{outpost} &7Outpost has been neutralized by {faction}"),
    OUTPOST_STATUS_CAPTURING_BROADCAST("&b&l&n{faction}&r &b&lis Capturing &7&l- &r{formatted-percentage} &7&l(&b&l{percentage}%&7&l)"),
    OUTPOST_STATUS_CAPTURED("&4&lYour faction has captured {outpost} outpost!"),
    OUTPOST_STATUS_CAPTURED_BROADCAST("&c{outpost} &7outpost has been captured by {faction}"),
    OUTPOST_STATUS_LOST_CONTROL_BROADCAST("&7{faction} lost control of {outpost} outpost."),
    OUTPOST_STATUS_CAPTURING("&7{faction} is capturing {outpost} outpost."),

    OUTPOST_ENTERED_CAPTURE_ZONE("&7You have entered the outpost capture zone"),

    COMMAND_NO_PERMISSION("&cYou don't have access to execute this command!"),
    COMMAND_LIST("&eOutposts: &f{outposts}"),
    COMMAND_TELEPORT_SENT("&aYou have been teleported to {outpost} outpost!"),
    COMMAND_TELEPORT_CANT_FIND("&cUnable to find outpost with name '{outpost}'"),
    COMMAND_TELEPORT_CANT_FIND_LOCATION("&cUnable to find location for outpost '{outpost}'. Make sure the worldguard region is still defined and you have a warp setup for this outpost."),
    COMMAND_OUTPOST_CREATE("&aCreated outpost with name {outpost}");

    private String message;

    OutpostMessages(String message) {
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