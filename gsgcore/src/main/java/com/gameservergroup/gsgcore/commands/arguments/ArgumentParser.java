package com.gameservergroup.gsgcore.commands.arguments;

import com.gameservergroup.gsgcore.exceptions.CommandParseException;

import java.util.Optional;
import java.util.function.Function;

public interface ArgumentParser<T> {

    Function<String, Optional<T>> parse();

    default T forceParse(String string, String message) throws CommandParseException {
        final Optional<T> optionalT = parse().apply(string);
        if (optionalT.isPresent()) {
            return optionalT.get();
        }
        throw new CommandParseException(message);
    }
}
