package com.gameservergroup.gsgcore.commands.handler;

import com.gameservergroup.gsgcore.commands.arguments.Argument;
import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.command.CommandSender;

public interface ICommandHandler<T extends CommandSender> {

    T getSender();

    default void reply(String... messages) {
        for (String message : messages) {
            getSender().sendMessage(Text.toColor(message));
        }
    }

    String[] getRawArgs();

    Argument getArg(int index);

    String getRawArg(int index);

    String getLabel();

}