package com.gameservergroup.gsgcore.commands.arguments;

import java.util.Optional;

public interface IArgumentRegistry {

    void register(Class<?> aClass, ArgumentParser<?> argument);

    <T> Optional<T> parse(Class<T> tClass, String application);

}
