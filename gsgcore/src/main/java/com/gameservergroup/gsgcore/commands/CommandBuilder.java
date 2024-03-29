package com.gameservergroup.gsgcore.commands;

import com.gameservergroup.gsgcore.commands.handler.FunctionalCommandHandler;
import com.gameservergroup.gsgcore.commands.handler.ICommandHandler;
import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.commands.post.CommandPostExecutor;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.utils.CommandMapUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.function.Predicate;

public class CommandBuilder<T extends CommandSender> {

    private LinkedList<Predicate<ICommandHandler<? extends CommandSender>>> predicates;
    private CommandPost commandPost;
    private FunctionalCommandHandler<T> functionalCommandHandler;

    private CommandBuilder(LinkedList<Predicate<ICommandHandler<? extends CommandSender>>> predicates, CommandPost commandPost, FunctionalCommandHandler<T> functionalCommandHandler) {
        this.predicates = predicates;
        this.commandPost = commandPost;
        this.functionalCommandHandler = functionalCommandHandler;
    }

    public CommandBuilder(CommandPost commandPost) {
        this.commandPost = commandPost;
        this.predicates = new LinkedList<>();
    }

    public CommandBuilder<T> assertPermission(String permission) {
        filter(iCommandHandler -> {
            if (iCommandHandler.getSender().hasPermission(permission)) {
                return true;
            }
            iCommandHandler.reply("&cYou don't have permission to execute this command!");
            return false;
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    public CommandBuilder<Player> assertPlayer() {
        predicates.add(commandHandler -> {
            if (commandHandler.getSender() instanceof Player) {
                return true;
            }
            commandHandler.reply("&cYou may only use this command as a Player!");
            return false;
        });
        final CommandBuilder<Player> playerCommandBuilder = new CommandBuilder<>(predicates, commandPost, (FunctionalCommandHandler<Player>) functionalCommandHandler);
        commandPost.setCommandBuilder(playerCommandBuilder);
        return playerCommandBuilder;
    }

    @SuppressWarnings("unchecked")
    public CommandBuilder<ConsoleCommandSender> assertConsole() {
        predicates.add(commandHandler -> {
            if (commandHandler.getSender() instanceof ConsoleCommandSender) {
                return true;
            }
            commandHandler.reply("&cYou may only use this command as Console!");
            return false;
        });
        final CommandBuilder<ConsoleCommandSender> consoleCommandSenderCommandBuilder = new CommandBuilder<>(predicates, commandPost, (FunctionalCommandHandler<ConsoleCommandSender>) functionalCommandHandler);
        commandPost.setCommandBuilder(consoleCommandSenderCommandBuilder);
        return consoleCommandSenderCommandBuilder;
    }

    public CommandBuilder<T> filter(Predicate<ICommandHandler<? extends CommandSender>> predicate) {
        predicates.add(predicate);
        return this;
    }

    public void post(Module plugin, String... aliases) {
        post((Plugin) plugin, aliases);
        for (String alias : aliases) {
            plugin.getCommandAliases().add(alias);
        }
    }

    public void post(Plugin plugin, String... aliases) {
        for (String alias : aliases) {
            CommandPostExecutor.getCommandMap().put(alias, commandPost);
        }
        CommandMapUtil.registerCommand(plugin, aliases);
    }

    public LinkedList<Predicate<ICommandHandler<? extends CommandSender>>> getPredicates() {
        return predicates;
    }

    public CommandBuilder<T> handler(FunctionalCommandHandler<T> functionalCommandHandler) {
        this.functionalCommandHandler = functionalCommandHandler;
        return this;
    }

    public FunctionalCommandHandler<T> getFunctionalCommandHandler() {
        return functionalCommandHandler;
    }

}
