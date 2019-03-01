package com.gameservergroup.gsgcore.commands.post;

import com.gameservergroup.gsgcore.commands.Command;
import com.gameservergroup.gsgcore.commands.handler.FunctionalCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.Predicate;

public class FunctionalCommandPost<T extends CommandSender> implements ICommandPost {

    @Override
    public ICommandPost<Player> filter(Predicate commandSenderPredicate) {
        return null;
    }

    @Override
    public Command handler(FunctionalCommandHandler functionalCommandHandler) {
        return null;
    }

    @Override
    public ICommandPost<Player> assertPlayer() {
        return null;
    }

    @Override
    public ICommandPost<ConsoleCommandSender> assertConsole() {
        return null;
    }

    @Override
    public void post(Plugin plugin, String... aliases) {

    }
}
