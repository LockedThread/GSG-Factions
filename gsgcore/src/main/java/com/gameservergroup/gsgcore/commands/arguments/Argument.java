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

    public <T> Optional<T> parse(Class<T> tClass) throws CommandParseException {
        if (valueOptional.isPresent()) {
            return ArgumentRegistry.getInstance().parse(tClass, valueOptional.get());
        }
        throw new CommandParseException(tClass.getSimpleName(), index);
    }
}
