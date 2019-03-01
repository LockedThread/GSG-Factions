package com.gameservergroup.gsgcore.commands.arguments;

import java.util.*;

public class ArgumentRegistry implements IArgumentRegistry {

    private static ArgumentRegistry instance;
    private HashMap<Class<?>, HashSet<ArgumentParser<?>>> argumentParserRegistry;

    private ArgumentRegistry() {
        this.argumentParserRegistry = new HashMap<>();
        register(Integer.class, () -> s -> {
            try {
                return Optional.of(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
    }

    public static ArgumentRegistry getInstance() {
        if (instance == null) {
            instance = new ArgumentRegistry();
        }
        return instance;
    }

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
