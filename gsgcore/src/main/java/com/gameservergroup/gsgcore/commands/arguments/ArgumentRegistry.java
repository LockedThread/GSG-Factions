package com.gameservergroup.gsgcore.commands.arguments;

import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.items.migration.MigrationType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class ArgumentRegistry implements IArgumentRegistry {

    private static ArgumentRegistry instance;
    private HashMap<Class<?>, HashSet<ArgumentParser<?>>> argumentParserRegistry;

    private ArgumentRegistry() {
        this.argumentParserRegistry = new HashMap<>();
        // Integer
        register(int.class, () -> s -> {
            try {
                return Optional.of(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
        register(Integer.class, () -> s -> {
            try {
                return Optional.of(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
        // Double
        register(double.class, () -> s -> {
            try {
                return Optional.of(Double.parseDouble(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
        register(Double.class, () -> s -> {
            try {
                return Optional.of(Double.parseDouble(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
        // CustomItem
        register(CustomItem.class, () -> s -> Optional.ofNullable(CustomItem.getCustomItem(s)));
        // String
        register(String.class, () -> Optional::of);
        // Player
        register(Player.class, () -> s -> Optional.ofNullable(Bukkit.getPlayer(s)));
        // OfflinePlayer
        register(OfflinePlayer.class, () -> s -> Optional.ofNullable(Bukkit.getOfflinePlayer(s)));
        // UUID
        register(UUID.class, () -> s -> {
            try {
                return Optional.of(UUID.fromString(s));
            } catch (Exception e) {
                return Optional.empty();
            }
        });

        register(MigrationType.class, () -> s -> {
            try {
                return Optional.of(MigrationType.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });

        register(Material.class, () -> s -> {
            try {
                return Optional.of(Material.valueOf(s));
            } catch (IllegalArgumentException e) {
                for (Material material : Material.values()) {
                    if (material.name().equalsIgnoreCase(s)) {
                        return Optional.of(material);
                    }
                }
            }
            return Optional.empty();
        });
    }

    public static ArgumentRegistry getInstance() {
        return instance == null ? instance = new ArgumentRegistry() : instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> parse(Class<T> tClass, String application) {
        for (Map.Entry<Class<?>, HashSet<ArgumentParser<?>>> entry : argumentParserRegistry.entrySet()) {
            if (entry.getKey().equals(tClass)) {
                for (ArgumentParser<?> argumentParser : entry.getValue()) {
                    final Optional<?> apply = argumentParser.parse().apply(application);
                    if (apply.isPresent()) {
                        return (Optional<T>) apply;
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void register(Class<?> aClass, ArgumentParser<?> argument) {
        argumentParserRegistry.computeIfPresent(aClass, (aClass1, arguments) -> {
            arguments.add(argument);
            return arguments;
        });
        argumentParserRegistry.putIfAbsent(aClass, new HashSet<>(Collections.singleton(argument)));
    }
}
