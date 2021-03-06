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

    default void reply(Enum... eee) {
        for (Enum anEnum : eee) {
            getSender().sendMessage(Text.toColor(anEnum.toString()));
        }
    }

    String[] getRawArgs();

    Argument getArg(int index);

    String getRawArg(int index);

    boolean isPlayer();

    boolean isConsole();

    String getLabel();

}