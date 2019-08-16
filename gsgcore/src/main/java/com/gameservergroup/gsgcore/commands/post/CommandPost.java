package com.gameservergroup.gsgcore.commands.post;

import com.gameservergroup.gsgcore.commands.CommandBuilder;
import com.gameservergroup.gsgcore.commands.handler.CommandHandler;
import com.gameservergroup.gsgcore.commands.handler.ICommandHandler;
import com.gameservergroup.gsgcore.exceptions.CommandParseException;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.function.Predicate;

public class CommandPost {

    private CommandBuilder<CommandSender> commandBuilder;

    private CommandPost() {
    }

    public static CommandPost of() {
        return new CommandPost();
    }

    public static CommandPost create() {
        return new CommandPost();
    }

    public CommandBuilder<CommandSender> builder() {
        this.commandBuilder = new CommandBuilder<>(this);
        return this.commandBuilder;
    }

    protected CommandBuilder<CommandSender> getCommandBuilder() {
        return this.commandBuilder;
    }

    public void setCommandBuilder(CommandBuilder<? extends CommandSender> commandBuilder) {
        this.commandBuilder = (CommandBuilder<CommandSender>) commandBuilder;
    }

    @SuppressWarnings("unchecked")
    void call(CommandHandler commandHandler) throws CommandParseException, IOException {
        for (Predicate<ICommandHandler<? extends CommandSender>> predicate : getCommandBuilder().getPredicates()) {
            if (!predicate.test(commandHandler)) {
                return;
            }
        }

        getCommandBuilder().getFunctionalCommandHandler().handle(commandHandler);
    }
}
