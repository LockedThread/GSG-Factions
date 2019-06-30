package com.gameservergroup.gsgcore.exceptions;

import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public class CommandParseException extends Exception {

    private Consumer<CommandSender> senderConsumer;

    public CommandParseException(String type, int index) {
        this.senderConsumer = sender -> sender.sendMessage(Text.toColor("&cUnable to parse " + type + " at index " + index));
    }

    public CommandParseException(String message) {
        this.senderConsumer = sender -> sender.sendMessage(Text.toColor(message));
    }

    public CommandParseException(Consumer<CommandSender> senderConsumer) {
        this.senderConsumer = senderConsumer;
    }

    public Consumer<CommandSender> getSenderConsumer() {
        return senderConsumer;
    }
}