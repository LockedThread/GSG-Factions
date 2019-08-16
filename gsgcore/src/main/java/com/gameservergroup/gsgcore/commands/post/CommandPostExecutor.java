package com.gameservergroup.gsgcore.commands.post;

import com.gameservergroup.gsgcore.commands.handler.CommandHandler;
import com.gameservergroup.gsgcore.exceptions.CommandParseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandPostExecutor implements CommandExecutor {

    private static CommandPostExecutor instance;
    private final Map<String, CommandPost> COMMAND_MAP = new HashMap<>();

    public static CommandPostExecutor getInstance() {
        return instance == null ? instance = new CommandPostExecutor() : instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandPost commandPost = COMMAND_MAP.get(label.toLowerCase());
        if (commandPost != null) {
            try {
                commandPost.call(sender instanceof Player ? new CommandHandler<>((Player) sender, label, args) : new CommandHandler<>((ConsoleCommandSender) sender, label, args));
            } catch (CommandParseException | IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("This shouldn't happen, report to LockedThread if you see this.");
        }
        /*for (Map.Entry<String[], CommandPost> entry : CommandPost.getCommandPosts().entrySet()) {
            for (String alias : entry.getKey()) {
                if (alias.equalsIgnoreCase(command.getName())) {
                    try {
                        entry.getValue().call(sender instanceof Player ? new CommandHandler<>((Player) sender, label, args) : new CommandHandler<>((ConsoleCommandSender) sender, label, args));
                    } catch (CommandParseException ex) {
                        ex.getSenderConsumer().accept(sender);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;*/
        return true;
    }

    public Map<String, CommandPost> getCommandMap() {
        return COMMAND_MAP;
    }
}
