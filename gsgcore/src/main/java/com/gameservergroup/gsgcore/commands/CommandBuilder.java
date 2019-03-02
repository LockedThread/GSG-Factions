package com.gameservergroup.gsgcore.commands;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.commands.handler.CommandHandler;
import com.gameservergroup.gsgcore.commands.handler.ICommandHandler;
import com.gameservergroup.gsgcore.commands.post.CommandPost;
import com.gameservergroup.gsgcore.utils.CommandMapUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommandBuilder<T extends CommandSender> {

    private static final Predicate<ICommandHandler<? extends CommandSender>> IS_PLAYER = commandHandler -> commandHandler.getSender() instanceof Player;
    private static final Predicate<ICommandHandler<? extends CommandSender>> IS_CONSOLE = commandHandler -> commandHandler.getSender() instanceof ConsoleCommandSender;

    private Consumer<CommandHandler<T>> consumer;
    private HashSet<Predicate<ICommandHandler<? extends CommandSender>>> predicates;
    private CommandPost commandPost;
    private FunctionalCommandHandler<T> functionalCommandHandler;

    public CommandBuilder(HashSet<Predicate<ICommandHandler<? extends CommandSender>>> predicates, CommandPost commandPost) {
        this.predicates = predicates;
        this.commandPost = commandPost;
    }

    public CommandBuilder(CommandPost commandPost) {
        this.commandPost = commandPost;
        this.predicates = new HashSet<>();
    }

    public CommandBuilder<Player> assertPlayer() {
        predicates.add(IS_PLAYER);
        return new CommandBuilder<>(predicates, commandPost);
    }

    public CommandBuilder<ConsoleCommandSender> assertConsole() {
        predicates.add(IS_CONSOLE);
        return new CommandBuilder<>(predicates, commandPost);
    }

    public CommandBuilder<T> filter(Predicate<ICommandHandler<? extends CommandSender>> predicate) {
        predicates.add(predicate);
        return this;
    }

    public CommandBuilder<T> handle(Consumer<CommandHandler<T>> consumer) {
        this.consumer = consumer;
        return this;
    }

    public void post(Plugin plugin, String... aliases) {
        CommandPost.getCommandPosts().put(aliases, commandPost);
        CommandMapUtil.registerCommand(plugin, GSGCore.getInstance().getCommandPostExecutor(), aliases);
    }

    public HashSet<Predicate<ICommandHandler<? extends CommandSender>>> getPredicates() {
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
