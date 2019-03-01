package com.gameservergroup.gsgcore.commands.handler;

import com.gameservergroup.gsgcore.exceptions.CommandParseException;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface FunctionalCommandHandler<T extends CommandSender> {

    void handle(CommandHandler<T> c) throws CommandParseException;

}