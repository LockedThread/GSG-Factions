package com.gameservergroup.gsgcore.commands.post;

import com.gameservergroup.gsgcore.commands.CommandBuilder;
import com.gameservergroup.gsgcore.commands.handler.CommandHandler;
import com.gameservergroup.gsgcore.exceptions.CommandParseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandPostExecutor implements CommandExecutor {

    @SuppressWarnings("unchecked")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (Map.Entry<String[], CommandPost> entry : CommandPost.getCommandPosts().entrySet()) {
            for (String alias : entry.getKey()) {
                if (alias.equalsIgnoreCase(command.getName())) {
                    if (sender instanceof Player) {
                        CommandBuilder<Player> commandBuilder = (CommandBuilder<Player>) entry.getValue().getCommandBuilder();
                        CommandHandler<Player> playerCommandHandler = new CommandHandler<>((Player) sender, label, args);
                        try {
                            entry.getValue().call(playerCommandHandler);
                        } catch (CommandParseException ex) {
                            ex.getSenderConsumer().accept(sender);
                        }
                    }
                }
            }
        }
        return false;
    }
}
