package com.gameservergroup.gsgcore.commands.arguments;

import java.util.Optional;
import java.util.function.Function;

public interface ArgumentParser<T> {

    Function<String, Optional<T>> parse();

}
