package com.gameservergroup.gsgcore.commands.arguments;

import com.gameservergroup.gsgcore.exceptions.CommandParseException;

import java.util.Optional;

public class Argument {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<String> valueOptional;
    private int index;

    public Argument(int index, String value) {
        this.index = index;
        this.valueOptional = Optional.of(value);
    }

    public int getIndex() {
        return index;
    }

    public Optional<String> getValueOptional() {
        return valueOptional;
    }

    public boolean isPresent() {
        return valueOptional.isPresent();
    }

    public <T> Optional<T> parse(Class<T> tClass) {
        if (valueOptional.isPresent()) {
            final Optional<T> parse = ArgumentRegistry.getInstance().parse(tClass, valueOptional.get());
            if (parse.isPresent()) {
                return parse;
            }
        }
        return Optional.empty();
    }

    public <T> T forceParse(Class<T> tClass) throws CommandParseException {
        final Optional<T> parse = parse(tClass);
        if (parse.isPresent()) {
            return parse.get();
        }
        throw new CommandParseException(tClass.getSimpleName(), index);
    }
}
