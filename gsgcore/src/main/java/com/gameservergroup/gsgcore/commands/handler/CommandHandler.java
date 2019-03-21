package com.gameservergroup.gsgcore.commands.handler;

import com.gameservergroup.gsgcore.commands.arguments.Argument;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Objects;

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
        try {
            return args[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "CommandHandler{" +
                "sender=" + sender +
                ", label='" + label + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandHandler<?> that = (CommandHandler<?>) o;
        return Objects.equals(sender, that.sender) && Objects.equals(label, that.label) && Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = sender != null ? sender.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
