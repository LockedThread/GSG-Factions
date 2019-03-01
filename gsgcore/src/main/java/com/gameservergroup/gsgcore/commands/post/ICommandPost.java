package com.gameservergroup.gsgcore.commands.post;

import com.gameservergroup.gsgcore.commands.Command;
import com.gameservergroup.gsgcore.commands.handler.FunctionalCommandHandler;
import com.gameservergroup.gsgcore.commands.handler.ICommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

public interface ICommandPost<T extends CommandSender> {

    public ICommandPost<Player> filter(Predicate<ICommandHandler<T>> commandSenderPredicate);

    public Command handler(FunctionalCommandHandler<T> functionalCommandHandler);

    public ICommandPost<Player> assertPlayer();

    public ICommandPost<ConsoleCommandSender> assertConsole();

    public void post(Plugin plugin, String... aliases);
}
