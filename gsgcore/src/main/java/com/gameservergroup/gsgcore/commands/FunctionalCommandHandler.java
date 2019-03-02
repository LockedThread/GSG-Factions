package com.gameservergroup.gsgcore.commands;

import com.gameservergroup.gsgcore.commands.handler.CommandHandler;
import com.gameservergroup.gsgcore.exceptions.CommandParseException;
import org.bukkit.command.CommandSender;

public interface FunctionalCommandHandler<T extends CommandSender> {

    void handle(CommandHandler<T> c) throws CommandParseException;
}
