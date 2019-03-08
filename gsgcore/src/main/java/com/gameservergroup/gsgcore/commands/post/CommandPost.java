package com.gameservergroup.gsgcore.commands.post;

import com.gameservergroup.gsgcore.commands.CommandBuilder;
import com.gameservergroup.gsgcore.commands.handler.CommandHandler;
import com.gameservergroup.gsgcore.commands.handler.ICommandHandler;
import com.gameservergroup.gsgcore.exceptions.CommandParseException;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.function.Predicate;

public class CommandPost {

    private static HashMap<String[], CommandPost> commandPosts;

    private CommandBuilder<CommandSender> commandBuilder;

    private CommandPost() {
    }

    public static CommandPost of() {
        return new CommandPost();
    }

    public static HashMap<String[], CommandPost> getCommandPosts() {
        if (commandPosts == null) {
            commandPosts = new HashMap<>();
        }
        return commandPosts;
    }

    public CommandBuilder<CommandSender> build() {
        this.commandBuilder = new CommandBuilder<>(this);
        return this.commandBuilder;
    }

    public CommandBuilder<? extends CommandSender> getCommandBuilder() {
        return this.commandBuilder;
    }

    public void setCommandBuilder(CommandBuilder<CommandSender> commandBuilder) {
        this.commandBuilder = commandBuilder;
    }

    @SuppressWarnings("unchecked")
    void call(CommandHandler commandHandler) throws CommandParseException {
        for (Predicate<ICommandHandler<? extends CommandSender>> predicate : getCommandBuilder().getPredicates()) {
            if (!predicate.test(commandHandler)) {
                return;
            }
        }

        getCommandBuilder().getFunctionalCommandHandler().handle(commandHandler);
    }
}
