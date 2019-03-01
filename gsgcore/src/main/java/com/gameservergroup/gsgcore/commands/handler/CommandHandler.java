package com.gameservergroup.gsgcore.commands.handler;

import com.gameservergroup.gsgcore.commands.arguments.Argument;
import org.bukkit.command.CommandSender;

public class CommandHandler<T extends CommandSender> implements ICommandHandler<T> {

    private final T sender;
    private final String label;
    private final String[] args;

    public CommandHandler(T sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = args;
    }

    @Override
    public T getSender() {
        return sender;
    }

    @Override
    public String[] getRawArgs() {
        return args;
    }

    @Override
    public Argument getArg(int index) {
        return new Argument(index, getRawArg(index));
    }

    @Override
    public String getRawArg(int index) {
        return args[index];
    }

    @Override
    public String getLabel() {
        return label;
    }
}
